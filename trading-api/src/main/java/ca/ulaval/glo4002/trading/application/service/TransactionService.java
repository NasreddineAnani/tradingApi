package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.DTO.BuyTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.SellTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.assembler.TransactionAssembler;
import ca.ulaval.glo4002.trading.domain.exceptions.MarketClosedException;

import ca.ulaval.glo4002.trading.domain.market.Market;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Currency;


public class TransactionService {

    private final StockService stockService;
    private final MarketService marketService;
    private final AccountService accountService;
    private final TransactionAssembler transactionAssembler;

    @Inject
    public TransactionService(StockService stockService, AccountService accountService,
                              TransactionAssembler transactionAssembler, MarketService marketService) {
        this.stockService = stockService;
        this.accountService = accountService;
        this.transactionAssembler = transactionAssembler;
        this.marketService = marketService;
    }

    public BuyTransactionResponseDTO createBuyTransaction(
            TransactionRequestDTO transactionRequestDTO, String accountNumber) {
        BuyTransaction buyTransaction = this.transactionAssembler
                .fromTransactionRequestDTOToBuyTransaction(transactionRequestDTO);

        Stock stock = buyTransaction.getStock();
        Currency currency = marketService.getMarketCurrency(stock.getMarket());
        ZonedDateTime date = buyTransaction.getDate();

        verifyMarketOpen(stock.getMarket(), buyTransaction);

        buyTransaction.setPrice(stockService.getStockPrice(stock, date));
        buyTransaction.setCurrency(currency);

        this.accountService.buyStock(buyTransaction, accountNumber);
        return this.transactionAssembler.fromBuyTransactionToTransactionResponseDTO(buyTransaction);
    }

    public SellTransactionResponseDTO createSellTransaction(
            TransactionRequestDTO transactionRequestDTO, String accountNumber) {
        SellTransaction sellTransaction = this.transactionAssembler
                .fromTransactionRequestDTOToSellTransaction(transactionRequestDTO);

        Stock stock = sellTransaction.getStock();
        Currency currency = marketService.getMarketCurrency(stock.getMarket());
        ZonedDateTime date = sellTransaction.getDate();

        verifyMarketOpen(stock.getMarket(), sellTransaction);

        sellTransaction.setPrice(stockService.getStockPrice(stock, date));
        sellTransaction.setCurrency(currency);

        this.accountService.sellStock(sellTransaction, accountNumber);
        return this.transactionAssembler.fromSellTransactionToTransactionResponseDTO(sellTransaction);
    }

    public TransactionResponseDTO getTransactionResponseDTO(String accountNumber, String transactionNumber) {
        Transaction transaction = accountService.getTransaction(accountNumber, transactionNumber);
        if (transaction instanceof BuyTransaction) {
            return transactionAssembler.fromBuyTransactionToTransactionResponseDTO(transaction);
        } else {
            return transactionAssembler.fromSellTransactionToTransactionResponseDTO(transaction);
        }
    }

    private void verifyMarketOpen(String market, Transaction transaction) {
        Market theMarket = marketService.getMarketBySymbol(market);
        if (!theMarket.isOpen(transaction.getDate())) {
            throw new MarketClosedException(market, transaction.getTransactionNumber());
        }
    }
}
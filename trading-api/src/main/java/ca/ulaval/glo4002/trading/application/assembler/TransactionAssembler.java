package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.BuyTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.SellTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidDateException;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;


public class TransactionAssembler {

    private StockAssembler stockAssembler;

    @Inject
    public TransactionAssembler(StockAssembler stockAssembler) {
        this.stockAssembler = stockAssembler;
    }

    public BuyTransaction fromTransactionRequestDTOToBuyTransaction(
            TransactionRequestDTO transactionRequestDTO) {
        ZonedDateTime date = parseDateToZoneDateTime(transactionRequestDTO.date);
        Stock stock = transactionRequestDTO.stock;
        Long quantity = transactionRequestDTO.quantity;

        return new BuyTransaction(date, stock, quantity);
    }

    public SellTransaction fromTransactionRequestDTOToSellTransaction(
            TransactionRequestDTO transactionRequestDTO) {
        ZonedDateTime zonedDateTime = parseDateToZoneDateTime(transactionRequestDTO.date);
        Stock stock = transactionRequestDTO.stock;
        Long quantity = transactionRequestDTO.quantity;
        String buyTransactionNumber = transactionRequestDTO.transactionNumber;

        return new SellTransaction(zonedDateTime, stock, quantity, buyTransactionNumber);
    }

    public BuyTransactionResponseDTO fromBuyTransactionToTransactionResponseDTO(
            Transaction transaction) {
        BuyTransactionResponseDTO buyTransactionResponseDTO = new BuyTransactionResponseDTO();

        buyTransactionResponseDTO.transactionNumber = transaction.getTransactionNumber();
        buyTransactionResponseDTO.date = transaction.getDate();
        buyTransactionResponseDTO.stock = this.stockAssembler.fromStockToStockDTO(transaction.getStock());
        buyTransactionResponseDTO.price = transaction.getPrice();
        buyTransactionResponseDTO.fees = transaction.getFees();
        buyTransactionResponseDTO.quantity = transaction.getQuantity();

        return buyTransactionResponseDTO;
    }

    public SellTransactionResponseDTO fromSellTransactionToTransactionResponseDTO(
            Transaction transaction) {
        SellTransactionResponseDTO sellTransactionResponseDTO = new SellTransactionResponseDTO();

        sellTransactionResponseDTO.transactionNumber = transaction.getTransactionNumber();
        sellTransactionResponseDTO.date = transaction.getDate();
        sellTransactionResponseDTO.stock = this.stockAssembler.fromStockToStockDTO(transaction.getStock());
        sellTransactionResponseDTO.price = transaction.getPrice();
        sellTransactionResponseDTO.fees = transaction.getFees();
        sellTransactionResponseDTO.quantity = transaction.getQuantity();

        return sellTransactionResponseDTO;
    }

    private ZonedDateTime parseDateToZoneDateTime(String date) {
        try {
            return ZonedDateTime.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
    }
}
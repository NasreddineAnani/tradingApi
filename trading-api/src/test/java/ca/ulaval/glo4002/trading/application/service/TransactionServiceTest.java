package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.assembler.TransactionAssembler;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.market.Market;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.exceptions.MarketClosedException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.mockito.Mockito.*;


public class TransactionServiceTest {

    private final static String A_TRANSACTION_NUMBER = "12345";
    private final static String A_STOCK_SYMBOL = "GOOG";
    private final static String A_MARKET = "NASDAQ";
    private final static Long A_QUANTITY = 10L;
    private final static String A_VALID_DATE = "2018-08-21T15:23:20.142Z";
    private final static String AN_ACCOUNT_NUMBER = "1234";
    private final static BigDecimal A_PRICE = new BigDecimal(1);
    private final static Stock A_STOCK = new Stock(A_STOCK_SYMBOL, A_MARKET);
    private final static ZonedDateTime A_DATE = ZonedDateTime.parse(A_VALID_DATE);

    private TransactionService transactionService;
    private StockService stockServiceMock;
    private AccountService accountServiceMock;
    private MarketService marketServiceMock;
    private TransactionAssembler transactionAssemblerMock;
    private Market marketMock;
    private TransactionRequestDTO transactionRequestDTO;
    private BuyTransaction aBuyTransactionMock;
    private SellTransaction aSellTransactionMock;

    @Before
    public void setUp() {
        stockServiceMock = mock(StockService.class);
        accountServiceMock = mock(AccountService.class);
        marketServiceMock = mock(MarketService.class);
        transactionAssemblerMock = mock(TransactionAssembler.class);
        transactionService = new TransactionService(stockServiceMock, accountServiceMock,
                transactionAssemblerMock, marketServiceMock);

        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.transactionNumber = A_TRANSACTION_NUMBER;
        transactionRequestDTO.stock = new Stock(A_STOCK_SYMBOL, A_MARKET);
        transactionRequestDTO.quantity = A_QUANTITY;
        transactionRequestDTO.date = A_VALID_DATE;

        aSellTransactionMock = mock(SellTransaction.class);
        aBuyTransactionMock = mock(BuyTransaction.class);

        marketMock = mock(Market.class);
        prepareMarketMock();
        prepareBuyTransactionMock();
        prepareSellTransactionMock();
    }

    private void prepareMarketMock() {
        when(marketMock.isOpen(any())).thenReturn(true);
        when(marketServiceMock.getMarketBySymbol(anyString())).thenReturn(marketMock);
        when(transactionAssemblerMock.fromTransactionRequestDTOToBuyTransaction(transactionRequestDTO))
                .thenReturn(aBuyTransactionMock);
        when(transactionAssemblerMock.fromTransactionRequestDTOToSellTransaction(transactionRequestDTO))
                .thenReturn(aSellTransactionMock);
    }

    private void prepareSellTransactionMock() {
        when(aSellTransactionMock.getPrice()).thenReturn(A_PRICE);
        when(aSellTransactionMock.getStock()).thenReturn(A_STOCK);
        when(aSellTransactionMock.getDate()).thenReturn(A_DATE);
    }

    private void prepareBuyTransactionMock() {
        when(aBuyTransactionMock.getDate()).thenReturn(A_DATE);
        when(aBuyTransactionMock.getStock()).thenReturn(A_STOCK);
        when(aBuyTransactionMock.getDate()).thenReturn(A_DATE);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingABuyTransaction_thenCheckIfTheMarketIsOpen() {
        transactionService.createBuyTransaction(transactionRequestDTO, anyString());

        verify(marketMock).isOpen(any());
    }

    @Test(expected = MarketClosedException.class)
    public void givenATransactionRequestDTOAndAClosedMarket_whenCreatingABuyTransaction_thenTransactionIsInvalid() {
        when(marketMock.isOpen(any())).thenReturn(false);

        transactionService.createBuyTransaction(transactionRequestDTO, anyString());
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingABuyTransaction_thenExecuteTheBuyTransaction() {
        transactionService.createBuyTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(accountServiceMock).buyStock(aBuyTransactionMock, AN_ACCOUNT_NUMBER);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingABuyTransaction_thenABuyTransactionResponseDTOIsCreated() {
        when(transactionAssemblerMock.fromTransactionRequestDTOToBuyTransaction(any())).thenReturn(aBuyTransactionMock);

        transactionService.createBuyTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(transactionAssemblerMock).fromBuyTransactionToTransactionResponseDTO(aBuyTransactionMock);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingABuyTransaction_thenThePriceIsSetUsingTheStockServiceResponse() {
        prepareBuyTransactionMock();
        BigDecimal stockServiceResponse = A_PRICE;
        when(transactionAssemblerMock.fromTransactionRequestDTOToBuyTransaction(transactionRequestDTO))
                .thenReturn(aBuyTransactionMock);
        when(stockServiceMock.getStockPrice(A_STOCK, A_DATE)).thenReturn(stockServiceResponse);

        transactionService.createBuyTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(aBuyTransactionMock).setPrice(stockServiceResponse);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingABuyTransaction_thenTheCurrencyIsSetUsingTheMarketServiceResponse() {
        prepareBuyTransactionMock();
        Currency aValidCurrency = Currency.getInstance("CAD");
        when(transactionAssemblerMock.fromTransactionRequestDTOToBuyTransaction(transactionRequestDTO))
                .thenReturn(aBuyTransactionMock);
        when(marketServiceMock.getMarketCurrency(any())).thenReturn(aValidCurrency);

        transactionService.createBuyTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(aBuyTransactionMock).setCurrency(aValidCurrency);
    }

    @Test
    public void givenAnExistingAccountNumberAndTransactionNumber_whenAskingForABuyTransactionInformation_thenICreateABuyTransactionResponseDTO() {
        when(accountServiceMock.getTransaction(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER))
                .thenReturn(aBuyTransactionMock);

        transactionService.getTransactionResponseDTO(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        verify(transactionAssemblerMock).fromBuyTransactionToTransactionResponseDTO(aBuyTransactionMock);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingASellTransaction_thenCheckIfTheMarketIsOpen() {
        transactionService.createSellTransaction(transactionRequestDTO, anyString());

        verify(marketMock).isOpen(any());
    }

    @Test(expected = MarketClosedException.class)
    public void givenATransactionRequestDTOAndAClosedMarket_whenCreatingASellTransaction_thenTransactionIsInvalid() {
        when(marketMock.isOpen(any())).thenReturn(false);

        transactionService.createSellTransaction(transactionRequestDTO, anyString());
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingASellTransaction_thenExecuteTheSellTransaction() {
        transactionService.createSellTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(accountServiceMock).sellStock(aSellTransactionMock, AN_ACCOUNT_NUMBER);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingASellTransaction_thenASellTransactionDTOIsCreated() {
        when(transactionAssemblerMock.fromTransactionRequestDTOToSellTransaction(any())).thenReturn(aSellTransactionMock);

        transactionService.createSellTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(transactionAssemblerMock).fromSellTransactionToTransactionResponseDTO(aSellTransactionMock);
    }

    @Test
    public void givenATransactionDTO_whenCreatingASellTransaction_thenThePriceIsSetUsingTheStockServiceResponse() {
        prepareSellTransactionMock();
        BigDecimal stockServiceResponse = A_PRICE;
        when(transactionAssemblerMock.fromTransactionRequestDTOToSellTransaction(transactionRequestDTO))
                .thenReturn(aSellTransactionMock);
        when(stockServiceMock.getStockPrice(A_STOCK, A_DATE)).thenReturn(stockServiceResponse);

        transactionService.createSellTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(aSellTransactionMock).setPrice(stockServiceResponse);
    }

    @Test
    public void givenATransactionRequestDTO_whenCreatingASellTransaction_thenTheCurrencyIsSetUsingTheMarketServiceResponse() {
        prepareBuyTransactionMock();
        Currency aValidCurrency = Currency.getInstance("CAD");
        when(transactionAssemblerMock.fromTransactionRequestDTOToSellTransaction(transactionRequestDTO))
                .thenReturn(aSellTransactionMock);
        when(marketServiceMock.getMarketCurrency(any())).thenReturn(aValidCurrency);

        transactionService.createSellTransaction(transactionRequestDTO, AN_ACCOUNT_NUMBER);

        verify(aSellTransactionMock).setCurrency(aValidCurrency);
    }

    @Test
    public void givenAnExistingAccountNumberAndTransactionNumber_whenAskingForASellTransactionInformation_thenICreateASellTransactionResponseDTO() {
        when(accountServiceMock.getTransaction(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER))
                .thenReturn(aSellTransactionMock);

        transactionService.getTransactionResponseDTO(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        verify(transactionAssemblerMock).fromSellTransactionToTransactionResponseDTO(aSellTransactionMock);
    }
}
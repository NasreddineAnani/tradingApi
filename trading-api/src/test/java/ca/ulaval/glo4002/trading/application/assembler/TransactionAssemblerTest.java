package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.BuyTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.SellTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.StockDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidDateException;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionAssemblerTest {

    private final static String A_STOCK_SYMBOL = "GOOG";
    private final static String A_MARKET_SYMBOL = "NASDAQ";
    private final static String A_DATE = "2018-08-21T15:23:20.142Z";
    private final static String A_INVALID_DATE = "invalidFormatDate";
    private final static String A_TRANSACTION_NUMBER = "3f19c232-0fa6-4d43-acf7-addace7d13ec";
    private final static Long A_QUANTITY = 10L;
    private final static BigDecimal A_PRICE = new BigDecimal(10);

    private Stock aStock;
    private TransactionAssembler transactionAssembler;
    private StockAssembler stockAssemblerMock;
    private ZonedDateTime A_ZONEDDATETIME;
    private StockDTO expectedStockDTO;

    @Before
    public void setUp() {
        aStock = new Stock(A_STOCK_SYMBOL, A_MARKET_SYMBOL);
        stockAssemblerMock = mock(StockAssembler.class);
        transactionAssembler = new TransactionAssembler(stockAssemblerMock);
        A_ZONEDDATETIME = ZonedDateTime.parse(A_DATE);

        expectedStockDTO = new StockDTO();
        expectedStockDTO.symbol = aStock.getSymbol();
        expectedStockDTO.market = aStock.getMarket();
    }

    @Test
    public void givenATransactionRequestDTO_whenConvertItToBuyTransaction_thenTransactionIsCreatedWithTheSameAttributes() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.date = A_DATE;
        transactionRequestDTO.stock = aStock;
        transactionRequestDTO.quantity = A_QUANTITY;

        BuyTransaction buyTransaction = this.transactionAssembler
                .fromTransactionRequestDTOToBuyTransaction(transactionRequestDTO);

        assertEquals(A_ZONEDDATETIME, buyTransaction.getDate());
        assertEquals(aStock, buyTransaction.getStock());
        assertEquals(A_QUANTITY, buyTransaction.getQuantity());
    }

    @Test
    public void givenATransactionRequestDTO_whenConvertItToSellTransaction_thenTransactionIsCreatedWithTheSameAttributes() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.date = A_DATE;
        transactionRequestDTO.stock = aStock;
        transactionRequestDTO.quantity = A_QUANTITY;
        transactionRequestDTO.transactionNumber = A_TRANSACTION_NUMBER;

        SellTransaction sellTransaction = this.transactionAssembler
                .fromTransactionRequestDTOToSellTransaction(transactionRequestDTO);

        assertEquals(A_ZONEDDATETIME, sellTransaction.getDate());
        assertEquals(aStock, sellTransaction.getStock());
        assertEquals(A_QUANTITY, sellTransaction.getQuantity());
        assertEquals(A_TRANSACTION_NUMBER, sellTransaction.getBuyTransactionNumber());
    }

    @Test
    public void givenBuyTransaction_whenConvertItToBuyTransactionResponseDTO_thenBuyTransactionResponseDTOIsCreatedWithTheSameAttributes() {
        BuyTransaction buyTransaction = new BuyTransaction(A_ZONEDDATETIME, aStock, A_QUANTITY);
        buyTransaction.setPrice(A_PRICE);
        when(stockAssemblerMock.fromStockToStockDTO(buyTransaction.getStock())).thenReturn(expectedStockDTO);

        BuyTransactionResponseDTO buyTransactionResponseDTO = this.transactionAssembler
                .fromBuyTransactionToTransactionResponseDTO(buyTransaction);

        assertEquals(buyTransaction.getTransactionNumber(), buyTransactionResponseDTO
                .transactionNumber);
        assertEquals(buyTransaction.getDate(), buyTransactionResponseDTO.date);
        assertEquals(expectedStockDTO, buyTransactionResponseDTO.stock);
        assertEquals(A_PRICE, buyTransactionResponseDTO.price);
        assertEquals(buyTransaction.getQuantity(), buyTransactionResponseDTO.quantity);
        assertEquals(buyTransaction.getFees(), buyTransactionResponseDTO.fees);
    }

    @Test
    public void givenSellTransaction_whenConvertItToSellTransactionResponseDTO_thenSellTransactionResponseDTOIsCreatedWithTheSameAttributes() {
        SellTransaction sellTransaction = new SellTransaction(A_ZONEDDATETIME, aStock,
                A_QUANTITY, A_TRANSACTION_NUMBER);
        sellTransaction.setPrice(A_PRICE);
        when(stockAssemblerMock.fromStockToStockDTO(sellTransaction.getStock())).thenReturn(expectedStockDTO);

        SellTransactionResponseDTO sellTransactionResponseDTO = this.transactionAssembler
                .fromSellTransactionToTransactionResponseDTO(sellTransaction);

        assertEquals(sellTransaction.getTransactionNumber(), sellTransactionResponseDTO.transactionNumber);
        assertEquals(sellTransaction.getDate(), sellTransactionResponseDTO.date);
        assertEquals(expectedStockDTO, sellTransactionResponseDTO.stock);
        assertEquals(sellTransaction.getQuantity(), sellTransactionResponseDTO.quantity);
        assertEquals(A_PRICE, sellTransactionResponseDTO.price);
        assertEquals(sellTransaction.getFees(), sellTransactionResponseDTO.fees);
    }

    @Test(expected = InvalidDateException.class)
    public void givenATransactionRequestDTOWithAnInvalidDate_whenConvertItToSellTransaction_thenTransactionDateIsInvalid() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.date = A_INVALID_DATE;

        this.transactionAssembler.fromTransactionRequestDTOToSellTransaction(transactionRequestDTO);
    }
}
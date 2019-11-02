package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.NotEnoughStockException;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.exceptions.StockParametersDoNotMatch;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SellTransactionTest {

    private final static Long A_QUANTITY = 10L;
    private final static BigDecimal A_PRICE = new BigDecimal(1);
    private final static String A_BUY_TRANSACTION_NUMBER = "12345";
    private final static Currency A_CURRENCY = Currency.getInstance("CAD");
    private final static String A_SYMBOL = "GOOG";
    private final static String A_MARKET = "NASDAQ";
    private final static String A_DIFFERENT_SYMBOL = "MSFT";
    private final static String A_DIFFERENT_MARKET = "WISS";
    private final static ZonedDateTime A_DATE = ZonedDateTime.now();
    private Stock aValidStock;
    private Stock aDifferentValidStock;
    private SellTransaction aValidSellTransaction;
    private BigDecimal fees;

    private ExchangeRateConverter exchangeRateConverterMock;

    @Before
    public void setUp() {
        aValidStock = new Stock(A_SYMBOL, A_MARKET);
        aDifferentValidStock = new Stock(A_DIFFERENT_SYMBOL, A_DIFFERENT_MARKET);
        aValidSellTransaction = new SellTransaction(A_DATE, aValidStock, A_QUANTITY, A_BUY_TRANSACTION_NUMBER);
        aValidSellTransaction.setPrice(A_PRICE);
        aValidSellTransaction.setCurrency(A_CURRENCY);
        aValidSellTransaction.calculateFees();
        fees = aValidSellTransaction.getFees();

        exchangeRateConverterMock = mock(ExchangeRateConverter.class);
        setUpExchangeRateConverterMock();
    }

    @Test(expected = StockParametersDoNotMatch.class)
    public void givenSellTransactionWithDifferentStock_whenValidatingIt_thenItIsNotValid() {
        aValidSellTransaction.verifyTransactionValidity(aDifferentValidStock, A_QUANTITY);
    }

    @Test(expected = NotEnoughStockException.class)
    public void givenSellTransactionWithMoreQuantityThenBought_whenValidatingIt_thenItIsNotValid() {
        Long aSmallerQuantityThenSellTransactionQuantity = A_QUANTITY - 1;

        aValidSellTransaction.verifyTransactionValidity(aValidStock, aSmallerQuantityThenSellTransactionQuantity);
    }

    @Test
    public void givenASellTransaction_whenCalculatingTotalTransactionGain_thenFeesAreSubtractedFromTotalPrice() {
        BigDecimal expectedTransactionGain = A_PRICE.multiply(new BigDecimal(A_QUANTITY)).subtract(fees);

        BigDecimal returnedTransactionGain = aValidSellTransaction.calculateTotalTransactionGain(exchangeRateConverterMock);

        assertEquals(expectedTransactionGain, returnedTransactionGain);
    }

    @Test
    public void givenAValidSellTransaction_whenCalculatingTotalPrice_thenPriceIsConvertedInCAD_thenReconverted_thenFeesAreConvertedInActualCurrency() {
        InOrder orderVerifier = Mockito.inOrder(exchangeRateConverterMock);

        aValidSellTransaction.calculateTotalTransactionGain(exchangeRateConverterMock);

        orderVerifier.verify(exchangeRateConverterMock).toCAD(aValidSellTransaction.getCurrency(), aValidSellTransaction.getPrice());
        orderVerifier.verify(exchangeRateConverterMock).fromCADtoCurrency(aValidSellTransaction.getPrice(), aValidSellTransaction.getCurrency());
        orderVerifier.verify(exchangeRateConverterMock).fromCADtoCurrency(aValidSellTransaction.getFees(), aValidSellTransaction.getCurrency());
    }

    private void setUpExchangeRateConverterMock() {
        when(exchangeRateConverterMock.toCAD(A_CURRENCY, A_PRICE)).thenReturn(A_PRICE);
        when(exchangeRateConverterMock.fromCADtoCurrency(A_PRICE, A_CURRENCY)).thenReturn(A_PRICE);
        when(exchangeRateConverterMock.fromCADtoCurrency(fees, A_CURRENCY)).thenReturn(fees);
    }
}
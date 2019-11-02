package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidQuantityException;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
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

public class BuyTransactionTest {

    private final static ZonedDateTime A_DATE = ZonedDateTime.now();
    private final static BigDecimal A_PRICE = new BigDecimal(2);
    private final static Currency A_CURRENCY = Currency.getInstance("CAD");
    private final static Long A_QUANTITY = 10L;
    private final static Long A_LARGE_QUANTITY = 1000L;
    private final static String A_SYMBOL = "GOOG";
    private final static String A_MARKET = "NASDAQ";
    private Stock aValidStock;
    private BigDecimal fees;
    private BuyTransaction validBuyTransaction;

    private ExchangeRateConverter exchangeRateConverterMock;

    @Before
    public void setUp() {
        aValidStock = new Stock(A_SYMBOL, A_MARKET);
        validBuyTransaction = new BuyTransaction(A_DATE, aValidStock, A_QUANTITY);
        validBuyTransaction.setPrice(A_PRICE);
        validBuyTransaction.setCurrency(A_CURRENCY);
        validBuyTransaction.calculateFees();
        fees = validBuyTransaction.getFees();
        exchangeRateConverterMock = mock(ExchangeRateConverter.class);
        setUpExchangeRateConverterMock();
    }

    @Test
    public void givenAValidBuyTransaction_whenDeductingStockQuantity_thenTheStockQuantityIsDeducted() {
        Long halfTheQuantity = A_QUANTITY / 2;
        Long expectedQuantity = A_QUANTITY - halfTheQuantity;

        validBuyTransaction.deductTransactionStockQuantity(halfTheQuantity);

        assertEquals(expectedQuantity, validBuyTransaction.getQuantity());
    }

    @Test
    public void givenABuyTransaction_whenCalculatingTotalPrice_thenFeesAreAddedToTotalPrice() {
        BigDecimal expectedTransactionPriceTotal = A_PRICE.multiply(new BigDecimal(A_QUANTITY)).add(fees);

        BigDecimal returnedTransactionPriceTotal = validBuyTransaction.calculateTransactionPriceTotal(exchangeRateConverterMock);

        assertEquals(expectedTransactionPriceTotal, returnedTransactionPriceTotal);
    }

    @Test(expected = InvalidQuantityException.class)
    public void givenAValidBuyTransaction_whenDeductingMoreThanAvailable_thenOperationIsCanceled() {
        validBuyTransaction.deductTransactionStockQuantity(A_LARGE_QUANTITY);
    }

    @Test
    public void givenAValidBuyTransaction_whenCalculatingTotalPrice_thenPriceIsConvertedInCAD_thenReconverted_thenFeesAreConvertedInActualCurrency() {
        InOrder orderVerifier = Mockito.inOrder(exchangeRateConverterMock);

        validBuyTransaction.calculateTransactionPriceTotal(exchangeRateConverterMock);

        orderVerifier.verify(exchangeRateConverterMock).toCAD(validBuyTransaction.getCurrency(), validBuyTransaction.getPrice());
        orderVerifier.verify(exchangeRateConverterMock).fromCADtoCurrency(validBuyTransaction.getPrice(), validBuyTransaction.getCurrency());
        orderVerifier.verify(exchangeRateConverterMock).fromCADtoCurrency(validBuyTransaction.getFees(), validBuyTransaction.getCurrency());
    }

    private void setUpExchangeRateConverterMock() {
        when(exchangeRateConverterMock.toCAD(A_CURRENCY, A_PRICE)).thenReturn(A_PRICE);
        when(exchangeRateConverterMock.fromCADtoCurrency(A_PRICE, A_CURRENCY)).thenReturn(A_PRICE);
        when(exchangeRateConverterMock.fromCADtoCurrency(fees, A_CURRENCY)).thenReturn(fees);
    }
}
package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.UnsupportedCurrency;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class StaticExchangeRateConverterTest {

    private final static int DECIMAL_SCALE = 20;
    private final static BigDecimal USD_TO_CAD = new BigDecimal(1.31);
    private final static BigDecimal CHF_TO_CAD = new BigDecimal(1.45);
    private final static BigDecimal JPY_TO_CAD = new BigDecimal(0.01);
    private final static BigDecimal CREDIT_AMOUNT = BigDecimal.TEN;
    private final static Currency A_VALID_UNSUPPORTED_CURRENCY = Currency.getInstance("EUR");
    private StaticExchangeRateConverter staticExchangeRateConverter;

    @Before
    public void setUp() {
        staticExchangeRateConverter = new StaticExchangeRateConverter();
    }

    @Test(expected = UnsupportedCurrency.class)
    public void givenACurrencyNotSupported_whenConvertingThatCurrencyToCAD_thenNothingIsReturned() {
        staticExchangeRateConverter.toCAD(A_VALID_UNSUPPORTED_CURRENCY, CREDIT_AMOUNT);
    }

    @Test
    public void givenAnAmountInUSD_whenConvertingItToCAD_thenTheValueReturnedIsInCAD() {
        BigDecimal expectedValue = CREDIT_AMOUNT.multiply(USD_TO_CAD);

        BigDecimal returnedValue = staticExchangeRateConverter.toCAD(Currency.getInstance("USD"), CREDIT_AMOUNT);

        assertEquals(expectedValue, returnedValue);
    }

    @Test
    public void givenAnAmountInCHF_whenConvertingItToCAD_thenTheValueReturnedIsInCAD() {
        BigDecimal expectedValue = CREDIT_AMOUNT.multiply(CHF_TO_CAD);

        BigDecimal returnedValue = staticExchangeRateConverter.toCAD(Currency.getInstance("CHF"), CREDIT_AMOUNT);

        assertEquals(expectedValue, returnedValue);
    }

    @Test
    public void givenAnAmountInJPY_whenConvertingItToCAD_thenTheValueReturnedIsInCAD() {
        BigDecimal expectedValue = CREDIT_AMOUNT.multiply(JPY_TO_CAD);

        BigDecimal returnedValue = staticExchangeRateConverter.toCAD(Currency.getInstance("JPY"), CREDIT_AMOUNT);

        assertEquals(expectedValue, returnedValue);
    }

    @Test
    public void givenAnAmountInCAD_whenConvertingItToUSD_thenTheValueReturnedIsInUSD() {
        BigDecimal expectedValue = CREDIT_AMOUNT.divide(USD_TO_CAD, DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal returnedValue = staticExchangeRateConverter.fromCADtoCurrency
                (CREDIT_AMOUNT, Currency.getInstance("USD")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        assertEquals(expectedValue, returnedValue);
    }

    @Test
    public void givenAnAmountInCAD_whenConvertingItToCHF_thenTheValueReturnedIsInCHF() {
        BigDecimal expectedValue = CREDIT_AMOUNT.divide(CHF_TO_CAD, DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal returnedValue = staticExchangeRateConverter.fromCADtoCurrency
                (CREDIT_AMOUNT, Currency.getInstance("CHF")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        assertEquals(expectedValue, returnedValue);
    }

    @Test
    public void givenAnAmountInCAD_whenConvertingItToJPY_thenTheValueReturnedIsInJPY() {
        BigDecimal expectedValue = CREDIT_AMOUNT.divide(JPY_TO_CAD, DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal returnedValue = staticExchangeRateConverter.fromCADtoCurrency
                (CREDIT_AMOUNT, Currency.getInstance("JPY")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        assertEquals(expectedValue, returnedValue);
    }

}

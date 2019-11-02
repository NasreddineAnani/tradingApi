package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.UnsupportedCurrency;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class StaticExchangeRateConverter implements ExchangeRateConverter {

    private final static int DECIMAL_SCALE = 20;

    private final static BigDecimal USD_TO_CAD = new BigDecimal(1.31);
    private final static BigDecimal CHF_TO_CAD = new BigDecimal(1.45);
    private final static BigDecimal JPY_TO_CAD = new BigDecimal(0.01);
    private final static BigDecimal CAD_TO_CAD = BigDecimal.ONE;

    private final static Currency CAD_CURRENCY = Currency.getInstance("CAD");
    private final static Currency USD_CURRENCY = Currency.getInstance("USD");
    private final static Currency CHF_CURRENCY = Currency.getInstance("CHF");
    private final static Currency JPY_CURRENCY = Currency.getInstance("JPY");

    private static Map<Currency, BigDecimal> CADexchangeRateTables = new HashMap<>();

    public StaticExchangeRateConverter() {
        buildCADTable();
    }

    @Override
    public BigDecimal toCAD(Currency currency, BigDecimal amount) {
        BigDecimal exchangeRate = CADexchangeRateTables.get(currency);
        if (exchangeRate == null) {
            throw new UnsupportedCurrency();
        }
        return exchangeRate.multiply(amount);
    }

    @Override
    public BigDecimal fromCADtoCurrency(BigDecimal CADAmount, Currency currency) {
        BigDecimal exchangeRate = CADexchangeRateTables.get(currency);
        if (exchangeRate == null) {
            throw new UnsupportedCurrency();
        }
        return CADAmount.divide(exchangeRate, DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    private void buildCADTable() {
        CADexchangeRateTables.put(USD_CURRENCY, USD_TO_CAD);
        CADexchangeRateTables.put(CHF_CURRENCY, CHF_TO_CAD);
        CADexchangeRateTables.put(JPY_CURRENCY, JPY_TO_CAD);
        CADexchangeRateTables.put(CAD_CURRENCY, CAD_TO_CAD);
    }

}

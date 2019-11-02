package ca.ulaval.glo4002.trading.domain;

import java.math.BigDecimal;
import java.util.Currency;

public interface ExchangeRateConverter {

    BigDecimal toCAD(Currency currency, BigDecimal amount);

    BigDecimal fromCADtoCurrency(BigDecimal CADAmount, Currency currency);
}

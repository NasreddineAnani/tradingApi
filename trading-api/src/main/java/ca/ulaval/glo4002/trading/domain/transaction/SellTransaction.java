package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.NotEnoughStockException;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.exceptions.StockParametersDoNotMatch;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Entity
public class SellTransaction extends Transaction {

    private String buyTransactionNumber;

    public SellTransaction() {
        //for hibernate
    }

    public SellTransaction(ZonedDateTime date, Stock stock, Long quantity,
                           String buyTransactionNumber) {
        super(date, stock, quantity);
        this.buyTransactionNumber = buyTransactionNumber;
    }

    public void verifyTransactionValidity(Stock buyTransactionStock, Long buyTransactionQuantity) {
        if (!this.stock.hasSameSymbolAndMarket(buyTransactionStock)) {
            throw new StockParametersDoNotMatch(this.transactionNumber);
        }
        if (buyTransactionQuantity < this.quantity) {
            throw new NotEnoughStockException(this.stock.getMarket(), this.stock.getSymbol(), this.transactionNumber);
        }
    }

    public BigDecimal calculateTotalTransactionGain(ExchangeRateConverter exchangeRateConverter) {
        this.price = exchangeRateConverter.toCAD(getCurrency(), this.price);
        this.calculateFees();
        this.price = exchangeRateConverter.fromCADtoCurrency(price, getCurrency());
        this.fees = exchangeRateConverter.fromCADtoCurrency(fees, getCurrency());
        return price.multiply(new BigDecimal(this.quantity)).subtract(this.fees);
    }

    public String getBuyTransactionNumber() {
        return this.buyTransactionNumber;
    }
}
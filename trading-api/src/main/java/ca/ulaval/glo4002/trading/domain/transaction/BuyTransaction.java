package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidQuantityException;

import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Entity
public class BuyTransaction extends Transaction {

    public BuyTransaction() {
        //for hibernate
    }

    public BuyTransaction(ZonedDateTime date, Stock stock, Long quantity) {
        super(date, stock, quantity);
    }

    public BigDecimal calculateTransactionPriceTotal(ExchangeRateConverter exchangeRateConverter) {
        this.price = exchangeRateConverter.toCAD(getCurrency(), this.price);
        this.calculateFees();
        this.price = exchangeRateConverter.fromCADtoCurrency(price, getCurrency());
        this.fees = exchangeRateConverter.fromCADtoCurrency(fees, getCurrency());
        return this.price.multiply(new BigDecimal(this.quantity)).add(this.fees);
    }

    public void deductTransactionStockQuantity(Long quantity) {
        if (quantity > this.quantity) {
            throw new InvalidQuantityException();
        } else {
            this.quantity = this.quantity - quantity;
        }
    }
}

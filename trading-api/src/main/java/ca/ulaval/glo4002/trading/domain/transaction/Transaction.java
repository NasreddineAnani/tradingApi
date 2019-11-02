package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidQuantityException;

import ca.ulaval.glo4002.trading.domain.stock.Stock;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {

    @Transient
    private final static BigDecimal PERCENTAGE_FEES_FOR_TRANSACTION_PRICE_MORE_THAN_5000
            = new BigDecimal(0.03F);

    @Transient
    private final static BigDecimal FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100
            = new BigDecimal(0.25F);

    @Transient
    private final static BigDecimal FEES_FOR_TRANSACTION_QUANTITY_MORE_THAN_100
            = new BigDecimal(0.20F);

    @Transient
    private final static int PRICE_THRESHOLD_FOR_ADDITIONAL_FEES = 5000;

    @Id
    protected String transactionNumber;
    protected Stock stock;
    protected Long quantity;
    protected BigDecimal price;
    protected BigDecimal fees;
    private ZonedDateTime date;
    private Currency currency;

    @Transient
    private BigDecimal feesPerAction;

    @Transient
    private BigDecimal feesForTransactionPriceOverAnAmount;

    public Transaction() {
        //for hibernate
    }

    public Transaction(ZonedDateTime date, Stock stock, Long quantity) {
        this.transactionNumber = UUID.randomUUID().toString();
        this.date = date;
        this.stock = stock;
        this.quantity = quantity;
        this.verifyQuantityValidity();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ZonedDateTime getDate() {
        return date.withZoneSameInstant(ZoneOffset.UTC);
    }

    public Stock getStock() {
        return stock;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getFees() {
        return fees;
    }

    private BigDecimal calculateFeesPerAction() {
        if (this.quantity <= 100) {
            feesPerAction = FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100;
        } else {
            feesPerAction = FEES_FOR_TRANSACTION_QUANTITY_MORE_THAN_100;
        }
        return feesPerAction;
    }

    private BigDecimal calculateFeesForTransactionPriceOverThreshold() {
        BigDecimal priceOfQuantity = getPrice().multiply(new BigDecimal(this.quantity));
        if (priceOfQuantity.compareTo(new BigDecimal(PRICE_THRESHOLD_FOR_ADDITIONAL_FEES)) > 0) {
            feesForTransactionPriceOverAnAmount = priceOfQuantity
                    .multiply(PERCENTAGE_FEES_FOR_TRANSACTION_PRICE_MORE_THAN_5000);
        } else {
            feesForTransactionPriceOverAnAmount = new BigDecimal(0F);
        }
        return feesForTransactionPriceOverAnAmount;
    }

    public void calculateFees() {
        this.fees = (calculateFeesPerAction().multiply(new BigDecimal(this.quantity))
                .add(calculateFeesForTransactionPriceOverThreshold()));
    }

    public void verifyQuantityValidity() {
        if (this.quantity <= 0) {
            throw new InvalidQuantityException();
        }
    }
}

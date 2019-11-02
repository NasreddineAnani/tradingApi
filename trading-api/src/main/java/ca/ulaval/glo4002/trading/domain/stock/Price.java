package ca.ulaval.glo4002.trading.domain.stock;

import javax.persistence.Embeddable;
import java.math.BigDecimal;


@Embeddable
public class Price {

    private String date;
    private BigDecimal price;

    public Price() {
        //for hibernate
    }

    public Price(String date, BigDecimal price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
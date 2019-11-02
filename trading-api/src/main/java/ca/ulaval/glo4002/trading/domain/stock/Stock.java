package ca.ulaval.glo4002.trading.domain.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.List;

@Embeddable
@JsonIgnoreProperties
public class Stock {

    @Transient
    private Integer id;
    private String symbol;
    private String market;

    @Transient
    private List<Price> prices;

    @Transient
    private String type;

    public Stock() {
        //for hibernate
    }

    public Stock(String symbol, String market) {
        this.symbol = symbol;
        this.market = market;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public List<Price> getPrices() {
        return this.prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean hasSameSymbolAndMarket(Stock otherStock) {
        return this.symbol.equals(otherStock.getSymbol())
                && this.market.equals(otherStock.getMarket());
    }
}

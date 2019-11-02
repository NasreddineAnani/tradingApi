package ca.ulaval.glo4002.stocks.domain.stocks;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.rest.core.annotation.RestResource;

import ca.ulaval.glo4002.stocks.domain.companies.Company;

@Entity
public class Stock {

  @Id
  @GeneratedValue
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  @RestResource(exported = false)
  private Company company;

  @Column
  private String symbol;

  @Column(name = "stock_type")
  private String type;

  @Column
  private String market;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "stock")
  private List<Price> prices;

  public Integer getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getType() {
    return type;
  }

  public String getMarket() {
    return market;
  }

  public List<Price> getPrices() {
    return prices;
  }

  public void addPrice(Price price) {
    prices.add(price);
  }
}

package ca.ulaval.glo4002.stocks.domain.companies;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.rest.core.annotation.RestResource;

import ca.ulaval.glo4002.stocks.domain.stocks.Stock;

@Entity
public class Company {

  @Id
  @GeneratedValue
  private Integer id;

  @Column
  private String fullName;

  @Column
  private String industry;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
  @RestResource(exported = false)
  private List<Stock> stocks;

  public Integer getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public String getIndustry() {
    return industry;
  }

  public List<Stock> getStocks() {
    return stocks;
  }
}

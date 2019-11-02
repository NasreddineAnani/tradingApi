package ca.ulaval.glo4002.stocks.domain.stocks;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
public class Price {

  @Id
  @GeneratedValue
  private Integer id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "stock_id", nullable = false)
  @RestResource(exported = false)
  private Stock stock;

  @Column
  private Instant date;

  @Column(name = "amount")
  private BigDecimal price;

  public Instant getDate() {
    return date;
  }

  public BigDecimal getPrice() {
    return price;
  }
}

package ca.ulaval.glo4002.stocks.domain.markets;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Market {
  @Id
  @GeneratedValue
  private Integer id;

  @Column
  private String symbol;

  @Column(name = "open_hours")
  private String openHours;

  @Column
  private String timezone;

  public String getSymbol() {
    return symbol;
  }

  public String[] getOpenHours() {
    return openHours.split(", ");
  }

  public String getTimezone() {
    return timezone;
  }
}

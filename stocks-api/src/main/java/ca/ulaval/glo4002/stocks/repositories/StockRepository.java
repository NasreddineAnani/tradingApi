package ca.ulaval.glo4002.stocks.repositories;

import java.util.List;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ca.ulaval.glo4002.stocks.domain.stocks.Stock;
import org.springframework.data.rest.core.annotation.RestResource;

public interface StockRepository extends Repository<Stock, Integer> {
  @RestResource(exported = false)
  List<Stock> findAll();

  @RestResource(exported = false)
  Stock findOneByMarketAndSymbol(@Param("market") String market, @Param("symbol") String symbol);

  @RestResource(exported = false)
  Stock save(Stock stock);
}

package ca.ulaval.glo4002.stocks.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ulaval.glo4002.stocks.domain.stocks.Stock;
import ca.ulaval.glo4002.stocks.repositories.StockRepository;

@RepositoryRestController
@RequestMapping("/stocks")
public class StockController {

  private final StockRepository repository;

  @Autowired
  public StockController(StockRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/{market}/{symbol}")
  public @ResponseBody Stock getStockByMarketAndSymbol(@PathVariable("market") String market,
                                                       @PathVariable("symbol") String symbol)
  {
    return repository.findOneByMarketAndSymbol(market, symbol);
  }
}

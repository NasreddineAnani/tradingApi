package ca.ulaval.glo4002.stocks.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ulaval.glo4002.stocks.domain.markets.Market;
import ca.ulaval.glo4002.stocks.repositories.MarketRepository;

@RepositoryRestController
@RequestMapping("/markets")
public class MarketController {

  private final MarketRepository repository;

  @Autowired
  public MarketController(MarketRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/{symbol}")
  public @ResponseBody Market getMarketBySymbol(@PathVariable("symbol") String symbol) {
    return repository.findOneBySymbol(symbol);
  }

  @GetMapping("")
  public @ResponseBody List<Market> getMarkets() {
    return repository.findAll();
  }
}

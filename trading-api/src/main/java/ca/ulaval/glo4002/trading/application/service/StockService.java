package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.StockDateNotFoundException;
import ca.ulaval.glo4002.trading.application.service.exceptions.StockNotFoundException;
import ca.ulaval.glo4002.trading.domain.stock.Price;
import ca.ulaval.glo4002.trading.domain.stock.Stock;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


public class StockService {

    private StockApiCaller stockApiCaller;

    @Inject
    public StockService(StockApiCaller stockApiCaller) {
        this.stockApiCaller = stockApiCaller;
    }

    private Stock getStock(Stock stock) {
        Stock stockResponse = stockApiCaller.getStock(stock);
        if (stockResponse == null) {
            throw new StockNotFoundException(stock.getMarket(), stock.getSymbol());
        }
        return stockResponse;
    }

    public BigDecimal getStockPrice(Stock stock, ZonedDateTime date) {
        for (Price price : getStock(stock).getPrices()) {
            ZonedDateTime priceLocalDate = ZonedDateTime.parse(price.getDate());
            date = date.withZoneSameInstant(ZoneOffset.UTC);
            if (date.truncatedTo(ChronoUnit.DAYS).equals(priceLocalDate.truncatedTo(ChronoUnit.DAYS))) {
                return price.getPrice();
            }
        }
        throw new StockDateNotFoundException();
    }
}
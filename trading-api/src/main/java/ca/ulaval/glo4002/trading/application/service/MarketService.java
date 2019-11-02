package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.MarketNotFoundException;
import ca.ulaval.glo4002.trading.domain.market.Market;

import javax.inject.Inject;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;


public class MarketService {

    private MarketApiCaller marketApiCaller;
    private Map<String, Currency> marketCurrencyMap;

    @Inject
    public MarketService(MarketApiCaller marketApiCaller) {
        this.marketApiCaller = marketApiCaller;
        setUpMarketCurrencyMap();
    }

    private void setUpMarketCurrencyMap() {
        marketCurrencyMap = new HashMap<>();
        marketCurrencyMap.put("NASDAQ", Currency.getInstance("USD"));
        marketCurrencyMap.put("XTKS", Currency.getInstance("JPY"));
        marketCurrencyMap.put("XSWX", Currency.getInstance("CHF"));
        marketCurrencyMap.put("NYSE", Currency.getInstance("USD"));
    }

    public Market getMarketBySymbol(String market) {
        Market marketResponse = marketApiCaller.getMarket(market);
        if (marketResponse == null) {
            throw new MarketNotFoundException(market);
        }
        return marketResponse;
    }

    public Currency getMarketCurrency(String market) {
        return marketCurrencyMap.get(market);
    }
}
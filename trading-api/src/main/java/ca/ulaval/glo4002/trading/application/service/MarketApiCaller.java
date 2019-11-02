package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.market.Market;

public interface MarketApiCaller {

    Market getMarket(String market);
}

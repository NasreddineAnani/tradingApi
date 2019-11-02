package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.market.Market;


public class FakeMarketApiCaller implements MarketApiCaller {

    private final static String[] VALID_OPEN_HOURS = {"09:00-13:30", "14:00-20:00"};
    private final static String A_TIMEZONE = "UTC";
    private final static String VALID_MARKET_SYMBOL = "NASDAQ";

    private final static Market aValidMarket = new Market(VALID_MARKET_SYMBOL, VALID_OPEN_HOURS, A_TIMEZONE);

    @Override
    public Market getMarket(String market) {
        return aValidMarket;
    }
}

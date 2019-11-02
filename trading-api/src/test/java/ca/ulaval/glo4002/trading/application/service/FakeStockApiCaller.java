package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.stock.Price;
import ca.ulaval.glo4002.trading.domain.stock.Stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FakeStockApiCaller implements StockApiCaller {

    private final static String VALID_DATE_STRING = "2018-08-21T15:23:20.142Z";
    private final static String VALID_MARKET_SYMBOL = "NASDAQ";
    private final static String VALID_STOCK_SYMBOL = "GOOG";
    private final static int CREDIT_AMOUNT = 1000;
    private final static int PRICE_AMOUNT = CREDIT_AMOUNT / 100;
    private final static BigDecimal STOCK_PRICE = BigDecimal.valueOf(PRICE_AMOUNT);


    private Price aPrice = new Price(VALID_DATE_STRING, STOCK_PRICE);
    private List<Price> priceList = new ArrayList<>();
    private Stock aValidStock = new Stock(VALID_STOCK_SYMBOL, VALID_MARKET_SYMBOL);


    @Override
    public Stock getStock(Stock stock) {
        priceList.add(aPrice);
        aValidStock.setPrices(priceList);
        return aValidStock;
    }
}

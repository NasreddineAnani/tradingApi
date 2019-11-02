package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.stock.Stock;

public interface StockApiCaller {

    Stock getStock(Stock stock);
}


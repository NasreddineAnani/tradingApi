package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.StockDTO;
import ca.ulaval.glo4002.trading.domain.stock.Stock;


public class StockAssembler {

    public StockDTO fromStockToStockDTO(Stock stock) {
        StockDTO stockDTO = new StockDTO();

        stockDTO.market = stock.getMarket();
        stockDTO.symbol = stock.getSymbol();

        return stockDTO;
    }
}

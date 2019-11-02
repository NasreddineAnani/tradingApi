package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.StockDTO;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class StockAssemblerTest {

    private static String A_MARKET = "NASDAQ";
    private static String A_SYMBOL = "GOOG";

    private StockAssembler stockAssembler;

    @Before
    public void setUp() {
        stockAssembler = new StockAssembler();
    }

    @Test
    public void givenStock_whenConvertItToStockDTO_thenStockDTOIsCreatedWithTheSameAttributes() {
        Stock stock = new Stock(A_MARKET, A_SYMBOL);

        StockDTO stockDTO = this.stockAssembler.fromStockToStockDTO(stock);

        assertEquals(stock.getMarket(), stockDTO.market);
        assertEquals(stock.getSymbol(), stockDTO.symbol);
    }
}
package ca.ulaval.glo4002.trading.domain.stock;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StockTest {

    private final static String A_MARKET = "NASDAQ";
    private final static String A_SYMBOL = "GOOG";
    private final static String DIFFERENT_MARKET = "DAQ";
    private final static String DIFFERENT_SYMBOL = "APPL";

    @Test
    public void givenTwoEqualStocks_whenComparingThem_thenHaveSameSymbolAndMarket() {
        Stock firstStock = new Stock(A_SYMBOL, A_MARKET);
        Stock secondStock = new Stock(A_SYMBOL, A_MARKET);

        boolean response = firstStock.hasSameSymbolAndMarket(secondStock);

        assertTrue(response);
    }

    @Test
    public void givenTwoDifferentStocks_whenComparingThem_thenHaveDifferentSymbolAndMarket() {
        Stock firstStock = new Stock(A_SYMBOL, A_MARKET);
        Stock secondStock = new Stock(DIFFERENT_SYMBOL, DIFFERENT_MARKET);

        boolean response = firstStock.hasSameSymbolAndMarket(secondStock);

        assertFalse(response);
    }
}
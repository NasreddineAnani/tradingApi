package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.StockDateNotFoundException;
import ca.ulaval.glo4002.trading.application.service.exceptions.StockNotFoundException;
import ca.ulaval.glo4002.trading.domain.stock.Price;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class StockServiceTest {

    private final static String A_STRING_DATE = "2018-01-01T00:00:00Z";
    private final static String A_DIFFERENT_STRING_DATE = "2018-01-01T00:00:00Z";
    private final static ZonedDateTime A_DATE = ZonedDateTime.parse(A_STRING_DATE);
    private final static BigDecimal A_PRICE_VALUE = new BigDecimal(10);
    private final static String A_MARKET = "aMarket";
    private final static String A_SYMBOL = "aSymbol";

    private Price aPrice;
    private Stock aStock;
    private StockApiCaller stockApiCallerMock;
    private StockService stockService;
    private List<Price> aPriceList;

    @Before
    public void setUp() {
        aPrice = new Price(A_STRING_DATE, A_PRICE_VALUE);
        stockApiCallerMock = mock(StockApiCaller.class);
        stockService = new StockService(stockApiCallerMock);
        aPriceList = new ArrayList<>();
        aStock = new Stock(A_SYMBOL, A_MARKET);
    }

    @Test
    public void givenValidMarketAndSymbol_whenRetrievingStockInformation_thenPriceIsFound() {
        aStock.setPrices(aPriceList);
        aPriceList.add(aPrice);
        when(stockApiCallerMock.getStock(aStock)).thenReturn(aStock);

        BigDecimal returnPrice = stockService.getStockPrice(aStock, A_DATE);

        assertEquals(returnPrice, A_PRICE_VALUE);
    }

    @Test(expected = StockNotFoundException.class)
    public void givenInvalidMarketAndSymbol_whenRetrievingStockInformation_thenStockIsNotFound() {
        aStock.setPrices(aPriceList);
        when(stockApiCallerMock.getStock(aStock)).thenReturn(null);

        stockService.getStockPrice(aStock, A_DATE);
    }

    @Test(expected = StockDateNotFoundException.class)
    public void givenValidMarketAndSymbolButInvalidDate_whenRetrievingStockInformation_thenTheDatePriceIsNotFound() {
        aStock.setPrices(aPriceList);
        ZonedDateTime aDateNotInStockPrices = ZonedDateTime.parse(A_DIFFERENT_STRING_DATE);
        when(stockApiCallerMock.getStock(aStock)).thenReturn(aStock);

        stockService.getStockPrice(aStock, aDateNotInStockPrices);
    }
}
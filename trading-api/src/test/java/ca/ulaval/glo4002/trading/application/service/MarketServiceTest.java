package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.service.exceptions.MarketNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MarketServiceTest {

    private final static String A_MARKET_NAME = "anyMarket";

    private MarketApiCaller marketApiCallerMock;
    private MarketService marketService;

    @Before
    public void setUp() {
        marketApiCallerMock = mock(MarketApiCaller.class);
        marketService = new MarketService(marketApiCallerMock);
    }

    @Test(expected = MarketNotFoundException.class)
    public void givenNoneExistingMarket_whenRetrievingMarketInformation_thenMarketIsNotFound() {
        when(marketApiCallerMock.getMarket(A_MARKET_NAME)).thenReturn(null);

        marketService.getMarketBySymbol(A_MARKET_NAME);
    }

    @Test
    public void givenAnInvalidMarketSymbol_whenGettingTheCurrencyForThatMarket_thenShouldNotReturnACurrency() {
        Currency returnedCurrency = marketService.getMarketCurrency("invalid market symbole");

        assertNull(returnedCurrency);
    }

    @Test
    public void givenAValidMarketSymbol_whenGettingTheCurrencyForThatMarket_thenShouldReturnTheAssociatedCurrency() {
        Currency expectedCurrencyForNASDAQ = Currency.getInstance("USD");

        Currency returnedCurrency = marketService.getMarketCurrency("NASDAQ");

        assertEquals(expectedCurrencyForNASDAQ, returnedCurrency);
    }
}

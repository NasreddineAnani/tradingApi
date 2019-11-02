package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.market.Market;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class LocalMarketApiCaller implements MarketApiCaller {

    private final static String API_BASE_URL = "http://localhost:8080";
    private final static String MARKET_URI = "/markets/%s";
    private Client client;

    public LocalMarketApiCaller() {
        client = ClientBuilder.newClient();
    }

    @Override
    public Market getMarket(String market) {
        Response response = client.target(API_BASE_URL
                + String.format(MARKET_URI, market))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Accept", "application/json")
                .get();
        return response.readEntity(Market.class);
    }
}
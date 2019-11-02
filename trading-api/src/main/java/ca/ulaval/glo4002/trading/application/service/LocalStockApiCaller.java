package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.domain.stock.Stock;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class LocalStockApiCaller implements StockApiCaller {

    private final static String STOCK_API_BASE_URL = "http://localhost:8080";
    private final static String GET_STOCK_URI = "/stocks/%s/%s";
    private Client client;

    public LocalStockApiCaller() {
        client = ClientBuilder.newClient();
    }

    @Override
    public Stock getStock(Stock stock) {
        Response response = client.target(STOCK_API_BASE_URL
                + String.format(GET_STOCK_URI, stock.getMarket(), stock.getSymbol()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Accept", "application/json")
                .get();
        return response.readEntity(Stock.class);
    }
}

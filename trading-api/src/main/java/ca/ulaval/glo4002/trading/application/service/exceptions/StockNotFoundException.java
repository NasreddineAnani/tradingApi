package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class StockNotFoundException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "STOCK_NOT_FOUND";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "stock '%s:%s' not found";

    public StockNotFoundException(String market, String symbol) {
        super();
        this.setError(ERROR_MESSAGE);
        this.setErrorCode(ERROR_CODE);
        this.setDescription(String.format(DESCRIPTION, market, symbol));
    }
}

package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class MarketNotFoundException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "MARKET_NOT_FOUND";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private String DESCRIPTION = "market '%s' not found ";

    public MarketNotFoundException(String symbol) {
        super();
        this.setError(ERROR_MESSAGE);
        this.setErrorCode(ERROR_CODE);
        this.setDescription(String.format(DESCRIPTION, symbol));
    }
}

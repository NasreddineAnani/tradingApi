package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class InvalidQuantityException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "INVALID_QUANTITY";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "quantity is invalid";

    public InvalidQuantityException() {
        super();
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

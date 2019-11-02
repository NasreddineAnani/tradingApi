package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;

public class InvalidDateException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "INVALID_DATE";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "the transaction date is invalid";

    public InvalidDateException() {
        super();
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

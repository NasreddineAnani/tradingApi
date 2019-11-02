package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class NotEnoughCreditsException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "NOT_ENOUGH_CREDITS";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "not enough credits in wallet";

    public NotEnoughCreditsException() {
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

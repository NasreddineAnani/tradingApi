package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class AccountInvalidCreditsException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "INVALID_AMOUNT";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "credit amount cannot be lower than or equal to zero";

    public AccountInvalidCreditsException() {
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

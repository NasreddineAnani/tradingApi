package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class AccountInvalidDetailsException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "INVALID_ACCOUNT_DETAILS";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "Some details are missing to properly create an account.";

    public AccountInvalidDetailsException() {
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

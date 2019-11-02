package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class AccountAlreadyOpenException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "ACCOUNT_ALREADY_OPEN";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "account already open for investor %s";

    public AccountAlreadyOpenException(long investorId) {
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, investorId));
        this.setErrorCode(ERROR_CODE);
    }
}

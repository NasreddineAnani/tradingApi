package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class AccountNotFoundException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "ACCOUNT_NOT_FOUND";
    private final static int ERROR_CODE = Response.Status.NOT_FOUND.getStatusCode();
    private String DESCRIPTION = "account with number %s not found";

    public AccountNotFoundException(String accountNumber) {
        super();
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, accountNumber));
        this.setErrorCode(ERROR_CODE);
    }
}
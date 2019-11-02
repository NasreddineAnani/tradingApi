package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class UnsupportedTransactionTypeException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "UNSUPPORTED_TRANSACTION_TYPE";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private String DESCRIPTION = "transaction %s is not supported";

    public UnsupportedTransactionTypeException(String type) {
        super();
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, type));
        this.setErrorCode(ERROR_CODE);
    }
}

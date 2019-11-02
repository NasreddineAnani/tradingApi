package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class InvalidTransactionNumberException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "INVALID_TRANSACTION_NUMBER";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private String DESCRIPTION = "the transaction number is invalid";

    public InvalidTransactionNumberException(String transactionNumber) {
        super(transactionNumber);
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, transactionNumber));
        this.setErrorCode(ERROR_CODE);
    }
}

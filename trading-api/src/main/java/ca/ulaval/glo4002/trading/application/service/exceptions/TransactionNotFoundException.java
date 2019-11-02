package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class TransactionNotFoundException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "TRANSACTION_NOT_FOUND";
    private final static int ERROR_CODE = Response.Status.NOT_FOUND.getStatusCode();
    private String DESCRIPTION = "transaction with number %s not found";

    public TransactionNotFoundException(String transactionNumber) {
        super(transactionNumber);
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, transactionNumber));
        this.setErrorCode(ERROR_CODE);
    }
}

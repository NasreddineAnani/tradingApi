package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;


public class NotEnoughStockException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "NOT_ENOUGH_STOCK";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "not enough stock \'%s:%s\' ";

    public NotEnoughStockException(String market, String symbol, String transactionNumber) {
        super(transactionNumber);
        this.setError(ERROR_MESSAGE);
        this.setDescription(String.format(DESCRIPTION, symbol, market));
        this.setErrorCode(ERROR_CODE);
    }
}
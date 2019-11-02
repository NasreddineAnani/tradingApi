package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class MarketClosedException extends TradingAPIException {

    private final static String ERROR_MESSAGE = "MARKET_CLOSED";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "market '%s' is closed";

    public MarketClosedException(String market, String transactionNumber) {
        super(transactionNumber);
        this.setError(ERROR_MESSAGE);
        this.setErrorCode(ERROR_CODE);
        this.setDescription(String.format(DESCRIPTION, market));
    }
}

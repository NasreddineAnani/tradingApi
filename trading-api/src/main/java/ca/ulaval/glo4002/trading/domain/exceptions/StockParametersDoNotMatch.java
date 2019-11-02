package ca.ulaval.glo4002.trading.domain.exceptions;

import javax.ws.rs.core.Response;


public class StockParametersDoNotMatch extends TradingAPIException {

    private final static String ERROR_MESSAGE = "STOCK_PARAMETERS_DONT_MATCH";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private final static String DESCRIPTION = "stock parameters don't match existing ones";

    public StockParametersDoNotMatch(String transactionNumber) {
        super(transactionNumber);
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }
}

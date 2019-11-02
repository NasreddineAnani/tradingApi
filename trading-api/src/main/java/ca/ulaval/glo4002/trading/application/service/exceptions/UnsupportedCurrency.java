package ca.ulaval.glo4002.trading.application.service.exceptions;

import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.ws.rs.core.Response;

public class UnsupportedCurrency extends TradingAPIException {

    private final static String ERROR_MESSAGE = "UNSUPPORTED_CURRENCY";
    private final static int ERROR_CODE = Response.Status.BAD_REQUEST.getStatusCode();
    private String DESCRIPTION = "Unsupported currency symbol";

    public UnsupportedCurrency() {
        super();
        this.setError(ERROR_MESSAGE);
        this.setDescription(DESCRIPTION);
        this.setErrorCode(ERROR_CODE);
    }

}

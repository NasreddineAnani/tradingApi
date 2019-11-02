package ca.ulaval.glo4002.trading.domain.exceptions;

import org.json.JSONObject;


public class TradingAPIException extends RuntimeException {

    private String error;
    private String description;
    private int errorCode;
    private String transactionNumber;

    public TradingAPIException() {
        super();
        transactionNumber = null;
    }

    public TradingAPIException(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String generateErrorMessage() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("error", error);
        responseJson.put("description", description);
        if (transactionNumber != null) {
            responseJson.put("transactionNumber", transactionNumber);
        }
        return responseJson.toString();
    }
}

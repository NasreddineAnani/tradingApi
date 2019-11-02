package ca.ulaval.glo4002.trading.application.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


public class BuyTransactionResponseDTO extends TransactionResponseDTO {

    public final String type = "BUY";
    @JsonProperty("purchasedPrice")
    public BigDecimal price;
}

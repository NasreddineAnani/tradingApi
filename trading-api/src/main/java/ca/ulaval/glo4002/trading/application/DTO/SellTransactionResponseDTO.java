package ca.ulaval.glo4002.trading.application.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


public class SellTransactionResponseDTO extends TransactionResponseDTO {

    public final String type = "SELL";
    @JsonProperty("priceSold")
    public BigDecimal price;
}
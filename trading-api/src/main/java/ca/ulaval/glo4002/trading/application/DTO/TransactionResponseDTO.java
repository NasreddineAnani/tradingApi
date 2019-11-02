package ca.ulaval.glo4002.trading.application.DTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


public class TransactionResponseDTO {

    public String transactionNumber;
    public ZonedDateTime date;
    public BigDecimal fees;
    public StockDTO stock;
    public Long quantity;
}
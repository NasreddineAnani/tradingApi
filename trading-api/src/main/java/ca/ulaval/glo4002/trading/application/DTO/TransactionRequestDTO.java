package ca.ulaval.glo4002.trading.application.DTO;

import ca.ulaval.glo4002.trading.domain.stock.Stock;


public class TransactionRequestDTO {

    public String type;
    public String date;
    public Stock stock;
    public Long quantity;
    public String transactionNumber;
}


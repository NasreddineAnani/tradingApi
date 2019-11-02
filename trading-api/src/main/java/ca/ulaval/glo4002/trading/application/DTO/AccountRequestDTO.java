package ca.ulaval.glo4002.trading.application.DTO;


import java.util.List;

public class AccountRequestDTO {

    public Long investorId;
    public String investorName;
    public String email;
    public List<CreditDTO> credits;

}

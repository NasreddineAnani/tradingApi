package ca.ulaval.glo4002.trading.application.DTO;

import ca.ulaval.glo4002.trading.domain.profile.Profile;

import java.math.BigDecimal;
import java.util.List;


public class AccountResponseDTO {

    public String accountNumber;
    public Long investorId;
    public Profile investorProfile;
    public List<CreditDTO> credits;
    public BigDecimal total;

}

package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.AccountResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class AccountAssembler {

    public AccountAssembler() {
    }

    public Account fromAccountRequestDTOToAccount(AccountRequestDTO accountRequestDTO) {
        Long investorId = accountRequestDTO.investorId;
        String investorName = accountRequestDTO.investorName;

        if (Objects.isNull(investorId) || StringUtils.isEmpty(investorName)) {
            throw new AccountInvalidDetailsException();
        }

        if (accountRequestDTO.credits == null) {
            throw new AccountInvalidCreditsException();
        }

        return new Account(investorId, investorName, accountRequestDTO.credits);
    }

    public AccountResponseDTO fromAccountToAccountResponseDTO(Account account) {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();

        accountResponseDTO.accountNumber = account.getAccountNumber();
        accountResponseDTO.investorProfile = account.getProfile();
        accountResponseDTO.investorId = account.getInvestorId();
        accountResponseDTO.credits = fromMapToList(account.getWallet().getCredits());
        accountResponseDTO.total = account.getWallet().getTotalCreditsInCAD()
                .setScale(2, RoundingMode.HALF_UP);

        return accountResponseDTO;
    }

    private List<CreditDTO> fromMapToList(Map<Currency, BigDecimal> map) {
        List<CreditDTO> list = new ArrayList<>();
        for (Map.Entry<Currency, BigDecimal> pair : map.entrySet()) {
            CreditDTO creditDTO = new CreditDTO();
            creditDTO.currency = pair.getKey().toString();
            creditDTO.amount = pair.getValue().setScale(2, RoundingMode.HALF_UP);
            list.add(creditDTO);
        }
        return list;
    }
}

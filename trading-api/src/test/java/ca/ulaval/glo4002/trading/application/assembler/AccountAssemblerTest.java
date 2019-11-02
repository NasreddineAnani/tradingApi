package ca.ulaval.glo4002.trading.application.assembler;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.AccountResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AccountAssemblerTest {

    private final static Long INVESTOR_ID = 123L;
    private final static String A_VALID_INVESTOR_NAME = "Nom Prénom";
    private final static String A_VALID_EMAIL = "test@test.com";
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static float A_CREDIT_AMOUNT = 100F;
    private AccountAssembler accountAssembler;

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @Before
    public void setUp() {
        setUpCreditsDTO();
        accountAssembler = new AccountAssembler();
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }

    @Test(expected = AccountInvalidCreditsException.class)
    public void givenAccountInformationWithNullCredits_whenCreatingAccount_thenAccountHasInvalidCredits() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.investorId = INVESTOR_ID;
        accountRequestDTO.investorName = A_VALID_INVESTOR_NAME;
        accountRequestDTO.email = A_VALID_EMAIL;
        accountRequestDTO.credits = null;

        this.accountAssembler.fromAccountRequestDTOToAccount(accountRequestDTO);
    }


    @Test(expected = AccountInvalidDetailsException.class)
    public void givenAnAccountRequestDTOWithNullInvestorId_whenCreatingAccount_thenAccountHasInvalidDetails() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.investorId = null;
        accountRequestDTO.investorName = A_VALID_INVESTOR_NAME;
        accountRequestDTO.email = A_VALID_EMAIL;
        accountRequestDTO.credits = credits;

        this.accountAssembler.fromAccountRequestDTOToAccount(accountRequestDTO);
    }

    @Test(expected = AccountInvalidDetailsException.class)
    public void givenAnAccountRequestDTOWithNullInvestorName_whenCreatingAccount_thenAccountHasInvalidDetails() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.investorId = INVESTOR_ID;
        accountRequestDTO.investorName = null;
        accountRequestDTO.email = A_VALID_EMAIL;
        accountRequestDTO.credits = credits;

        this.accountAssembler.fromAccountRequestDTOToAccount(accountRequestDTO);
    }

    @Test
    public void givenAnAccountRequestDTO_whenConvertItToAccount_thenAccountIsCreatedWithTheSameAttributes() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.investorId = INVESTOR_ID;
        accountRequestDTO.investorName = A_VALID_INVESTOR_NAME;
        accountRequestDTO.email = A_VALID_EMAIL;
        accountRequestDTO.credits = credits;

        Account account = this.accountAssembler.fromAccountRequestDTOToAccount(accountRequestDTO);

        assertEquals(accountRequestDTO.investorId, account.getInvestorId());
        assertEquals(accountRequestDTO.investorName, account.getInvestorName());
        assertTrue(accountRequestDTO.credits.get(0).amount
                .compareTo(account.getWallet().getCredits().get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL))) == 0);
    }

    @Test
    public void givenAnAccount_whenConvertItToAccountResponseDTO_thenAccountResponseDTOIsCreatedWithTheSameAttributes() {
        Account account = new Account(123L, "Nom Prénom", credits);
        account.getWallet().setTotalCreditsInCAD(BigDecimal.valueOf(A_CREDIT_AMOUNT));

        AccountResponseDTO accountResponseDTO = this.accountAssembler.fromAccountToAccountResponseDTO(account);

        assertEquals(account.getAccountNumber(), accountResponseDTO.accountNumber);
        assertEquals(account.getProfile(), accountResponseDTO.investorProfile);
        assertEquals(account.getInvestorId(), accountResponseDTO.investorId);
        assertTrue(account.getWallet().getTotalCreditsInCAD().compareTo(accountResponseDTO.total) == 0);
        assertTrue(account.getWallet().getCredits().get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL))
                .compareTo(accountResponseDTO.credits.get(0).amount) == 0);
    }

    @Test
    public void givenAnAccount_whenConvertItToAccountResponseDTO_thenTheMoneyIsRoundedUpToTwoDecimals() {
        Account account = new Account(123L, "Nom Prénom", credits);
        account.getWallet().setTotalCreditsInCAD(BigDecimal.valueOf(A_CREDIT_AMOUNT));

        AccountResponseDTO accountResponseDTO = this.accountAssembler.fromAccountToAccountResponseDTO(account);

        assertEquals(account.getWallet().getTotalCreditsInCAD()
                        .setScale(2, RoundingMode.HALF_UP),
                accountResponseDTO.total);
        assertEquals(account.getWallet().getCredits()
                        .get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL))
                        .setScale(2, RoundingMode.HALF_UP),
                accountResponseDTO.credits.get(0).amount);
    }

}


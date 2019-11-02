package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.suites.ApiITestSuite;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;


public class AccountsResourceRestITest extends RestTestBase {
    private static final int CREATED_CODE = 201;
    private static final int OK_CODE = 200;
    private static final int BAD_REQUEST = 400;

    private static final Long VALID_INVESTOR_ID = 1234L;
    private final static String A_VALID_INVESTOR_NAME = "Nom Prenom";
    private final static String A_VALID_EMAIL = "test@test.com";
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static float A_CREDIT_AMOUNT = 100F;

    private static Long investorIdCounter = 0L;

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;
    private Account anAccount;

    private AccountRepository accountRepositoryMock;

    @Before
    public void setUp() {
        accountRepositoryMock = ApiITestSuite.repositoryMock;
        setUpCreditsDTO();
        anAccount = new Account(VALID_INVESTOR_ID, A_VALID_INVESTOR_NAME, credits);
    }

    @Test
    public void givenAnAccountDTO_whenCreatingAnAccountWithAnAlreadyExistingInvestorId_thenIReceivedAnErrorMessage() {
        Long investorId = investorIdCounter++;
        AccountRequestDTO accountDto = createAccountRequestDto(investorId);
        willReturn(true).given(accountRepositoryMock).accountAlreadyExists(investorId);

        givenBaseRequest().body(accountDto)
                .when().post("accounts/").then().statusCode(BAD_REQUEST)
                .and().body("error", equalTo("ACCOUNT_ALREADY_OPEN"))
                .and().body("description", equalTo("account already open for investor " + investorId));
    }

    @Test
    public void givenAnAccountDTO_whenCreatingAValidAccount_thenTheAccountIsCreated() {
        Long investorId = investorIdCounter++;
        AccountRequestDTO accountDto = createAccountRequestDto(investorId);
        willReturn(false).given(accountRepositoryMock).accountAlreadyExists(investorId);

        givenBaseRequest().body(accountDto)
                .when().post("accounts/").then().statusCode(CREATED_CODE);
    }

    @Test
    public void givenAnExistingAccountNumber_whenGettingAccountDetails_thenTheDetailsAreReceived() {
        willReturn(anAccount).given(accountRepositoryMock).findAccountWithAccountNumber(anAccount.getAccountNumber());

        givenBaseRequest().when().get("accounts/" + anAccount.getAccountNumber())
                .then().statusCode(OK_CODE);
    }

    @Test
    public void givenAnUnExistentAccountNumber_whenGettingAccountDetails_theIReceiveAnErrorMessage() {
        String description = "account with number %s not found";
        willThrow(new AccountNotFoundException(anAccount.getAccountNumber()))
                .given(accountRepositoryMock).findAccountWithAccountNumber(anAccount.getAccountNumber());

        givenBaseRequest().when().get("accounts/" + anAccount.getAccountNumber())
                .then().statusCode(404)
                .and().body("error", equalTo("ACCOUNT_NOT_FOUND"))
                .and().body("description", equalTo(String.format(description, anAccount.getAccountNumber())));
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }

    private AccountRequestDTO createAccountRequestDto(long investorId) {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.investorId = investorId;
        accountRequestDTO.investorName = A_VALID_INVESTOR_NAME;
        accountRequestDTO.email = A_VALID_EMAIL;
        accountRequestDTO.credits = credits;

        return accountRequestDTO;
    }
}

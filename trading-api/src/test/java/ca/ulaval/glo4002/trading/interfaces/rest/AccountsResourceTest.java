package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.AccountResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.assembler.AccountAssembler;
import ca.ulaval.glo4002.trading.application.service.AccountService;
import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.profile.Profile;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class AccountsResourceTest {

    private final static Long A_VALID_INVESTOR_ID = 1234L;
    private final static String A_VALID_INVESTOR_NAME = "FirstName LastName";
    private final static String AN_ACCOUNT_NUMBER = "myAccountNumber";
    private final static int BAD_REQUEST_STATUS_CODE = 400;
    private final static int NOT_FOUND_STATUS_CODE = 404;
    private final static int OK_STATUS_CODE = 200;
    private final static int CREATED_STATUS_CODE = 201;
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static int A_CREDIT_AMOUNT = 100;

    private AccountsResource anAccountResource;
    private Account anAccountWithValidInformation;
    private AccountService anAccountServiceMock;
    private AccountRequestDTO anAccountRequestDTO;
    private AccountResponseDTO anAccountResponseDTO;
    private Profile anInvestorProfile = new Profile("CONSERVATIVE", new ArrayList<>());

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @Before
    public void setUp() {
        setUpCreditsDTO();

        anAccountServiceMock = mock(AccountService.class);
        AccountAssembler anAccountAssemblerMock = mock(AccountAssembler.class);

        anAccountWithValidInformation = new Account(A_VALID_INVESTOR_ID, A_VALID_INVESTOR_NAME, credits);

        anAccountResource = new AccountsResource(anAccountServiceMock, anAccountAssemblerMock);

        anAccountRequestDTO = new AccountRequestDTO();
        anAccountRequestDTO.investorId = A_VALID_INVESTOR_ID;
        anAccountRequestDTO.investorName = A_VALID_INVESTOR_NAME;

        anAccountResponseDTO = new AccountResponseDTO();
        anAccountResponseDTO.investorId = A_VALID_INVESTOR_ID;
        anAccountResponseDTO.accountNumber = AN_ACCOUNT_NUMBER;
        anAccountResponseDTO.investorProfile = anInvestorProfile;
    }

    @Test
    public void givenAnAccountRequestDTOContainingInvalidInformation_whenPostingAccount_thenReturnBadRequest() {
        doThrow(new AccountInvalidDetailsException()).when(anAccountServiceMock).createAccount(anAccountRequestDTO);

        Response response = anAccountResource.postAccount(anAccountRequestDTO);

        assertEquals(BAD_REQUEST_STATUS_CODE, response.getStatus());
    }

    @Test
    public void givenAnAccountRequestDTOContainingValidInformation_whenPostingAccount_thenReturnCreated() {
        when(anAccountServiceMock.createAccount(anAccountRequestDTO)).thenReturn(anAccountWithValidInformation.getAccountNumber());

        Response response = anAccountResource.postAccount(anAccountRequestDTO);

        assertEquals(CREATED_STATUS_CODE, response.getStatus());
    }

    @Test
    public void givenAnAccountNumberRelatedToAnAccountNotRegistered_whenGettingAccount_thenReturnNotFound() {
        when(anAccountServiceMock.findAccountWithAccountNumber(AN_ACCOUNT_NUMBER))
                .thenThrow(new AccountNotFoundException(AN_ACCOUNT_NUMBER));

        Response response = anAccountResource.getAccount(AN_ACCOUNT_NUMBER);

        assertEquals(NOT_FOUND_STATUS_CODE, response.getStatus());
    }

    @Test
    public void givenAnAccountNumberRelatedToARegisteredAccount_whenGettingAccount_thenReturnOk() {
        when(anAccountServiceMock.findAccountWithAccountNumber(AN_ACCOUNT_NUMBER))
                .thenReturn(anAccountWithValidInformation);

        Response response = anAccountResource.getAccount(AN_ACCOUNT_NUMBER);

        assertEquals(OK_STATUS_CODE, response.getStatus());
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }
}
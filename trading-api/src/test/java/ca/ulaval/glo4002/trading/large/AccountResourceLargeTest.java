package ca.ulaval.glo4002.trading.large;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.AccountResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.interfaces.rest.RestTestBase;
import ca.ulaval.glo4002.trading.suites.ApiITestSuite;
import ca.ulaval.glo4002.trading.suites.ApiLargeITestSuite;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountResourceLargeTest extends RestTestBase {

    private final static String A_VALID_INVESTOR_NAME = "Nom Prenom";
    private final static String A_VALID_EMAIL = "test@test.com";
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static float A_CREDIT_AMOUNT = 100F;

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @Before
    public void setUp() {
        setUpCreditsDTO();
    }

    @Test
    public void givenAnAccountDto_whenICreateANewAccount_thenICanRetrieveTheCreatedAccountWithValidInformation() {
        Long investorId = ApiLargeITestSuite.investorIdCount++;
        RequestSpecification postRequest = RestAssured.given().accept(ContentType.JSON)
                .port(ApiITestSuite.TEST_SERVER_PORT).contentType(ContentType.JSON).body(createAccountRequestDto(investorId));

        String locationHeader = postRequest.post("accounts/").getHeaders().get("Location").getValue();

        String accountNumber = extractAccountNumber(locationHeader);
        RequestSpecification getRequest = RestAssured.given().accept(ContentType.JSON)
                .port(ApiITestSuite.TEST_SERVER_PORT).contentType(ContentType.JSON);
        Response response = getRequest.get("accounts/" + accountNumber);
        AccountResponseDTO account = response.getBody().as(AccountResponseDTO.class);

        assertEquals(investorId, account.investorId);
        assertTrue(creditsComparator(credits, account.credits));
        assertTrue(response.body().asString().contains("total"));
        assertTrue(response.body().asString().contains("investorProfile"));
    }

    private String extractAccountNumber(String locationHeader) {
        StringBuilder sb = new StringBuilder();
        for (int i = locationHeader.length() - 1; i > 0; i--) {
            if (locationHeader.charAt(i) != '/') {
                sb.append(locationHeader.charAt(i));
            } else {
                break;
            }
        }
        sb.reverse();
        return sb.toString();
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

    private boolean creditsComparator(List<CreditDTO> A, List<CreditDTO> B) {
        if (A.size() != B.size()) {
            return false;
        }

        int count = 0;
        for (CreditDTO a : A) {
            for (CreditDTO b : B) {
                if (a.amount.compareTo(b.amount) == 0 && a.currency.equals(b.currency)) {
                    count++;
                }
            }
        }

        return count == A.size();
    }
}

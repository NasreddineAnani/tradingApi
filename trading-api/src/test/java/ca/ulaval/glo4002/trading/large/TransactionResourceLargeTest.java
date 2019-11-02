package ca.ulaval.glo4002.trading.large;

import ca.ulaval.glo4002.trading.application.DTO.*;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.interfaces.rest.RestTestBase;
import ca.ulaval.glo4002.trading.suites.ApiITestSuite;
import ca.ulaval.glo4002.trading.suites.ApiLargeITestSuite;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionResourceLargeTest extends RestTestBase {

    private static final int NOT_FOUND = 404;
    private final static String A_VALID_INVESTOR_NAME = "Nom Prenom";
    private final static String A_VALID_EMAIL = "test@test.com";
    private final static String A_VALID_CURRENCY_SYMBOL = "USD";
    private final static String VALID_DATE_STRING = "2018-08-21T15:23:20.142Z";

    private final static Long QUANTITY = 10L;
    private final static float A_CREDIT_AMOUNT = 10000F;
    private final static String VALID_MARKET_SYMBOL = "NASDAQ";
    private final static String VALID_STOCK_SYMBOL = "GOOG";
    private final static String A_TRANSACTION_NUMBER = "1234";

    private Stock aValidStock;
    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @Before
    public void setUp() {
        aValidStock = new Stock(VALID_STOCK_SYMBOL, VALID_MARKET_SYMBOL);
        setUpCreditsDTO();

    }

    @Test
    public void givenAnAccountAndATransactionRequestDTO_whenIBuyATransaction_thenICanRetrieveTheTransactionDetails() {
        String accountNumber = createAccount();
        TransactionRequestDTO transactionRequestDTO = createTransactionRequestDTO("BUY", A_TRANSACTION_NUMBER);

        String transactionNumber = performTransaction(accountNumber, transactionRequestDTO);
        Response transactionDetailsResponse = retrieveTransactionDetails(accountNumber, transactionNumber);

        String responseBody = transactionDetailsResponse.body().asString();
        BuyTransactionResponseDTO transactionResponseDTO = transactionDetailsResponse.getBody().as(BuyTransactionResponseDTO.class);

        assertTrue(responseBody.contains("purchasedPrice"));
        assertTrue(responseBody.contains("quantity"));
        assertTrue(responseBody.contains("fees"));
        assertEquals(transactionNumber, transactionResponseDTO.transactionNumber);
        assertEquals("BUY", transactionResponseDTO.type);
        assertTrue(transactionResponseDTO.date.toInstant()
                .compareTo(ZonedDateTime.parse(transactionRequestDTO.date).toInstant()) == 0);
        assertEquals(transactionRequestDTO.stock.getMarket(), transactionResponseDTO.stock.market);
        assertEquals(transactionRequestDTO.stock.getSymbol(), transactionResponseDTO.stock.symbol);
    }

    @Test
    public void givenAnAccountWithABuyTransaction_whenISellThatTransaction_thenICanRetrieveTheDetailsOfThatSellTransaction() {
        String accountNumber = createAccount();
        TransactionRequestDTO buyTransactionRequestDTO = createTransactionRequestDTO("BUY", A_TRANSACTION_NUMBER);
        String buyTransactionNumber = performTransaction(accountNumber, buyTransactionRequestDTO);
        TransactionRequestDTO sellTransactionRequestDTO = createTransactionRequestDTO("SELL", buyTransactionNumber);

        String sellTransactionNumber = performTransaction(accountNumber, sellTransactionRequestDTO);
        Response sellTransactionDetailsResponse = retrieveTransactionDetails(accountNumber, sellTransactionNumber);

        String responseBody = sellTransactionDetailsResponse.body().asString();
        SellTransactionResponseDTO sellTransactionResponseDTO =
                sellTransactionDetailsResponse.getBody().as(SellTransactionResponseDTO.class);

        assertTrue(responseBody.contains("priceSold"));
        assertTrue(responseBody.contains("quantity"));
        assertTrue(responseBody.contains("fees"));
        assertEquals(sellTransactionNumber, sellTransactionResponseDTO.transactionNumber);
        assertEquals("SELL", sellTransactionResponseDTO.type);
        assertTrue(sellTransactionResponseDTO.date.toInstant()
                .compareTo(ZonedDateTime.parse(sellTransactionRequestDTO.date).toInstant()) == 0);
        assertEquals(sellTransactionRequestDTO.stock.getMarket(), sellTransactionResponseDTO.stock.market);
        assertEquals(sellTransactionRequestDTO.stock.getSymbol(), sellTransactionResponseDTO.stock.symbol);
    }

    @Test
    public void whenITryToRetrieveTheDetailsofAnUnexistentTransaction_thenIReceivedAnErrorMessage() {
        String accountNumber = createAccount();
        String unexistentTransactionNumber = "fake number";

        givenBaseRequest()
                .when().get("accounts/" + accountNumber + "/transactions/" + unexistentTransactionNumber)
                .then().statusCode(NOT_FOUND)
                .and().body("error", equalTo("TRANSACTION_NOT_FOUND"))
                .and().body("description", equalTo(String.format("transaction with number %s not found", unexistentTransactionNumber)));
    }

    private Response retrieveTransactionDetails(String accountNumber, String transactionNumber) {
        return RestAssured.given().accept(ContentType.JSON)
                .port(ApiITestSuite.TEST_SERVER_PORT)
                .contentType(ContentType.JSON)
                .get("accounts/" + accountNumber + "/transactions/" + transactionNumber);
    }

    private String createAccount() {
        Long investorId = ApiLargeITestSuite.investorIdCount++;
        Response postAccountResponse = RestAssured.given().accept(ContentType.JSON)
                .port(ApiITestSuite.TEST_SERVER_PORT)
                .contentType(ContentType.JSON)
                .body(createAccountRequestDto(investorId))
                .post("accounts/");

        String accountLocationHeader = postAccountResponse.getHeaders().get("Location").getValue();
        return extractUniqueNumberFromURL(accountLocationHeader);
    }

    private String performTransaction(String accountNumber, TransactionRequestDTO transactionRequestDTO) {
        Response postTransactionResponse = RestAssured.given().accept(ContentType.JSON)
                .port(ApiITestSuite.TEST_SERVER_PORT)
                .contentType(ContentType.JSON)
                .body(transactionRequestDTO)
                .post("accounts/" + accountNumber + "/transactions");

        String transactionLocationHeader = postTransactionResponse.getHeaders().get("Location").getValue();
        return extractUniqueNumberFromURL(transactionLocationHeader);
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

    private TransactionRequestDTO createTransactionRequestDTO(String type, String transactionNumber) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.date = VALID_DATE_STRING;
        transactionRequestDTO.quantity = QUANTITY;
        transactionRequestDTO.stock = aValidStock;
        transactionRequestDTO.type = type;
        transactionRequestDTO.transactionNumber = transactionNumber;

        return transactionRequestDTO;
    }

    private String extractUniqueNumberFromURL(String locationHeader) {
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
}

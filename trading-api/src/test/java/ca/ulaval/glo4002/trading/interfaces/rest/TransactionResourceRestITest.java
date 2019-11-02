package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.service.MarketApiCaller;
import ca.ulaval.glo4002.trading.application.service.StockApiCaller;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.market.Market;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.domain.stock.Price;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.suites.ApiITestSuite;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;

public class TransactionResourceRestITest extends RestTestBase {

    private final static ZonedDateTime A_DATE = ZonedDateTime.now();
    private static final int CREATED_CODE = 201;
    private static final int OK_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;
    private static final Long VALID_INVESTOR_ID = 1234L;
    private static final String VALID_INVESTOR_NAME = "Wissam Goghrod";
    private final static String VALID_DATE_STRING = "2018-08-21T15:23:20.142Z";
    private final static String[] VALID_OPEN_HOURS = {"09:00-13:30", "14:00-20:00"};
    private final static String A_TIMEZONE = "UTC";
    private final static Long QUANTITY = 10L;
    private final static String VALID_MARKET_SYMBOL = "NASDAQ";
    private final static String VALID_STOCK_SYMBOL = "GOOG";
    private final static String VALID_CURRENCY_SYMBOL_SYMBOL = "USD";
    private final static String ACCOUNT_NUMBER = "1234";
    private final static int CREDIT_AMOUNT = 1000;
    private final static int PRICE_AMOUNT = CREDIT_AMOUNT / 100;
    private final static BigDecimal STOCK_PRICE = BigDecimal.valueOf(PRICE_AMOUNT);

    private Stock aValidStock;
    private Account aValidAccount;
    private Market aValidMarket;
    private Price aPrice;
    private BuyTransaction aValidBuyTransaction;
    private AccountRepository accountRepositoryMock;
    private MarketApiCaller marketApiCallerMock;
    private StockApiCaller stockApiCallerMock;


    @Before
    public void setUp() {
        aPrice = new Price(VALID_DATE_STRING, STOCK_PRICE);
        List<Price> priceList = new ArrayList<>();
        priceList.add(aPrice);
        aValidStock = new Stock(VALID_STOCK_SYMBOL, VALID_MARKET_SYMBOL);
        aValidStock.setPrices(priceList);
        aValidMarket = new Market(VALID_MARKET_SYMBOL, VALID_OPEN_HOURS, A_TIMEZONE);

        aValidAccount = new Account(VALID_INVESTOR_ID, VALID_INVESTOR_NAME, setUpCreditsDTO());
        aValidBuyTransaction = new BuyTransaction(A_DATE, aValidStock, QUANTITY);
        aValidAccount.addTransaction(aValidBuyTransaction);


        accountRepositoryMock = ApiITestSuite.repositoryMock;
        marketApiCallerMock = ApiITestSuite.marketApiCallerMock;
        stockApiCallerMock = ApiITestSuite.stockApiCallerMock;
    }

    @Test
    public void givenATransactionRequestDTO_whenBuyingATransaction_thenTheTransactionIsCreated() {
        TransactionRequestDTO buyTransactionRequestDTO = createTransactionRequestDTO("BUY");
        willReturn(aValidAccount).given(accountRepositoryMock).findAccountWithAccountNumber(ACCOUNT_NUMBER);
        willReturn(aValidMarket).given(marketApiCallerMock).getMarket(VALID_MARKET_SYMBOL);
        willReturn(aValidStock).given(stockApiCallerMock).getStock(any());

        givenBaseRequest().body(buyTransactionRequestDTO)
                .when().post("/accounts/" + ACCOUNT_NUMBER + "/transactions/")
                .then().statusCode(CREATED_CODE);
    }

    @Test
    public void givenATransactionRequestDTO_whenSellATransaction_thenTheTransactionIsCreated() {
        TransactionRequestDTO sellTransactionDTO = createTransactionRequestDTO("SELL");
        sellTransactionDTO.transactionNumber = aValidBuyTransaction.getTransactionNumber();
        willReturn(aValidAccount).given(accountRepositoryMock).findAccountWithAccountNumber(ACCOUNT_NUMBER);
        willReturn(aValidMarket).given(marketApiCallerMock).getMarket(VALID_MARKET_SYMBOL);
        willReturn(aValidStock).given(stockApiCallerMock).getStock(any());

        givenBaseRequest().body(sellTransactionDTO)
                .when().post("/accounts/" + ACCOUNT_NUMBER + "/transactions/")
                .then().statusCode(CREATED_CODE);
    }

    @Test
    public void givenATransactionNumberAndAnAccountNumber_whenILookForAnExistingTransactionDetails_thenIReceiveTheDetails() {
        willReturn(aValidAccount).given(accountRepositoryMock).findAccountWithAccountNumber(ACCOUNT_NUMBER);

        givenBaseRequest().when()
                .get("/accounts/" + ACCOUNT_NUMBER + "/transactions/" + aValidBuyTransaction.getTransactionNumber())
                .then().statusCode(OK_CODE);
    }

    @Test
    public void givenAnUnexistentTransactionNumber_whenILookForThatTransactionsDetails_thenIReceiveAnErrorMessage() {
        String unexistentTransactionNumber = "fake number";
        willReturn(aValidAccount).given(accountRepositoryMock).findAccountWithAccountNumber(ACCOUNT_NUMBER);
        String description = "transaction with number %s not found";

        givenBaseRequest().when()
                .get("/accounts/" + ACCOUNT_NUMBER + "/transactions/" + unexistentTransactionNumber)
                .then().statusCode(NOT_FOUND_CODE).and().body("error", equalTo("TRANSACTION_NOT_FOUND"))
                .and().body("description", equalTo(String.format(description, unexistentTransactionNumber)));
    }


    private TransactionRequestDTO createTransactionRequestDTO(String type) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.date = VALID_DATE_STRING;
        transactionRequestDTO.quantity = QUANTITY;
        transactionRequestDTO.stock = aValidStock;
        transactionRequestDTO.type = type;

        return transactionRequestDTO;
    }

    private List<CreditDTO> setUpCreditsDTO() {
        List<CreditDTO> credits = new ArrayList<>();
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.currency = VALID_CURRENCY_SYMBOL_SYMBOL;
        creditDTO.amount = new BigDecimal(CREDIT_AMOUNT);
        credits.add(creditDTO);
        return credits;
    }
}

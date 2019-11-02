package ca.ulaval.glo4002.trading.domain.account;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidTransactionNumberException;
import ca.ulaval.glo4002.trading.application.service.exceptions.TransactionNotFoundException;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.wallet.Wallet;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


public class AccountTest {

    private final static Long A_VALID_INVESTOR_ID = 1234L;
    private final static String A_VALID_INVESTOR_NAME = "FirstName LastName";
    private final static BigDecimal A_TRANSACTION_PRICE = new BigDecimal(1000F);
    private final static Long A_TRANSACTION_QUANTITY = 10L;
    private final static String A_TRANSACTION_NUMBER_NOT_IN_THE_ACCOUNT_WITH_VALID_INFORMATION =
            "transactionNumberNotInTheAccount";
    private final static String A_TRANSACTION_NUMBER =
            "transactionNumberInTheAccount";
    private final static String A_MARKET = "NASDAQ";
    private final static String A_SYMBOL = "GOOG";
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static int A_CREDIT_AMOUNT = 100;
    private final static BigDecimal A_LOWER_CREDIT_AMOUNT = new BigDecimal(50);
    private final static ZonedDateTime a_ZONED_DATE_TIME = ZonedDateTime.now();

    private Account anAccountWithValidInformation;

    private Stock aStock;

    private BuyTransaction aBuyTransactionMock;
    private SellTransaction aSellTransactionMock;
    private ExchangeRateConverter exchangeRateConverterMock;

    private BuyTransaction aBuyTransaction;

    private SellTransaction aSellTransaction;

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    private void prepareABuyTransactionMock() {
        when(aBuyTransactionMock.calculateTransactionPriceTotal(exchangeRateConverterMock)).thenReturn(A_LOWER_CREDIT_AMOUNT);
        when(aBuyTransactionMock.getCurrency()).thenReturn(Currency.getInstance(A_VALID_CURRENCY_SYMBOL));
        when(aBuyTransactionMock.getTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);
        when(aBuyTransactionMock.getStock()).thenReturn(aStock);
        when(aBuyTransactionMock.getQuantity()).thenReturn(A_TRANSACTION_QUANTITY);
    }

    private void prepareASellTransactionMock() {
        when(aSellTransactionMock.getBuyTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);
        when(aSellTransactionMock.calculateTotalTransactionGain(exchangeRateConverterMock)).thenReturn(A_TRANSACTION_PRICE);
        when(aSellTransactionMock.calculateTotalTransactionGain(exchangeRateConverterMock)).thenReturn(A_LOWER_CREDIT_AMOUNT);
        when(aSellTransactionMock.getCurrency()).thenReturn(Currency.getInstance(A_VALID_CURRENCY_SYMBOL));
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }

    private void setUpNegativeCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(-A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }

    @Before
    public void setUp() {
        setUpCreditsDTO();

        anAccountWithValidInformation = new Account(A_VALID_INVESTOR_ID, A_VALID_INVESTOR_NAME, credits);

        exchangeRateConverterMock = mock(ExchangeRateConverter.class);
        aBuyTransactionMock = mock(BuyTransaction.class);
        aSellTransactionMock = mock(SellTransaction.class);

        aStock = new Stock(A_SYMBOL, A_MARKET);

        aBuyTransaction = new BuyTransaction(a_ZONED_DATE_TIME, aStock, A_TRANSACTION_QUANTITY);

        aSellTransaction = new SellTransaction(a_ZONED_DATE_TIME,
                aStock, A_TRANSACTION_QUANTITY, A_TRANSACTION_NUMBER);
        aSellTransaction.setPrice(A_TRANSACTION_PRICE);

        anAccountWithValidInformation.addTransaction(aBuyTransaction);

    }

    @Test(expected = AccountInvalidCreditsException.class)
    public void givenAccountInformationWithNegativeCredits_whenCreatingAccount_thenAccountHasInvalidCredits() {
        setUpNegativeCreditsDTO();
        new Account(A_VALID_INVESTOR_ID, A_VALID_INVESTOR_NAME, credits);
    }


    @Test
    public void givenAnAccountAndABuyTransactionCheaperThanTheAccountCredits_whenBuyingStocks_thenTheTransactionCreditsAreDeductedFromTheAccountCredits() {
        Wallet wallet = anAccountWithValidInformation.getWallet();
        Map<Currency, BigDecimal> credits = wallet.getCredits();
        BigDecimal initialCredits = credits.get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL));
        BigDecimal expectedFinalFunds = initialCredits.subtract(A_LOWER_CREDIT_AMOUNT);
        prepareABuyTransactionMock();

        anAccountWithValidInformation.buyStock(aBuyTransactionMock, exchangeRateConverterMock);

        assertEquals(expectedFinalFunds, anAccountWithValidInformation.getWallet().
                getCredits().get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL)));
    }

    @Test
    public void givenAnAccountAndABuyTransactionCheaperThanTheAccountCredits_whenBuyingStocks_thenTheTransactionIsSavedInTheAccount() {
        int expectedAccountTransactionListSize =
                anAccountWithValidInformation.getTransactions().size() + 1;
        prepareABuyTransactionMock();

        anAccountWithValidInformation.buyStock(aBuyTransactionMock, exchangeRateConverterMock);

        assertEquals(expectedAccountTransactionListSize,
                anAccountWithValidInformation.getTransactions().size());
    }

    @Test
    public void givenAnAccountAndABuyTransaction_whenBuyingStocks_thenTheTransactionTotalIsComputed() {
        prepareABuyTransactionMock();

        anAccountWithValidInformation.buyStock(aBuyTransactionMock, exchangeRateConverterMock);

        verify(aBuyTransactionMock).calculateTransactionPriceTotal(exchangeRateConverterMock);
    }

    @Test
    public void givenAnAccountAndASellTransaction_whenSellingStocks_thenTheTransactionTotalGainIsComputed() {
        prepareABuyTransactionMock();
        prepareASellTransactionMock();
        anAccountWithValidInformation.addTransaction(aBuyTransactionMock);

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);

        verify(aSellTransactionMock).calculateTotalTransactionGain(exchangeRateConverterMock);
    }

    @Test
    public void givenASellTransactionAndAnAccountContainingTheBuyTransaction_whenSellingStocks_thenVerifyTransactionValidity() {
        prepareABuyTransactionMock();
        prepareASellTransactionMock();
        anAccountWithValidInformation.addTransaction(aBuyTransactionMock);

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);

        verify(aSellTransactionMock).verifyTransactionValidity(aStock, A_TRANSACTION_QUANTITY);
    }

    @Test
    public void givenAValidSellTransactionAndAnAccountContainingTheBuyTransaction_whenSellingStocks_thenSellTransactionQuantityIsDeductedInTheRelatedBuyTransaction() {
        prepareABuyTransactionMock();
        when(aBuyTransactionMock.getTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);
        when(aSellTransactionMock.getBuyTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);
        anAccountWithValidInformation.addTransaction(aBuyTransactionMock);

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);

        verify(aBuyTransactionMock).deductTransactionStockQuantity(
                aSellTransactionMock.getQuantity());
    }

    @Test
    public void givenAnAccountWithABuyTransactionAndARelatedSellTransaction_whenSellingStocks_thenTransactionCreditsAreAddedToAccount() {
        prepareASellTransactionMock();
        Wallet wallet = anAccountWithValidInformation.getWallet();
        Map<Currency, BigDecimal> credits = wallet.getCredits();
        BigDecimal initialCredits = credits.get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL));
        BigDecimal expectedFinalFunds = initialCredits.add(A_LOWER_CREDIT_AMOUNT);
        when(aSellTransactionMock.getBuyTransactionNumber()).thenReturn(aBuyTransaction.getTransactionNumber());

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);

        assertEquals(expectedFinalFunds, anAccountWithValidInformation.getWallet().getCredits().get(Currency.getInstance(A_VALID_CURRENCY_SYMBOL)));
    }

    @Test
    public void givenAnAccountAndASellTransaction_whenSellingStocks_thenTheTransactionIsSavedInTheAccount() {
        prepareABuyTransactionMock();
        prepareASellTransactionMock();
        anAccountWithValidInformation.addTransaction(aBuyTransactionMock);
        int expectedAccountTransactionListSize =
                anAccountWithValidInformation.getTransactions().size() + 1;

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);

        assertEquals(expectedAccountTransactionListSize,
                anAccountWithValidInformation.getTransactions().size());
    }

    @Test(expected = TransactionNotFoundException.class)
    public void givenATransactionNumberAndAnAccountNotContainingTheTransactionRelatedToTheNumber_whenFindingTheTransactionInTheAccountWithTheTransactionNumber_thenAccountIsNotFound() {
        anAccountWithValidInformation.getTransactionWithTransactionNumber(
                A_TRANSACTION_NUMBER_NOT_IN_THE_ACCOUNT_WITH_VALID_INFORMATION);
    }

    @Test
    public void givenAnAccountATransactionAndItsTransactionNumber_whenFindingTheTransactionInTheAccountWithTheTransactionNumber_thenTheTransactionAssociatedWithTheNumberIsReturned() {
        anAccountWithValidInformation.addTransaction(aBuyTransaction);
        String theTransactionNumber = aBuyTransaction.getTransactionNumber();

        Transaction returnedTransaction = anAccountWithValidInformation
                .getTransactionWithTransactionNumber(theTransactionNumber);

        assertEquals(aBuyTransaction, returnedTransaction);
    }

    @Test(expected = InvalidTransactionNumberException.class)
    public void givenAnAccountAndASellTransactionAssociatedWithAnotherSellTransaction_whenTryingToSellStocksFromASellTransaction_thenTheTransactionFails() {
        prepareASellTransactionMock();
        when(aSellTransactionMock.getTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);
        anAccountWithValidInformation.addTransaction(aSellTransactionMock);

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);
    }

    @Test(expected = InvalidTransactionNumberException.class)
    public void givenAnAccountAndASellTransactionWithANonExistentBuyTransactionNumber_whenSellingStocks_thenTheTransactionFails() {
        prepareASellTransactionMock();
        when(aSellTransactionMock.getTransactionNumber()).thenReturn(A_TRANSACTION_NUMBER);

        anAccountWithValidInformation.sellStock(aSellTransactionMock, exchangeRateConverterMock);
    }

    @Test
    public void givenAnAccountAndAnExchangeRateConverter_whenCalcultingTotalValueOfWallet_thenTheResultIsSetToTheAccount() {
        when(exchangeRateConverterMock.toCAD(any(), any())).thenReturn(BigDecimal.valueOf(A_CREDIT_AMOUNT));
        anAccountWithValidInformation.calculateAndSetTotalCreditsInCAD(exchangeRateConverterMock);

        assertNotNull(anAccountWithValidInformation.getWallet().getTotalCreditsInCAD());
    }
}
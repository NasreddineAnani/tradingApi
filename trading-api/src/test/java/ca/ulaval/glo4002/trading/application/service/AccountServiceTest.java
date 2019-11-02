package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.assembler.AccountAssembler;
import ca.ulaval.glo4002.trading.domain.*;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountAlreadyOpenException;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    private final static Long A_VALID_INVESTOR_ID = 1234L;
    private final static String A_VALID_INVESTOR_NAME = "FirstName LastName";
    private final static BigDecimal A_TRANSACTION_PRICE = new BigDecimal(100F);
    private final static Long A_TRANSACTION_QUANTITY = 2L;
    private final static String A_MARKET = "NASDAQ";
    private final static String A_SYMBOL = "GOOG";
    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static int A_CREDIT_AMOUNT = 10000;
    private final static String AN_ACCOUNT_NUMBER = "account number";

    private AccountRepository anAccountRepositoryMock;
    private AccountAssembler anAccountAssemblerMock;
    private ExchangeRateConverter exchangeRateConverterMock;
    private Account anAccountMock;
    private SellTransaction sellTransactionMock;
    private BuyTransaction buyTransactionMock;

    private AccountService anAccountService;
    private BuyTransaction aBuyTransaction;
    private SellTransaction aSellTransactionRelatedToABuyTransaction;
    private AccountRequestDTO anAccountRequestDTO;
    private Account theAccountRelatedToAnAccountRequestDTO;
    private String theAccountRelatedToAnAccountRequestDTOAccountNumber;
    private Account anAccountWithABuyTransaction;

    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @Before
    public void setUp() {
        setUpCreditsDTO();
        anAccountRepositoryMock = mock(AccountRepository.class);
        anAccountAssemblerMock = mock(AccountAssembler.class);
        exchangeRateConverterMock = mock(ExchangeRateConverter.class);
        when(exchangeRateConverterMock.toCAD(any(), any())).thenReturn(BigDecimal.valueOf(A_CREDIT_AMOUNT));
        when(exchangeRateConverterMock.fromCADtoCurrency(any(), any())).thenReturn(BigDecimal.valueOf(A_CREDIT_AMOUNT));
        sellTransactionMock = mock(SellTransaction.class);
        buyTransactionMock = mock(BuyTransaction.class);
        anAccountMock = mock(Account.class);

        anAccountService = new AccountService(anAccountRepositoryMock, anAccountAssemblerMock, exchangeRateConverterMock);

        Stock aStock = new Stock(A_SYMBOL, A_MARKET);

        aBuyTransaction = new BuyTransaction(ZonedDateTime.now(), aStock, A_TRANSACTION_QUANTITY);
        aBuyTransaction.setPrice(A_TRANSACTION_PRICE);
        aBuyTransaction.setCurrency(Currency.getInstance("CAD"));

        aSellTransactionRelatedToABuyTransaction = new SellTransaction(ZonedDateTime.now(), aStock,
                A_TRANSACTION_QUANTITY, aBuyTransaction.getTransactionNumber());
        aSellTransactionRelatedToABuyTransaction.setPrice(A_TRANSACTION_PRICE);
        aSellTransactionRelatedToABuyTransaction.setCurrency(Currency.getInstance("CAD"));

        anAccountRequestDTO = new AccountRequestDTO();
        anAccountRequestDTO.investorId = A_VALID_INVESTOR_ID;
        anAccountRequestDTO.investorName = A_VALID_INVESTOR_NAME;

        theAccountRelatedToAnAccountRequestDTO = new Account(A_VALID_INVESTOR_ID,
                A_VALID_INVESTOR_NAME, credits);
        theAccountRelatedToAnAccountRequestDTOAccountNumber = theAccountRelatedToAnAccountRequestDTO.getAccountNumber();

        anAccountWithABuyTransaction = new Account(A_VALID_INVESTOR_ID,
                A_VALID_INVESTOR_NAME, credits);
    }

    @Test
    public void givenAnAccountRequestDTOAndAnAccountService_whenCreatingAccount_thenAccountRequestDTOIsConvertedToAccount() {
        when(anAccountAssemblerMock.fromAccountRequestDTOToAccount(anAccountRequestDTO))
                .thenReturn(theAccountRelatedToAnAccountRequestDTO);

        anAccountService.createAccount(anAccountRequestDTO);

        verify(anAccountAssemblerMock).fromAccountRequestDTOToAccount(anAccountRequestDTO);
    }

    @Test(expected = AccountAlreadyOpenException.class)
    public void givenAnAccountRequestDTORelatedToAnAccountInTheRepositoryAndAnAccountService_whenCreatingAccount_thenCreationIsBlockedSinceAccountIsAlreadyOpened() {
        when(anAccountAssemblerMock.fromAccountRequestDTOToAccount(anAccountRequestDTO))
                .thenReturn(theAccountRelatedToAnAccountRequestDTO);

        when(anAccountRepositoryMock.accountAlreadyExists(theAccountRelatedToAnAccountRequestDTO.getInvestorId()))
                .thenReturn(true);

        anAccountService.createAccount(anAccountRequestDTO);
    }

    @Test
    public void givenAnAccountRequestDTONotRelatedToAnAccountPresentInTheRepositoryAndAnAccountService_whenCreatingAccount_thenAccountIsSavedToTheRepository() {
        when(anAccountAssemblerMock.fromAccountRequestDTOToAccount(anAccountRequestDTO))
                .thenReturn(theAccountRelatedToAnAccountRequestDTO);

        when(anAccountRepositoryMock.accountAlreadyExists(theAccountRelatedToAnAccountRequestDTO.getInvestorId()))
                .thenReturn(false);

        anAccountService.createAccount(anAccountRequestDTO);

        verify(anAccountRepositoryMock).persist(theAccountRelatedToAnAccountRequestDTO);
    }

    @Test
    public void givenANewAccountRequestDTO_whenCreatingAccount_thenTheAccountNumberOfTheCreatedAccountIsReturned() {
        String expectedAccountNumber = theAccountRelatedToAnAccountRequestDTO.getAccountNumber();
        when(anAccountAssemblerMock.fromAccountRequestDTOToAccount(anAccountRequestDTO))
                .thenReturn(theAccountRelatedToAnAccountRequestDTO);
        when(anAccountRepositoryMock
                .accountAlreadyExists(theAccountRelatedToAnAccountRequestDTO.getInvestorId()))
                .thenReturn(false);

        String returnedAccountNumber = anAccountService.createAccount(anAccountRequestDTO);

        assertEquals(expectedAccountNumber, returnedAccountNumber);
    }

    @Test
    public void givenABuyTransactionAndAnExistingAccountNumber_whenBuyingStocks_thenStocksAreBoughtFromTheAccount() {
        when(anAccountRepositoryMock
                .findAccountWithAccountNumber(theAccountRelatedToAnAccountRequestDTOAccountNumber))
                .thenReturn(anAccountMock);

        anAccountService.buyStock(aBuyTransaction,
                theAccountRelatedToAnAccountRequestDTOAccountNumber);

        verify(anAccountMock).buyStock(aBuyTransaction, exchangeRateConverterMock);
    }

    @Test
    public void givenABuyTransactionAndAnExistingAccountNumBer_whenBuyingStocks_thenAccountIsUpdatedIntoTheRepository() {
        when(anAccountRepositoryMock
                .findAccountWithAccountNumber(AN_ACCOUNT_NUMBER))
                .thenReturn(anAccountMock);

        anAccountService.buyStock(buyTransactionMock,
                AN_ACCOUNT_NUMBER);

        verify(anAccountRepositoryMock).persist(anAccountMock);
    }


    @Test
    public void givenABuyTransactionAndAnExistingAccountNumber_whenLookingForATransaction_thenTransactionIsRetrievedFromTheAccount() {
        when(anAccountRepositoryMock
                .findAccountWithAccountNumber(theAccountRelatedToAnAccountRequestDTOAccountNumber))
                .thenReturn(anAccountMock);

        anAccountService.getTransaction(theAccountRelatedToAnAccountRequestDTOAccountNumber,
                aBuyTransaction.getTransactionNumber());

        verify(anAccountMock).getTransactionWithTransactionNumber(aBuyTransaction
                .getTransactionNumber());
    }

    @Test
    public void givenASellTransactionAndAndAnExistingAccountNumber_whenSellingStocks_thenStocksAreSoldFromTheAccount() {
        when(anAccountRepositoryMock
                .findAccountWithAccountNumber(theAccountRelatedToAnAccountRequestDTOAccountNumber))
                .thenReturn(anAccountMock);

        anAccountService.sellStock(aSellTransactionRelatedToABuyTransaction,
                theAccountRelatedToAnAccountRequestDTOAccountNumber);

        verify(anAccountMock).sellStock(aSellTransactionRelatedToABuyTransaction, exchangeRateConverterMock);
    }

    @Test
    public void givenASellTransactionAndAnAccountThatHasABuyTransaction_whenSellingStocks_thenAccountIsUpdatedInTheRepository() {
        when(anAccountRepositoryMock
                .findAccountWithAccountNumber(AN_ACCOUNT_NUMBER))
                .thenReturn(anAccountMock);

        anAccountService.sellStock(sellTransactionMock, AN_ACCOUNT_NUMBER);

        verify(anAccountRepositoryMock).persist(anAccountMock);
    }

    @Test
    public void givenAnAccountNumber_whenGettingTheRelatedAccount_theTotalCreditsAreComputedBeforeReturningTheAccount() {
        when(anAccountRepositoryMock.findAccountWithAccountNumber
                (anAccountMock.getAccountNumber())).thenReturn(anAccountMock);

        anAccountService.findAccountWithAccountNumber(anAccountMock.getAccountNumber());

        verify(anAccountMock).calculateAndSetTotalCreditsInCAD(exchangeRateConverterMock);
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }

}
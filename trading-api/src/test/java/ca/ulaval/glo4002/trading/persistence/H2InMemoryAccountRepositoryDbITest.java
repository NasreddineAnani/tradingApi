package ca.ulaval.glo4002.trading.persistence;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.domain.account.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class H2InMemoryAccountRepositoryDbITest {

    private final static String A_VALID_CURRENCY_SYMBOL = "CAD";
    private final static float A_CREDIT_AMOUNT = 100F;
    private final static long VALID_INVESTOR_ID = 100L;
    private final static String VALID_INVESTOR_NAME = "Wissam Goghrod";

    private static EntityManagerFactory entityManagerFactory;
    private H2InMemoryAccountRepository accountRepository;
    private EntityManager entityManager;

    private Account account;
    private List<CreditDTO> credits;
    private CreditDTO creditDTO;

    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("h2-InMemory-test");
    }

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        EntityManagerProvider.setEntityManager(entityManager);

        accountRepository = new H2InMemoryAccountRepository();

        setUpCreditsDTO();
        account = new Account(VALID_INVESTOR_ID, VALID_INVESTOR_NAME, credits);
    }

    @After
    public void afterTest() {
        entityManager.getTransaction().begin();
        entityManager.remove(account);
        entityManager.getTransaction().commit();
    }

    @Test
    public void givenAnAccount_whenItIsSaved_thenItCanBeRetrieved() {
        accountRepository.persist(account);

        Account retrievedAccount = accountRepository.findAccountWithInvestorId(account.getInvestorId());
        assertEquals(account.getAccountNumber(), retrievedAccount.getAccountNumber());
    }

    @Test
    public void givenTheInvestorIdOfASavedAccount_whenCheckingIfItExists_thenTheRepoFindsAnAssociatedAccount() {
        accountRepository.persist(account);

        assertTrue(accountRepository.accountAlreadyExists(account.getInvestorId()));
    }

    @Test
    public void givenAnUnexistingInvestorId_whenCheckingIfItExists_thenTheRepoCantFindAnAccountAssociated() {
        long unexistentInvestorId = 9999L;

        assertFalse(accountRepository.accountAlreadyExists(unexistentInvestorId));
    }

    @Test
    public void givenAnExistentAccountNumber_whenICheckForAnAccountWithThatNumber_thenTheAccountIsRetrieved() {
        accountRepository.persist(account);

        Account retrievedAccount = accountRepository.findAccountWithAccountNumber(account.getAccountNumber());

        assertEquals(account.getAccountNumber(), retrievedAccount.getAccountNumber());
    }

    @Test(expected = AccountNotFoundException.class)
    public void givenAnUnExistentAccountNumber_whenICheckForAnAccountWithThatNumber_thenNoAccountIsFound() {
        String unexistentAccountNumber = "9999";

        accountRepository.findAccountWithAccountNumber(unexistentAccountNumber);
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTO = new CreditDTO();
        creditDTO.currency = A_VALID_CURRENCY_SYMBOL;
        creditDTO.amount = new BigDecimal(A_CREDIT_AMOUNT);
        credits.add(creditDTO);
    }
}

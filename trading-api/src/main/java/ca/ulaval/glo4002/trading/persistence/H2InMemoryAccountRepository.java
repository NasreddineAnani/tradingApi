package ca.ulaval.glo4002.trading.persistence;

import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;


public class H2InMemoryAccountRepository implements AccountRepository {

    @Override
    public void persist(Account account) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(account);
        entityManager.getTransaction().commit();
    }

    @Override
    public Account findAccountWithInvestorId(long investorId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        return (Account) entityManager.createQuery("SELECT a FROM Account a WHERE a.investorId = :investorId").setParameter(
                "investorId", investorId).getSingleResult();
    }

    @Override
    public boolean accountAlreadyExists(long investorId) {
        try {
            findAccountWithInvestorId(investorId);
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    @Override
    public Account findAccountWithAccountNumber(String accountNumber) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        Account account = entityManager.find(Account.class, accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }

        return account;
    }
}

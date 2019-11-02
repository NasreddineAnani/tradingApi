package ca.ulaval.glo4002.trading.application.service;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.assembler.AccountAssembler;
import ca.ulaval.glo4002.trading.domain.*;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountAlreadyOpenException;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;

import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;
import javax.inject.Inject;


public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountAssembler accountAssembler;
    private final ExchangeRateConverter exchangeRateConverter;

    @Inject
    public AccountService(AccountRepository accountRepository, AccountAssembler accountAssembler,
                          ExchangeRateConverter exchangeRateConverter) {
        this.accountRepository = accountRepository;
        this.accountAssembler = accountAssembler;
        this.exchangeRateConverter = exchangeRateConverter;
    }

    public String createAccount(AccountRequestDTO accountRequestDTO) {
        Account account = this.accountAssembler.fromAccountRequestDTOToAccount(accountRequestDTO);
        this.verifyAccountDoesNotAlreadyExist(account.getInvestorId());
        this.accountRepository.persist(account);
        return account.getAccountNumber();
    }

    public Account findAccountWithAccountNumber(String accountNumber) {
        Account account = this.accountRepository.findAccountWithAccountNumber(accountNumber);

        account.calculateAndSetTotalCreditsInCAD(exchangeRateConverter);
        return account;
    }

    private void verifyAccountDoesNotAlreadyExist(long investorId) {
        if (this.accountRepository.accountAlreadyExists(investorId)) {
            throw new AccountAlreadyOpenException(investorId);
        }
    }

    public void buyStock(BuyTransaction buyTransaction, String accountNumber) {
        Account account = findAccountWithAccountNumber(accountNumber);
        account.buyStock(buyTransaction, exchangeRateConverter);
        this.accountRepository.persist(account);
    }

    public void sellStock(SellTransaction sellTransaction, String accountNumber) {
        Account account = findAccountWithAccountNumber(accountNumber);
        account.sellStock(sellTransaction, exchangeRateConverter);
        this.accountRepository.persist(account);
    }

    public Transaction getTransaction(String accountNumber, String transactionNumber) {
        Account account = findAccountWithAccountNumber(accountNumber);
        return account.getTransactionWithTransactionNumber(transactionNumber);
    }
}

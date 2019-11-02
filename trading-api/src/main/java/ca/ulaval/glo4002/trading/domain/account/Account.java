package ca.ulaval.glo4002.trading.domain.account;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidTransactionNumberException;
import ca.ulaval.glo4002.trading.application.service.exceptions.TransactionNotFoundException;

import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.profile.Profile;
import ca.ulaval.glo4002.trading.domain.wallet.Wallet;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "Account")
public class Account {

    private final static String DEFAULT_PROFILE_TYPE = "CONSERVATIVE";

    @Id
    private String accountNumber;
    private Long investorId;
    private String investorName;
    private Profile profile;
    private Wallet wallet;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn
    private List<Transaction> transactions;

    public Account() {
    }

    public Account(Long investorId, String investorName, List<CreditDTO> creditDTO) {
        this.accountNumber = AccountNumber.getNewAccountNumber(investorName);
        this.investorId = investorId;
        this.investorName = investorName;
        this.profile = new Profile(DEFAULT_PROFILE_TYPE, new ArrayList<>());
        this.transactions = new ArrayList<>();
        this.wallet = new Wallet(creditDTO);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getInvestorId() {
        return this.investorId;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public String getInvestorName() {
        return this.investorName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void buyStock(BuyTransaction buyTransaction, ExchangeRateConverter exchangeRateConverter) {
        BigDecimal transactionTotal = buyTransaction.calculateTransactionPriceTotal(exchangeRateConverter);
        this.wallet.subtractCredits(buyTransaction.getCurrency(), transactionTotal);
        this.addTransaction(buyTransaction);
    }

    public void sellStock(SellTransaction sellTransaction, ExchangeRateConverter exchangeRateConverter) {
        BigDecimal totalTransactionGain = sellTransaction.calculateTotalTransactionGain(exchangeRateConverter);

        BuyTransaction buyTransaction = this.findBuyTransactionRelatedToSellTransaction(sellTransaction);

        sellTransaction.verifyTransactionValidity(buyTransaction.getStock(), buyTransaction.getQuantity());
        buyTransaction.deductTransactionStockQuantity(sellTransaction.getQuantity());

        this.wallet.addCredits(sellTransaction.getCurrency(), totalTransactionGain);
        this.addTransaction(sellTransaction);
    }

    public Transaction getTransactionWithTransactionNumber(String transactionNumber) {
        for (Transaction transaction : this.transactions) {
            if (transaction.getTransactionNumber().equals(transactionNumber)) {
                return transaction;
            }
        }
        throw new TransactionNotFoundException(transactionNumber);
    }

    private BuyTransaction findBuyTransactionRelatedToSellTransaction(SellTransaction sellTransaction) {
        try {
            Transaction transaction = this.getTransactionWithTransactionNumber(sellTransaction.getBuyTransactionNumber());
            if (transaction instanceof BuyTransaction) {
                return (BuyTransaction) transaction;
            } else {
                throw new InvalidTransactionNumberException(transaction.getTransactionNumber());
            }
        } catch (TransactionNotFoundException e) {
            throw new InvalidTransactionNumberException(sellTransaction.getBuyTransactionNumber());
        }
    }

    public void calculateAndSetTotalCreditsInCAD(ExchangeRateConverter exchangeRateCalculator) {
        this.wallet.calculateAndSetTotalCreditsInCAD(exchangeRateCalculator);
    }
}

package ca.ulaval.glo4002.trading.domain.persistence;

import ca.ulaval.glo4002.trading.domain.account.Account;

public interface AccountRepository {

    void persist(Account account);

    Account findAccountWithInvestorId(long investorId);

    boolean accountAlreadyExists(long investorId);

    Account findAccountWithAccountNumber(String accountNumber);
}

package ca.ulaval.glo4002.trading.domain.account;

import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;

import java.util.UUID;


public class AccountNumber {

    public static String getNewAccountNumber(String name) {
        String[] splitName = splitFullNameInTwo(name);
        String firstName = splitName[0];
        String lastName = splitName[1];
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return String.format("%s%s-%s", firstName.charAt(0), lastName.charAt(0), uuid);
    }

    private static String[] splitFullNameInTwo(String fullName) {
        String cleanName = fullName.trim();
        String[] splitName = cleanName.split(" ");

        if (splitName.length != 2) {
            throw new AccountInvalidDetailsException();
        }

        return splitName;
    }
}

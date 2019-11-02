package ca.ulaval.glo4002.trading.domain.account;

import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AccountNumberTest {

    private final static String AN_EMPTY_NAME = "";
    private final static String A_ONE_WORD_NAME = "FirstName";
    private final static String A_TWO_WORDS_NAME = "FirstName LastName";
    private final static String A_THREE_WORDS_NAME = "FirstName LastName Nickname";

    @Test(expected = AccountInvalidDetailsException.class)
    public void givenEmptyName_whenCreatingAccountNumber_thenAccountNumberIsInvalid() {
        AccountNumber.getNewAccountNumber(AN_EMPTY_NAME);
    }

    @Test(expected = AccountInvalidDetailsException.class)
    public void givenNameWithLessThanTwoWords_whenCreatingAccountNumber_thenAccountNumberIsInvalid() {
        AccountNumber.getNewAccountNumber(A_ONE_WORD_NAME);
    }

    @Test(expected = AccountInvalidDetailsException.class)
    public void givenNameWithMoreThanTwoWords_whenCreatingAccountNumber_thenAccountNumberIsInvalid() {
        AccountNumber.getNewAccountNumber(A_THREE_WORDS_NAME);
    }

    @Test
    public void givenATwoWordNameAndItsInitials_whenGeneratingAccountNumber_thenTheAccountNumberPrefixIsTheInitialsOfTheInvestorName() {
        String expectedInitialsAccountNumber = "FL";

        String twoFirstCharAccountNumber = AccountNumber.getNewAccountNumber(A_TWO_WORDS_NAME)
                .substring(0, 2);

        assertEquals(expectedInitialsAccountNumber, twoFirstCharAccountNumber);
    }
}
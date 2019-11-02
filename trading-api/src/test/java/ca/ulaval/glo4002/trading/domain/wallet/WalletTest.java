package ca.ulaval.glo4002.trading.domain.wallet;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import ca.ulaval.glo4002.trading.domain.exceptions.NotEnoughCreditsException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WalletTest {

    private final static int A_POSITIVE_NUMBER = 10;
    private final static Currency USD_CURRENCY = Currency.getInstance("USD");
    private final static String CAD_CURRENCY_SYMBOL = "CAD";
    private final static String USD_CURRENCY_SYMBOL = "USD";
    private final static float CREDIT_AMOUNT = 100F;
    private static float TWICE_THE_CREDIT_AMOUNT;
    private BigDecimal A_POSITIVE_AMOUNT;
    private BigDecimal HALF_THE_POSITIVE_AMOUNT;
    private Wallet aWalletWithPositiveAmountOfCredits;

    private ExchangeRateConverter exchangeRateConverterMock;

    private List<CreditDTO> credits;
    private CreditDTO creditDTOinCAD;
    private CreditDTO creditDTOinUSD;

    @Before
    public void setUp() {
        setUpCreditsDTO();
        exchangeRateConverterMock = mock(ExchangeRateConverter.class);
        TWICE_THE_CREDIT_AMOUNT = CREDIT_AMOUNT * 2;
        A_POSITIVE_AMOUNT = new BigDecimal(A_POSITIVE_NUMBER);
        HALF_THE_POSITIVE_AMOUNT = A_POSITIVE_AMOUNT.divide(new BigDecimal(2));
        aWalletWithPositiveAmountOfCredits = new Wallet(credits);
    }

    @Test
    public void givenAnAmountAndACurrency_whenIAddTheAmountToTheWallet_thenTheAmountIsAddedToTheCredits() {
        BigDecimal initialAmount = aWalletWithPositiveAmountOfCredits.getCredits().get(USD_CURRENCY);
        BigDecimal expectedAmount = initialAmount.add(A_POSITIVE_AMOUNT);

        aWalletWithPositiveAmountOfCredits.addCredits(USD_CURRENCY, A_POSITIVE_AMOUNT);

        BigDecimal returnedAmount = aWalletWithPositiveAmountOfCredits.getCredits().get(USD_CURRENCY);
        assertEquals(expectedAmount, returnedAmount);
    }

    @Test
    public void givenAnAmountAndACurrency_whenISubtractTheAmountToTheWallet_thenTheAmountISubtractedToTheCredits() {
        BigDecimal initialAmount = aWalletWithPositiveAmountOfCredits.getCredits().get(USD_CURRENCY);
        BigDecimal expectedAmount = initialAmount.subtract(HALF_THE_POSITIVE_AMOUNT);

        aWalletWithPositiveAmountOfCredits.subtractCredits(USD_CURRENCY, HALF_THE_POSITIVE_AMOUNT);

        BigDecimal returnedAmount = aWalletWithPositiveAmountOfCredits.getCredits().get(USD_CURRENCY);
        assertEquals(expectedAmount, returnedAmount);
    }

    @Test(expected = NotEnoughCreditsException.class)
    public void givenAnAmountAndACurrency_whenISubtractAnAmountBiggerThanTheWalletsCreditsForThatCurrency_thenTheModificationIsBlocked() {
        aWalletWithPositiveAmountOfCredits.subtractCredits(USD_CURRENCY, new BigDecimal(TWICE_THE_CREDIT_AMOUNT));
    }

    @Test(expected = AccountInvalidDetailsException.class)
    public void givenAListOfCreditDTOsWithAnInvalidCurrencySymbol_whenCreatingAWallet_thenTheCreationFails() {
        CreditDTO invalidCreditDTO = new CreditDTO();
        invalidCreditDTO.currency = "invalid currency symbol";
        invalidCreditDTO.amount = new BigDecimal(CREDIT_AMOUNT);
        credits.add(invalidCreditDTO);

        new Wallet(credits);
    }

    @Test(expected = AccountInvalidCreditsException.class)
    public void givenAListOfCreditDTOsWithCreditAmountLowerOrEqualToZero_whenCreatingAWallet_thenCreationFails() {
        CreditDTO invalidCreditDTO = new CreditDTO();
        invalidCreditDTO.currency = CAD_CURRENCY_SYMBOL;
        invalidCreditDTO.amount = new BigDecimal(0);
        credits.add(invalidCreditDTO);

        new Wallet(credits);
    }

    @Test
    public void givenAWalletAndAnExchangeRateConverter_whenCalculatingTotalWalletValue_thenItIsProperlyConvertedInCAD() {
        int conversionFactor = 2;
        when(exchangeRateConverterMock.toCAD(any(), any()))
                .thenReturn(BigDecimal.valueOf(CREDIT_AMOUNT * conversionFactor));

        BigDecimal expectedTotalInCAD = BigDecimal.valueOf(CREDIT_AMOUNT * conversionFactor * credits.size());

        aWalletWithPositiveAmountOfCredits.calculateAndSetTotalCreditsInCAD(exchangeRateConverterMock);

        assertEquals(expectedTotalInCAD.setScale(2, BigDecimal.ROUND_HALF_UP),
                aWalletWithPositiveAmountOfCredits.getTotalCreditsInCAD().setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    private void setUpCreditsDTO() {
        credits = new ArrayList<>();
        creditDTOinCAD = new CreditDTO();
        creditDTOinCAD.currency = CAD_CURRENCY_SYMBOL;
        creditDTOinCAD.amount = new BigDecimal(CREDIT_AMOUNT);
        creditDTOinUSD = new CreditDTO();
        creditDTOinUSD.currency = USD_CURRENCY_SYMBOL;
        creditDTOinUSD.amount = new BigDecimal(CREDIT_AMOUNT);

        credits.add(creditDTOinCAD);
        credits.add(creditDTOinUSD);
    }

}

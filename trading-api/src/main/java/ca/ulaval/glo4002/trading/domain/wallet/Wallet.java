package ca.ulaval.glo4002.trading.domain.wallet;

import ca.ulaval.glo4002.trading.application.DTO.CreditDTO;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;
import ca.ulaval.glo4002.trading.domain.exceptions.NotEnoughCreditsException;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class Wallet {

    @ElementCollection
    @Column(columnDefinition = "DECIMAL(100,20)")
    private Map<Currency, BigDecimal> credits;

    @Column(columnDefinition = "DECIMAL(100,20)")
    private BigDecimal totalCreditsInCAD;

    public Wallet() {
        //for hibernate
    }

    public Wallet(List<CreditDTO> creditDTOS) {
        credits = new HashMap<>();

        for (CreditDTO credit : creditDTOS) {
            try {
                //IllegalArgumentException is thrown if the currency symbol doesnt exist
                Currency currency = Currency.getInstance(credit.currency);
                credits.put(currency, credit.amount);

            } catch (IllegalArgumentException e) { //TODO Faire un ExceptionMapper si je trouve le temps
                throw new AccountInvalidDetailsException();
            }
        }
        verifyCreditsValidity();
    }

    public Map<Currency, BigDecimal> getCredits() {
        return credits;
    }

    public BigDecimal getTotalCreditsInCAD() {
        return totalCreditsInCAD;
    }

    public void setTotalCreditsInCAD(BigDecimal totalCreditsInCAD) {
        this.totalCreditsInCAD = totalCreditsInCAD;
    }

    public void addCredits(Currency currency, BigDecimal amount) {
        BigDecimal currentAmount = credits.get(currency);
        if (currentAmount == null) {
            credits.put(currency, amount);
        } else {
            BigDecimal updatedAmount = currentAmount.add(amount);
            credits.put(currency, updatedAmount);
        }
    }

    public void subtractCredits(Currency currency, BigDecimal amount) {
        verifyFundsAvailability(currency, amount);

        BigDecimal currentAmount = credits.get(currency);

        BigDecimal updatedAmount = currentAmount.subtract(amount);
        credits.put(currency, updatedAmount);
    }

    private void verifyFundsAvailability(Currency currency, BigDecimal transactionTotal) {
        if (this.credits.get(currency).compareTo(transactionTotal) < 0) {
            throw new NotEnoughCreditsException();
        }
    }

    private void verifyCreditsValidity() {
        for (Map.Entry<Currency, BigDecimal> pair : credits.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AccountInvalidCreditsException();
            }
        }
    }

    public void calculateAndSetTotalCreditsInCAD(ExchangeRateConverter exchangeRateCalculator) {
        totalCreditsInCAD = BigDecimal.ZERO;
        for (Map.Entry<Currency, BigDecimal> pair : credits.entrySet()) {
            BigDecimal creditInCAD = exchangeRateCalculator.toCAD(pair.getKey(), pair.getValue());
            totalCreditsInCAD = totalCreditsInCAD.add(creditInCAD);
        }
    }

}

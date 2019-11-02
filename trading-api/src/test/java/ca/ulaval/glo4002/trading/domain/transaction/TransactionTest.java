package ca.ulaval.glo4002.trading.domain.transaction;

import ca.ulaval.glo4002.trading.application.service.exceptions.InvalidQuantityException;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

    private final static Long A_QUANTITY_OVER_100 = 101L;
    private final static Long A_QUANTITY_UNDER_100 = 99L;
    private final static Long A_QUANTITY_OF_100 = 100L;
    private final static BigDecimal A_PRICE = new BigDecimal(1);
    private final static BigDecimal A_PRICE_OVER_5000 = new BigDecimal(5001);
    private final static ZonedDateTime A_DATE = ZonedDateTime.now();
    private final static Stock A_STOCK = new Stock("anyMarket", "anySymbol");
    private final static BigDecimal PERCENTAGE_FEES_FOR_TRANSACTION_PRICE_MOREL_THAN_5000 = new BigDecimal(0.03F);
    private final static BigDecimal FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100 = new BigDecimal(0.25F);
    private final static BigDecimal FEES_FOR_TRANSACTION_QUANTITY_MORE_THAN_100 = new BigDecimal(0.20F);

    @Test
    public void givenATransactionWithQuantityOver100_whenAddingFees_thenFeesAreCalculatedCorrectly() {
        Transaction transactionWithQuantityOver100 = new TransactionTester(A_DATE, A_STOCK, A_QUANTITY_OVER_100);
        transactionWithQuantityOver100.setPrice(A_PRICE);
        BigDecimal expectedFeesForTransactionQuantityOver100 = FEES_FOR_TRANSACTION_QUANTITY_MORE_THAN_100
                .multiply(new BigDecimal(A_QUANTITY_OVER_100));

        transactionWithQuantityOver100.calculateFees();

        assertEquals(expectedFeesForTransactionQuantityOver100, transactionWithQuantityOver100.getFees());
    }

    @Test
    public void givenATransactionWithAQuantityOf100_whenAddingFees_thenFeesCalculatedCorrectly() {
        Transaction transactionWithQuantityOf100 = new TransactionTester(A_DATE, A_STOCK, A_QUANTITY_OF_100);
        transactionWithQuantityOf100.setPrice(A_PRICE);
        BigDecimal expectedFeesForTransactionQuantityOf100 = FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100
                .multiply(new BigDecimal(A_QUANTITY_OF_100));

        transactionWithQuantityOf100.calculateFees();

        assertEquals(expectedFeesForTransactionQuantityOf100, transactionWithQuantityOf100.getFees());
    }

    @Test
    public void givenATransactionWithQuantityUnder100_whenAddingFees_thenFeesCalculatedCorrectly() {
        Transaction transactionWithQuantityUnder100 = new TransactionTester(A_DATE, A_STOCK, A_QUANTITY_UNDER_100);
        transactionWithQuantityUnder100.setPrice(A_PRICE);
        BigDecimal expectedFeesForTransactionQuantityUnder100 = FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100
                .multiply(new BigDecimal(A_QUANTITY_UNDER_100));

        transactionWithQuantityUnder100.calculateFees();

        assertEquals(expectedFeesForTransactionQuantityUnder100, transactionWithQuantityUnder100.getFees());
    }

    @Test
    public void givenATransactionWithPriceOver5000_whenAddingFees_thenFeesCalculatedCorrectly() {
        Transaction transactionWithPriceOver5000 = new TransactionTester(A_DATE, A_STOCK, A_QUANTITY_UNDER_100);
        transactionWithPriceOver5000.setPrice(A_PRICE_OVER_5000);
        BigDecimal feesForQuantity = FEES_FOR_TRANSACTION_QUANTITY_LESS_OR_EQUAL_THAN_100.multiply(new BigDecimal(A_QUANTITY_UNDER_100));
        BigDecimal priceMultipliedByQuantity = A_PRICE_OVER_5000.multiply(new BigDecimal(A_QUANTITY_UNDER_100));
        BigDecimal expectedTotalFeesForTransactionPriceOver5000 = feesForQuantity
                .add((PERCENTAGE_FEES_FOR_TRANSACTION_PRICE_MOREL_THAN_5000.multiply(priceMultipliedByQuantity)));

        transactionWithPriceOver5000.calculateFees();

        assertEquals(expectedTotalFeesForTransactionPriceOver5000, transactionWithPriceOver5000.getFees());
    }

    @Test(expected = InvalidQuantityException.class)
    public void givenAQuantityLessOrEqualToZero_whenCreatingATransaction_thenQuantityIsInvalid() {
        Long aQuantityLessOrEqualToZero = 0L;

        new TransactionTester(A_DATE, A_STOCK, aQuantityLessOrEqualToZero);
    }

    private class TransactionTester extends Transaction {
        public TransactionTester(ZonedDateTime date, Stock stock, Long quantity) {
            super(date, stock, quantity);
        }
    }
}
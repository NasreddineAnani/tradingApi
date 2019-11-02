package ca.ulaval.glo4002.trading.domain.market;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MarketTest {

    private final static String A_MARKET_NAME = "aSymbol";
    private final static String[] VALID_OPEN_HOURS = {"09:00-13:30", "14:00-20:00"};
    private final static String A_TIMEZONE = "UTC";
    private final static String A_WEEKEND_DATE_TIME = "2018-11-17T10:00:00.00Z";
    private final static String A_DATE_TIME_OUT_OF_OPEN_HOURS = "2018-11-16T08:00:00.00Z";
    private final static String A_DATE_TIME_INT_OPEN_HOURS = "2018-11-16T10:00:00.00Z";

    private Market aMarketWithValidInformation;

    @Before
    public void setUp() {
        aMarketWithValidInformation = new Market(A_MARKET_NAME, VALID_OPEN_HOURS, A_TIMEZONE);
    }

    @Test
    public void givenDateWithDayOfWeekEnd_whenCheckingIfMarketIsOpen_thenMarketIsClosed() {
        ZonedDateTime dateWithDayOfWeekEnd = ZonedDateTime.parse(A_WEEKEND_DATE_TIME);

        boolean marketIsOpen = aMarketWithValidInformation.isOpen(dateWithDayOfWeekEnd);

        assertFalse(marketIsOpen);
    }

    @Test
    public void givenATimeOutOfOpenHours_whenCheckingIfMarketIsOpen_thenMarketIsClosed() {
        ZonedDateTime dateTimeOutOfOpenHours = ZonedDateTime.parse(A_DATE_TIME_OUT_OF_OPEN_HOURS);

        boolean marketIsOpen = aMarketWithValidInformation.isOpen(dateTimeOutOfOpenHours);

        assertFalse(marketIsOpen);
    }

    @Test
    public void givenTimeInOpenHours_whenCheckingIfMarketIsOpen_thenItIsOpen() {
        ZonedDateTime dateTimeInOpenHours = ZonedDateTime.parse(A_DATE_TIME_INT_OPEN_HOURS);

        boolean marketIsOpen = aMarketWithValidInformation.isOpen(dateTimeInOpenHours);

        assertTrue(marketIsOpen);
    }
}

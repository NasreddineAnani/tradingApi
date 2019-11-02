package ca.ulaval.glo4002.trading.domain.market;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class Market {

    private String symbol;
    private String[] openHours;
    private String timezone;

    public Market() {
        //for jackson
    }

    public Market(String symbol, String[] openHours, String timezone) {
        this.symbol = symbol;
        this.openHours = openHours;
        this.timezone = timezone;
    }

    public String getSymbol() {
        return symbol;
    }

    public String[] getOpenHours() {
        return openHours;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isOpen(ZonedDateTime transactionZonedDateTime) {
        transactionZonedDateTime = transactionZonedDateTime.withZoneSameInstant(ZoneId.of(timezone));
        return this.isInOpenDays(transactionZonedDateTime) && this.isInOpenHours(transactionZonedDateTime);
    }

    private boolean isInOpenHours(ZonedDateTime transactionZonedDateTime) {
        LocalTime transactionLocalTime = transactionZonedDateTime.toLocalTime();
        for (String openHours : getOpenHours()) {
            if (isInOpenDays(transactionLocalTime, openHours)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInOpenDays(LocalTime transactionLocalTime, String openHours) {
        String[] hours = openHours.split("-");
        LocalTime openingHour = LocalTime.parse(formatHour(hours[0]));
        LocalTime closingHour = LocalTime.parse(formatHour(hours[1]));
        return transactionLocalTime.isAfter(openingHour) && transactionLocalTime.isBefore(closingHour);
    }

    private boolean isInOpenDays(ZonedDateTime transactionZonedDateTime) {
        return !transactionZonedDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                && !transactionZonedDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }

    private String formatHour(String hour) {
        if (hour.matches("\\d:\\d\\d")) {
            return "0" + hour;
        }
        return hour;
    }
}
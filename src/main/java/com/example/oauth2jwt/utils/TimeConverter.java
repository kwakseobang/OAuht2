package com.example.oauth2jwt.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TimeConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm").withLocale(Locale.KOREAN);

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy. M. d.").withLocale(Locale.KOREAN);

    private TimeConverter() {
    }

    // ============= 날짜, 시간까지 있는 경우 ==========
    public static String datetimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime stringToDateTime(
        String strTime) {  // 현재 미사용 메소드이나, 차후 활용가능성을 위해 작성해두었음.
        return LocalDateTime.parse(strTime, DATE_TIME_FORMATTER);
    }

    // ============= 날짜만 있는 경우 =============
    public static String dateToString(LocalDate localDate) {
        return localDate.format(DATE_FORMATTER);
    }

    public static LocalDate stringToDate(String strTime) {
        return LocalDate.parse(strTime, DATE_FORMATTER);
    }

}
package com.example.oauth2jwt.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm").withLocale(Locale.KOREAN);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy. M. d.").withLocale(Locale.KOREAN);

    // ============= 날짜, 시간까지 있는 경우 ==========
    public static String DatetimeToString(LocalDateTime localDateTime) {
        String strTime = localDateTime.format(DATE_TIME_FORMATTER);
        return strTime;
    }


    public static LocalDateTime stringToDateTime(String strTime) {  // 현재 미사용 메소드이나, 차후 활용가능성을 위해 작성해두었음.
        LocalDateTime localDateTime = LocalDateTime.parse(strTime, DATE_TIME_FORMATTER);
        return localDateTime;
    }

    // ============= 날짜만 있는 경우 =============
    public static String DateToString(LocalDate localDate) {
        String strTime = localDate.format(DATE_FORMATTER);
        return strTime;
    }


    public static LocalDate stringToDate(String strTime) {
        LocalDate localDate = LocalDate.parse(strTime, DATE_FORMATTER);
        return localDate;
    }
}

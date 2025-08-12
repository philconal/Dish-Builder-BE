package com.conal.dishbuilder.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeUtils {

    // Common date/time patterns
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME = "HH:mm:ss";

    // Convert Date -> LocalDateTime (using default ZoneId)
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // Convert LocalDateTime -> Date
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    // Format LocalDateTime to String
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    // Parse String to LocalDateTime
    public static LocalDateTime parseToLocalDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    // Get current date
    public static LocalDate nowDate() {
        return LocalDate.now();
    }

    // Get current date-time
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    // Calculate difference between two LocalDateTime in given ChronoUnit (days, hours, minutes...)
    public static long between(LocalDateTime from, LocalDateTime to, ChronoUnit unit) {
        if (from == null || to == null) return 0;
        return unit.between(from, to);
    }

    // Add amount of time to LocalDateTime
    public static LocalDateTime add(LocalDateTime dateTime, long amountToAdd, ChronoUnit unit) {
        if (dateTime == null) return null;
        return dateTime.plus(amountToAdd, unit);
    }

    // Subtract amount of time from LocalDateTime
    public static LocalDateTime subtract(LocalDateTime dateTime, long amountToSubtract, ChronoUnit unit) {
        if (dateTime == null) return null;
        return dateTime.minus(amountToSubtract, unit);
    }

    // Check if two LocalDateTime have the same date
    public static boolean isSameDate(LocalDateTime dt1, LocalDateTime dt2) {
        if (dt1 == null || dt2 == null) return false;
        return dt1.toLocalDate().isEqual(dt2.toLocalDate());
    }

    // Convert String date/time from one pattern to another
    public static String convertFormat(String dateTimeStr, String fromPattern, String toPattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        LocalDateTime dt = parseToLocalDateTime(dateTimeStr, fromPattern);
        return format(dt, toPattern);
    }
}

package ru.practicum.statsserver.stats.util;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
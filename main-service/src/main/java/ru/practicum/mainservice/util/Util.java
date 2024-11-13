package ru.practicum.mainservice.util;


import java.time.format.DateTimeFormatter;

public class Util {
    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_ZONE_UTC = " UTC";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static String INCOR_REQ = "Incorrectly made request.";

    public static String compilationNotFound(Long compId) {
        return String.format("Compilation with id=%d not found", compId);
    }

    public static String eventNotFound(Long eventId) {
        return String.format("Event with id=%d not found", eventId);
    }

    public static String userNotFound(Long userId) {
        return String.format("User with id=%d not found", userId);
    }
}
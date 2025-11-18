package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeMapper {
    private static final java.time.format.DateTimeFormatter FORMATTER =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String mapToString(ZonedDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(FORMATTER);
    }

    public static ZonedDateTime mapToZonedDateTime(String time) {
        if (time == null || time.isBlank()) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(time, FORMATTER);
        return localDateTime.atZone(ZoneOffset.UTC);
    }
}

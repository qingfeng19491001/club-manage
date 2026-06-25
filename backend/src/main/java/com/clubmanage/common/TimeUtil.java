package com.clubmanage.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TimeUtil {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private TimeUtil() {}

    public static String now() {
        return LocalDateTime.now().format(FMT);
    }

    public static String format(LocalDateTime dt) {
        return dt == null ? null : dt.format(FMT);
    }

    public static LocalDateTime parse(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(s.trim(), FMT);
    }
}

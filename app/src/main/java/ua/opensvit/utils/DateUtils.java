package ua.opensvit.utils;

import java.util.TimeZone;

public class DateUtils {
    private DateUtils() {
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getTimeZone("GMT");
    }
}

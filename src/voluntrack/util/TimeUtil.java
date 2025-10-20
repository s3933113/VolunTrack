package voluntrack.util;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Utility for ISO8601 timestamp generation.
 */
public final class TimeUtil {
    private TimeUtil() {}


    public static String nowIso() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


    public static String nowPretty() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
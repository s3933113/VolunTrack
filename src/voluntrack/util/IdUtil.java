package voluntrack.util;

/**
 * Utility for generating zero-padded identifiers.
 */
public final class IdUtil {
    private IdUtil() {}

    public static String zeroPad4(int number) {
        return String.format("%04d", number);
    }

    public static String zeroPad6(int number) {
        return String.format("%06d", number);
    }
}
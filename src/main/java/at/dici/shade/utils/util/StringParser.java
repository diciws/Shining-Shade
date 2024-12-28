package at.dici.shade.utils.util;

public class StringParser {

    /**
     * Similar to Integer::parseInt but returns null instead of throwing an exception.
     * @param str The String to parse
     * @return The parsed value or null if the string does not contain a parsable integer.
     */
    public static Integer parseInteger(String str) {
        int value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
        return value;
    }

    /**
     * Similar to Long::parseLong but returns null instead of throwing an exception.
     * @param str The String to parse
     * @return The parsed value or null if the string does not contain a parsable long.
     */
    public static Long parseLong(String str) {
        long value;
        try {
            value = Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
        return value;
    }
}

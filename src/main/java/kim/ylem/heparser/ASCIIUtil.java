package kim.ylem.heparser;

public final class ASCIIUtil {
    private static final long WHITESPACE = 1L << 0x0009 | 1L << 0x000A | 1L << 0x000D | 1L << 0x0020;

    public static final int UPPER_LOWER_OFFSET = 32;

    private ASCIIUtil() {
    }

    public static boolean isAlphabet(char c) {
        return (c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a');
    }

    public static boolean isUpperCase(char c) {
        return c <= 'Z' && c >= 'A';
    }

    public static char toLowerCase(char c) {
        return isUpperCase(c) ? (char) (c + UPPER_LOWER_OFFSET) : c;
    }

    public static boolean isWhitespace(char c) {
        return c <= 0x0020 && (WHITESPACE >> c & 1L) != 0;
    }

    public static int getStyle(char first, char second) {
        if (isUpperCase(first)) {
            return isUpperCase(second) ? 2 : 1;
        }
        return 0;
    }
}

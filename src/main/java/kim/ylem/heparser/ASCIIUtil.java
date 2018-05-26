package kim.ylem.heparser;

public final class ASCIIUtil {
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

    public static int getStyle(char first, char second) {
        if (isUpperCase(first)) {
            return isUpperCase(second) ? 2 : 1;
        }
        return 0;
    }
}

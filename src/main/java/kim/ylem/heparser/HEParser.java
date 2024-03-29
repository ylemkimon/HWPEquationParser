package kim.ylem.heparser;

import kim.ylem.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The HEParser parses HWP(Hangul Document) equation.
 */
public final class HEParser {
    private static final Logger logger = LogManager.getLogger();

    private final String originalEquation;
    private final Deque<GroupParser> groupParserStack = new ArrayDeque<>();

    private String equation;
    private int pos = -1;

    private HEParser(String s) {
        originalEquation = s;
        equation = '{' + s + '}';
    }

    public static String parseToLaTeX(String s) {
        return '$' + new HEParser(s).parse() + '$';
    }

    @Contract(pure = true)
    private static @NotNull String searchCamel(String sub, char[] search, int len) {
        search[0] -= ASCIIUtil.UPPER_LOWER_OFFSET;
        String camel = new String(search, 0, len);
        return AtomMap.containsKey(camel) ? camel : sub;
    }

    @Contract(pure = true)
    public ParserException newUnexpectedException(String expected, String actual) {
        return new ParserException("Expected " + expected + " at " + pos + ", but got " + actual + " instead");
    }

    public @NotNull String parse() {
        String result = originalEquation;
        try {
            while (pos < equation.length() - 1) {
                if (pos > -1) {
                    logger.warn("Expected EOF, appending { to the left");
                    equation = '{' + equation;
                }
                pos = -1;

                ThreadContext.put("equation", equation);
                ThreadContext.put("pos", Integer.toString(pos));

                result = parseMatrix("eqalign").toString();
            }
        } catch (ParserException e) {
            logger.error("A ParserException occurred", e);
        }

        logger.trace("Result: {}", result);

        return result;
    }

    @Contract(pure = true)
    public char next() {
        return pos + 1 < equation.length() ? equation.charAt(pos + 1) : '\0';
    }

    public void expect(char expected, String name, boolean consume) throws ParserException {
        search(""); // skip whitespaces
        char c = next();
        if (c != expected) {
            throw newUnexpectedException(name + ", " + expected, Character.toString(c));
        }
        if (consume) {
            consume(null, 1);
        }
    }

    @Contract(pure = true)
    @NotNull
    Token nextToken(boolean forceSymbol) {
        char c = next();
        if (c <= '9' && c >= '0') {
            return new Token(Character.toString(c), false);
        } else if (!ASCIIUtil.isAlphabet(c)) {
            return searchNonAlphabetic(forceSymbol);
        }
        Token special = searchSpecial();
        return special != null ? special : searchAlphabetic();
    }

    @Contract(pure = true)
    private Token searchNonAlphabetic(boolean forceSymbol) {
        int remaining = equation.length() - pos - 2;
        for (int i = Math.min(remaining, 3); i > 0; i--) {
            String sub = equation.substring(pos + 1, pos + i + 1);
            if (AtomMap.containsKey(sub)) {
                if ("<-".equals(sub) && remaining > 2) {
                    char ch = equation.charAt(pos + 3);
                    if (ch > '=' && ch != '{') {
                        break;
                    }
                }
                return new Token(sub, true, forceSymbol);
            }
        }
        return new Token(Character.toString(next()), false);
    }

    @Contract(pure = true)
    private Token searchSpecial() {
        for (int i = 2; i <= 4 && pos + i + 1 < equation.length(); i++) {
            String sub = equation.substring(pos + 1, pos + i + 1);
            if (AtomMap.isSpecial(sub)) {
                return new Token(sub, true);
            }
        }
        return null;
    }

    @Contract(pure = true)
    private Token searchAlphabetic() {
        char c = next();
        int style = ASCIIUtil.getStyle(c, equation.charAt(pos + 2));
        char[] search = new char[10];
        search[0] = ASCIIUtil.toLowerCase(c);

        int len;
        int searchLen = 1;
        boolean match = true;
        for (len = 1; pos + len + 2 < equation.length(); len++) {
            char ch = equation.charAt(pos + len + 1);
            if (!ASCIIUtil.isAlphabet(ch)) {
                break;
            }

            match = match && ASCIIUtil.isUpperCase(ch) == (style == 2) && len < 10;
            if (match) {
                search[searchLen++] = ASCIIUtil.toLowerCase(ch);
            }
        }

        for (int i = searchLen; i > 1; i--) {
            String sub = new String(search, 0, i);
            if (AtomMap.containsKey(sub)) {
                if (style != 0) {
                    if (AtomMap.isSpecial(sub)) {
                        break;
                    }
                    sub = searchCamel(sub, search, i);
                }
                return new Token(sub, true);
            }
        }
        return new Token(equation.substring(pos + 1, pos + len + 1), false);
    }

    public boolean search(String... searchStrings) {
        if (pos >= equation.length() - 1) {
            return false;
        }
        int n = 1;
        while (ASCIIUtil.isWhitespace(equation.charAt(pos + n))) {
            n++;
        }

        for (String s : searchStrings) {
            if (equation.startsWith(s, pos + n)) {
                pos += n + s.length() - 1;
                ThreadContext.put("pos", Integer.toString(pos));
                return true;
            }
        }
        return false;
    }

    public void consume(Token token, int length) {
        if (length > 0) {
            pos += token == null || token.toString().length() > 1 ? length : token.getLength();
            ThreadContext.put("pos", Integer.toString(pos));
        }
    }

    public void skipToEnd() {
        pos = equation.length() - 2;
        ThreadContext.put("pos", Integer.toString(pos));
    }

    @Contract(pure = true)
    public @NotNull Options getCurrentOptions() {
        return groupParserStack.isEmpty() ? new Options() : getGroupParser().getOptions();
    }

    @Contract(pure = true)
    public @NotNull GroupParser getGroupParser() {
        return groupParserStack.getFirst();
    }

    public @NotNull Atom parseMatrix(String command) throws ParserException {
        GroupParser groupParser = new GroupParser(this, ParserMode.GROUP, getCurrentOptions());
        groupParserStack.push(groupParser);
        Atom matrix = groupParser.parseMatrix(command);
        groupParserStack.pop();
        return matrix;
    }

    public @Nullable Atom parseGroup(ParserMode mode) throws ParserException {
        return parseGroup(mode, getCurrentOptions());
    }

    public @Nullable // only when mode == ARGUMENT || mode == DELIMITER
    Atom parseGroup(ParserMode mode, Options options) throws ParserException {
        GroupParser groupParser = new GroupParser(this, mode, options);
        groupParserStack.push(groupParser);
        Atom group = groupParser.parse();
        groupParserStack.pop();
        return group;
    }
}

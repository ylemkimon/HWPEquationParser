package kim.ylem.heparser;

import kim.ylem.ParserException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HEParser implements Iterator<Character> {
    private static final int MAX_ATTEMPT = 50;

    private final String originalEquation;
    private final Deque<GroupParser> groupParserStack = new ArrayDeque<>();

    private String equation;
    private String warning = "";

    private int pos = -1;
    private int attempt;

    public HEParser(String s) {
        originalEquation = s;
        equation = '{' + s + '}';
    }

    public ParserException newUnexpectedException(String expected, String actual) {
        return new ParserException("Expected " + expected + " at " + pos + ", but got " + actual + " instead");
    }

    public String parse() {
        String result = "";
        try {
            while (hasNext()) {
                if (pos > -1) {
                    appendWarning("expected EOF, appending { to the left");
                    equation = '{' + equation;
                }
                pos = -1;
                result = parseMatrix("eqalign").toString();
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

        if (!warning.isEmpty()) {
            System.err.println("While parsing equation: " + originalEquation);
            System.err.println(warning.trim());
            System.err.println("Result: " + result);
        }
        return result;
    }

    public void appendWarning(String message) throws ParserException {
        warning += pos + ": " + message + '\n';
        if (++attempt > MAX_ATTEMPT) {
            throw new ParserException("Too many warnings, stopping to prevent infinite loops");
        }
    }

    @Override
    public boolean hasNext() {
        return pos < equation.length() - 1;
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Reached EOF");
        }
        return equation.charAt(++pos);
    }

    public void expect(char expected, String name, boolean consume) throws ParserException {
        skipWhitespaces();
        char c = consume ? next() : peek();
        if (c != expected) {
            throw newUnexpectedException(name + ", " + expected, Character.toString(c));
        }
    }

    Token nextToken(boolean forceSymbol) {
        char c = peek();
        if (c <= '9' && c >= '0') {
            return new Token(Character.toString(c), false);
        } else if (!ASCIIUtil.isAlphabet(c)) {
            return searchNonAlphabetic(forceSymbol);
        }
        Token special = searchSpecial();
        return special != null ? special : searchAlphabetic();
    }

    private Token searchNonAlphabetic(boolean forceSymbol) {
        int remaining = equation.length() - pos - 2;
        for (int i = Math.min(remaining, 3); i > 0; i--) {
            String sub = equation.substring(pos + 1, pos + i + 1);
            if (AtomMap.containsKey(sub)) {
                if ("<-".equals(sub) && remaining > 2) {
                    char ch = equation.charAt(pos + 3);
                    if (!(Character.isWhitespace(ch) || (ch >= '!' && ch <= '=') || ch == '{')) {
                        break;
                    }
                }
                return new Token(sub, true, forceSymbol);
            }
        }
        return new Token(peek().toString(), false);
    }

    private Token searchSpecial() {
        for (int i = 2; i <= 4 && pos + i + 1 < equation.length(); i++) {
            String sub = equation.substring(pos + 1, pos + i + 1);
            if (AtomMap.isSpecial(sub)) {
                return new Token(sub, true);
            }
        }
        return null;
    }

    private Token searchAlphabetic() {
        char c = peek();
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

    private static String searchCamel(String sub, char[] search, int len) {
        search[0] -= ASCIIUtil.UPPER_LOWER_OFFSET;
        String camel = new String(search, 0, len);
        return AtomMap.containsKey(camel) ? camel : sub;
    }

    public boolean search(String... searchStrings) {
        if (!hasNext()) {
            return false;
        }
        int n = 1;
        while (Character.isWhitespace(equation.charAt(pos + n))) {
            n++;
        }

        for (String s : searchStrings) {
            if (equation.startsWith(s, pos + n)) {
                pos += n + s.length() - 1;
                return true;
            }
        }
        return false;
    }

    public Character peek() {
        return pos + 1 < equation.length() ? equation.charAt(pos + 1) : '\0';
    }

    void consume(Token token, int length) {
        if (length > 0) {
            pos += token.toString().length() > 1 ? length : token.getLength();
        }
    }

    public void skipToEnd() {
        pos = equation.length() - 2;
    }

    public void skipWhitespaces() {
        while (Character.isWhitespace(peek())) {
            pos++;
        }
    }

    public Options getCurrentOptions() {
        return groupParserStack.isEmpty() ? new Options() : getGroupParser().getOptions();
    }

    public GroupParser getGroupParser() {
        return groupParserStack.peek();
    }

    public Atom parseMatrix(String command) throws ParserException {
        GroupParser groupParser = new GroupParser(this, ParserMode.GROUP, getCurrentOptions());
        groupParserStack.push(groupParser);
        Atom matrix = groupParser.parseMatrix(command);
        groupParserStack.pop();
        return matrix;
    }

    public Atom parseGroup(ParserMode mode) throws ParserException {
        return parseGroup(mode, getCurrentOptions());
    }

    public Atom parseGroup(ParserMode mode, Options options) throws ParserException {
        GroupParser groupParser = new GroupParser(this, mode, options);
        groupParserStack.push(groupParser);
        Atom group = groupParser.parse();
        groupParserStack.pop();
        return group;
    }
}

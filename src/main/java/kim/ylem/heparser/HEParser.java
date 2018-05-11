package kim.ylem.heparser;

import kim.ylem.ParserException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HEParser implements Iterator<Character> {
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
            do {
                if (pos > -1) {
                    appendWarning("expected EOF, appending { to the left");
                    equation = '{' + equation;
                }
                pos = -1;
                result = parseMatrix("eqalign").toString();
            } while (hasNext());
        } catch (ParserException e) {
            System.err.println("While parsing equation: " + originalEquation);
            e.printStackTrace();
            System.err.println(warning.trim());
            System.err.println("Result: " + result);
            return result;
        }

        if (!warning.isEmpty()) {
            System.err.println("While parsing equation: " + originalEquation);
            System.err.println(warning.trim());
            System.err.println("Result: " + result);
        }
        return result;
    }

    public void appendWarning(String warning) throws ParserException {
        this.warning += pos + ": " + warning + '\n';
        if (++attempt > 50) {
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

    public boolean search(String... searchStrings) {
        int n = 1;
        while (Character.isWhitespace(peek(n))) {
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
        return peek(1);
    }

    public Character peek(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Cannot peek backward");
        }
        return pos + n < equation.length() ? equation.charAt(pos + n) : '\0';
    }

    public void retreat(int n) {
        if (n < 0 || pos - n < -1) {
            throw new IllegalArgumentException("Cannot retreat " + (n < 0 ? "forward" : "beyond start"));
        }
        pos -= n;
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
        GroupParser groupParser = new GroupParser(this, ParserMode.IMPLICIT, getCurrentOptions());
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

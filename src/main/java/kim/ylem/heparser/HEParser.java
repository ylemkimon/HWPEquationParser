package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.GroupParser.Mode;
import kim.ylem.heparser.atoms.Group;
import kim.ylem.heparser.atoms.MatrixAtom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HEParser implements Iterator<Character> {
    private final String originalEquation;
    private final Deque<GroupParser> groupParserStack = new ArrayDeque<>();

    private String equation;
    private String warning = "";

    private int pos = -2;
    private int attempt = 0;

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
                if (pos >= -1) {
                    appendWarning("expected EOF, appending { to the left");
                    equation = '{' + equation;
                }
                pos = -1;
                result = MatrixAtom.parse(this, "eqalign").toString();
            } while (hasNext());

            if (!warning.isEmpty()) {
                throw new ParserException();
            }
        } catch (ParserException e) {
            System.err.println("While parsing equation: " + originalEquation);
            e.printStackTrace();
            System.err.println(warning.trim());
            System.err.println("Result: " + result);
        }
        return result;
    }

    public void appendWarning(String warning) throws ParserException {
        this.warning += pos + ": " + warning + '\n';
        if (++attempt > 50) {
            throw new ParserException("Too many warnings, stopping to prevent infinitive loops");
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

    public void skipWhitespaces() {
        while (Character.isWhitespace(peek())) {
            pos++;
        }
    }

    public Atom popGroup() {
        return groupParserStack.peek().pop();
    }

    private Group parseGroup(Mode mode, int maxLength, boolean onlySub) throws ParserException {
        GroupParser groupParser = new GroupParser(this, mode, maxLength, onlySub);
        groupParserStack.push(groupParser);
        Group group = groupParser.parse();
        groupParserStack.pop();
        return group;
    }

    // TODO: better name
    public Group parseImplicitGroup(String function) throws ParserException {
        return parseImplicitGroup(function, false);
    }

    public Group parseImplicitGroup(String function, boolean sub) throws ParserException {
        Mode mode = Mode.IMPLICIT;
        if (function != null) {
            if (function.startsWith("{")) {
                mode = Mode.EXPLICIT;
            } else if ("left".equalsIgnoreCase(function)) {
                mode = Mode.LEFT_RIGHT;
            } else if ("\\".equals(function)) {
                mode = Mode.ESCAPE;
            }
        }
        return parseGroup(mode, 9, sub);
    }

    public Group nextGroup() throws ParserException {
        return nextGroup(false);
    }

    public Group nextGroup(boolean sub) throws ParserException {
        skipWhitespaces();
        return peek() == '{' ? parseImplicitGroup(next().toString(), sub) : parseGroup(Mode.TERM, 9, sub);
    }

    public Group nextArgument(int maxLength) throws ParserException {
        return parseGroup(Mode.ARGUMENT, maxLength, false);
    }

    public Group nextSymbol() throws ParserException {
        return parseGroup(Mode.SYMBOL, 1, false);
    }

    public Group nextDelimiter(String side) throws ParserException {
        Group delim = parseGroup(Mode.DELIMITER, 1, false);
        if (delim != null && delim.isEmpty()) {
            appendWarning(side + " delimiter not found, using empty delimiter");
            delim = null;
        }
        return delim;
    }
}

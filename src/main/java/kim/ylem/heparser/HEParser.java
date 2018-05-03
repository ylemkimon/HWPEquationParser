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
    private final String equation;
    private final Deque<GroupParser> groupParserStack = new ArrayDeque<>();
    private String warning = "";

    private int pos = -1;

    public HEParser(String equation) {
        this.equation = '{' + equation + '}';
    }

    public ParserException newUnexpectedException(String expected, String actual) {
        return new ParserException("While parsing equation: " + equation +
                "\nExpected " + expected + " at " + pos + ", but got " + actual + " instead");
    }

    public String parse() throws ParserException {
        Atom root = MatrixAtom.parse(this, "eqalign");
        if (hasNext()) {
            // TODO: recovery
            throw newUnexpectedException("EOF", equation.substring(pos + 1));
        }
        String result = root.toString();
        if (!warning.isEmpty()) {
            System.err.println("While parsing equation: " + equation);
            System.err.println(warning.trim());
            System.err.println("Result: " + result);
        }
        return result;
    }

    public void appendWarning(String warning) {
        this.warning += pos + ": " + warning + '\n';
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

    private Group parseGroup(Mode mode, int maxLength) throws ParserException {
        GroupParser groupParser = new GroupParser(this, mode, maxLength);
        groupParserStack.push(groupParser);
        Group group =  groupParser.parse();
        groupParserStack.pop();
        return group;
    }

    // TODO: better name
    public Group parseGroups(String function) throws ParserException {
        Mode mode = "\\".equals(function) ? Mode.BACKSLASH : ("left".equalsIgnoreCase(function) ? Mode.LEFT_RIGHT : Mode.NORMAL);
        Group result = parseGroup(mode, 9);

        if (function != null && function.startsWith("{")) {
            char next = next();
            if (next != '}') {
                throw newUnexpectedException("an end of group, }", Character.toString(next));
            }
            result.parseSubSup(this, function.endsWith("sub"));
        }
        return result;
    }

    public Group nextGroup() throws ParserException {
        return nextGroup(false);
    }

    public Group nextGroup(boolean sub) throws ParserException {
        skipWhitespaces();
        return peek() == '{' ? parseGroups(next() + (sub ? "sub" : "")) :
                parseGroup(sub ? Mode.TERM_SUB : Mode.TERM, 9);
    }

    public Group nextArgument(int maxLength) throws ParserException {
        return parseGroup(Mode.ARGUMENT, maxLength);
    }

    public Group nextSymbol() throws ParserException {
        return parseGroup(Mode.SYMBOL, 1);
    }

    public Group nextDelimiter(String side) throws ParserException {
        Group delim = parseGroup(Mode.DELIMITER, 1);
        if (delim != null && delim.isEmpty()) {
            appendWarning(side + " delimiter not found, using empty delimiter");
            delim = null;
        }
        return delim;
    }
}

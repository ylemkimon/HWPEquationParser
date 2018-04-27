package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.GroupParser.Mode;
import kim.ylem.heparser.atoms.Group;
import kim.ylem.heparser.atoms.MatrixAtom;
import kim.ylem.heparser.atoms.SubSupAtom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HEParser implements Iterator<Character> {
    private final String equation;
    private final Deque<GroupParser> groupParserStack = new ArrayDeque<>();
    private String error = null;

    private int pos = -1;

    public HEParser(String equation) {
        this.equation = '{' + equation + '}';
    }

    public ParserException newUnexpectedException(String expected, String actual) {
        return new ParserException("While parsing equation: " + equation +
                "\nExpected " + expected + " at " + pos + ", but got " + actual + " instead");
    }

    public String parse() throws ParserException {
        Atom root = MatrixAtom.parse(this, "eqalign").simplify();
        if (hasNext()) {
            // TODO: recovery
            throw newUnexpectedException("EOF", equation.substring(pos + 1));
        }
        String result = root.toLaTeX(0);
        if (error != null) {
            System.err.println("While parsing equation: " + equation);
            System.err.println(error);
            System.err.println(result);
        }
        return result;
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

    // TODO: improve
    public String search(String... searchStrings) {
        int n = 1;
        while (Character.isWhitespace(peek(n))) {
            n++;
        }

        for (String s : searchStrings) {
            if (equation.startsWith(s, pos + n)) {
                pos += n + s.length() - 1;
                return s;
            }
        }
        return null;
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

    private Group parseGroup(Mode mode, int maxLength) throws ParserException {
        GroupParser groupParser = new GroupParser(this, mode, maxLength);
        groupParserStack.push(groupParser);
        Group group =  groupParser.parse();
        groupParserStack.pop();
        return group;
    }

    // TODO: better name
    public Group parseGroups(String function) throws ParserException {
        Group result = parseGroup(function != null && "left".equals(function.toLowerCase()) ? Mode.LEFTRIGHT : Mode.NORMAL, 9);
        if (function != null && function.startsWith("{")) {
            char next = next();
            if (next != '}') {
                throw newUnexpectedException("an end of group, }", Character.toString(next));
            }
            if (!function.endsWith("sub")) {
                SubSupAtom supsub = SubSupAtom.parse(this, false, false, true);
                result.push(supsub);
            }
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

    // TODO: improve, control character
    public Group nextSymbol(int maxLength) throws ParserException {
        return parseGroup(Mode.SYMBOL, maxLength);
    }

    // TODO:
    public Atom popGroup() {
        GroupParser current = groupParserStack.peek();
        return current != null ? current.pop() : null;
    }

    public boolean isEmpty() {
        GroupParser current = groupParserStack.peek();
        return current == null || current.isEmpty();
    }

    public void appendError(String error) {
        if (this.error == null) {
            this.error = "";
        }
        this.error += error + '\n';
    }
}

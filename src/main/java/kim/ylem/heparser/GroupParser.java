package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.atoms.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class GroupParser {
    private final HEParser parser;
    private final ParserMode originalMode;
    private final ParserMode mode;
    @SuppressWarnings("StringBufferField")
    private final StringBuilder textBuilder;

    private Options options;

    private Group group = new Group();
    private int leftRight;

    GroupParser(HEParser parser, ParserMode mode, Options options) {
        this.parser = parser;
        this.mode = mode.getParserMode() != null ? mode.getParserMode() : mode;
        originalMode = mode;
        textBuilder = new StringBuilder(mode.getMaxLength());

        this.options = options;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public void updateLeftRightDepth(LeftRightAtom.Side side) throws ParserException {
        if (side == LeftRightAtom.Side.left) {
            leftRight++;
        } else if (leftRight <= 0) {
            parser.appendWarning("unexpected right, adding left with null delimiter");
            group.addFirst(new LeftRightAtom(LeftRightAtom.Side.left, null));
            leftRight = 0;
        } else {
            leftRight--;
        }
    }

    private void checkResult() throws ParserException {
        while (leftRight > 0) {
            parser.appendWarning("right not found, adding right with null delimiter");
            group.push(new LeftRightAtom(LeftRightAtom.Side.right, null));
            leftRight--;
        }

        if (group.isEmpty()) {
            if (mode == ParserMode.SYMBOL || mode == ParserMode.TERM) {
                throw parser.newUnexpectedException(mode == ParserMode.TERM ? "a term" : "a symbol",
                        parser.peek().toString());
            } else if (mode != ParserMode.GROUP) {
                group = null;
            }
        }
    }

    public Atom popGroup() {
        return group.pop();
    }

    private boolean breakTerm() {
        if (textBuilder.length() > 0) {
            group.push(new TextAtom(textBuilder.toString(), options));
            textBuilder.setLength(0);
        }
        return mode != ParserMode.GROUP && (mode != ParserMode.TERM || !group.isEmpty());
    }

    private boolean appendText(Token token) {
        String text = token.toString();
        int maxLength = originalMode.getMaxLength();
        int length = text.length();
        int remaining = maxLength - textBuilder.length();

        if (textBuilder.length() > 0) {
            if ((textBuilder.charAt(0) == '~' && !"~".equals(text)) ||
                    (textBuilder.charAt(0) == '`' && !"`".equals(text))) {
                remaining = 0;
            }
        }

        if (length < remaining) {
            textBuilder.append(text);
        } else {
            textBuilder.append(text, 0, remaining);

            if (breakTerm()) {
                parser.consume(token, remaining);
                return false;
            }

            while (remaining + maxLength <= length) {
                group.push(new TextAtom(text.substring(remaining, remaining + maxLength), options));
                remaining += maxLength;
            }
            if (remaining < length) {
                textBuilder.append(text.substring(remaining));
            }
        }
        parser.consume(token, length);
        return true;
    }

    private Queue<Atom> parseRow() throws ParserException {
        Queue<Atom> row = new ArrayDeque<>();
        do {
            parser.next();
            row.add(parse());
            group = new Group();
        } while (parser.peek() == '&');
        return row;
    }

    Atom parseMatrix(String command) throws ParserException {
        parser.expect('{', "start of matrix", false);

        Queue<Queue<Atom>> rows = new ArrayDeque<>();
        int colCount = 0;
        do {
            Queue<Atom> row = parseRow();
            rows.add(row);
            if (row.size() > colCount) {
                colCount = row.size();
            }

            if ("eqalign".equals(command)) {
                options = options.withRomanFont(false);
            }
        } while (parser.peek() == '#');
        parser.expect('}', "end of matrix", true);

        if (rows.size() == 1 && colCount == 1 && MatrixAtom.hasNoDelimiters(command)) {
            return rows.remove().remove();
        }
        return new MatrixAtom(command, rows, colCount);
    }

    Atom parse() throws ParserException {
        char c = parser.peek();
        if (mode == ParserMode.ARGUMENT) {
            if (Character.isWhitespace(c) || c == '`' || c == '~') {
                return null;
            }
        } else {
            parser.skipWhitespaces();
        }

        //noinspection StatementWithEmptyBody
        while (parseNext());
        checkResult();

        if (group != null && (mode == ParserMode.ARGUMENT || mode == ParserMode.TERM)) {
            return ScriptAtom.parse(parser, group, originalMode.getScriptParseMode());
        }
        return group;
    }

    private boolean parseNext() throws ParserException {
        char c = parser.peek();
        if (!parser.hasNext()) {
            parser.appendWarning("unexpected EOF, assuming }");
            parser.skipToEnd();
            c = '}';
        } else if (isTermBreaker(c)) {
            if (breakTerm()) {
                return false;
            }
            parser.skipWhitespaces();
            c = parser.peek();
        }
        if (isGroupTerminator(c)) {
            breakTerm();
            return false;
        }

        Token token = parser.nextToken(mode == ParserMode.SYMBOL || mode == ParserMode.DELIMITER);
        if (mode == ParserMode.DELIMITER && !checkDelimiter(token.toString())) {
            return false;
        }

        AtomParser atomParser = token.getAtomParser();
        if (atomParser == null) {
            return appendText(token);
        } else if (breakTerm()) {
            return false;
        }
        parser.consume(token, token.getLength());
        group.push(atomParser.parse(parser, token.toString()));
        return !breakTerm();
    }

    private boolean checkDelimiter(String text) throws ParserException {
        if (SymbolMap.isDelimiter(text)) {
            return true;
        }
        if (!".".equals(text)) {
            parser.appendWarning("delimiter not found, using null delimiter");
        }
        return false;
    }

    private boolean isGroupTerminator(char c) {
        return (mode != ParserMode.DELIMITER && c == '}') ||
                (mode != ParserMode.SYMBOL && (c == '&' || c == '#'));
    }

    private boolean isTermBreaker(char c) {
        return Character.isWhitespace(c) ||
                (c == '`' && textBuilder.length() > 0 && textBuilder.charAt(0) != '`');
    }
}

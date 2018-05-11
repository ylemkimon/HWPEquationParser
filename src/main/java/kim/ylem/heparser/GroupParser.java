package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.atoms.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class GroupParser {
    private final HEParser parser;
    private final int maxLength;

    private final ScriptAtom.Mode scriptParseMode;
    private final StringBuilder textBuilder;

    private ParserMode mode;
    private Options options;

    private Group group = new Group();
    private int leftRight;

    GroupParser(HEParser parser, ParserMode mode, Options options) {
        this.parser = parser;
        this.mode = mode;
        this.options = options;

        switch (mode) {
            case EXPLICIT:
            case IMPLICIT:
            case TERM:
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 9;
                break;
            case SUB_TERM:
                this.mode = ParserMode.TERM;
                scriptParseMode = ScriptAtom.Mode.IN_SUB;
                maxLength = 9;
                break;
            case UNDEROVER_TERM:
                this.mode = ParserMode.TERM;
                scriptParseMode = ScriptAtom.Mode.UNDEROVER;
                maxLength = 9;
                break;
            case SYMBOL:
            case DELIMITER:
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 1;
                break;
            case ARGUMENT_1:
                this.mode = ParserMode.ARGUMENT;
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 1;
                break;
            case ARGUMENT_2:
                this.mode = ParserMode.ARGUMENT;
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 2;
                break;
            case ARGUMENT_3:
                this.mode = ParserMode.ARGUMENT;
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 3;
                break;
            case ARGUMENT_4:
                this.mode = ParserMode.ARGUMENT;
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 4;
                break;
            case ARGUMENT_5:
                this.mode = ParserMode.ARGUMENT;
                scriptParseMode = ScriptAtom.Mode.NORMAL;
                maxLength = 5;
                break;
            default:
                throw new IllegalArgumentException("Invalid mode!");
        }

        textBuilder = new StringBuilder(maxLength);
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public void leftRight(boolean left) throws ParserException {
        if (left) {
            leftRight++;
        } else if (leftRight <= 0) {
            leftRight = 0;
            parser.appendWarning("unexpected right, adding left with null delimiter");
            group.addFirst(new LeftRightAtom(true, null));
        } else {
            leftRight--;
        }
    }

    public Atom popGroup() {
        return group.pop();
    }

    private boolean isSingle() {
        return mode != ParserMode.IMPLICIT && mode != ParserMode.EXPLICIT;
    }

    private void buildTextAtom() {
        if (textBuilder.length() > 0) {
            group.push(new TextAtom(textBuilder.toString(), options));
            textBuilder.setLength(0);
        }
    }

    private boolean appendText(String text) {
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
            buildTextAtom();

            if (isSingle()) {
                parser.retreat(length - remaining);
                return true;
            }

            while (remaining + maxLength <= length) {
                group.push(new TextAtom(text.substring(remaining, remaining + maxLength), options));
                remaining += maxLength;
            }
            if (remaining < length) {
                textBuilder.append(text.substring(remaining));
            }
        }
        return false;
    }

    Atom parseMatrix(String command) throws ParserException {
        parser.skipWhitespaces();
        char c = parser.next();
        if (c != '{') {
            throw parser.newUnexpectedException("start of matrix, {", Character.toString(c));
        }

        Queue<Queue<Atom>> rows = new ArrayDeque<>();
        int cols = 0;
        do {
            Queue<Atom> row = new ArrayDeque<>();
            do {
                row.add(parse());
                group = new Group();
                c = parser.next();
            } while (c == '&');
            rows.add(row);

            if ("eqalign".equals(command)) {
                options = options.withRomanFont(false);
            }
            if (row.size() > cols) {
                cols = row.size();
            }
        } while (c == '#');

        if (rows.size() == 1 && cols == 1 && ("matrix".equals(command) || "eqalign".equals(command) ||
                command.endsWith("col") || command.endsWith("pile"))) {
            return rows.remove().remove();
        }
        return new MatrixAtom(command, rows, cols);
    }

    // TODO: simplify
    Atom parse() throws ParserException {
        if (mode != ParserMode.ARGUMENT) {
            parser.skipWhitespaces();
        }
        if (mode == ParserMode.ARGUMENT && (parser.peek() == '`' || parser.peek() == '~')) {
            return null;
        }
        if (mode == ParserMode.TERM && parser.peek() == '{') {
            mode = ParserMode.EXPLICIT;
            parser.next();
        }

        while (true) {
            char c = parser.peek();
            if (Character.isWhitespace(c) || (c == '`' && (textBuilder.length() > 0 && textBuilder.charAt(0) != '`'))) {
                if (isSingle()) {
                    break;
                }
                buildTextAtom();
                parser.skipWhitespaces();
                c = parser.peek();
            }
            if (!parser.hasNext()) {
                parser.retreat(1);
                parser.appendWarning("unexpected EOF, assuming }");
                continue;
            }
            if ((mode != ParserMode.DELIMITER && c == '}') || (mode != ParserMode.SYMBOL && (c == '&' || c == '#'))) {
                break;
            }

            parser.next();

            StringBuilder tokenBuilder = new StringBuilder(10).append(c);
            char next = parser.peek();
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                while ((next >= 'A' && next <= 'Z') || (next >= 'a' && next <= 'z')) {
                    tokenBuilder.append(parser.next());
                    next = parser.peek();
                }
            } else if (mode != ParserMode.DELIMITER && (((c == '+' || c == '<') && next == '-') ||
                    (c == '-' && (next == '+' || next == '>')) ||
                    ((c == '!' || c == '=' || c == '<' || c == '>') && next == '=') ||
                    (c == '<' && next == '<') || (c == '>' && next == '>'))) {
                char nn = parser.peek(2);
                if (!(c == '<' && next == '-') ||
                        nn == '\0' || Character.isWhitespace(nn) || (nn >= '!' && nn <= '=') || nn == '{') {
                    tokenBuilder.append(parser.next());
                    if (((c == '<' && next == '<') || (c == '>' && next == '>')) && nn == c) {
                        tokenBuilder.append(parser.next());
                    }
                }
            }
            String token = tokenBuilder.toString();

            if (mode == ParserMode.DELIMITER) {
                if (!SymbolMap.isDelimiter(token)) {
                    if (!".".equals(token)) {
                        parser.retreat(token.length());
                        parser.appendWarning("delimiter not found, using null delimiter");
                    }
                    return null;
                }
                appendText(token);
                break;
            }

            String command = (mode != ParserMode.SYMBOL || !("{".equals(token) ||
                    "\"".equals(token) || "\\".equals(token) || "_".equals(token) || "^".equals(token)))
                    ? AtomMap.search(token) : null;

            if (command != null) {
                parser.retreat(token.length() - command.length());
                AtomParser atomParser = AtomMap.get(command);

                if (atomParser == null) {
                    String symbol = SymbolMap.getSymbol(command).toString();
                    if (mode == ParserMode.DELIMITER && !SymbolMap.isDelimiter(symbol)) {
                        parser.retreat(command.length());
                        break;
                    }
                    if (appendText(symbol)) {
                        break;
                    }
                } else {
                    if (mode == ParserMode.SYMBOL || mode == ParserMode.ARGUMENT || mode == ParserMode.DELIMITER ||
                            (mode == ParserMode.TERM && textBuilder.length() > 0)) {
                        parser.retreat(command.length());
                        break;
                    }
                    buildTextAtom();

                    group.push(atomParser.parse(parser, command));
                    if (isSingle()) {
                        break;
                    }
                }
            } else if (appendText(token)) {
                break;
            }
        }
        buildTextAtom();

        while (leftRight > 0) {
            parser.appendWarning("right not found, adding right with null delimiter");
            group.push(new LeftRightAtom(false, null));
            leftRight--;
        }

        if (mode == ParserMode.EXPLICIT) {
            char next = parser.next();
            if (next != '}') {
                throw parser.newUnexpectedException("an end of group, }", Character.toString(next));
            }
        } else if (group.isEmpty() && (mode == ParserMode.SYMBOL || mode == ParserMode.TERM)) {
            throw parser.newUnexpectedException(mode == ParserMode.SYMBOL ? "a symbol" : "a term",
                    parser.peek().toString());
        }
        if (mode == ParserMode.ARGUMENT || mode == ParserMode.EXPLICIT || mode == ParserMode.TERM) {
            return ScriptAtom.parseScript(parser, group, scriptParseMode);
        }
        return group;
    }
}

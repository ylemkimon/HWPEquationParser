package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.atoms.*;

class GroupParser {
    private final HEParser parser;
    private Mode mode;
    private final int maxLength;

    private final Group group = new Group();
    private final StringBuilder textBuilder;

    enum Mode {
        NORMAL,
        LEFT_RIGHT,
        BACKSLASH,
        SYMBOL,
        DELIMITER,
        ARGUMENT,
        TERM,
        TERM_SUB
    }

    GroupParser(HEParser parser, Mode mode, int maxLength) {
        this.parser = parser;
        this.mode = mode;
        this.maxLength = maxLength;
        textBuilder = new StringBuilder(maxLength);
    }

    Atom pop() {
        return group.pop();
    }

    private void buildTextAtom() {
        if (textBuilder.length() > 0) {
            group.push(new TextAtom(textBuilder.toString()));
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

            if (mode == Mode.BACKSLASH) {
                buildTextAtom();
                mode = Mode.NORMAL;
            }
        } else {
            textBuilder.append(text, 0, remaining);

            if (mode == Mode.BACKSLASH) {
                textBuilder.setLength(maxLength - 1);
                mode = Mode.NORMAL;
            }
            buildTextAtom();

            if (mode != Mode.NORMAL && mode != Mode.LEFT_RIGHT) {
                parser.retreat(length - remaining);
                return true;
            }

            while (remaining + maxLength <= length) {
                group.push(new TextAtom(text.substring(remaining, remaining + maxLength)));
                remaining += maxLength;
            }
            if (remaining < length) {
                textBuilder.append(text.substring(remaining));
            }
        }
        return false;
    }

    // TODO: simplify
    Group parse() throws ParserException {
        if (mode != Mode.ARGUMENT) {
            if (mode != Mode.BACKSLASH) {
                parser.skipWhitespaces();
            }
        } else if (parser.peek() == '`' || parser.peek() == '~') {
            return group;
        }

        while (true) {
            char c = parser.peek();
            if (Character.isWhitespace(c) || (c == '`' && (textBuilder.length() > 0 && textBuilder.charAt(0) != '`'))) {
                if (mode == Mode.NORMAL || mode == Mode.LEFT_RIGHT || mode == Mode.BACKSLASH) {
                    if (mode == Mode.BACKSLASH) {
                        mode = Mode.NORMAL;
                    } else {
                        buildTextAtom();
                    }
                    parser.skipWhitespaces();
                    c = parser.peek();
                } else {
                    break;
                }
            }
            if (!parser.hasNext()) {
                parser.retreat(1);
                parser.appendWarning("Unexpected EOF, attempting to recover");
                continue;
            }
            if (mode != Mode.BACKSLASH && ((mode != Mode.DELIMITER && c == '}') || (mode != Mode.SYMBOL && (c == '&' || c == '#')))) {
                break;
            }
            parser.next();

            // \ {
            // " ` _ ^ (sup sub)

            StringBuilder tokenBuilder = new StringBuilder(10).append(c);
            char next = parser.peek();
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                while (('A' <= next && next <= 'Z') || ('a' <= next && next <= 'z')) {
                    tokenBuilder.append(parser.next());
                    next = parser.peek();
                }
            } else if (mode != Mode.BACKSLASH && mode != Mode.DELIMITER && (((c == '+' || c == '<') && next == '-') ||
                    (c == '-' && (next == '+' || next == '>')) ||
                    ((c == '!' || c == '=' || c == '<' || c == '>') && next == '=') ||
                    (c == '<' && next == '<') || (c == '>' && next == '>'))) {
                char nn = parser.peek(2);
                if (!(c == '<' && next == '-') ||
                        nn == '\0' || Character.isWhitespace(nn) || ('!' <= nn && nn <= '=') || nn == '{') {
                    tokenBuilder.append(parser.next());
                    if (((c == '<' && next == '<') || (c == '>' && next == '>')) && nn == c) {
                        tokenBuilder.append(parser.next());
                    }
                }
            }
            String token = tokenBuilder.toString();
            String command = (mode != Mode.BACKSLASH && ((mode != Mode.SYMBOL && mode != Mode.DELIMITER) || !("{".equals(token) ||
                    "\"".equals(token) || "\\".equals(token) || "_".equals(token) || "^".equals(token))))
                    ? AtomMap.search(token) : null;

            if (command != null) {
                if (mode == Mode.LEFT_RIGHT && "right".equals(command)) {
                    parser.retreat(token.length());
                    break;
                }

                parser.retreat(token.length() - command.length());
                AtomParser atomParser = AtomMap.get(command);

                if (atomParser == null) {
                    String symbol = SymbolMap.getSymbol(command).toString();
                    if (mode == Mode.DELIMITER && !SymbolMap.isDelimiter(symbol)) {
                        parser.retreat(command.length());
                        break;
                    }
                    if (appendText(symbol)) {
                        break;
                    }
                } else {
                    if (mode == Mode.SYMBOL || mode == Mode.ARGUMENT || mode == Mode.DELIMITER ||
                            (mode != Mode.NORMAL && mode != Mode.LEFT_RIGHT && textBuilder.length() > 0)) {
                        parser.retreat(command.length());
                        break;
                    }
                    buildTextAtom();

                    group.push(atomParser.parse(parser, command));
                    if (mode != Mode.NORMAL && mode != Mode.LEFT_RIGHT) {
                        break;
                    }
                }
            } else if (mode == Mode.DELIMITER && ".".equals(token)) {
                return null;
            } else if (mode == Mode.DELIMITER && !SymbolMap.isDelimiter(token)) {
                parser.retreat(token.length());
                break;
            } else if (appendText(token)) {
                break;
            }
        }
        buildTextAtom();

        if (mode != Mode.NORMAL && mode != Mode.LEFT_RIGHT && mode != Mode.DELIMITER) {
            if (group.isEmpty()) {
                if (mode != Mode.ARGUMENT) {
                    throw parser.newUnexpectedException(mode == Mode.SYMBOL ? "a symbol" : "a term",
                            parser.peek().toString());
                }
            }

            if (mode != Mode.SYMBOL) {
                group.parseSubSup(parser, mode == Mode.TERM_SUB);
            }
        }

        return group;
    }
}

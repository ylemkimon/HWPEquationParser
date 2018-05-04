package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.atoms.*;

class GroupParser {
    private final HEParser parser;
    private Mode mode;
    private final int maxLength;

    private final Group group = new Group();
    private final StringBuilder textBuilder;
    private final boolean onlySub;

    enum Mode {
        IMPLICIT,
        EXPLICIT,
        LEFT_RIGHT,
        ESCAPE,
        SYMBOL,
        DELIMITER,
        ARGUMENT,
        TERM
    }

    GroupParser(HEParser parser, Mode mode, int maxLength, boolean onlySub) {
        this.parser = parser;
        this.mode = mode;
        this.maxLength = maxLength;
        this.onlySub = onlySub;

        textBuilder = new StringBuilder(maxLength);
    }

    Atom pop() {
        return group.pop();
    }

    private boolean isSingle() {
        return mode != Mode.IMPLICIT && mode != Mode.EXPLICIT && mode != Mode.LEFT_RIGHT;
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
        } else {
            textBuilder.append(text, 0, remaining);
            buildTextAtom();

            if (isSingle()) {
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
        if (mode == Mode.ESCAPE && parser.hasNext()) {
            char c = parser.next();
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                StringBuilder tokenBuilder = new StringBuilder(8).append(c);
                c = parser.peek();
                while (tokenBuilder.length() < 8 && (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z'))) {
                    tokenBuilder.append(parser.next());
                    c = parser.peek();
                }
                if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                    parser.next();
                }
                group.push(new TextAtom(tokenBuilder.toString()));
            } else {
                group.push(new TextAtom(Character.isWhitespace(c) ? "~" : Character.toString(c)));
            }
        }

        if (mode != Mode.ARGUMENT) {
            parser.skipWhitespaces();
        }
        if (mode == Mode.ARGUMENT && (parser.peek() == '`' || parser.peek() == '~')) {
            return null;
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
            if ((mode != Mode.DELIMITER && c == '}') || (mode != Mode.SYMBOL && (c == '&' || c == '#'))) {
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
            } else if (mode != Mode.DELIMITER && (((c == '+' || c == '<') && next == '-') ||
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
            String command = ((mode != Mode.SYMBOL && mode != Mode.DELIMITER) || !("{".equals(token) ||
                    "\"".equals(token) || "\\".equals(token) || "_".equals(token) || "^".equals(token)))
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
                            (mode == Mode.TERM && textBuilder.length() > 0)) {
                        parser.retreat(command.length());
                        break;
                    }
                    buildTextAtom();

                    group.push(atomParser.parse(parser, command));
                    if (isSingle()) {
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

        if (mode == Mode.EXPLICIT) {
            char next = parser.next();
            if (next != '}') {
                throw parser.newUnexpectedException("an end of group, }", Character.toString(next));
            }
        } else if (group.isEmpty() && (mode == Mode.SYMBOL || mode == Mode.TERM)) {
            throw parser.newUnexpectedException(mode == Mode.SYMBOL ? "a symbol" : "a term",
                    parser.peek().toString());
        }
        if (mode == Mode.ARGUMENT || mode == Mode.EXPLICIT || mode == Mode.TERM) {
            group.parseSubSup(parser, onlySub, false);
        }

        return group;
    }
}

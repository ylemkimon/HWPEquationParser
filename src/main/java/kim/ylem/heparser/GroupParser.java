package kim.ylem.heparser;

import kim.ylem.ParserException;
import kim.ylem.heparser.atoms.*;

class GroupParser {
    private final HEParser parser;
    private final Mode mode;
    private final int maxLength;

    private final Group group = new Group();
    private final StringBuilder textBuilder;

    enum Mode {
        NORMAL,
        LEFTRIGHT,
        SYMBOL,
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

    public boolean isEmpty() {
        return group.isEmpty();
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

            if (mode != Mode.NORMAL && mode != Mode.LEFTRIGHT) {
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
        parser.skipWhitespaces();

        while (true) {
            char c = parser.peek();
            if ((mode == Mode.NORMAL || mode == Mode.LEFTRIGHT) && (Character.isWhitespace(c) || (c == '`' && textBuilder.length() > 0 && textBuilder.charAt(0) != '`'))) {
                buildTextAtom();
                parser.skipWhitespaces();
                c = parser.peek();
            }
            if (!parser.hasNext()) {
                parser.retreat(1);
                parser.appendError("Unexpected EOF, attempting to recover");
                continue;
            }
            if (Character.isWhitespace(c) || c == '}' || (mode != Mode.SYMBOL && (c == '&' || c == '#'))) {
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
            } else if (((c == '+' || c == '<') && next == '-') || (c == '-' && (next == '+' || next == '>')) ||
                    ((c == '!' || c == '=' || c == '<' || c == '>') && next == '=') ||
                    (c == '<' && next == '<') || (c == '>' && next == '>')) {
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

            String command = AtomMap.search(token);
            if (command != null) {
                if (mode == Mode.LEFTRIGHT && "right".equals(command.toLowerCase())) {
                    parser.retreat(token.length());
                    break;
                }

                parser.retreat(token.length() - command.length());
                AtomParser atomParser = AtomMap.get(command);

                if (atomParser == null) {
                    if (appendText(TextAtom.getIntermediateChar(command).toString())) {
                        break;
                    }
                } else {
                    if (mode == Mode.SYMBOL || (mode != Mode.NORMAL && mode != Mode.LEFTRIGHT && textBuilder.length() > 0)) {
                        parser.retreat(command.length());
                        break;
                    }
                    buildTextAtom();

                    group.push(atomParser.parse(parser, command));
                    if (mode != Mode.NORMAL && mode != Mode.LEFTRIGHT) {
                        break;
                    }
                }
            } else if (appendText(token)) {
                break;
            }
        }
        buildTextAtom();

        if (mode != Mode.NORMAL && mode != Mode.LEFTRIGHT) {
            if (group.isEmpty()) {
                throw parser.newUnexpectedException(mode == Mode.SYMBOL ? "a symbol" : "a term",
                        parser.peek().toString());
            }

            if (mode != Mode.SYMBOL) {
                SubSupAtom supsub = SubSupAtom.parse(parser, false,
                        mode == Mode.TERM_SUB, true);
                group.push(supsub);
            }
        }

        return group;
    }
}

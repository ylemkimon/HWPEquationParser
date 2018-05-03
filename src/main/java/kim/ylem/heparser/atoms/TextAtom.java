package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.SymbolMap;

public class TextAtom extends Atom {
    public static void init() {
        AtomMap.putTo(TextAtom::parse, "\"");
    }

    private static TextAtom parse(HEParser parser, String command) throws ParserException {
        StringBuilder sb = new StringBuilder();
        while (parser.hasNext() && parser.peek() != '"') {
            sb.append(parser.next());
        }
        if (!parser.hasNext()) {
            throw parser.newUnexpectedException("end of group, \"", "EOF");
        }
        parser.next();
        return new TextAtom(sb.toString());
    }

    private final String text;

    public TextAtom(String text) {
        this.text = text;
    }

    @Override
    public String toLaTeX(int flag) {
        String mathCommand = (flag & STYLE_ROMAN) == STYLE_ROMAN ? "\\mathrm{" : "";
        String textCommand = (flag & STYLE_BOLD) == STYLE_BOLD ? "\\textbf{" : "\\textrm{";

        StringBuilder result = new StringBuilder();
        String currentCommand = "";
        int state = -1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String latex = SymbolMap.getLaTeX(c);
            if (latex != null) {
                if (!currentCommand.isEmpty()) {
                    result.append('}');
                    currentCommand = "";
                }
                result.append(latex);
                result.append(' ');
                state = -1;
            } else {
                String newCommand = c <= 0x7F ? mathCommand : textCommand;
                if (state == -1 && (text.startsWith("det", i) || text.startsWith("arc", i))) {
                    newCommand = "\\mathrm{";
                    state = 1;
                }

                if (!newCommand.equals(currentCommand)) {
                    if (!currentCommand.isEmpty()) {
                        result.append('}');
                    }
                    result.append(newCommand);
                    currentCommand = newCommand;
                }

                if (state == 1) {
                    result.append(text, i, i + 3);
                    i += 2;
                } else {
                    result.append(c);
                }
                state = c < 'A' || c > 'z' || (c > 'Z' && c <'a') ? -1 : 0;
            }
        }
        if (!currentCommand.isEmpty()) {
            result.append('}');
        }
        return result.toString();
    }

}

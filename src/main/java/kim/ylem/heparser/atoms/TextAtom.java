package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public class TextAtom implements Atom {
    public static void init() {
        AtomMap.putTo(TextAtom::parse, "\"");
    }

    private static TextAtom parse(HEParser parser, String command) throws ParserException {
        StringBuilder sb = new StringBuilder();
        while (parser.hasNext() && parser.peek() != '"') {
            sb.append(parser.next());
        }
        if (!parser.hasNext()) {
            parser.appendWarning("expected \", got EOF instead, skipping to the end");
            parser.retreat(1);
            return null;
        }
        parser.next();
        return new TextAtom(sb.toString(), parser.getCurrentOptions());
    }

    private final String text;
    private final boolean roman;
    private final boolean bold;

    public TextAtom(String text, Options options) {
        this.text = text;
        roman = options.isRoman();
        bold = options.isBold();
    }

    @Override
    public String toString() {
        String mathCommand = roman ? "\\mathrm{" : "";
        String textCommand = bold ? "\\textbf{" : "\\textrm{";

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
                // TODO: or
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

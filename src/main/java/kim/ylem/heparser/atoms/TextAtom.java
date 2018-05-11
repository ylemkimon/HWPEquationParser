package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.ArrayList;
import java.util.Collection;

public class TextAtom implements Atom {
    private static final Collection<String> keywords = new ArrayList<>(3);

    static {
        keywords.add("or");
        keywords.add("arc");
        keywords.add("det");
    }

    public static void init() {
        AtomMap.putTo(TextAtom::parse, "\"");
        AtomMap.putTo(TextAtom::parseFont, "\\", "rm", "it");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
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

    private static Atom parseFont(HEParser parser, String command) {
        parser.getGroupParser().setOptions(parser.getCurrentOptions().withRomanFont(!"it".equals(command)));
        return null;
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

            String newCommand = latex == null ? (c <= 0x7F ? mathCommand : textCommand) : "";
            if (state == -1 && c <= 0x7F) {
                for (String s : keywords) {
                    if (text.startsWith(s, i)) {
                        newCommand = "\\mathrm{";
                        state = s.length();
                        break;
                    }
                }
            }
            if (!newCommand.equals(currentCommand)) {
                if (!currentCommand.isEmpty()) {
                    result.append('}');
                }
                result.append(newCommand);
                currentCommand = newCommand;
            }

            if (latex != null) {
                result.append(latex);
                result.append(' ');
            } else if (state > 0) {
                result.append(text, i, i + state);
                i += state - 1;
            } else {
                result.append(c);
            }

            state = c > 'z' || c < 'A' || (c > 'Z' && c <'a') ? -1 : 0;
        }
        if (!currentCommand.isEmpty()) {
            result.append('}');
        }
        return result.toString();
    }
}

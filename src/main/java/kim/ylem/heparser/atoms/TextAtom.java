package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.ArrayList;
import java.util.Collection;

public class TextAtom implements Atom {
    private final static Collection<String> keywords = new ArrayList<>(3);

    static {
        keywords.add("or");
        keywords.add("arc");
        keywords.add("det");
    }

    public static void init() {
        AtomMap.putTo(TextAtom::parse, "\"");
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
                if (state == -1) {
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

                if (state > 0) {
                    result.append(text, i, i + state);
                    i += state - 1;
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

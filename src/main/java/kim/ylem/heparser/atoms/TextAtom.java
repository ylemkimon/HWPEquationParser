package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.ArrayList;
import java.util.Collection;

public class TextAtom implements Atom {
    private static final long serialVersionUID = -6448670049973372435L;
    private static final Collection<String> keywords = new ArrayList<>(3);

    static {
        keywords.add("or");
        keywords.add("arc");
        keywords.add("det");
    }

    private final String text;
    private final boolean roman;
    private final boolean bold;

    private transient String currentCommand;
    private transient int state;

    public TextAtom(String text, Options options) {
        this.text = text;
        roman = options.isRoman();
        bold = options.isBold();
    }

    public static void init() {
        AtomMap.register(TextAtom::parse, "\"");
        AtomMap.register(TextAtom::parseFont, "\\", "rm", "it");
    }

    private static Atom parse(HEParser parser, @SuppressWarnings("unused") String command) throws ParserException {
        StringBuilder sb = new StringBuilder();
        while (parser.hasNext() && parser.peek() != '"') {
            sb.append(parser.next());
        }
        if (!parser.hasNext()) {
            parser.appendWarning("expected \", got EOF instead, skipping to the end");
            parser.skipToEnd();
            return null;
        }
        parser.next();
        return new TextAtom(sb.toString(), parser.getCurrentOptions());
    }

    private static Atom parseFont(HEParser parser, String command) {
        Options options = parser.getCurrentOptions().withRomanFont(!"it".equals(command));
        parser.getGroupParser().setOptions(options);

        if ("\\".equals(command)) {
            char c = parser.next();
            if (!ASCIIUtil.isAlphabet(c)) {
                return new TextAtom(Character.isWhitespace(c) ? "~" : Character.toString(c), options);
            }

            StringBuilder sb = new StringBuilder(9).append(c);
            while (ASCIIUtil.isAlphabet(parser.peek()) && sb.length() < 9) {
                sb.append(parser.next());
            }
            if (sb.length() == 9) {
                sb.setLength(8);
            }
            return new TextAtom(sb.toString(), options);
        }
        return null;
    }

    private String getCommand(char c, int pos) {
        if (c > 0x7F) {
            return bold ? "\\textbf{" : "\\textrm{";
        } else if (state == -1) {
            for (String s : keywords) {
                if (text.startsWith(s, pos)) {
                    state = s.length();
                    return "\\mathrm{";
                }
            }
        }
        return roman ? "\\mathrm{" : "";
    }

    private void changeCommand(StringBuilder sb, String newCommand) {
        if (!newCommand.equals(currentCommand)) {
            if (currentCommand != null && !currentCommand.isEmpty()) {
                sb.append('}');
            }
            sb.append(newCommand);
            currentCommand = newCommand;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        state = -1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String latex = SymbolMap.getLaTeX(c);
            //noinspection VariableNotUsedInsideIf
            changeCommand(result, latex == null ? getCommand(c, i) : "");

            if (state > 0) {
                result.append(text, i, i + state);
                //noinspection AssignmentToForLoopParameter
                i += state - 1;
            } else if (latex == null) {
                result.append(c);
                if (c == '<') { // escape HTML
                    result.append(' ');
                }
            } else {
                result.append(latex);
                result.append(' ');
            }
            state = ASCIIUtil.isAlphabet(c) ? 0 : -1;
        }
        changeCommand(result, "");
        return result.toString();
    }
}

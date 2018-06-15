package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class TextAtom implements Atom {
    private static final long serialVersionUID = -6448670049973372435L;
    private static final Logger logger = LogManager.getLogger();
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
        char c = parser.next();
        while (c != '\0' && c != '"') {
            sb.append(c);
            parser.consume(null, 1);
            c = parser.next();
        }
        if (c == '\0') {
            logger.warn("Expected \", got EOF instead, skipping to the end");
            parser.skipToEnd();
            return null;
        }
        parser.consume(null, 1);
        return new TextAtom(sb.toString(), parser.getCurrentOptions());
    }

    private static Atom parseFont(HEParser parser, String command) {
        Options options = parser.getCurrentOptions().withRomanFont(!"it".equals(command));
        parser.getGroupParser().setOptions(options);

        if ("\\".equals(command)) {
            char c = parser.next();
            if (!ASCIIUtil.isAlphabet(c)) {
                parser.consume(null, 1);
                return new TextAtom(Character.isWhitespace(c) ? "~" : Character.toString(c), options);
            }

            StringBuilder sb = new StringBuilder(9).append(c);
            c = parser.next();
            while (ASCIIUtil.isAlphabet(c) && sb.length() < 9) {
                sb.append(c);
                parser.consume(null, 1);
                c = parser.next();
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
    public @NotNull String toString() {
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

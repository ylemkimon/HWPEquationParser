package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

import java.util.HashMap;
import java.util.Map;

public final class FractionAtom implements Atom {
    private static final long serialVersionUID = 4727732048507667055L;
    private static final Map<String, String> fractionMap = new HashMap<>(6);
    private static final Map<String, String> dfractionMap = new HashMap<>(6);

    static {
        put("binom", "\\binom", "\\dbinom");
        put("overbrace", "\\overbrace", "\\overbrace");
        put("underbrace", "\\underbrace", "\\underbrace");

        // infix
        put("over", "\\frac", "\\dfrac");
        put("atop", "\\genfrac{}{}{0pt}{}", "\\genfrac{}{}{0pt}0");
        put("choose", "\\binom", "\\dbinom");
    }

    private static void put(String command, String textStyle, String displayStyle) {
        fractionMap.put(command, textStyle);
        dfractionMap.put(command, displayStyle);
    }

    public static void init() {
        AtomMap.register(FractionAtom::parse, fractionMap.keySet());
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom first;
        if ("over".equals(command) ||"atop".equals(command) || "choose".equals(command)) {
            first = parser.getGroupParser().popGroup();
            if (first == null) {
                throw parser.newUnexpectedException("a term", command);
            }
        } else {
            first = parser.parseGroup(ParserMode.TERM);
        }
        Atom second = parser.parseGroup(ParserMode.TERM);
        return new FractionAtom(command, first, second, parser.getCurrentOptions().isTextStyle());
    }

    private final String function;
    private final Atom first;
    private final Atom second;

    private FractionAtom(String command, Atom first, Atom second, boolean textStyle) {
        function = textStyle ? fractionMap.get(command) : dfractionMap.get(command);
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        if ("\\overbrace".equals(function)) {
            return function + '{' + first + "}^{" + second + '}';
        } else if ("\\underbrace".equals(function)) {
            return function + '{' + second + "}_{" + first + '}';
        }
        return function + '{' + first + "}{" + second + '}';
    }

}

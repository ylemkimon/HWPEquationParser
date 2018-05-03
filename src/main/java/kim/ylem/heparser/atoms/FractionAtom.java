package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;

public final class FractionAtom extends Atom {
    private static final Map<String, String> fractionMap = new HashMap<>(6);

    static {
        fractionMap.put("binom", "\\binom");
        fractionMap.put("overbrace", "\\overbrace");
        fractionMap.put("underbrace", "\\underbrace");

        // infix
        fractionMap.put("over", "\\frac");
        fractionMap.put("atop", "\\genfrac{}{}{0pt}{}");
        fractionMap.put("choose", "\\binom");
    }

    public static void init() {
        AtomMap.putAll(fractionMap.keySet(), FractionAtom::parse);
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom first;
        if ("over".equals(command) ||"atop".equals(command) || "choose".equals(command)) {
            first = parser.popGroup();
            if (first == null) {
                throw parser.newUnexpectedException("a term", command);
            }
        } else {
            first = parser.nextGroup();
        }
        Atom second = parser.nextGroup();
        return new FractionAtom(command, first, second);
    }

    private final String function;
    private final Atom first;
    private final Atom second;

    private FractionAtom(String command, Atom first, Atom second) {
        function = fractionMap.get(command);
        this.first = first;
        this.second = second;
    }

    @Override
    public String toLaTeX(int flag) {
        if ("\\overbrace".equals(function)) {
            return "\\overbrace{" + first.toLaTeX(flag) + "}^{" + second.toLaTeX(flag) + '}';
        } else if ("\\underbrace".equals(function)) {
            return "\\underbrace{" + second.toLaTeX(flag) + "}_{" + first.toLaTeX(flag) + '}';
        }
        return function + '{' + first.toLaTeX(flag) + "}{" + second.toLaTeX(flag) + '}';
    }

}

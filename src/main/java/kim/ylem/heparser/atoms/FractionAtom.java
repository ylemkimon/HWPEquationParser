package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.HashMap;
import java.util.Map;

public final class FractionAtom implements Atom {
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
            first = parser.getGroupParser().popGroup();
            if (first == null) {
                throw parser.newUnexpectedException("a term", command);
            }
        } else {
            first = parser.parseGroup(ParserMode.TERM);
        }
        Atom second = parser.parseGroup(ParserMode.TERM);
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
    public String toString() {
        if ("\\overbrace".equals(function)) {
            return function + '{' + first + "}^{" + second + '}';
        } else if ("\\underbrace".equals(function)) {
            return function + '{' + second + "}_{" + first + '}';
        }
        return function + '{' + first + "}{" + second + '}';
    }

}

package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;

public class Fraction extends Atom {
    private static final HashMap<String, String> fractionMap = new HashMap<>();

    public static void register() {
        fractionMap.put("binom", "\\binom");
        fractionMap.put("overbrace", "\\overbrace");
        fractionMap.put("underbrace", "\\underbrace");

        // infix
        fractionMap.put("over", "\\frac");
        fractionMap.put("atop", "\\genfrac{}{}{0pt}{}");
        fractionMap.put("choose", "\\binom");

        AtomMap.putAll(fractionMap, Fraction.class);
    }

    private final String function;
    private final Group first;
    private final Group second;

    public Fraction(String function, Group first, Group second) {
        this.function = fractionMap.get(function.toLowerCase());
        this.first = first;
        this.second = second;
    }

    @Override
    protected String toLaTeX(int flag) {
        if ("\\overbrace".equals(function)) {
            return "\\overbrace{" + first.toLaTeX(flag) + "}^{" + second.toLaTeX(flag) + "}";
        } else if ("\\underbrace".equals(function)) {
            return "\\underbrace{" + second.toLaTeX(flag) + "}_{" + first.toLaTeX(flag) + "}";
        }
        return function + "{" + first.toLaTeX(flag) + "}{" + second.toLaTeX(flag) + "}";
    }
}

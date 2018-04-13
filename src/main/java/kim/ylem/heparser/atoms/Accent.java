package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;
import java.util.Map;

public class Accent extends Atom {
    private static final Map<String, String> accentMap = new HashMap<>();

    public static void register() {
        accentMap.put("acute", "\\acute");
        accentMap.put("arch", "\\wideparen"); // yhmath, mathabx, MdSymbol, MnSymbol, fdsymbol, fourier
        accentMap.put("bar", "\\overline");
        accentMap.put("box", "\\boxed");
        accentMap.put("check", "\\widecheck"); // mathabx, stix
        accentMap.put("ddot", "\\ddot");
        accentMap.put("dot", "\\dot");
        accentMap.put("dyad", "\\overleftrightarrow");
        accentMap.put("grave", "\\grave");
        accentMap.put("hat", "\\widehat");
        accentMap.put("overline", "\\overline");
        accentMap.put("tilde", "\\widetilde");
        accentMap.put("under", "\\underline");
        accentMap.put("underline", "\\underline");
        accentMap.put("vec", "\\overrightarrow");

        // letter
        accentMap.put("not", "\\not");
        accentMap.put("bigg", "\\bigg");

        AtomMap.putAll(accentMap, Accent.class);
    }

    private final String function;
    private final Group content;

    public Accent(String function, Group content) {
        this.function = accentMap.get(function.toLowerCase());
        this.content = content;
    }

    @Override
    protected String toLaTeX(int flag) {
        return function + "{" + content.toLaTeX(flag) + "}";
    }
}

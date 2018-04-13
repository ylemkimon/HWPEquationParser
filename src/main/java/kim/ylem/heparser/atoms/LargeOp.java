package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;
import java.util.Map;

public class LargeOp extends Atom {
    private static final Map<String, String> largeOpMap = new HashMap<>();

    public static void register() {
        largeOpMap.put("bigcap", "\\bigcap");
        largeOpMap.put("bigcup", "\\bigcup");
        largeOpMap.put("bigodiv", "\\bigoslash"); // mathabx, MnSymbol
        largeOpMap.put("bigodot", "\\bigodot");
        largeOpMap.put("bigominus", "\\bigominus"); // mathabx, MnSymbol
        largeOpMap.put("bigoplus", "\\bigoplus");
        largeOpMap.put("bigotimes", "\\bigotimes");
        largeOpMap.put("bigsqcap", "\\bigsqcap"); // stmaryrd, mathabx, MnSymbol, txfonts, pxfonts, stix, fdsymbol
        largeOpMap.put("bigsqcup", "\\bigsqcup");
        largeOpMap.put("biguplus", "\\biguplus");
        largeOpMap.put("bigvee", "\\bigvee");
        largeOpMap.put("bigwedge", "\\bigwedge");
        largeOpMap.put("coprod", "\\coprod");
        largeOpMap.put("dint", "\\iint");
        largeOpMap.put("int", "\\int");
        largeOpMap.put("inter", "\\bigcap");
        largeOpMap.put("odint", "\\oiint"); // esint, mathdesign, mathabx, MdSymbol, MnSymbol, txfonts, pxfonts, stix, fdsymbol, fourier
        largeOpMap.put("oint", "\\oint");
        largeOpMap.put("otint", "\\oiiint"); // mathdesign, MdSymbol, txfonts, pxfonts, stix, fdsymbol, fourier
        largeOpMap.put("prod", "\\prod");
        largeOpMap.put("sum", "\\sum");
        largeOpMap.put("tint", "\\iiint");
        largeOpMap.put("union", "\\bigcup");

        AtomMap.putAll(largeOpMap, LargeOp.class);
    }

    private final String function;
    private final Group sub;
    private final Group sup;

    public LargeOp(String function, Group sub, Group sup) {
        this.function = largeOpMap.get(function.toLowerCase());
        this.sub = sub;
        this.sup = sup;
    }

    @Override
    protected String toLaTeX(int flag) {
        String result = function;
        if (sub != null) {
            result += "_{" + sub.toLaTeX(flag) + "}";
        }
        if (sup != null) {
            result += "^{" + sup.toLaTeX(flag) + "}";
        }
        return result + " ";
    }
}

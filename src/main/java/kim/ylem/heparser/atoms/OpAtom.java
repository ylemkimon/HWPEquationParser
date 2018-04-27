package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;

public final class OpAtom implements Atom {
    private static final Map<String, String> opMap = new HashMap<>(25);

    static {
        opMap.put("bigcap", "\\bigcap");
        opMap.put("bigcup", "\\bigcup");
        opMap.put("bigodiv", "\\bigoslash"); // mathabx, MnSymbol
        opMap.put("bigodot", "\\bigodot");
        opMap.put("bigominus", "\\bigominus"); // mathabx, MnSymbol
        opMap.put("bigoplus", "\\bigoplus");
        opMap.put("bigotimes", "\\bigotimes");
        opMap.put("bigsqcap", "\\bigsqcap"); // stmaryrd, mathabx, MnSymbol, txfonts, pxfonts, stix, fdsymbol
        opMap.put("bigsqcup", "\\bigsqcup");
        opMap.put("biguplus", "\\biguplus");
        opMap.put("bigvee", "\\bigvee");
        opMap.put("bigwedge", "\\bigwedge");
        opMap.put("coprod", "\\coprod");
        opMap.put("dint", "\\iint");
        opMap.put("int", "\\int");
        opMap.put("inter", "\\bigcap");
        opMap.put("odint", "\\oiint"); // esint, mathdesign, mathabx, MdSymbol, MnSymbol, txfonts, pxfonts,
                                            // stix, fdsymbol, fourier
        opMap.put("oint", "\\oint");
        opMap.put("otint", "\\oiiint"); // mathdesign, MdSymbol, txfonts, pxfonts, stix, fdsymbol, fourier
        opMap.put("prod", "\\prod");
        opMap.put("sum", "\\sum");
        opMap.put("tint", "\\iiint");
        opMap.put("union", "\\bigcup");

        opMap.put("lim", "\\lim");
        opMap.put("Lim", "\\operatorname*{Lim}");
    }

    public static void init() {
        AtomMap.putAll(opMap.keySet(), OpAtom::parse);
        AtomMap.remove("LIM");
    }

    private static OpAtom parse(HEParser parser, String function) throws ParserException {
        SubSupAtom subsup = SubSupAtom.parse(parser, true, false, !"lim".equals(function.toLowerCase()));
        return new OpAtom(function, subsup);
    }

    private final String function;
    private final SubSupAtom subsup;

    private OpAtom(String function, SubSupAtom subsup) {
        this.function = opMap.getOrDefault(function, opMap.get(function.toLowerCase()));
        this.subsup = subsup;
    }

    @Override
    public String toLaTeX(int flag) {
        return function + subsup.toLaTeX(flag) + ' ';
    }

}

package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

import java.util.HashMap;
import java.util.Map;

public final class AccentAtom implements Atom {
    private static final long serialVersionUID = 7616109053854504180L;
    private static final Map<String, String> accentMap = new HashMap<>(20);

    static {
        // accent
        accentMap.put("acute", "\\acute");
        accentMap.put("arch", "\\wideparen"); // yhmath, mathabx, MdSymbol, MnSymbol, fdsymbol, fourier
        accentMap.put("bar", "\\overline");
        accentMap.put("box", "\\boxed");
        accentMap.put("breve", "");
        accentMap.put("check", "\\widecheck"); // mathabx, stix
        accentMap.put("ddot", "\\ddot");
        accentMap.put("dot", "\\dot");
        accentMap.put("dyad", "\\overleftrightarrow");
        accentMap.put("grave", "\\grave");
        accentMap.put("hat", "\\widehat");
        accentMap.put("overline", "\\overline");
        accentMap.put("phantom", "\\phantom");
        accentMap.put("tilde", "\\widetilde");
        accentMap.put("under", "\\underline");
        accentMap.put("underline", "\\underline");
        accentMap.put("vec", "\\overrightarrow");

        // bold
        accentMap.put("bold", "\\boldsymbol");

        // letter
        // double quote(") not allowed
        accentMap.put("not", "\\not");
        accentMap.put("bigg", "\\huge");
    }

    private final String function;
    private final Atom content;

    private AccentAtom(String command, Atom content) {
        function = accentMap.get(command);
        this.content = content;
    }

    public static void init() {
        AtomMap.register(AccentAtom::parse, accentMap.keySet());
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom content;
        if ("not".equals(command) || "bigg".equals(command)) {
            content = parser.parseGroup(ParserMode.SYMBOL);
        } else if ("bold".equals(command)) {
            content = parser.parseGroup(ParserMode.TERM, parser.getCurrentOptions().withBoldFont(true));
        } else {
            content = parser.parseGroup(ParserMode.TERM);
        }
        return new AccentAtom(command, content);
    }

    @Override
    public String toString() {
        return "\\huge".equals(function) ? '{' + function + ' ' + content + '}' : function + '{' + content + '}';
    }
}

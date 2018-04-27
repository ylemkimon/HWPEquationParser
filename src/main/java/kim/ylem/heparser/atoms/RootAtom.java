package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class RootAtom implements Atom {
    public static void init() {
        AtomMap.put("sqrt", RootAtom::parse);
        AtomMap.put("root", RootAtom::parse);
    }

    private static RootAtom parse(HEParser parser, String function) throws ParserException {
        Atom degree = null;
        Atom content = parser.nextGroup();

        // TODO: sqrt behavior
        if (parser.search("of", "Of", "OF") != null) {
            degree = content;
            content = parser.nextGroup();
        }
        return new RootAtom(degree, content);
    }

    private final Atom degree;
    private final Atom content;

    private RootAtom(Atom degree, Atom content) {
        this.degree = degree;
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        String result = "\\sqrt";
        if (degree != null) {
            result += '[' + degree.toLaTeX(flag) + ']';
        }
        return result + '{' + content.toLaTeX(flag) + '}';
    }

}

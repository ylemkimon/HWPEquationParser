package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public final class RootAtom implements Atom {
    public static void init() {
        AtomMap.putTo(RootAtom::parse, "sqrt", "root");
    }

    private static RootAtom parse(HEParser parser, String command) throws ParserException {
        Atom degree = null;
        Atom content = parser.parseGroup(ParserMode.TERM);
        if (parser.search("of", "Of", "OF")) {
            degree = content;
            content = parser.parseGroup(ParserMode.TERM);
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
    public String toString() {
        String result = "\\sqrt";
        if (degree != null) {
            result += '[' + degree.toString() + ']';
        }
        return result + '{' + content + '}';
    }

}

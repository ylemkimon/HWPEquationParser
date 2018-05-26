package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

public final class RootAtom implements Atom {
    private static final long serialVersionUID = -5879748029482233369L;

    private final Atom degree;
    private final Atom content;

    private RootAtom(Atom degree, Atom content) {
        this.degree = degree;
        this.content = content;
    }

    public static void init() {
        AtomMap.register(RootAtom::parse, "sqrt", "root");
    }

    private static Atom parse(HEParser parser, @SuppressWarnings("unused") String command) throws ParserException {
        Atom degree = null;
        Atom content = parser.parseGroup(ParserMode.TERM);
        if (parser.search("of", "Of", "OF")) {
            degree = content;
            content = parser.parseGroup(ParserMode.TERM);
        }
        return new RootAtom(degree, content);
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

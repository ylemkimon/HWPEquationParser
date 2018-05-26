package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

public final class UnderOverAtom implements Atom {
    private static final long serialVersionUID = -6552226985365505508L;

    private final Atom content;
    private final Atom over;
    private final Atom under;

    UnderOverAtom(Atom content, Atom over, Atom under) {
        this.content = content;
        this.over = over;
        this.under = under;
    }

    public static void init() {
        AtomMap.register(UnderOverAtom::parse, "underover", "buildrel", "rel");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        if ("underover".equals(command)) {
            return parser.parseGroup(ParserMode.UNDEROVER_TERM);
        }
        Atom content = parser.parseGroup(ParserMode.SYMBOL, parser.getCurrentOptions().withRomanFont(true));
        Atom over = parser.parseGroup(ParserMode.TERM);
        Atom under = "rel".equals(command) ? parser.parseGroup(ParserMode.TERM) : null;
        return new UnderOverAtom(content, over, under);
    }

    @Override
    public String toString() {
        String result = content.toString();
        if (over != null) {
            result = "\\overset{" + over + "}{" + result + '}';
        }
        if (under != null) {
            result = "\\underset{" + under + "}{" + result + '}';
        }
        return result;
    }
}

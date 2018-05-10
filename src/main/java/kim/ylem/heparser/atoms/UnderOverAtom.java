package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public final class UnderOverAtom implements Atom {
    public static void init() {
        AtomMap.putTo(UnderOverAtom::parse, "underover", "buildrel", "rel");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Group content;
        Atom over;
        Atom under;
        if ("underover".equals(command)) {
            content = parser.parseGroup(ParserMode.TERM);
            under = content.popSub();
            over = content.popSup();
        } else {
            content = parser.parseGroup(ParserMode.SYMBOL, parser.getCurrentOptions().withFontStyle(true));
            over = parser.parseGroup(ParserMode.TERM);
            under = "rel".equals(command) ? parser.parseGroup(ParserMode.TERM) : null;
        }

        return new UnderOverAtom(content, over, under);
    }

    private final Atom content;
    private final Atom over;
    private final Atom under;

    private UnderOverAtom(Atom content, Atom over, Atom under) {
        this.content = content;
        this.over = over;
        this.under = under;
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

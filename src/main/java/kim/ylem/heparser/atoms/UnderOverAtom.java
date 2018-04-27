package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class UnderOverAtom implements Atom {
    public static void init() {
        AtomMap.put("underover", UnderOverAtom::parse);

        // letter
        AtomMap.put("buildrel", UnderOverAtom::parse);
        AtomMap.put("rel", UnderOverAtom::parse);
    }

    private static Atom parse(HEParser parser, String function) throws ParserException {
        Atom content;
        Atom over;
        Atom under;

        if ("underover".equals(function)) {
            content = parser.nextGroup();

            SubSupAtom subsup = SubSupAtom.parse(parser, false, false, true);
            under = subsup.getSub();
            over = subsup.getSup();
        } else {
            content = parser.nextSymbol(1);
            over = parser.nextGroup();
            under = "rel".equals(function) ? parser.nextGroup() : null;
        }

        return new UnderOverAtom(function, content, over, under);
    }

    private final String function;
    private final Atom content;
    private final Atom over;
    private final Atom under;

    private UnderOverAtom(String function, Atom content, Atom over, Atom under) {
        this.function = function;
        this.content = content;
        this.over = over;
        this.under = under;
    }

    @Override
    public String toLaTeX(int flag) {
        String result = content.toLaTeX(function.endsWith("rel") ? (flag & STYLE_ROMAN) : flag);
        if (over != null) {
            result = "\\overset{" + over.toLaTeX(flag) + "}{" + result + '}';
        }
        if (under != null) {
            result = "\\underset{" + under.toLaTeX(flag) + "}{" + result + '}';
        }
        return result;
    }

}

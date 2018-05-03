package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class UnderOverAtom extends Atom {
    public static void init() {
        AtomMap.putTo(UnderOverAtom::parse, "underover", "buildrel", "rel");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Group content;
        Atom over;
        Atom under;
        if ("underover".equals(command)) {
            content = parser.nextGroup();
            under = content.popSub();
            over = content.popSup();
        } else {
            content = parser.nextSymbol();
            over = parser.nextGroup();
            under = "rel".equals(command) ? parser.nextGroup() : null;
        }

        return new UnderOverAtom(command, content, over, under);
    }

    private final String command;
    private final Atom content;
    private final Atom over;
    private final Atom under;

    private UnderOverAtom(String command, Atom content, Atom over, Atom under) {
        this.command = command;
        this.content = content;
        this.over = over;
        this.under = under;
    }

    @Override
    public String toLaTeX(int flag) {
        String result = content.toLaTeX(command.endsWith("rel") ? (flag | STYLE_ROMAN) : flag);
        if (over != null) {
            result = "\\overset{" + over.toLaTeX(flag) + "}{" + result + '}';
        }
        if (under != null) {
            result = "\\underset{" + under.toLaTeX(flag) + "}{" + result + '}';
        }
        return result;
    }

}

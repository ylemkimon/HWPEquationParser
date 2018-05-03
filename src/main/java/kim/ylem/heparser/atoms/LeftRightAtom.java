package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class LeftRightAtom extends Atom {
    public static void init() {
        AtomMap.putTo(LeftRightAtom::parse, "left", "right");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom delim = parser.nextDelimiter(command);
        if ("right".equals(command)) {
            parser.appendWarning("Unexpected \\right");
            return delim;
        }

        Atom content = parser.parseGroups(command);
        Atom rightDelim;
        if (parser.search("right", "Right", "RIGHT")) {
            rightDelim = parser.nextDelimiter("right");
        } else {
            parser.appendWarning("\\right not found, using empty delimiter");
            rightDelim = null;
        }

        return delim != null || rightDelim != null ? new LeftRightAtom(delim, rightDelim, content) : content;
    }

    private final Atom leftDelim;
    private final Atom rightDelim;
    private final Atom content;

    private LeftRightAtom(Atom leftDelim, Atom rightDelim, Atom content) {
        this.leftDelim = leftDelim;
        this.rightDelim = rightDelim;
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        String left = leftDelim != null ? leftDelim.toLaTeX(flag) : ".";
        String right = rightDelim != null ? rightDelim.toLaTeX(flag) : ".";
        return "\\left" + left + content.toLaTeX(flag) + "\\right" + right;
    }

}

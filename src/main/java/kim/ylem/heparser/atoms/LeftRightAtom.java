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
        Atom leftDelim = null;
        Atom rightDelim = null;
        Group content;
        if ("left".equals(command)) {
            leftDelim = parser.nextDelimiter("left");
            content = parser.parseImplicitGroup(command);

            if (parser.search("right", "Right", "RIGHT")) {
                rightDelim = parser.nextDelimiter("right");
            } else {
                parser.appendWarning("right not found, using empty delimiter");
            }
        } else {
            rightDelim = parser.nextDelimiter("right");
            parser.appendWarning("unexpected right, wrapping current group");

            content = new Group();
            Atom atom;
            while ((atom = parser.popGroup()) != null) {
                content.addFirst(atom);
            }
        }

        return leftDelim != null || rightDelim != null ? new LeftRightAtom(leftDelim, rightDelim, content) : content;
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
        String left = leftDelim != null ? leftDelim.toLaTeX(0) : ".";
        String right = rightDelim != null ? rightDelim.toLaTeX(0) : ".";
        return "\\left" + left + content.toLaTeX(flag) + "\\right" + right;
    }

}

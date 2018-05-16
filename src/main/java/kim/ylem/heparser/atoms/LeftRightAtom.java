package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

public final class LeftRightAtom implements Atom {
    public static void init() {
        AtomMap.register(LeftRightAtom::parse, "left", "right");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom delim = parser.parseGroup(ParserMode.DELIMITER, parser.getCurrentOptions().withRomanFont(false));
        boolean left = "left".equals(command);
        parser.getGroupParser().leftRight(left);
        return new LeftRightAtom(left, delim);
    }

    private final boolean left;
    private final Atom delim;

    public LeftRightAtom(boolean left, Atom delim) {
        this.left = left;
        this.delim = delim;
    }

    @Override
    public String toString() {
        return '\\' + (left ? "left" : "right") + (delim != null ? delim : ".");
    }

}

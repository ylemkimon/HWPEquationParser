package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

public final class LeftRightAtom implements Atom {
    private static final long serialVersionUID = -1865251277686618811L;

    private final Side side;
    private final Atom delim;

    public LeftRightAtom(Side side, Atom delim) {
        this.side = side;
        this.delim = delim;
    }

    public static void init() {
        AtomMap.register(LeftRightAtom::parse, "left", "right");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom delim = parser.parseGroup(ParserMode.DELIMITER, parser.getCurrentOptions().withRomanFont(false));
        Side side = Side.valueOf(command);
        parser.getGroupParser().updateLeftRightDepth(side);
        return new LeftRightAtom(side, delim);
    }

    @Override
    public String toString() {
        return '\\' + side.toString() + (delim != null ? delim : ".");
    }

    public enum Side {left, right}
}

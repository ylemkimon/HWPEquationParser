package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public final class LeftRightAtom implements Atom {
    public static void init() {
        AtomMap.putTo(LeftRightAtom::parse, "left", "right");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Group delim = parser.parseGroup(ParserMode.DELIMITER, parser.getCurrentOptions().withFontStyle(true));
        if (delim != null && delim.isEmpty()) {
            parser.appendWarning(command + " delimiter not found, using null delimiter");
            delim = null;
        }

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

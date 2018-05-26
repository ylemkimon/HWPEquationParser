package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public final class ScriptAtom implements Atom {
    private static final long serialVersionUID = -7787380238205077540L;

    private final Atom content;
    private final Atom sub;
    private final Atom sup;

    private ScriptAtom(Atom content, Atom sub, Atom sup) {
        this.content = content;
        this.sub = sub;
        this.sup = sup;
    }

    public static void init() {
        AtomMap.register(ScriptAtom::parse, "_", "^", "sub", "sup", "from", "to");
    }

    private static boolean searchSup(HEParser parser, boolean allowTo) {
        return (allowTo && parser.search("to", "To", "TO")) ||
                parser.search("^", "sup", "Sup", "SUP");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        boolean isFrom = "from".equals(command);

        Atom content = parser.getGroupParser().popGroup();
        if (content == null) {
            parser.appendWarning("expected a term, using empty term");
        } else if (!content.isFromToAllowed() && (isFrom || "to".equals(command))) {
            parser.appendWarning("unexpected " + command + ", skipping to the end");
            parser.skipToEnd();
            return content;
        }

        Options textStyleOption = parser.getCurrentOptions().withTextStyle(true);
        Atom sub = null;
        Atom sup = null;
        if (isFrom || "_".equals(command) || "sub".equals(command)) {
            sub = parser.parseGroup(isFrom ? ParserMode.TERM : ParserMode.SUB_TERM, textStyleOption);
        }
        if (sub == null || searchSup(parser, isFrom)) {
            sup = parser.parseGroup(ParserMode.TERM, textStyleOption);
        }
        return new ScriptAtom(content, sub, sup);
    }

    public static Atom parse(HEParser parser, Atom content, Mode mode) throws ParserException {
        Options textStyleOption = parser.getCurrentOptions().withTextStyle(true);
        Atom sub = null;
        Atom sup = null;
        if (parser.search("_", "sub", "Sub", "SUB")) {
            sub = parser.parseGroup(ParserMode.SUB_TERM, textStyleOption);
        }
        if ((mode != Mode.IN_SUB || sub != null) && searchSup(parser, false)) {
            sup = parser.parseGroup(ParserMode.TERM, textStyleOption);
        }

        if (sub == null && sup == null) {
            return content;
        }
        return mode == Mode.UNDEROVER ? new UnderOverAtom(content, sup, sub) : new ScriptAtom(content, sub, sup);
    }

    @Override
    public String toString() {
        String result = "";
        if (content != null) {
            result += content;
        }
        if (sub != null) {
            result += "_{" + sub + '}';
        }
        if (sup != null) {
            result += "^{" + sup + '}';
        }
        return result;
    }

    public enum Mode { NORMAL, IN_SUB, UNDEROVER }
}

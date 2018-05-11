package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

public final class ScriptAtom implements Atom {
    public enum Mode {
        NORMAL,
        IN_SUB,
        UNDEROVER
    }

    public static void init() {
        AtomMap.putTo(ScriptAtom::parse, "_", "^", "sub", "sup", "from", "to");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        boolean isFrom = "from".equals(command);
        Options textStyleOption = parser.getCurrentOptions().withTextStyle(true);
        Atom sub = null;
        Atom sup = null;

        Atom content = parser.getGroupParser().popGroup();
        if (content == null) {
            parser.appendWarning("expected a term, using empty term");
        } else if (!content.isFromToAllowed() && (isFrom || "to".equals(command))) {
            parser.appendWarning("unexpected " + command + ", skipping to the end");
            parser.skipToEnd();
            return content;
        }

        if (isFrom || "_".equals(command) || "sub".equals(command)) {
            sub = parser.parseGroup(isFrom ? ParserMode.TERM : ParserMode.SUB_TERM, textStyleOption);
            if ((isFrom && parser.search("to", "To", "TO")) ||
                    parser.search("^", "sup", "Sup", "SUP")) {
                sup = parser.parseGroup(ParserMode.TERM, textStyleOption);
            }
        } else {
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
        if ((mode != Mode.IN_SUB || sub != null) && parser.search("^", "sup", "Sup", "SUP")) {
            sup = parser.parseGroup(ParserMode.TERM, textStyleOption);
        }

        if (sub == null && sup == null) {
            return content;
        } else if (mode == Mode.UNDEROVER) {
            return new UnderOverAtom(content, sup, sub);
        }
        return new ScriptAtom(content, sub, sup);
    }

    private final Atom content;
    private final Atom sub;
    private final Atom sup;

    private ScriptAtom(Atom content, Atom sub, Atom sup) {
        this.content = content;
        this.sub = sub;
        this.sup = sup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (content != null) {
            sb.append(content);
        }
        if (sub != null) {
            sb.append("_{");
            sb.append(sub);
            sb.append('}');
        }
        if (sup != null) {
            sb.append("^{");
            sb.append(sup);
            sb.append('}');
        }
        return sb.toString();
    }
}

package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class SubSupAtom implements Atom {
    public static void init() {
        AtomMap.put("_", SubSupAtom::parse);
        AtomMap.put("^", SubSupAtom::parse);
        AtomMap.put("sub", SubSupAtom::parse);
        AtomMap.put("sup", SubSupAtom::parse);
    }

    private final Atom content;
    private final Atom sub;
    private final Atom sup;

    private SubSupAtom(Atom content, Atom sub, Atom sup) {
        this.content = content;
        this.sub = sub;
        this.sup = sup;
    }

    private static SubSupAtom parse(HEParser parser, String command) throws ParserException {
        if (parser.isEmpty()) {
            throw parser.newUnexpectedException("a term", command);
        }

        parser.retreat(command.length());
        return parse(parser, false, false, true);
    }

    public static SubSupAtom parse(HEParser parser, boolean allowFromTo,
                                   boolean onlySub, boolean parseSup) throws ParserException {
        // XXX: is this OK?
        Atom content = parser.popGroup();
        Atom sub = null;
        Atom sup = null;

        if (parser.search("_", "sub", "Sub", "SUB") != null) {
            sub = parser.nextGroup(true);
            allowFromTo = false;
        } else if (allowFromTo && parser.search("from", "From", "FROM") != null) {
            sub = parser.nextGroup();
        }
        if (!onlySub && (parser.search("^", "sup", "Sup", "SUP") != null ||
                (allowFromTo && parser.search("to", "To", "TO") != null))) {
            if (!parseSup) {
                throw parser.newUnexpectedException("a term", "superscript");
            }
            sup = parser.nextGroup();
        }
        return new SubSupAtom(content, sub, sup);
    }

    @Override
    public String toLaTeX(int flag) {
        String result = content != null ? content.toLaTeX(flag) : "";
        if (sub != null) {
            result += "_{" + sub.toLaTeX(flag) + '}';
        }
        if (sup != null) {
            result += "^{" + sup.toLaTeX(flag) + '}';
        }
        return result;
    }

    public Atom getSub() {
        return sub;
    }

    public Atom getSup() {
        return sup;
    }
}

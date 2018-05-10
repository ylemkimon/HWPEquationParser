package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class Group implements Atom {
    public static void init() {
        AtomMap.putTo((parser, function) -> parser.parseGroup(ParserMode.EXPLICIT), "{");
        AtomMap.putTo(Group::parseSubSupp, "_", "^", "sub", "sup", "from", "to");
    }

    // XXX: separate SubSup?
    private static Atom parseSubSupp(HEParser parser, String command) throws ParserException {
        boolean allowFromTo = false;

        Atom content = parser.getGroupParser().popGroup();
        if (content == null) {
            parser.appendWarning("expected a term, using empty term");
        } else if (!content.isFromToAllowed() && ("from".equals(command) || "to".equals(command))) {
            parser.appendWarning("unexpected " + command + ", skipping to the end");
            parser.skipToEnd();
            return content;
        } else {
            allowFromTo = true;
        }

        Group result = new Group();
        result.push(content);

        parser.retreat(command.length());
        result.parseSubSup(parser, false, allowFromTo);
        return result;
    }

    private final Deque<Atom> children = new ArrayDeque<>();

    private Atom sub = null;
    private Atom sup = null;

    public void parseSubSup(HEParser parser, boolean onlySub, boolean allowFromTo) throws ParserException {
        if (parser.search("_", "sub", "Sub", "SUB")) {
            sub = parser.parseGroup(ParserMode.SUB_TERM);
            allowFromTo = false;
        } else if (allowFromTo && parser.search("from", "From", "FROM")) {
            sub = parser.parseGroup(ParserMode.TERM);
        }
        if ((!onlySub || sub != null) && (parser.search("^", "sup", "Sup", "SUP") ||
                (allowFromTo && parser.search("to", "To", "TO")))) {
            sup = parser.parseGroup(ParserMode.TERM);
        }
    }

    public Atom popSub() {
        Atom result = sub;
        sub = null;
        return result;
    }

    public Atom popSup() {
        Atom result = sup;
        sup = null;
        return result;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void push(Atom atom) {
        if (atom != null) {
            children.addLast(atom);
        }
    }

    public void addFirst(Atom atom) {
        if (atom != null) {
            children.addFirst(atom);
        }
    }

    public Atom pop() {
        return children.pollLast();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Atom atom : children) {
            sb.append(atom);
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

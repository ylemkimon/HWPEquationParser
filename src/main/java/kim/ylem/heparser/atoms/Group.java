package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class Group extends Atom {
    public static void init() {
        AtomMap.putTo(HEParser::parseGroups, "{");
        AtomMap.putTo(Group::parseSubSupp, "_", "^", "sub", "sup", "from", "to");
    }

    // XXX: separate SubSup?
    private static Atom parseSubSupp(HEParser parser, String command) throws ParserException {
        Atom content = parser.popGroup();

        if (content == null) {
            throw parser.newUnexpectedException("a term", command);
        }
        if (!content.isFromToAllowed() && ("from".equals(command) || "to".equals(command))) {
            parser.appendWarning("Unexpected " + command);
            parser.parseGroups(null);
            return content;
        }

        Group result = new Group();
        result.push(content);

        parser.retreat(command.length());
        result.parseSubSup(parser, false, content.isFromToAllowed());
        return result;
    }

    private final Deque<Atom> children = new ArrayDeque<>();

    private Atom sub = null;
    private Atom sup = null;

    public Group() {
    }

    public void parseSubSup(HEParser parser, boolean onlySub) throws ParserException {
        parseSubSup(parser, onlySub, false);
    }

    private void parseSubSup(HEParser parser, boolean onlySub, boolean allowFromTo) throws ParserException {
        if (parser.search("_", "sub", "Sub", "SUB")) {
            sub = parser.nextGroup(true);
            allowFromTo = false;
        } else if (allowFromTo && parser.search("from", "From", "FROM")) {
            sub = parser.nextGroup();
        }
        if ((!onlySub || sub != null) && (parser.search("^", "sup", "Sup", "SUP") ||
                (allowFromTo && parser.search("to", "To", "TO")))) {
            sup = parser.nextGroup();
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
        children.addLast(atom);
    }

    public Atom pop() {
        return children.pollLast();
    }

    @Override
    public String toLaTeX(int flag) {
        StringBuilder sb = new StringBuilder();
        for (Atom atom : children) {
            sb.append(atom.toLaTeX(flag));
        }
        if (sub != null) {
            sb.append("_{");
            sb.append(sub.toLaTeX(flag));
            sb.append('}');
        }
        if (sup != null) {
            sb.append("^{");
            sb.append(sup.toLaTeX(flag));
            sb.append('}');
        }
        return sb.toString();
    }

}

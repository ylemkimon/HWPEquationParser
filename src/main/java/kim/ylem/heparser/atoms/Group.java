package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class Group implements Atom {
    private static final long serialVersionUID = 2901822524810878507L;

    private final Deque<Atom> children = new ArrayDeque<>();

    public static void init() {
        AtomMap.register(Group::parse, "{");
    }

    private static @NotNull Atom parse(HEParser parser, @SuppressWarnings("unused") String function) throws ParserException {
        Atom result = parser.parseGroup(ParserMode.GROUP);
        parser.expect('}', "end of group", true);
        return result;
    }

    @Contract(pure = true)
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

    @Nullable
    public Atom pop() {
        return children.pollLast();
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        for (Atom atom : children) {
            sb.append(atom);
        }
        return sb.toString();
    }
}

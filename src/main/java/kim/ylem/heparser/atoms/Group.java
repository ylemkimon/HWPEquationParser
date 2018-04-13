package kim.ylem.heparser.atoms;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Group extends Atom {
    private final Deque<Atom> children = new LinkedList<>();

    @Override
    protected String toLaTeX(int flag) {
        return children.stream()
                .map(child -> child.toLaTeX(flag))
                .collect(Collectors.joining());
    }

    public void addChild(Atom atom) {
        children.add(atom);
    }
}

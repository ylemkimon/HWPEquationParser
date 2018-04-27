package kim.ylem.heparser.atoms;

import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class Group implements Atom {
    public Group() {
    }

    public static void init() {
        AtomMap.put("{", HEParser::parseGroups);
    }

    private final Deque<Atom> children = new ArrayDeque<>();

    @Override
    public String toLaTeX(int flag) {
        return children.stream()
                .map(child -> child.toLaTeX(flag))
                .collect(Collectors.joining());
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void push(Atom atom) {
        children.addLast(atom);
    }

    public Atom pop() {
        // FIXME: removeLast?
        return children.pollLast();
    }

}

package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.*;
import java.util.stream.Collectors;

public final class MatrixAtom implements Atom {
    private static final Map<String, String> matrixMap = new HashMap<>(9);

    static {
        matrixMap.put("matrix", "matrix");
        matrixMap.put("pmatrix", "pmatrix");
        matrixMap.put("bmatrix", "bmatrix");
        matrixMap.put("dmatrix", "vmatrix");
        matrixMap.put("cases", "cases");
        matrixMap.put("eqalign", "aligned");
        matrixMap.put("pile", "cases");
        matrixMap.put("lpile", "vmatrix");
        matrixMap.put("rpile", "aligned");
    }

    public static void init() {
        AtomMap.putAll(matrixMap.keySet(), MatrixAtom::parse);
    }

    public static MatrixAtom parse(HEParser parser, String function) throws ParserException {
        parser.skipWhitespaces();
        char next = parser.next();
        if (next != '{') {
            throw parser.newUnexpectedException("start of matrix, {", Character.toString(next));
        }

        MatrixAtom matrix = new MatrixAtom(function);
        do {
            Queue<Group> row = new ArrayDeque<>();
            do {
                row.add(parser.parseGroups(null));
                next = parser.next();
            } while (next == '&');
            matrix.addRow(row);
        } while (next == '#');
        return matrix;
    }

    private final Queue<Queue<Group>> rows = new ArrayDeque<>();
    private final String function;

    private MatrixAtom(String function) {
        this.function = matrixMap.get(function.toLowerCase());
    }

    private void addRow(Queue<Group> row) {
        rows.add(row);
    }

    public Atom simplify() {
        return rows.size() == 1 && rows.peek().size() == 1 ? rows.remove().remove() : this;
    }

    @Override
    public String toLaTeX(int flag) {
        String result = rows.stream()
                .map(row -> row.stream()
                        .map(group -> group.toLaTeX(flag))
                        .collect(Collectors.joining("&")))
                .collect(Collectors.joining("\\\\"));
        return "\\begin{" + function + '}' + result + "\\end{" + function + '}';
    }

}

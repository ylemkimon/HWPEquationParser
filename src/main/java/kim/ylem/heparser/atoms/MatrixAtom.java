package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.*;
import java.util.stream.Collectors;

public final class MatrixAtom extends Atom {
    private static final Map<String, String> matrixMap = new HashMap<>(10);

    static {
        matrixMap.put("matrix", "matrix");
        matrixMap.put("pmatrix", "pmatrix");
        matrixMap.put("bmatrix", "bmatrix");
        matrixMap.put("dmatrix", "vmatrix");
        matrixMap.put("cases", "cases");
        matrixMap.put("eqalign", "aligned");

        matrixMap.put("col", "arrayc");
        matrixMap.put("ccol", "arrayc");
        matrixMap.put("pile", "arrayc");
        matrixMap.put("cpile", "arrayc");
        matrixMap.put("lcol", "arrayl");
        matrixMap.put("lpile", "arrayl");
        matrixMap.put("rcol", "arrayr");
        matrixMap.put("rpile", "arrayr");
    }

    public static void init() {
        AtomMap.putAll(matrixMap.keySet(), MatrixAtom::parse);
    }

    public static Atom parse(HEParser parser, String command) throws ParserException {
        parser.skipWhitespaces();
        char next = parser.next();
        if (next != '{') {
            throw parser.newUnexpectedException("start of matrix, {", Character.toString(next));
        }

        MatrixAtom matrix = new MatrixAtom(command);
        int size = 0;
        do {
            Queue<Atom> row = new ArrayDeque<>();
            do {
                row.add(parser.parseImplicitGroup(null));
                size++;
                next = parser.next();
            } while (next == '&');
            matrix.addRow(row);
        } while (next == '#');

        return size != 1 || !("matrix".equals(command) || "eqalign".equals(command)
                || "col".equals(command) || command.endsWith("pile")) ? matrix : matrix.rows.remove().remove();
    }

    private final Queue<Queue<Atom>> rows = new ArrayDeque<>();
    private final String function;
    private int cols = 0;

    private MatrixAtom(String command) {
        function = matrixMap.get(command);
    }

    private void addRow(Queue<Atom> row) {
        rows.add(row);
        if (row.size() > cols) {
            cols = row.size();
        }
    }

    @Override
    public String toLaTeX(int flag) {
        String result = rows.stream()
                .map(row -> row.stream()
                        .map(group -> group.toLaTeX(flag))
                        .collect(Collectors.joining("&")))
                .collect(Collectors.joining("\\\\"));

        if (function.startsWith("array")) {
            char[] alignment = new char[cols];
            for (int i = 0; i < cols; i++) {
                alignment[i] = function.charAt(5);
            }
            return "\\begin{array}{" + new String(alignment) + '}' + result + "\\end{array}";
        }

        return "\\begin{" + function + '}' + result + "\\end{" + function + '}';
    }

}

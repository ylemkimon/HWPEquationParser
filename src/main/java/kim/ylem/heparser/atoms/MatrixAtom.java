package kim.ylem.heparser.atoms;

import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public final class MatrixAtom implements Atom {
    private static final Map<String, String> matrixMap = new HashMap<>(14);

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
        AtomMap.putAll(matrixMap.keySet(), HEParser::parseMatrix);
    }

    private final String function;
    private final Queue<Queue<Atom>> rows;
    private final int colCount;

    public MatrixAtom(String command, Queue<Queue<Atom>> rows, int colCount) {
        function = matrixMap.get(command);
        this.rows = rows;
        this.colCount = colCount;
    }

    @Override
    public String toString() {
        String result = rows.stream()
                .map(row -> row.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("&")))
                .collect(Collectors.joining("\\\\"));

        if (function.startsWith("array")) {
            char[] alignment = new char[colCount];
            for (int i = 0; i < colCount; i++) {
                alignment[i] = function.charAt(5);
            }
            return "\\begin{array}{" + new String(alignment) + '}' + result + "\\end{array}";
        }

        return "\\begin{" + function + '}' + result + "\\end{" + function + '}';
    }

}

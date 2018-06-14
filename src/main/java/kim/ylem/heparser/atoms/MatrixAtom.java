package kim.ylem.heparser.atoms;

import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MatrixAtom implements Atom {
    private static final long serialVersionUID = 641351819632980790L;
    private static final Map<String, String> matrixMap = new HashMap<>(14);

    static {
        matrixMap.put("matrix", "matrix");
        matrixMap.put("pmatrix", "pmatrix");
        matrixMap.put("bmatrix", "bmatrix");
        matrixMap.put("dmatrix", "vmatrix");
        matrixMap.put("cases", "cases");
        matrixMap.put("eqalign", "alignedat");

        matrixMap.put("col", "arrayc");
        matrixMap.put("ccol", "arrayc");
        matrixMap.put("pile", "arrayc");
        matrixMap.put("cpile", "arrayc");
        matrixMap.put("lcol", "arrayl");
        matrixMap.put("lpile", "arrayl");
        matrixMap.put("rcol", "arrayr");
        matrixMap.put("rpile", "arrayr");
    }

    private final String function;
    private final Queue<Queue<Atom>> rows;
    private final int colCount;

    public MatrixAtom(String command, Queue<Queue<Atom>> rows, int colCount) {
        function = matrixMap.get(command);
        this.rows = rows;
        this.colCount = colCount;
    }

    public static void init() {
        AtomMap.register(HEParser::parseMatrix, matrixMap.keySet());
    }

    @Contract(pure = true)
    public static boolean hasNoDelimiters(String command) {
        return "matrix".equals(command) || "eqalign".equals(command) ||
                command.endsWith("col") || command.endsWith("pile");
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        Function<Queue<Atom>, String> rowToString;
        String func = function;
        String argument = null;

        if ("alignedat".equals(func)) {
            rowToString = colCount == 1 ? row -> '&' + row.remove().toString()
                    : row -> row.remove().toString() + '&' + row.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("&&"));
            argument = Integer.toString(colCount);
        } else {
            rowToString = row -> row.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("&"));

            if (func.startsWith("array")) {
                char[] alignment = new char[colCount];
                for (int i = 0; i < colCount; i++) {
                    alignment[i] = func.charAt(5);
                }
                func = "array";
                argument = new String(alignment);
            }
        }

        return "\\begin{" + func + '}' + (argument != null ? '{' + argument  + '}' : "") +
                rows.stream()
                        .map(rowToString)
                        .collect(Collectors.joining("\\\\")) + "\\end{" + func + '}';
    }
}

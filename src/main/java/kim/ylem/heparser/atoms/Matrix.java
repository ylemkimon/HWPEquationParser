package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Matrix extends Atom {
    private static final HashMap<String, String> matrixMap = new HashMap<>();

    public static void register() {
        matrixMap.put("matrix", "matrix");
        matrixMap.put("pmatrix", "pmatrix");
        matrixMap.put("bmatrix", "bmatrix");
        matrixMap.put("dmatrix", "vmatrix");
        matrixMap.put("cases", "cases");
        matrixMap.put("eqalign", "aligned");
        matrixMap.put("pile", "cases");
        matrixMap.put("lpile", "vmatrix");
        matrixMap.put("rpile", "aligned");

        AtomMap.putAll(matrixMap, Matrix.class);
    }

    private LinkedList<LinkedList<Group>> rows = new LinkedList<>();

    private final String function;

    public Matrix(String function) {
        this.function = matrixMap.get(function.toLowerCase());
    }

    public void addRow(LinkedList<Group> row) {
        rows.add(row);
    }

    @Override
    protected String toLaTeX(int flag) {
        String result = rows.stream()
                .map(row -> row.stream()
                        .map(group -> group.toLaTeX(flag))
                        .collect(Collectors.joining("&")))
                .collect(Collectors.joining("\\\\"));
        return "\\begin{" + function + "}" + result + "\\end{" + function + "}";
    }
}

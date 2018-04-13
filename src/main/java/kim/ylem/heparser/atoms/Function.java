package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;
import java.util.Map;

public class Function extends Atom {
    private static final Map<String, String> functionMap = new HashMap<>();

    public static void register() {
        functionMap.put("and", "\\operatorname{and}");
        functionMap.put("arccos", "\\arccos");
        functionMap.put("arcsin", "\\arcsin");
        functionMap.put("arctan", "\\arctan");
        functionMap.put("arg", "\\arg");
        functionMap.put("cos", "\\cos");
        functionMap.put("cosec", "\\operatorname{cosec}");
        functionMap.put("cosh", "\\cosh");
        functionMap.put("cot", "\\cot");
        functionMap.put("coth", "\\coth");
        functionMap.put("csc", "\\csc");
        functionMap.put("deg", "\\deg");
        functionMap.put("dim", "\\dim");
        functionMap.put("exp", "\\exp");
        functionMap.put("Exp", "\\operatorname{Exp}");
        functionMap.put("for", "\\operatorname{for}");
        functionMap.put("gcd", "\\gcd");
        functionMap.put("hom", "\\hom");
        functionMap.put("if", "\\operatorname{if}");
        functionMap.put("ker", "\\ker");
        functionMap.put("lg", "\\lg");
        functionMap.put("ln", "\\ln");
        functionMap.put("Ln", "\\operatorname{Ln}");
        functionMap.put("log", "\\log");
        functionMap.put("max", "\\max");
        functionMap.put("min", "\\min");
        functionMap.put("mod", "\\operatorname{mod}");
        functionMap.put("Pr", "\\Pr");
        functionMap.put("sec", "\\sec");
        functionMap.put("sin", "\\sin");
        functionMap.put("sinh", "\\sinh");
        functionMap.put("tan", "\\tan");
        functionMap.put("tanh", "\\tanh");

        AtomMap.putAllExact(functionMap, Function.class);
    }

    private final String function;

    public Function(String function) {
        this.function = functionMap.get(function);
    }

    @Override
    protected String toLaTeX(int flag) {
        return function + " ";
    }
}

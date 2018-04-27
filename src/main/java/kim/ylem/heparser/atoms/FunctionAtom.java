package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;

public final class FunctionAtom implements Atom {
    private static final Map<String, String> functionMap = new HashMap<>(33);

    static {
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
    }

    public static void init() {
        AtomMap.putAllExact(functionMap.keySet(), FunctionAtom::parse);
    }

    private static FunctionAtom parse(HEParser parser, String function) throws ParserException {
        // TODO: argument behavior
        Atom content = parser.nextSymbol(7 - function.length());
        return new FunctionAtom(function, content);
    }

    private final String function;
    private final Atom content;

    private FunctionAtom(String function, Atom content) {
        this.function = functionMap.get(function);
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        return function + ' ' + content.toLaTeX(flag);
    }

}

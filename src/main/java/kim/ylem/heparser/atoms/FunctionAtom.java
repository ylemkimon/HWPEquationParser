package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;
import kim.ylem.heparser.ParserMode;

import java.util.HashMap;
import java.util.Map;

public final class FunctionAtom implements Atom {
    private static final Map<String, String> functionMap = new HashMap<>(37);

    static {
        functionMap.put("and", "\\operatorname{and}");
        functionMap.put("arccos", "\\arccos");
        functionMap.put("arcsin", "\\arcsin");
        functionMap.put("arctan", "\\arctan");
        functionMap.put("arg", "\\arg");
        functionMap.put("cos", "\\cos");
        functionMap.put("cosec", "\\operatorname{cosec}");
        functionMap.put("cosech", "\\operatorname{cosech}");
        functionMap.put("cosh", "\\cosh");
        functionMap.put("cot", "\\cot");
        functionMap.put("coth", "\\coth");
        functionMap.put("csc", "\\csc");
        functionMap.put("csch", "\\operatorname{csch}");
        functionMap.put("def", "\\operatorname{def}");
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
        functionMap.put("sech", "\\operatorname{sech}");
        functionMap.put("sin", "\\sin");
        functionMap.put("sinh", "\\sinh");
        functionMap.put("tan", "\\tan");
        functionMap.put("tanh", "\\tanh");
    }

    public static void init() {
        AtomMap.register(FunctionAtom::parse, functionMap.keySet(), true);
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        ParserMode mode = ParserMode.getArgumentMode(7 - command.length());
        Atom content = parser.parseGroup(mode);
        return new FunctionAtom(command, content);
    }

    private final String function;
    private final Atom content;

    private FunctionAtom(String command, Atom content) {
        function = functionMap.get(command);
        this.content = content;
    }

    @Override
    public String toString() {
        return function + ' ' + (content != null ? content : "");
    }

}

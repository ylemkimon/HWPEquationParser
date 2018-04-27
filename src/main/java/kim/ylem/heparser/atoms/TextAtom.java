package kim.ylem.heparser.atoms;

import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;

public class TextAtom implements Atom {
    private static final Map<String, Character> symbolMap = new HashMap<>(262);
    private static final Map<Character, String> latexMap = new HashMap<>(262);

    private static char puaChar = '\uE000';

    private static void put(String function, String latex) {
        symbolMap.put(function, puaChar);
        latexMap.put(puaChar, latex);
        puaChar++;
    }

    private static void put(String function, Character intermediate, String latex) {
        symbolMap.put(function, intermediate);
        latexMap.put(intermediate, latex);
    }

    static {
        latexMap.put('`', "\\, ");

        for (int i = 0; i < 26; i++) {
            symbolMap.put(Character.toString(puaChar++), (char) ('A' + i));
        }
        for (int i = 0; i < 26; i++) {
            latexMap.put(puaChar++, "\\mathrm " + (char) ('a' + i));
        }
        for (int i = 0; i < 9; i++) {
            symbolMap.put(Character.toString(puaChar++), (char) ('1' + i));
        }
        symbolMap.put(Character.toString(puaChar++), '0');
        symbolMap.put(Character.toString(puaChar++), '!');
        symbolMap.put(Character.toString(puaChar++), '@');
        symbolMap.put(Character.toString(puaChar++), '#');
        symbolMap.put(Character.toString(puaChar++), '$');
        symbolMap.put(Character.toString(puaChar++), '%');
        symbolMap.put(Character.toString(puaChar++), '*');
        symbolMap.put(Character.toString(puaChar++), '(');
        symbolMap.put(Character.toString(puaChar++), ')');
        symbolMap.put(Character.toString(puaChar++), '-');
        symbolMap.put(Character.toString(puaChar++), '=');
        symbolMap.put(Character.toString(puaChar++), '+');
        symbolMap.put(Character.toString(puaChar++), '[');
        symbolMap.put(Character.toString(puaChar++), ']');
        put("lbrace", puaChar++, "\\lbrace");
        put("rbrace", puaChar++, "\\rbrace");
        symbolMap.put(Character.toString(puaChar++), '|');
        symbolMap.put(Character.toString(puaChar++), ';');
        symbolMap.put(Character.toString(puaChar++), ':');
        symbolMap.put(Character.toString(puaChar++), '\'');
        symbolMap.put(Character.toString(puaChar++), '"');
        symbolMap.put(Character.toString(puaChar++), ',');
        symbolMap.put(Character.toString(puaChar++), '.');
        symbolMap.put(Character.toString(puaChar++), '/');
        symbolMap.put(Character.toString(puaChar++), '<');
        symbolMap.put(Character.toString(puaChar++), '>');
        symbolMap.put(Character.toString(puaChar++), '?');
        puaChar += 5;
        symbolMap.put(Character.toString(puaChar++), '\'');
        symbolMap.put(Character.toString(puaChar++), '`');

        puaChar += 38;
        put("Alpha", puaChar++, "A");
        put("Beta", puaChar++, "B");
        put("Gamma", puaChar++, "\\Gamma");
        put("Delta", puaChar++, "\\Delta");
        put("Epsilon", puaChar++, "E");
        put("Zeta", puaChar++, "Z");
        put("Eta", puaChar++, "H");
        put("Theta", puaChar++, "\\Theta");
        put("Iota", puaChar++, "I");
        put("Kappa", puaChar++, "K");
        put("Lambda", puaChar++, "\\Lambda");
        put("Mu", puaChar++, "M");
        put("Nu", puaChar++, "N");
        put("Xi", puaChar++, "\\Xi");
        put("Omicron", puaChar++, "O");
        put("Pi", puaChar++, "\\Pi");
        put("Rho", puaChar++, "P");
        put("Sigma", puaChar++, "\\Sigma");
        put("Tau", puaChar++, "T");
        put("Upsilon", puaChar++, "\\Upsilon");
        put("Phi", puaChar++, "\\Phi");
        put("Chi", puaChar++, "X");
        put("Psi", puaChar++, "\\Psi");
        put("Omega", puaChar++, "\\Omega");

        put("alpha", puaChar++, "\\alpha");
        put("beta", puaChar++, "\\beta");
        put("gamma", puaChar++, "\\gamma");
        put("delta", puaChar++, "\\delta");
        put("epsilon", puaChar++, "\\epsilon");
        put("zeta", puaChar++, "\\zeta");
        put("eta", puaChar++, "\\eta");
        put("theta", puaChar++, "\\theta");
        put("iota", puaChar++, "\\iota");
        put("kappa", puaChar++, "\\kappa");
        put("lambda", puaChar++, "\\lambda");
        put("mu", puaChar++, "\\mu");
        put("nu", puaChar++, "\\nu");
        put("xi", puaChar++, "\\xi");
        put("omicron", puaChar++, "\\omicron");
        put("pi", puaChar++, "\\pi");
        put("rho", puaChar++, "\\rho");
        put("sigma", puaChar++, "\\sigma");
        put("tau", puaChar++, "\\tau");
        put("upsilon", puaChar++, "\\upsilon");
        put("phi", puaChar++, "\\phi");
        put("chi", puaChar++, "\\chi");
        put("psi", puaChar++, "\\psi");
        put("omega", puaChar++, "\\omega");

        put("aleph", puaChar++, "\\aleph");
        put("hbar", puaChar++, "\\hbar");
        put("imath", puaChar++, "\\imath");
        put("jmath", puaChar++, "\\jmath");
        put("mho", puaChar++, "\\mho");
        put("ell", puaChar++, "\\ell");
        put("liter", puaChar++, "\\ell");
        put("wp", puaChar++, "\\wp");

        put("Imag", puaChar++, "\\Im");
        put("vartheta", puaChar++, "\\vartheta");
        put("varpi", puaChar++, "\\varpi");
        put("varsigma", puaChar++, "\\varsigma");
        put("varupsilon", puaChar++, "\\varupsilon");
        put("varphi", puaChar++, "\\varphi");
        puaChar++;
        put("partial", puaChar++, "\\partial");
        put("Lnot", puaChar++, "\\lnot");
        puaChar += 2;
        put("Deg", puaChar++, " ^\\circ");
        put("dagger", puaChar++, "\\dagger");
        put("ddagger", puaChar++, "\\ddagger");

        for (int i = 0; i < 26; i++) {
            put("Vec" + (char) ('A' + i), puaChar++, "\\mathbb " + (char) ('A' + i));
        }
        for (int i = 0; i < 26; i++) {
            symbolMap.put(Character.toString(puaChar++), (char) ('a' + i));
        }

        puaChar += 15;
        put("varepsilon", puaChar++, "\\varepsilon");

        puaChar += 2;

        symbolMap.put("＜", '<');
        symbolMap.put("＞", '>');
        symbolMap.put("，", ',');

        put("≦", "\\leq");
        put("≧", "\\geq");
        put("∴", "\\therefore");
        put("∵", "\\because");
        put("․", "\\cdot");
        put("ㆍ", "\\cdot");
        put("·", "\\cdot");
        put("‧", "\\cdot");
        put("°", " ^\\circ");
        put("∘", "\\circ");
        put("⌈", "\\lceil");
        put("⌉", "\\rceil");
        put("⌊", "\\lfloor");
        put("⌋", "\\rfloor");
        put("∍", "\\owns");
        put("±", "\\pm");
        put("÷", "\\div");
        put("∣", "\\vert");
        put("∥", "\\Vert");
        put("≦", "\\leq");
        put("⊐", "\\sqsupset");
        put("⊒", "\\sqsupseteq");
        put("▷", "\\triangleright");
        put("◁", "\\triangleleft");
        put("□", "\\square");
        put("→", "\\rightarrow");
        put("-+", "\\mp");
        put("->", "\\rightarrow");
        put("!=", "\\neq");
        put("+-", "\\pm");
        put("<->", "\\leftrightarrow");
        put("<<", "\\ll");
        put("<<<", "\\lll");
        put("<=", "\\leq");
        put("==", "\\equiv");
        put(">=", "\\geq");
        put(">>", "\\gg");
        put(">>>", "\\ggg");
        put("angle", "\\angle");
        put("approx", "\\approx");
        put("assert", "\\vdash");
        put("ast", "\\ast");
        put("asymp", "\\asymp");
        put("att", "\\mathop ※");
        put("base", "\\mathop ⌂");
        put("because", "\\because");
        put("benzene", "\\mathop ⌬");
        put("bigcirc", "\\bigcirc");
        put("bot", "\\bot");
        put("bullet", "\\bullet");
        put("cap", "\\cap");
        put("cdot", "\\cdot");
        put("cdots", "\\cdots");
        put("centigrade", "^{\\circ}C");
        put("circ", "\\circ");
        put("cong", "\\cong");
        put("cup", "\\cup");
        put("dashv", "\\dashv");
        put("ddots", "\\ddots");
        put("del", "\\nabla");
        put("diamond", "\\diamond");
        put("div", "\\div");
        put("divide", "\\div");
        put("dline", "\\Vert");
        put("doteq", "\\doteq");
        put("dotsaxis", "\\cdots");
        put("dotsdiag", "\\ddots");
        put("dotslow", "\\ldots");
        put("dotsvert", "\\vdots");
        put("dsum", "\\dotplus");
        put("emptyset", "\\emptyset");
        put("equiv", "\\equiv");
        put("exist", "\\exists");
        put("exists", "\\exists");
        put("fahrenheit", "^{\\circ}F");
        put("ge", "\\geq");
        put("geq", '≥', "\\geq");
        put("ggg", "\\ggg");
        put("hcross", "┼");
        put("hdown", "┬");
        put("hleft", "┤");
        put("hookleft", "\\hookleftarrow");
        put("hookright", "\\hookrightarrow");
        put("hright", "├");
        put("hund", "\\mathop ‰");
        put("hup", "┴");
        put("identical", "\\mathop ∷");
        put("in", "\\in");
        put("inf", "\\infty");
        put("infinity", "\\infty");
        put("infty", "\\infty");
        put("iso", "\\Bumpeq");
        put("land", "\\land");
        put("langle", "\\langle");
        put("laplace", "\\mathcal{L}");
        put("lbrace", "\\lbrace");
        put("lbrack", "\\lbrack");
        put("lceil", "\\lceil");
        put("ldots", "\\ldots");
        put("le", "\\leq");
        put("leq", '≤', "\\leq");
        put("lfloor", "\\lfloor");
        put("line", "\\vert");
        put("lll", "\\lll");
        put("lor", "\\lor");
        put("lslant", "\\diagup");
        put("mapsto", "\\mapsto");
        put("massert", "\\dashv");
        put("msangle", "\\measuredangle");
        put("nabla", "\\nabla");
        put("nearrow", "\\nearrow");
        put("ne", "\\neq");
        put("neg", "\\neg");
        put("neq", "\\neq");
        put("ni", "\\owns");
        put("nin", "\\notin");
        put("notin", "\\notin");
        put("nowns", "\\not\\owns");
        put("nsubset", "\\not\\subset");
        put("nsubseteq", "\\nsubseteq");
        put("nsupset", "\\not\\supset");
        put("nsupseteq", "\\nsupseteq");
        put("nwarrow", "\\nwarrow");
        put("odiv", "\\oslash");
        put("oslash", "\\oslash");
        put("odot", "\\odot");
        put("ominus", "\\ominus");
        put("oplus", "\\oplus");
        put("otimes", "\\otimes");
        put("owns", "\\owns");
        put("parallel", "\\parallel");
        put("phor", "═");
        put("plusminus", "\\pm");
        put("pver", "║");
        put("rangle", "\\rangle");
        put("rbrack", "\\rbrack");
        put("rceil", "\\rceil");
        put("reimage", "\\risingdotseq");
        put("rfloor", "\\rfloor");
        put("round", "\\partial");
        put("rslant", "\\diagdown");
        put("rtangle", "\\mathop ⊾");
        put("sangle", "\\sphericalangle");
        put("sdots", "\\kern3mu\\raise1mu{.}\\kern3mu\\raise6mu{.}\\kern3mu\\raise12mu{.}");
        put("searrow", "\\searrow");
        put("sim", "\\sim");
        put("simeq", "\\simeq");
        put("smallinter", "\\cap");
        put("smallprod", "\\prod");
        put("smallsum", "\\sum");
        put("smallunion", "\\cup");
        put("smcoprod", "\\coprod");
        put("sqcap", "\\sqcap");
        put("sqcup", "\\sqcup");
        put("sqsubset", "\\sqsubset");
        put("sqsubseteq", "\\sqsubseteq");
        put("sqsupset", "\\sqsupset");
        put("sqsupseteq", "\\sqsupseteq");
        put("star", "\\bigstar");
        put("subset", "\\subset");
        put("subseteq", "\\subseteq");
        put("succ", "\\succ");
        put("superset", "\\supset");
        put("supseteq", "\\supseteq");
        put("swarrow", "\\swarrow");
        put("therefore", "\\therefore");
        put("thou", "\\mathop ‱");
        put("times", "\\times");
        put("top", "\\top");
        put("triangle", "\\triangle");
        put("triangled", "\\triangledown");
        put("trianglel", "\\triangleleft");
        put("triangler", "\\triangleright");
        put("uplus", "\\uplus");
        put("vdash", "\\vdash");
        put("vdots", "\\vdots");
        put("vee", "\\vee");
        put("wedge", "\\wedge");
        put("well", "\\#");
        put("xor", "\\veebar");

        put("downarrow", "\\downarrow");
        put("larrow", "\\leftarrow");
        put("leftarrow", "\\leftarrow");
        put("lrarrow", "\\leftrightarrow");
        put("rarrow", "\\rightarrow");
        put("rightarrow", "\\rightarrow");
        put("udarrow", "\\updownarrow");
        put("uparrow", "\\uparrow");
        put("vert", "\\vert");

        put("Downarrow", "\\Downarrow");
        put("Larrow", "\\Leftarrow");
        put("Leftarrow", "\\Leftarrow");
        put("Lrarrow", "\\Leftrightarrow");
        put("Rarrow", "\\Rightarrow");
        put("Rightarrow", "\\Rightarrow");
        put("Udarrow", "\\Updownarrow");
        put("Uparrow", "\\Uparrow");
        put("Vert", "\\Vert");

        put("Forall", "\\forall");
        put("image", "\\fallingdotseq");
        put("Minusplus", "\\mp");
        put("Models", "\\models");
        put("prec", "\\prec");
        put("prep", "\\bot");
        put("prime", "'");
        put("propto", "\\propto");
    }

    public static void init() {
        AtomMap.putAll(symbolMap.keySet(), null);
        AtomMap.remove("Imag");
        AtomMap.remove("IMAGE");
        AtomMap.remove("Lnot");
        AtomMap.remove("Prec");
        AtomMap.remove("Prep");
        AtomMap.remove("Prime");
        AtomMap.remove("PRIME");
        AtomMap.remove("Propto");
        AtomMap.remove("Varepsilon");
        AtomMap.remove("VAREPSILON");
        AtomMap.remove("Varphi");
        AtomMap.remove("VARPHI");
        AtomMap.remove("Varpi");
        AtomMap.remove("VARPI");
        AtomMap.remove("Varsigma");
        AtomMap.remove("VARSIGMA");
        AtomMap.remove("Vartheta");
        AtomMap.remove("VARTHETA");
        AtomMap.remove("Varupsilon");
        AtomMap.remove("VARUPSILON");

        // TODO: AtomMap.put("\"", TextAtom::parse);
    }

    public static Character getIntermediateChar(String function) {
        return symbolMap.getOrDefault(function.charAt(0) + function.substring(1).toLowerCase(),
                symbolMap.get(function.toLowerCase()));
    }

    private static TextAtom parse(HEParser parser, String function) {
        return null;
    }

    private final String text;

    public TextAtom(String text) {
        this.text = text;
    }

    @Override
    public String toLaTeX(int flag) {
        // TODO det, arc, VecA case sensitivity, unicode
        String mathCommand = (flag & STYLE_ROMAN) == STYLE_ROMAN ? "\\mathrm{" : "";
        String textCommand = (flag & STYLE_BOLD) == STYLE_BOLD ? "\\textbf{" : "\\textrm{";

        StringBuilder result = new StringBuilder();
        String currentCommand = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (latexMap.containsKey(c)) {
                if (!currentCommand.isEmpty()) {
                    result.append('}');
                    currentCommand = "";
                }
                result.append(latexMap.get(c));
                result.append(' ');
            } else {
                String newCommand = c <= 0x7F ? mathCommand : textCommand;
                if (!newCommand.equals(currentCommand)) {
                    if (!currentCommand.isEmpty()) {
                        result.append('}');
                    }
                    result.append(newCommand);
                    currentCommand = newCommand;
                }
                result.append(c);
            }
        }
        if (!currentCommand.isEmpty()) {
            result.append('}');
        }
        return result.toString();
    }

}

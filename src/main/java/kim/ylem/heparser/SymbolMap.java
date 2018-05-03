package kim.ylem.heparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SymbolMap {
    private SymbolMap() {
    }

    private static final Map<String, Character> symbolMap = new HashMap<>(495);
    private static final Map<Character, String> latexMap = new HashMap<>(259);
    private static final Set<String> delimiters = new HashSet<>(16);

    private static void put(Character ch, String latex, String... commands) {
        for (String s : commands) {
            symbolMap.put(s, ch);
        }
        if (!latex.equals(ch.toString())) {
            latexMap.put(ch, latex);
        }
    }

    private static void putDelim(Character ch, String latex, String... commands) {
        put(ch, latex, commands);
        delimiters.add(ch.toString());
    }

    static {
        // delimiters
        putDelim('(', "(", "（", "lparen", "\uE044");
        putDelim(')', ")", "）", "rparen", "\uE045");
        putDelim('[', "[", "［", "lbrack", "\uE049");
        putDelim(']', "]", "］", "rbrack", "\uE04A");
        putDelim('{', "\\{", "｛", "lbrace", "\uE04B");
        putDelim('}', "\\}", "｝", "rbrace", "\uE04C");
        putDelim('⟨', "\\langle", "langle");
        putDelim('⟩', "\\rangle", "rangle");
        putDelim('<', "<", "＜", "\uE055");
        putDelim('>', ">", "＞", "\uE056");
        putDelim('|', "|", "vert", "line", "∣", "｜", "\uE04D");
        putDelim('‖', "\\|", "Vert", "dline", "parallel", "ǁ", "∥");
        putDelim('⌈', "\\lceil", "lceil", "\uE100");
        putDelim('⌉', "\\rceil", "rceil", "\uE102");
        putDelim('⌊', "\\lfloor", "lfloor", "\uE103");
        putDelim('⌋', "\\rfloor", "rfloor", "\uE104");

        for (int i = 0; i < 26; i++) {
            char upper = (char) ('A' + i);
            char lower = (char) ('a' + i);

            symbolMap.put(Character.toString((char) ('\uE000' + i)), upper); // uppercase
            symbolMap.put(Character.toString((char) ('\uE0E5' + i)), lower); // lowercase
            latexMap.put((char) ('\uE01A' + i), "\\mathrm " + lower); // roman lowercase

            // blackboard uppercase
            put((char) ('\uE0CB' + i), "\\mathbb " + upper, "Vec" + upper);
        }

        symbolMap.put("ℂ", '\uE0CD');
        symbolMap.put("ℍ", '\uE0D2');
        symbolMap.put("ℕ", '\uE0D8');
        symbolMap.put("ℙ", '\uE0DA');
        symbolMap.put("ℚ", '\uE0DB');
        symbolMap.put("ℝ", '\uE0DC');
        symbolMap.put("ℤ", '\uE0E4');

        for (int i = 0; i < 10; i++) {
            symbolMap.put(Character.toString((char) ('\uE034' + i)), (char) ('0' + (i + 1) % 10)); // numeral
        }

        // misc
        symbolMap.put("，", ',');
        symbolMap.put("att", '※');
        symbolMap.put("hcross", '┼');
        symbolMap.put("hdown", '┬');
        symbolMap.put("hleft", '┤');
        symbolMap.put("hright", '├');
        symbolMap.put("hund", '‰');
        symbolMap.put("hup", '┴');
        symbolMap.put("phor", '═');
        symbolMap.put("pver", '║');
        symbolMap.put("thou", '‱');

        latexMap.put('`', "\\,");
        latexMap.put('□', "\\square");
        latexMap.put('<', "<"); // escape HTML

        // Greek uppercase
        put('Α', "A", "Alpha", "\uE085");
        put('Β', "B", "Beta", "\uE086");
        put('Γ', "\\Gamma", "Gamma", "\uE087");
        put('Δ', "\\Delta", "Delta", "\uE088");
        put('Ε', "E", "Epsilon", "\uE089");
        put('Ζ', "Z", "Zeta", "\uE08A");
        put('Η', "H", "Eta", "\uE08B");
        put('Θ', "\\Theta", "Theta", "\uE08C");
        put('Ι', "I", "Iota", "\uE08D");
        put('Κ', "K", "Kappa", "\uE08E");
        put('Λ', "\\Lambda", "Lambda", "\uE08F");
        put('Μ', "M", "Mu", "\uE090");
        put('Ν', "N", "Nu", "\uE091");
        put('Ξ', "\\Xi", "Xi", "\uE092");
        put('Ο', "O", "Omicron", "\uE093");
        put('Π', "\\Pi", "Pi", "\uE094");
        put('Ρ', "P", "Rho", "\uE095");
        put('Σ', "\\Sigma", "Sigma", "\uE096");
        put('Τ', "T", "Tau", "\uE097");
        put('Υ', "\\Upsilon", "Upsilon", "\uE098");
        put('Φ', "\\Phi", "Phi", "\uE099");
        put('Χ', "X", "Chi", "\uE09A");
        put('Ψ', "\\Psi", "Psi", "\uE09B");
        put('Ω', "\\Omega", "Omega", "\uE09C");

        // Greek lowercase
        put('α', "\\alpha", "alpha", "\uE09D");
        put('β', "\\beta", "beta", "\uE09E");
        put('γ', "\\gamma", "gamma", "\uE09F");
        put('δ', "\\delta", "delta", "\uE0A0");
        put('ϵ', "\\epsilon", "epsilon", "\uE0A1");
        put('ζ', "\\zeta", "zeta", "\uE0A2");
        put('η', "\\eta", "eta", "\uE0A3");
        put('θ', "\\theta", "theta", "\uE0A4");
        put('ι', "\\iota", "iota", "\uE0A5");
        put('κ', "\\kappa", "kappa", "\uE0A6");
        put('λ', "\\lambda", "lambda", "\uE0A7");
        put('μ', "\\mu", "mu", "\uE0A8");
        put('ν', "\\nu", "nu", "\uE0A9");
        put('ξ', "\\xi", "xi", "\uE0AA");
        put('ο', "\\omicron", "omicron", "\uE0AB");
        put('π', "\\pi", "pi", "\uE0AC");
        put('ρ', "\\rho", "rho", "\uE0AD");
        put('σ', "\\sigma", "sigma", "\uE0AE");
        put('τ', "\\tau", "tau", "\uE0AF");
        put('υ', "\\upsilon", "upsilon", "\uE0B0");
        put('ϕ', "\\phi", "phi", "\uE0B1");
        put('χ', "\\chi", "chi", "\uE0B2");
        put('ψ', "\\psi", "psi", "\uE0B3");
        put('ω', "\\omega", "omega", "\uE0B4");

        // varGreek
        put('ε', "\\varepsilon", "varepsilon", "\uE10E");
        put('ϑ', "\\vartheta", "vartheta", "\uE0BE");
        put('ϖ', "\\varpi", "varpi", "\uE0BF");
        put('ϱ', "\\varrho", "varrho");
        put('ς', "\\varsigma", "varsigma", "\uE0C0");
        put('\uE0C1', "\\varUpsilon", "varupsilon");
        put('φ', "\\varphi", "varphi", "\uE0C2");

        // double arrows
        put('⇓', "\\Downarrow", "Downarrow");
        put('⇐', "\\Leftarrow", "Larrow", "Leftarrow");
        put('⇔', "\\Leftrightarrow", "Lrarrow");
        put('⇒', "\\Rightarrow", "Rarrow", "Rightarrow");
        put('⇕', "\\Updownarrow", "Udarrow");
        put('⇑', "\\Uparrow", "Uparrow");

        // arrows
        put('↑', "\\uparrow", "uparrow");
        put('↕', "\\updownarrow", "udarrow");
        put('→', "\\rightarrow", "rarrow", "rightarrow", "->");
        put('↔', "\\leftrightarrow", "lrarrow", "<->");
        put('←', "\\leftarrow", "larrow", "leftarrow", "<-");
        put('↓', "\\downarrow", "downarrow");

        put('!', "!", "！", "\uE03E");
        put('@', "@", "＠", "\uE03F");
        put('*', "*", "＊", "\uE043");
        put('-', "-", "－", "\uE046");
        put('=', "=", "＝", "\uE047");
        put('+', "+", "＋", "\uE048");
        put(';', ";", "；", "\uE04E");
        put(':', ":", "：", "\uE04F");
        put('"', "\"", "＂", "\uE051");
        put(',', ",", "，", "\uE052");
        put('.', ".", "．", "\uE053");
        put('/', "/", "／", "\uE054");
        put('?', "?", "？", "\uE057");
        put('#', "\\#", "＃", "\uE040");
        put('&', "\\&", "＆");
        put('$', "\\$", "＄", "\uE041");
        put('%', "\\%", "％", "\uE042");
        put('_', "\\_", "＿");
        put('^', "\\hat{}", "＾");
        put('≠', "\\neq", "!=", "ne", "neq");
        put('≡', "\\equiv", "==", "equiv");
        put('±', "\\pm", "+-", "plusminus");
        put('∓', "\\mp", "-+", "minusplus");
        put('≤', "\\leq", "<=", "le", "leq", "≦");
        put('≪', "\\ll", "<<");
        put('⋘', "\\lll", "<<<", "lll");
        put('≥', "\\geq", ">=", "ge", "geq", "≧");
        put('≫', "\\gg", ">>");
        put('⋙', "\\ggg", ">>>", "ggg");
        put('ℵ', "\\aleph", "aleph", "\uE0B5");
        put('⨿', "\\amalg", "amalg");
        put('∠', "\\angle", "angle");
        put('≈', "\\approx", "approx");
        put('∗', "\\ast", "ast");
        put('≍', "\\asymp", "asymp");
        put('\\', "\\backslash", "backslash");
        put('⌂', "\\house", "base");
        put('∵', "\\because", "because");
        put('⌬', "\\varhexagonlrbonds", "benzene");
        put('◯', "\\bigcirc", "bigcirc");
        put('⊥', "\\bot", "bot", "prep");
        put('∙', "\\bullet", "bullet");
        put('∩', "\\cap", "cap", "smallinter");
        put('⋅', "\\cdot", "cdot", "․", "ㆍ", "·", "‧");
        put('⋯', "\\cdots", "cdots", "dotsaxis");
        put('℃', "^{\\circ}C", "centigrade");
        put('∘', "\\circ", "circ");
        put('≅', "\\cong", "cong");
        put('∪', "\\cup", "cup", "smallunion");
        put('†', "\\dagger", "dagger", "\uE0C9");
        put('⊣', "\\dashv", "dashv", "massert");
        put('‡', "\\ddagger", "ddagger", "\uE0CA");
        put('⋱', "\\ddots", "ddots", "dotsdiag");
        put('°', "^\\circ", "deg", "\uE0C8");
        put('⋄', "\\diamond", "diamond");
        put('÷', "\\div", "div", "divide");
        put('≐', "\\doteq", "doteq");
        put('∔', "\\dotplus", "dsum");
        put('ℓ', "\\ell", "ell", "liter", "\uE0BA", "\uE0BB");
        put('∅', "\\emptyset", "emptyset");
        put('∃', "\\exists", "exist", "exists");
        put('℉', "^{\\circ}F", "fahrenheit");
        put('∀', "\\forall", "forall");
        put('ℏ', "\\hbar", "hbar", "\uE0B6");
        put('↩', "\\hookleftarrow", "hookleft");
        put('↪', "\\hookrightarrow", "hookright");
        put('∷', "\\mathop ∷", "identical");
        put('ℑ', "\\Im", "IMAG", "\uE0BD");
        put('≒', "\\fallingdotseq", "image");
        put('ı', "\\imath", "imath", "\uE0B7");
        put('∈', "\\in", "in");
        put('∞', "\\infty", "inf", "infinity", "infty");
        put('≎', "\\Bumpeq", "iso");
        put('ȷ', "\\jmath", "jmath", "\uE0B8");
        put('ℒ', "\\mathcal{L}", "laplace");
        put('…', "\\ldots", "ldots", "dotslow");
        put('╱', "\\diagup", "lslant");
        put('↦', "\\mapsto", "mapsto");
        put('℧', "\\mho", "mho", "\uE0B9");
        put('⊨', "\\models", "models");
        put('∡', "\\measuredangle", "msangle");
        put('∇', "\\nabla", "nabla", "del");
        put('↗', "\\nearrow", "nearrow");
        put('¬', "\\neg", "neg", "lnot", "\uE0C5");
        put('≢', "\\nequiv", "nequiv");
        put('∋', "\\owns", "ni", "owns", "∍");
        put('∉', "\\notin", "nin", "notin");
        put('∌', "\\not\\owns", "nowns");
        put('⊄', "\\not\\subset", "nsubset");
        put('⊈', "\\nsubseteq", "nsubseteq");
        put('⊅', "\\not\\supset", "nsupset");
        put('⊉', "\\nsupseteq", "nsupseteq");
        put('↖', "\\nwarrow", "nwarrow");
        put('⊘', "\\oslash", "odiv", "oslash");
        put('⊙', "\\odot", "odot");
        put('⊖', "\\ominus", "ominus");
        put('⊕', "\\oplus", "oplus");
        put('⊗', "\\otimes", "otimes");
        put('≺', "\\prec", "prec");
        put('\'', "\'", "prime", "\uE050");
        put('∝', "\\propto", "propto");
        put('≓', "\\risingdotseq", "reimage");
        put('∂', "\\partial", "round", "partial", "\uE0C4");
        put('╲', "\\diagdown", "rslant");
        put('⊾', "\\measuredrightangle", "rtangle");
        put('∢', "\\sphericalangle", "sangle");
        put('⋰', "\\iddots", "sdots");
        put('↘', "\\searrow", "searrow");
        put('∼', "\\sim", "sim");
        put('≃', "\\simeq", "simeq");
        put('∫', "\\smallint", "smallint", "\uE05B", "\uE072");
        put('∮', "\\smalloint", "smalloint", "\uE075");
        put('∏', "\\prod", "smallprod");
        put('∑', "\\sum", "smallsum");
        put('∐', "\\coprod", "smcoprod");
        put('⊓', "\\sqcap", "sqcap");
        put('⊔', "\\sqcup", "sqcup");
        put('⊏', "\\sqsubset", "sqsubset");
        put('⊑', "\\sqsubseteq", "sqsubseteq");
        put('⊐', "\\sqsupset", "sqsupset");
        put('⊒', "\\sqsupseteq", "sqsupseteq");
        put('★', "\\bigstar", "star");
        put('⊂', "\\subset", "subset");
        put('⊆', "\\subseteq", "subseteq");
        put('≻', "\\succ", "succ");
        put('⊃', "\\supset", "supset", "superset");
        put('⊇', "\\supseteq", "supseteq");
        put('↙', "\\swarrow", "swarrow");
        put('∴', "\\therefore", "therefore");
        put('×', "\\times", "times");
        put('⊤', "\\top", "top");
        put('△', "\\triangle", "triangle");
        put('▽', "\\triangledown", "triangled");
        put('◃', "\\triangleleft", "trianglel", "◁");
        put('▹', "\\triangleright", "triangler", "▷");
        put('⊎', "\\uplus", "uplus");
        put('⊢', "\\vdash", "vdash", "assert", "⊦");
        put('⋮', "\\vdots", "vdots", "dotsvert");
        put('∨', "\\vee", "vee", "lor");
        put('∧', "\\wedge", "wedge", "land");
        put('⌗', "\\viewdata", "well");
        put('℘', "\\wp", "wp", "\uE0BC");
        put('⊻', "\\veebar", "xor");
    }

    public static void init() {
        AtomMap.putAll(symbolMap.keySet(), null);
        AtomMap.addSpecial("IMAG");
        AtomMap.addSpecial("prime");
        AtomMap.addSpecial("varepsilon");
        AtomMap.addSpecial("varphi");
        AtomMap.addSpecial("varpi");
        AtomMap.addSpecial("varsigma");
        AtomMap.addSpecial("vartheta");
        AtomMap.addSpecial("varupsilon");
        AtomMap.addSpecial("varrho");
        for (char c = 'A'; c <= 'Z'; c++) {
            AtomMap.addSpecial("Vec" + c);
        }
    }

    public static Character getSymbol(String function) {
        return symbolMap.get(function);
    }

    public static String getLaTeX(char c) {
        return latexMap.get(c);
    }

    public static boolean isDelimiter(String s) {
        return delimiters.contains(s);
    }
}

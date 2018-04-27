package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

import java.util.HashMap;
import java.util.Map;

public final class LeftRightAtom implements Atom {
    private static final Map<String, String> delimMap = new HashMap<>(33);

    static {
        delimMap.put("(", "(");
        delimMap.put("\uE044", "(");
        delimMap.put(")", ")");
        delimMap.put("\uE045", ")");
        delimMap.put("[", "[");
        delimMap.put("\uE049", "[");
        delimMap.put("lbrack", "[");
        delimMap.put("]", "]");
        delimMap.put("\uE04A", "]");
        delimMap.put("rbrack", "]");
        delimMap.put("{", "\\lbrace ");
        delimMap.put("\uE04B", "\\lbrace ");
        delimMap.put("lbrace", "\\lbrace ");
        delimMap.put("}", "\\rbrace ");
        delimMap.put("\uE04C", "\\rbrace ");
        delimMap.put("rbrace", "\\rbrace ");
        delimMap.put("<", "<");
        delimMap.put("\uE055", "<");
        delimMap.put("langle", "<");
        delimMap.put("⌈", "\\lceil ");
        delimMap.put("lceil", "\\lceil ");
        delimMap.put("⌉", "\\rceil ");
        delimMap.put("rceil", "\\rceil ");
        delimMap.put("⌊", "\\lfloor ");
        delimMap.put("lfloor",  "\\lfloor ");
        delimMap.put("⌋", "\\rfloor ");
        delimMap.put("rfloor",  "\\rfloor ");
        delimMap.put("|", ">");
        delimMap.put("\uE04D", "|");
        delimMap.put("vert", "\\vert ");
        delimMap.put("∣", "\\vert ");
        delimMap.put("line", "\\vert ");
        delimMap.put("Vert", "\\Vert ");
        delimMap.put("∥", "\\Vert ");
        delimMap.put("dline", "\\Vert ");
        delimMap.put(".", null);
    }

    public static void init() {
        AtomMap.put("left", LeftRightAtom::parse);
        AtomMap.put("right", LeftRightAtom::parse);
    }

    private String leftDelim;
    private String rightDelim;
    private Atom content;

    public LeftRightAtom(String leftDelim, String rightDelim, Atom content) {
        this.leftDelim = delimMap.get(leftDelim);
        this.rightDelim = delimMap.get(rightDelim);
        this.content = content;
    }

    private static Atom parse(HEParser parser, String function) throws ParserException {
        parser.skipWhitespaces();
        String delim = parser.search(delimMap.keySet().toArray(new String[0]));
        String rightDelim;

        if (delim == null) {
            parser.appendError(function + " delimiter not found, using empty delimiter");
            delim = ".";
        }

        if ("right".equals(function.toLowerCase())) {
            parser.appendError("Unexpected \\right");
            return new LeftRightAtom(null, delim, null);
        }

        Atom content = parser.parseGroups(function);

        if (parser.search("right", "Right", "RIGHT") != null) {
            rightDelim = parser.search(delimMap.keySet().toArray(new String[0]));
            if (rightDelim == null) {
                parser.appendError("Right delimiter not found, using empty delimiter");
                rightDelim = ".";
            }
        } else {
            parser.appendError("\\right not found, using empty delimiter");
            rightDelim = ".";
        }

        return new LeftRightAtom(delim, rightDelim, content);
    }

    @Override
    public String toLaTeX(int flag) {
        if (content == null) {
            return rightDelim;
        } else if (leftDelim == null && rightDelim == null) {
            return content.toLaTeX(flag);
        } else {
            return "\\left" + (leftDelim != null ? leftDelim : ".") + " " + content.toLaTeX(flag) + "\\right" + (rightDelim != null ? rightDelim : ".") + " ";
        }
    }

}

package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class FontAtom implements Atom {
    public static void init() {
        AtomMap.put("\\", FontAtom::parse);
        AtomMap.put("rm", FontAtom::parse);
        AtomMap.put("it", FontAtom::parse);
    }

    private static FontAtom parse(HEParser parser, String function) throws ParserException {
        Atom content = parser.parseGroups(null);
        return new FontAtom(function, content);
    }

    private final String function;
    private final Atom content;

    private FontAtom(String function, Atom content) {
        this.function = function.toLowerCase();
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        return content.toLaTeX("it".equals(function) ? (flag & ~STYLE_ROMAN) : (flag | STYLE_ROMAN));
    }
}

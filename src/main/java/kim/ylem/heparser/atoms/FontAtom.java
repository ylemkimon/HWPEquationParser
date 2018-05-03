package kim.ylem.heparser.atoms;

import kim.ylem.ParserException;
import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class FontAtom extends Atom {
    public static void init() {
        AtomMap.putTo(FontAtom::parse, "\\", "rm", "it");
    }

    private static Atom parse(HEParser parser, String command) throws ParserException {
        Atom content = parser.parseGroups(command);
        return new FontAtom(command, content);
    }

    private final String command;
    private final Atom content;

    private FontAtom(String command, Atom content) {
        this.command = command;
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        return content.toLaTeX("it".equals(command) ? (flag & ~STYLE_ROMAN) : (flag | STYLE_ROMAN));
    }
}

package kim.ylem.heparser.atoms;

import kim.ylem.heparser.Atom;
import kim.ylem.heparser.AtomMap;
import kim.ylem.heparser.HEParser;

public final class FontAtom {
    // TODO: move to GroupParser?
    public static void init() {
        AtomMap.putTo(FontAtom::parse, "\\", "rm", "it");
    }

    private static Atom parse(HEParser parser, String command) {
        parser.getGroupParser().setOptions(parser.getCurrentOptions().withRomanFont(!"it".equals(command)));

        if ("\\".equals(command) && parser.hasNext()) {
            char c = parser.next();
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                StringBuilder tokenBuilder = new StringBuilder(8).append(c);
                c = parser.peek();
                while (tokenBuilder.length() < 8 && (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z'))) {
                    tokenBuilder.append(parser.next());
                    c = parser.peek();
                }
                if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
                    parser.next();
                }
                return new TextAtom(tokenBuilder.toString(), parser.getCurrentOptions());
            } else if (Character.isWhitespace(c)) {
                return new TextAtom("~", parser.getCurrentOptions());
            } else {
                return new TextAtom(Character.toString(c), parser.getCurrentOptions());
            }
        }
        return null;
    }
}

package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

public class Font extends Atom {
    public static void register() {
        AtomMap.put("rm", Font.class);
        AtomMap.put("it", Font.class);
    }

    private final String function;
    private final Group content;

    public Font(String function, Group content) {
        this.function = function.toLowerCase();
        this.content = content;
    }

    @Override
    public String toLaTeX(int flag) {
        return content.toLaTeX("rm".equals(function) ? (flag | STYLE_ROMAN) : (flag & ~STYLE_ROMAN));
    }
}

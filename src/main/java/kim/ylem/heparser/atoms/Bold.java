package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

public class Bold extends Atom {
    public static void register() {
        AtomMap.put("bold", Bold.class);
    }

    private final Group content;

    public Bold(Group content) {
        this.content = content;
    }

    @Override
    protected String toLaTeX(int flag) {
        return "\\boldsymbol{" + content.toLaTeX(flag | STYLE_BOLD) + "}";
    }
}

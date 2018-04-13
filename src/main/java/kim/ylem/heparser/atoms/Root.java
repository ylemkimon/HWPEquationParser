package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

public class Root extends Atom {
    public static void register() {
        AtomMap.put("sqrt", Root.class);
        AtomMap.put("root", Root.class);
    }

    private final Group degree;
    private final Group content;

    public Root(Group degree, Group content) {
        this.degree = degree;
        this.content = content;
    }

    @Override
    protected String toLaTeX(int flag) {
        String result = "\\sqrt";
        if (degree != null) {
            result += "[" + degree.toLaTeX(flag) + "]";
        }
        return result + "{" + content.toLaTeX(flag) + "}";
    }
}

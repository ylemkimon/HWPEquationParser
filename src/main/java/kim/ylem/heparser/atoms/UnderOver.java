package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

public class UnderOver extends Atom {
    public static void register() {
        AtomMap.put("underover", UnderOver.class);

        // letter
        AtomMap.put("buildrel", UnderOver.class);
        AtomMap.put("rel", UnderOver.class);
    }

    private final String function;
    private final Group content;
    private final Group over;
    private final Group under;

    public UnderOver(Group content, Group over) {
        this("buildrel", content, over, null);
    }

    public UnderOver(String function, Group content, Group over, Group under) {
        this.function = function;
        this.content = content;
        this.over = over;
        this.under = under;
    }

    @Override
    protected String toLaTeX(int flag) {
        String result = content.toLaTeX(function.endsWith("rel") ? flag & STYLE_ROMAN : flag);
        if (over != null) {
            result = "\\overset{" + over.toLaTeX(flag) + "}{" + result +"}";
        }
        if (under != null) {
            result = "\\underset{" + under.toLaTeX(flag) + "}{" + result + "}";
        }
        return result;
    }
}

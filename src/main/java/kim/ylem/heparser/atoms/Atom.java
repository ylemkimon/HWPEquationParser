package kim.ylem.heparser.atoms;

public abstract class Atom {
    protected static final int STYLE_ROMAN = 1 << 0;
    protected static final int STYLE_BOLD = 1 << 1;

    protected abstract String toLaTeX(int flag);

    public String toLaTeX() {
        return toLaTeX(0);
    }
}

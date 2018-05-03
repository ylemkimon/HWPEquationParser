package kim.ylem.heparser;

public abstract class Atom {
    protected static final int STYLE_ROMAN = 1 << 0;
    protected static final int STYLE_BOLD = 1 << 1;

    protected Atom() {
    }

    public abstract String toLaTeX(int flag);

    @Override
    public String toString() {
        return toLaTeX(0);
    }

    public boolean isFromToAllowed() {
        return false;
    }
}

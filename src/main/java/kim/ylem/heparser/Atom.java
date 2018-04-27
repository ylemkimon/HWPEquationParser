package kim.ylem.heparser;

public interface Atom {
    int STYLE_ROMAN = 1 << 0;
    int STYLE_BOLD = 1 << 1;

    String toLaTeX(int flag);
}

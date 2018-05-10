package kim.ylem.heparser;

public class Options {
    private final boolean roman;
    private final boolean bold;

    Options() {
        this(false, false);
    }

    private Options(boolean roman, boolean bold) {
        this.roman = roman;
        this.bold = bold;
    }

    public Options withFontStyle(boolean roman) {
        return new Options(roman, bold);
    }

    public Options withFontWeight(boolean bold) {
        return new Options(roman, bold);
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isRoman() {
        return roman;
    }
}

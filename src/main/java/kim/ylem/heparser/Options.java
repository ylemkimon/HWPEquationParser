package kim.ylem.heparser;

public class Options {
    private final boolean textStyle;
    private final boolean roman;
    private final boolean bold;

    Options() {
        this(false, false, false);
    }

    private Options(boolean textStyle, boolean roman, boolean bold) {
        this.textStyle = textStyle;
        this.roman = roman;
        this.bold = bold;
    }

    public Options withTextStyle(boolean textStyle) {
        return new Options(textStyle, roman, bold);
    }

    public Options withRomanFont(boolean roman) {
        return new Options(textStyle, roman, bold);
    }

    public Options withBoldFont(boolean bold) {
        return new Options(textStyle, roman, bold);
    }

    public boolean isTextStyle() {
        return textStyle;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isRoman() {
        return roman;
    }

    @Override
    public String toString() {
        return "{textStyle: " + textStyle + ", roman: " + roman + ", bold: " + bold + '}';
    }
}

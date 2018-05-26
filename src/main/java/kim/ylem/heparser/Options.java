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

    public Options withTextStyle(boolean newTextStyle) {
        return new Options(newTextStyle, roman, bold);
    }

    public Options withRomanFont(boolean newRoman) {
        return new Options(textStyle, newRoman, bold);
    }

    public Options withBoldFont(boolean newBold) {
        return new Options(textStyle, roman, newBold);
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

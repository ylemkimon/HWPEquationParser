package kim.ylem.heparser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull Options withTextStyle(boolean newTextStyle) {
        return new Options(newTextStyle, roman, bold);
    }

    public @NotNull Options withRomanFont(boolean newRoman) {
        return new Options(textStyle, newRoman, bold);
    }

    public @NotNull Options withBoldFont(boolean newBold) {
        return new Options(textStyle, roman, newBold);
    }

    @Contract(pure = true)
    public boolean isTextStyle() {
        return textStyle;
    }

    @Contract(pure = true)
    public boolean isBold() {
        return bold;
    }

    @Contract(pure = true)
    public boolean isRoman() {
        return roman;
    }

    @Override
    public String toString() {
        return "{textStyle: " + textStyle + ", roman: " + roman + ", bold: " + bold + '}';
    }
}

package kim.ylem.heparser.atoms;

public class Text extends Atom {
    private String text;

    public Text(String text) {
        this.text = text;
    }

    public void append(String text) {
        this.text += text;
    }

    @Override
    protected String toLaTeX(int flag) {
        boolean ascii = true;
        boolean roman = (flag & STYLE_ROMAN) == STYLE_ROMAN;
        String textCommand = ((flag & STYLE_BOLD) == STYLE_BOLD) ? "\\textbf{" : "\\textrm{";
        StringBuilder result = new StringBuilder();

        int i = 0;
        for (; i < text.length(); i++) {
            if ((text.codePointAt(i) < 128 && (!ascii || i == 0))
                    || (text.codePointAt(i) >= 128 && (ascii || i == 0))) {
                if (i > 0 && (roman || !ascii)) {
                    result.append("}");
                }

                ascii = text.codePointAt(i) < 128;
                result.append(ascii ? (roman ? "\\mathrm{" : "") : textCommand);
            }
            result.append(text.charAt(i));
        }
        if (i > 0 && (roman || !ascii)) {
            result.append("}");
        }
        return result.toString();
    }
}

package kim.ylem.qextractor;

import kim.ylem.hmlparser.Updatable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionBuilder implements Updatable {
    private static final Pattern ANSWER_PATTERN = Pattern.compile("\\[?\\uc815?[\\ub2f5\\x{f00bc}]]?\\s*\\.?:?\\s*");
    private static final Pattern[] CHOICE_PATTERN = new Pattern[5];

    static {
        for (int i = 0; i < 5; i++) {
            CHOICE_PATTERN[i] = Pattern.compile("[\\u278" + i + "\\u246" + i +
                    "]([^\\n\\u278" + (i + 1) + "\\u246" + (i + 1) + "]*)");
        }
    }

    private StringBuilder textBuilder = new StringBuilder();
    private StringBuilder expTextBuilder = new StringBuilder();

    public StringBuilder getTextBuilder() {
        return textBuilder;
    }

    public StringBuilder getExpTextBuilder() {
        return expTextBuilder;
    }

    private static boolean replace(StringBuilder sb, String searchString, String replacement) {
        int index = sb.indexOf(searchString);
        if (index == -1) {
            return false;
        }
        sb.replace(index, index + searchString.length(), replacement);
        return true;
    }

    public synchronized void update(String placeholder, String replacement) {
        if (!replace(textBuilder, placeholder, replacement)) {
            replace(expTextBuilder, placeholder, replacement);
        }
    }

    public Question build() {
        String text = textBuilder.toString().trim();
        String expText = expTextBuilder.toString().trim();

        int nlIndex = expText.indexOf('\n');
        String firstLine = nlIndex != -1 ? expText.substring(0, nlIndex) : expText;
        String answer = ANSWER_PATTERN.matcher(firstLine).replaceAll("");

        Map<Integer, String> choices = new HashMap<>(5);
        for (int i = 0; i < 5; i++) {
            Matcher matcher = CHOICE_PATTERN[i].matcher(text);
            if (matcher.find()) {
                choices.put(i + 1, matcher.group(1).trim());
            }
        }

        return new Question(text, expText, choices, answer);
    }
}

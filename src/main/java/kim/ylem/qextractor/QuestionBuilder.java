package kim.ylem.qextractor;

import kim.ylem.hmlparser.Updatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("StringBufferField")
public class QuestionBuilder implements Updatable {
    private static final Pattern ANSWER_PATTERN = Pattern.compile("\\[?\\uc815?[\\ub2f5\\x{f00bc}]]?\\s*\\.?:?\\s*");
    private static final Pattern[] CHOICE_PATTERN = new Pattern[5];

    static {
        for (int i = 0; i < 5; i++) {
            //noinspection Annotator,StringConcatenationMissingWhitespace
            CHOICE_PATTERN[i] = Pattern.compile("[\\u278" + i + "\\u246" + i +
                    "]([^\\n\\u278" + (i + 1) + "\\u246" + (i + 1) + "]*)");
        }
    }

    private final StringBuilder textBuilder = new StringBuilder();
    private final StringBuilder expTextBuilder = new StringBuilder();
    private final int qno;

    QuestionBuilder(int qno) {
        this.qno = qno;
    }

    private static boolean replace(StringBuilder sb, String searchString, String replacement) {
        int index = sb.indexOf(searchString);
        if (index == -1) {
            return false;
        }
        sb.replace(index, index + searchString.length(), replacement);
        return true;
    }

    public StringBuilder getTextBuilder() {
        return textBuilder;
    }

    public StringBuilder getExpTextBuilder() {
        return expTextBuilder;
    }

    public boolean isEmpty() {
        return textBuilder.toString().trim().isEmpty() && expTextBuilder.toString().trim().isEmpty();
    }

    public void reset() {
        textBuilder.setLength(0);
        expTextBuilder.setLength(0);
    }

    public synchronized void update(String placeholder, String replacement) {
        if (!replace(textBuilder, placeholder, replacement)) {
            replace(expTextBuilder, placeholder, replacement);
        }
    }

    public QuestionInfo build() {
        String text = textBuilder.toString().trim();
        String expText = expTextBuilder.toString().trim();

        String answer = null;
        try (BufferedReader reader = new BufferedReader(new StringReader(expText))) {
            do {
                answer = reader.readLine();
            } while (answer != null && (answer.trim().isEmpty() || answer.contains("<img") || answer.contains("<div")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (answer != null) {
            Matcher matcher = ANSWER_PATTERN.matcher(answer);
            if (matcher.find()) {
                answer = answer.substring(matcher.end());
            }
        } else {
            answer = "";
        }

        Map<Integer, String> choices = new HashMap<>(5);
        for (int i = 0; i < 5; i++) {
            Matcher matcher = CHOICE_PATTERN[i].matcher(text);
            if (matcher.find()) {
                choices.put(i + 1, matcher.group(1).trim());
            }
        }

        return new QuestionInfo(qno, text, expText, choices, answer);
    }
}

package kim.ylem.qextractor;

import java.io.Serializable;
import java.util.Map;

public class Question implements Serializable {
    public static final long serialVersionUID = 1L;
    private final String text;
    private final String expText;
    private final Map<Integer, String> choices;
    private final String answer;

    public Question(String text, String expText, Map<Integer, String> choices, String answer) {
        this.text = text;
        this.expText = expText;
        this.choices = choices;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public String getExpText() {
        return expText;
    }

    public Map<Integer, String> getChoices() {
        return choices;
    }

    public String getAnswer() {
        return answer;
    }
}

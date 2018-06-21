package kim.ylem.qextractor;

import java.io.Serializable;
import java.util.Map;

public class QuestionInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int qno;
    private final String text;
    private final String expText;
    private final Map<Integer, String> choices;
    private final String answer;

    public QuestionInfo(int qno, String text, String expText, Map<Integer, String> choices, String answer) {
        this.qno = qno;
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

    public int getQno() {
        return qno;
    }
}

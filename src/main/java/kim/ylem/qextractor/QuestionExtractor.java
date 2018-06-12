package kim.ylem.qextractor;

import kim.ylem.hmlparser.HTMLExtractor;
import kim.ylem.hmlparser.Updatable;
import kim.ylem.hmlparser.image.ImageTaskFactory;
import kim.ylem.ParserException;
import kim.ylem.xmlparser.HMLCloner;
import kim.ylem.xmlparser.XMLParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class QuestionExtractor extends HTMLExtractor {
    private QuestionBuilder current;
    private final List<QuestionBuilder> questionBuilderList = new ArrayList<>();

    private final Set<String> outlineParaShapes = new HashSet<>();
    private String currentParaShape;

    public QuestionExtractor(XMLParser xmlParser, Function<String, String> equationParser,
                             ExecutorService imageTaskExecutor, ImageTaskFactory imageTaskFactory, HMLCloner hmlCloner) {
        super(xmlParser, equationParser, imageTaskExecutor, imageTaskFactory, hmlCloner);
    }

    @Override
    public List<Question> parse() throws ParserException {
        super.parse();

        List<Question> questionList = new ArrayList<>();
        for (QuestionBuilder qb : questionBuilderList) {
            questionList.add(qb.build());
        }
        return questionList;
    }

    @Override
    protected Updatable getCurrent() {
        return current;
    }

    @Override
    protected void processParaShape() throws ParserException {
        if ("Outline".equals(xmlParser.getAttribute("HeadingType"))) {
            outlineParaShapes.add(xmlParser.getAttribute("Id"));
        }
    }

    @Override
    protected void processParagraph() throws ParserException {
        currentParaShape = xmlParser.getAttribute("ParaShape");
        if (outlineParaShapes.contains(currentParaShape)) {
            if (hmlCloner != null && !questionBuilderList.isEmpty()) {
                hmlCloner.extract(Integer.toString(questionBuilderList.size()));
                hmlCloner.reset();
            }
            current = new QuestionBuilder();
            questionBuilderList.add(current);
        } else if (questionBuilderList.isEmpty()) {
            current = new QuestionBuilder();
        }

        parseParagraph(current.getTextBuilder());
        if (hmlCloner != null && !questionBuilderList.isEmpty()) {
            hmlCloner.cloneParagraph();
        }
    }

    @Override
    protected void processEndnote() throws ParserException {
        if (questionBuilderList.isEmpty()) {
            questionBuilderList.add(current);
            outlineParaShapes.add(currentParaShape);
        }

        parseEndnote(current.getExpTextBuilder());
    }
}

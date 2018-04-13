package kim.ylem.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kim.ylem.ParserException;
import kim.ylem.hmlparser.image.imageio.ToFileTaskFactory;
import kim.ylem.qextractor.Question;
import kim.ylem.qextractor.QuestionExtractor;
import kim.ylem.xmlparser.XMLParser;
import kim.ylem.xmlparser.XMLStreamParser;

import java.util.List;
import java.util.concurrent.Executors;

public class Demo {
    // TODO
    // 1. Unit tests
    // 2. JavaDoc
    // 3. Logger
    // 4. Examples

    public static void main(String args[]) {
        try (XMLParser xmlParser = XMLStreamParser.newInstance(
                "C:\\Users\\wisec\\Documents\\GitHub\\hml-equation-parser\\book.hml")) {
            QuestionExtractor extractor = new QuestionExtractor(xmlParser,
                    equation -> '$' + equation + '$',
                    Executors.newCachedThreadPool(),
                    new ToFileTaskFactory("C:\\Users\\wisec\\images"));
            List<Question> questionList = extractor.parse();

            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            System.out.println(gson.toJson(questionList));
        } catch (ParserException e) {
            e.printStackTrace();
        }

        /*FileWriter fw = new FileWriter(path);
        fw.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset='utf-8'>\n" +
                "    <title>Extracted</title>\n" +
                "    <script src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.3/MathJax.js?config=TeX-AMS_HTML'></script>\n" +
                "    <script type='text/x-mathjax-config'>\n" +
                "      MathJax.Hub.Config({\n" +
                "        tex2jax: {inlineMath: [['$','$']]}\n" +
                "      });\n" +
                "    </script>\n" +
                "    <style>\n" +
                "      .box {\n" +
                "        border: 1px solid;\n" +
                "      }\n" +
                "      table, th, td {\n" +
                "        border: 1px solid black;\n" +
                "        border-collapse: collapse;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n");
        for (int i = 0; i < questionList.size(); i++) {
            QuestionBuilder q = questionList.get(i);
            fw.write("    <h1>" + i + "</h1>\n");
            fw.write("    <h2>QuestionBuilder</h2>\n");
            fw.write(q.getText());
            fw.write("    <h2>Answer</h2>\n");
            fw.write(q.getAnswer());
            fw.write("    <h2>Exp</h2>\n");
            fw.write(q.getExpText());
        }
        fw.write("  </body>\n" +
                "</html>\n");
        fw.close();*/
    }
}

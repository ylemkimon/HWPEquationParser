package kim.ylem.heparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kim.ylem.ParserException;
import kim.ylem.hmlparser.HMLDOMCloner;
import kim.ylem.hmlparser.HMLParser;
import kim.ylem.hmlparser.HTMLExtractor;
import kim.ylem.hmlparser.image.ImageTask;
import kim.ylem.hmlparser.image.ImageTaskFactory;
import kim.ylem.hmlparser.image.ImageToFileTask;
import kim.ylem.hmlparser.image.ImageToFileTaskFactory;
import kim.ylem.qextractor.QuestionInfo;
import kim.ylem.qextractor.QuestionExtractor;
import kim.ylem.xmlparser.XMLDOMParser;
import kim.ylem.xmlparser.XMLParser;
import kim.ylem.xmlparser.XMLStreamParser;

public class ConvertHwpTest {
	private static final Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) throws Exception {
		String filename = "C:\\Users\\haenvi\\Desktop\\test\\testcase\\t3-deg.hml";
		File targetFile = new File(filename);
		String workDir =  targetFile.getParent() + "/";

		if (!targetFile.exists()) {
			throw new Exception("file not found...");
		}

		ImageToFileTaskFactory imgFactory = new ImageToFileTaskFactory(workDir);
		imgFactory.setProcMode(false);
		//imgFactory.setPublicPath(prefix_resourceUri);
		
		try (XMLDOMParser xmlParser = XMLDOMParser.newInstance(filename)) {

			QuestionExtractor extractor = new QuestionExtractor(
                    xmlParser,
                    HEParser::parseToLaTeX,
                    Executors.newCachedThreadPool(),				// 멀티 쓰레드
                    imgFactory,            							// 이미지 파일로 추출
                    new HMLDOMCloner(xmlParser, workDir)); 			// 개별 문제 추출

			List<QuestionInfo> questionList = extractor.parse();

			exportToHTML(questionList, workDir + "/output.html");
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			System.out.println(gson.toJson(questionList));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    private static void exportToHTML(List<QuestionInfo> questionList, String path) {
        try (FileWriter fw = new FileWriter(path)) {
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
                    "      body {\n" +
                    "        white-space: pre-wrap;\n" +
                    "      }\n" +
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
                QuestionInfo q = questionList.get(i);
                fw.write("\n<h1>" + (i + 1) + "</h1> <h2>QuestionInfo</h2>\n");
                fw.write(q.getText());
                fw.write("\n<h2>Choices</h2>\n");
                fw.write(q.getChoices().toString());
                fw.write("\n<h2>Answer</h2>\n");
                fw.write(q.getAnswer());
                fw.write("\n<h2>Exp</h2>\n");
                fw.write(q.getExpText());
            }
            fw.write("\n  </body>\n</html>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

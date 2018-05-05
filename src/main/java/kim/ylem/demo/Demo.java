package kim.ylem.demo;

import kim.ylem.heparser.HEParser;

public class Demo {
    // TODO:
    // 1. Unit tests
    // 2. JavaDoc
    // 3. Logger
    // 4. Examples

    public static void main(String args[]) {
        System.out.println(new HEParser("breveA").parse());
        //        /** HML 파일을 읽어들이기 위해서는 kim.ylem.xmlparser.XMLParser 인터페이스를 구현한 클래스가 필요합니다.
//         * 기본적으로 javax.xml.stream.XMLStreamReader를 사용하여 구현한 kim.ylem.xmlparser.XMLStreamParser가
//         * 존재하며, HML 파일 경로(String), InputStream 또는 Reader를 인수로 XMLStreamParser.newInstance를 호출하여
//         * 인스턴스를 생성할 수 있습니다. 인스턴스 사용 후 close()를 호출해주어야 하며, AutoCloseable를 구현하고
//         * 있어 try-with-resources를 사용할 수 있습니다.
//         */
//        try (XMLParser xmlParser = XMLStreamParser.newInstance(
//                "C:\\Users\\wisec\\Documents\\hml\\book8.hml")) {
//
//            /** HML 파일을 해석하는데는 kim.ylem.hmlparser.HMLParser 추상 클래스를 상속한 클래스가 사용됩니다.
//             * 문제를 추출할 수 있는 kim.ylem.qextractor.QuestionExtractor가 기본적으로 제공됩니다.
//             * QuestionExtractor는 HML을 HTML로 추출하는 kim.ylem.hmlparser.HTMLExtractor를 상속하며, 이 클래스를
//             * 수정하면 HTML 출력을 수정할 수 있습니다.
//             *
//             * QuestionExtractor 생성자는 아래와 같이 4가지 인수를 받습니다.
//             * - 첫번째 인수 : XMLParser 인스턴스
//             * - 두번째 인수 : Function<String, String>으로 한글 수식을 해석하는 함수
//             * - 세번째 인수 : 이미지 처리 쓰레드를 관리할 ExecutorService, null일 경우 현재 쓰레드에서 실행
//             * - 네번째 인수 : 이미지 처리 Runnable을 반환하는 ImageTaskFactory 인스턴스 또는 함수, null일 경우
//             *                 이미지를 처리하지 않음
//             *
//             * 이미지 처리는 위와 같이, ImageTaskFactory가 반환하는 Runnable를 ExecutorService가 실행하여 처리합니다.
//             * 기본적으로 data url로 추출하는 kim.ylem.hmlparser.image.imageio.ToBase64Task와 파일로 추출하는
//             * kim.ylem.hmlparser.image.imageio.ToFileTask가 제공됩니다.
//             */
//            QuestionExtractor extractor = new QuestionExtractor(xmlParser,
//                    equation -> {
//                        try {
//                            equation = '$' + new HEParser(equation).parse() + '$';
//                        } catch (ParserException e) {
//                            e.printStackTrace();
//                        }
//                        return equation;
//                    },
//                    null, null);
//
//            /** parse()를 호출하게 되면, 문제 추출이 진행되며, 추출한 문제를 포함하고 있는 List<Question>을
//             * 반환합니다.
//             */
//            List<Question> questionList = extractor.parse();
//
//            /** Question은 Serializable를 구현하여 직렬화가 가능합니다. Gson 등의 라이브러리를 사용하면 JSON
//             * 형식 등으로 추출이 가능합니다.
//             */
//            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
//            System.out.println(gson.toJson(questionList));
//
//            /** 아래와 같이 getText, getChoices, getAnswer, getExpText를 통해 문제 본문, 선택지, 정답, 해설에
//             * 접근할 수 있습니다. getChoices는 Map<Integer, String>를 반환하며, 나머지는 String을 반환합니다.
//             * 아래에서는 문제를 HTML 파일로 추출합니다.
//             */
//            FileWriter fw = new FileWriter("C:\\Users\\wisec\\Documents\\hml\\resulthtml.html");
//            fw.write("<!DOCTYPE html>\n" +
//                    "<html>\n" +
//                    "  <head>\n" +
//                    "    <meta charset='utf-8'>\n" +
//                    "    <title>Extracted</title>\n" +
//                    "    <script src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.3/MathJax.js?config=TeX-AMS_HTML'></script>\n" +
//                    "    <script type='text/x-mathjax-config'>\n" +
//                    "      MathJax.Hub.Config({\n" +
//                    "        tex2jax: {inlineMath: [['$','$']]}\n" +
//                    "      });\n" +
//                    "    </script>\n" +
//                    "    <style>\n" +
//                    "      body {\n" +
//                    "        white-space: pre-wrap;\n" +
//                    "      }\n" +
//                    "      .box {\n" +
//                    "        border: 1px solid;\n" +
//                    "      }\n" +
//                    "      table, th, td {\n" +
//                    "        border: 1px solid black;\n" +
//                    "        border-collapse: collapse;\n" +
//                    "      }\n" +
//                    "    </style>\n" +
//                    "  </head>\n" +
//                    "  <body>\n");
//            for (int i = 0; i < questionList.size(); i++) {
//                Question q = questionList.get(i);
//                fw.write("<h1>" + (i + 1) + "</h1>");
//                fw.write("<h2>Question</h2>");
//                fw.write(q.getText());
//                fw.write("<h2>Choices</h2>");
//                fw.write(q.getChoices().toString());
//                fw.write("<h2>Answer</h2>");
//                fw.write(q.getAnswer());
//                fw.write("<h2>Exp</h2>");
//                fw.write(q.getExpText());
//            }
//            fw.write("  </body>\n" +
//                    "</html>\n");
//            fw.close();
//        } catch (ParserException | IOException e) {
//            e.printStackTrace();
//        }
    }
}

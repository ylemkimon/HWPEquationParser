package kim.ylem.demo;

import kim.ylem.heparser.HEParser;
import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.HMLDOMCloner;
import kim.ylem.hmlparser.ImageData;
import kim.ylem.hmlparser.image.ImageIOTask;
import kim.ylem.hmlparser.image.ImageToBase64Task;
import kim.ylem.hmlparser.image.ImageToFileTaskFactory;
import kim.ylem.qextractor.QuestionInfo;
import kim.ylem.qextractor.QuestionExtractor;
import kim.ylem.xmlparser.XMLDOMParser;
import kim.ylem.xmlparser.XMLStreamParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class Demo {
    public static void main(String args[]) throws Exception {
        String HML_PATH = "C:\\Users\\wisec\\Documents\\hml\\1.hml"; // HML 파일 경로
        String IMG_PATH = "C:\\Users\\wisec\\Documents\\extracted\\"; // 이미지 추출 경로
        String HTML_PATH = "C:\\Users\\wisec\\Documents\\extracted\\"; // HTML 추출 경로
        String QUESTION_HML_PATH = "C:\\Users\\wisec\\Documents\\extracted\\1_"; // 개별 문제 추출 경로

        // Example 1
        try (XMLStreamParser xmlParser = XMLStreamParser.newInstance(HML_PATH)) { // StAX 사용
            QuestionExtractor extractor = new QuestionExtractor(
                    xmlParser,
                    HEParser::parseToLaTeX,
                    null,                   // 단일 쓰레드
                    ImageToBase64Task::new, // Base64 data URL로 이미지 임베드
                    null);                  // StAX 사용시, 개별 문제 추출 미지원
            List<QuestionInfo> result1 = extractor.parse();
            exportToHTML(result1, HTML_PATH + "1.html");
        }

        // Example 2
        try (XMLDOMParser xmlParser = XMLDOMParser.newInstance(HML_PATH)) { // DOM 사용
            QuestionExtractor extractor = new QuestionExtractor(
                    xmlParser,
                    HEParser::parseToLaTeX,
                    Executors.newCachedThreadPool(),                 // 멀티 쓰레드
                    new ImageToFileTaskFactory(IMG_PATH),            // 이미지 파일로 추출
                    new HMLDOMCloner(xmlParser, QUESTION_HML_PATH)); // 개별 문제 추출
            List<QuestionInfo> result2 = extractor.parse();
            exportToHTML(result2, HTML_PATH + "2.html");
        }

        // Example 3: custom ImageTask, upload to S3
        try (XMLStreamParser xmlParser = XMLStreamParser.newInstance(HML_PATH)) {
            QuestionExtractor extractor = new QuestionExtractor(
                    xmlParser,
                    HEParser::parseToLaTeX,
                    Executors.newCachedThreadPool(),
                    ImageToS3Task::new,
                    null);
            List<QuestionInfo> result3 = extractor.parse();
            exportToHTML(result3, HTML_PATH + "3.html");
        }
    }

    public static class ImageToS3Task extends ImageIOTask {
        private static final String BUCKET_NAME = "qextractor-images";

        public ImageToS3Task(BinItem binItem, String base64) {
            super(binItem, base64);
        }

        @Override
        protected void writeBufferedImage(BufferedImage subimage, ImageData data) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            String filename = data.getUuid() + ".png";
            try {
                ImageIO.write(subimage, "png", os);
                byte[] image = os.toByteArray();
                ByteArrayInputStream is = new ByteArrayInputStream(image);

                /* requires AWS SDK(https://aws.amazon.com/ko/sdk-for-java/)
                // com.amazonaws.services.s3.model.ObjectMetadata
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(image.length);
                metadata.setContentType("image/png");
                metadata.setCacheControl("public, max-age=31536000");

                // com.amazonaws.services.s3.model.PutObjectRequest
                // com.amazonaws.services.s3.model.CannedAccessControlList
                PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, filename, is, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead);

                // com.amazonaws.services.s3.AmazonS3ClientBuilder
                // com.amazonaws.services.s3.AmazonS3
                // com.amazonaws.regions.Regions
                AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
                s3client.putObject(request);

                data.updateUpdatable("<img src=\"" + s3client.getUrl(BUCKET_NAME, filename)
                        + "\" style=\"width:" + data.getWidth() + "px;height:" + data.getHeight() + "px;\">");
                */
            } catch (/* AmazonServiceException | AmazonClientException |*/ IOException e) {
                // com.amazonaws.AmazonServiceException
                // com.amazonaws.AmazonClientException
                e.printStackTrace();
            }
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

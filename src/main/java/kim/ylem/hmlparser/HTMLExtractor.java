package kim.ylem.hmlparser;

import kim.ylem.hmlparser.image.ImageTaskFactory;
import kim.ylem.ParserException;
import kim.ylem.xmlparser.XMLParser;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public abstract class HTMLExtractor extends HMLParser {
    protected HTMLExtractor(XMLParser xmlParser, Function<String, String> equationParser,
                            ExecutorService imageTaskExecutor, ImageTaskFactory imageTaskFactory) {
        super(xmlParser, equationParser, imageTaskExecutor, imageTaskFactory);
    }

    @Override
    protected void parseRectangle(StringBuilder sb) throws ParserException {
        sb.append("<div class='box'>");
        super.parseRectangle(sb);
        sb.append("</div>");
    }

    @Override
    protected void parseTable(StringBuilder sb) throws ParserException {
        sb.append("<table>");
        super.parseTable(sb);
        sb.append("</table>");
    }

    @Override
    protected void parseRow(StringBuilder sb) throws ParserException {
        sb.append("<tr>");
        super.parseRow(sb);
        sb.append("</tr>");
    }

    @Override
    protected void parseCell(StringBuilder sb) throws ParserException {
        sb.append("<td rowspan='");
        sb.append(xmlParser.getAttribute("RowSpan"));
        sb.append("' colspan='");
        sb.append(xmlParser.getAttribute("ColSpan"));
        sb.append("'>");
        super.parseCell(sb);
        sb.append("</td>");
    }
}

package kim.ylem.hmlparser;

import kim.ylem.hmlparser.image.ImageTaskFactory;
import kim.ylem.ParserException;
import kim.ylem.xmlparser.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class HMLParser {
    private static final int POINT_TO_PIXEL = 75;

    protected final XMLParser xmlParser;
    private final Function<String, String> equationParser;
    private final ExecutorService imageTaskExecutor;
    private final ImageTaskFactory imageTaskFactory;

    private final List<BinItem> binList = new ArrayList<>();

    protected HMLParser(XMLParser xmlParser, Function<String, String> equationParser,
                     ExecutorService imageTaskExecutor, ImageTaskFactory imageTaskFactory) {
        this.xmlParser = xmlParser;
        this.equationParser = equationParser;
        this.imageTaskExecutor = imageTaskExecutor;
        this.imageTaskFactory = imageTaskFactory;
    }

    public Object parse() throws ParserException {
        processHWPML();
        awaitImageTask();
        return null;
    }

    protected abstract Updatable getCurrent();

    protected void awaitImageTask() {
        if (imageTaskExecutor != null) {
            imageTaskExecutor.shutdown();
            try {
                imageTaskExecutor.awaitTermination(1L, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void processHWPML() throws ParserException {
        xmlParser.forEach("HWPML", name -> {
            switch (name) {
                case "BINITEM":
                    processBinItem();
                    break;
                case "PARASHAPE":
                    processParaShape();
                    break;
                case "P":
                    processParagraph();
                    break;
                case "BINDATA":
                    processBinData();
                    break;
            }
        }, null, false);
    }

    protected void processBinItem() throws ParserException {
        binList.add(new BinItem(xmlParser.getAttribute("Format")));
    }

    protected abstract void processParaShape() throws ParserException;

    protected abstract void processParagraph() throws ParserException;

    protected void parseParagraph(StringBuilder sb) throws ParserException {
        xmlParser.forEach("P", name -> {
            switch (name) {
                case "ENDNOTE":
                    processEndnote();
                    break;
                case "CHAR":
                    parseChar(sb);
                    break;
                case "SCRIPT":
                    parseEquation(sb);
                    break;
                case "RECTANGLE":
                    parseRectangle(sb);
                    break;
                case "TABLE":
                    parseTable(sb);
                    break;
                case "PICTURE":
                    parsePicture(sb);
                    break;
            }
        });
        sb.append('\n');
    }

    protected abstract void processEndnote() throws ParserException;

    protected void parseEndnote(StringBuilder sb) throws ParserException {
        xmlParser.join("ENDNOTE", "P", sb, this::parseParagraph);
    }

    protected void parseChar(StringBuilder sb) throws ParserException {
        xmlParser.forEach("CHAR", name -> {
            switch (name) {
                case "LINEBREAK":
                    sb.append('\n');
                    break;
                case "NBSPACE":
                case "FWSPACE":
                    sb.append(' ');
                    break;
                case "TAB":
                    sb.append("    ");
                    break;
            }
        }, sb::append, true);
    }

    protected void parseEquation(StringBuilder sb) throws ParserException {
        sb.append(equationParser.apply(xmlParser.getElementText()));
    }

    protected void parseRectangle(StringBuilder sb) throws ParserException {
        xmlParser.join("RECTANGLE", "P", sb, this::parseParagraph);
    }

    protected void parseTable(StringBuilder sb) throws ParserException {
        xmlParser.join("TABLE", "ROW", sb, this::parseRow);
    }

    protected void parseRow(StringBuilder sb) throws ParserException {
        xmlParser.join("ROW", "CELL", sb, this::parseCell);
    }

    protected void parseCell(StringBuilder sb) throws ParserException {
        xmlParser.join("CELL", "P", sb, this::parseParagraph);
    }

    protected void parsePicture(StringBuilder sb) throws ParserException {
        ImageData image = new ImageData(getCurrent());
        sb.append(image.getPlaceholder());

        xmlParser.forEach("PICTURE", name -> {
            switch (name) {
                case "SIZE":
                    image.setSize(xmlParser.getIntAttribute("Width") / POINT_TO_PIXEL,
                            xmlParser.getIntAttribute("Height") / POINT_TO_PIXEL);
                    break;
                case "IMAGECLIP":
                    image.setCrop(xmlParser.getIntAttribute("Left") / POINT_TO_PIXEL,
                            xmlParser.getIntAttribute("Top") / POINT_TO_PIXEL,
                            xmlParser.getIntAttribute("Right") / POINT_TO_PIXEL,
                            xmlParser.getIntAttribute("Bottom") / POINT_TO_PIXEL);
                    break;
                case "IMAGE":
                    binList.get(xmlParser.getIntAttribute("BinItem") - 1).addImageData(image);
                    break;
            }
        });
    }

    protected void processBinData() throws ParserException {
        if (imageTaskFactory != null) {
            BinItem binItem = binList.get(xmlParser.getIntAttribute("Id") - 1);
            binItem.setCompressed("true".equals(xmlParser.getAttribute("Compress")));
            if (imageTaskExecutor != null) {
                imageTaskExecutor.execute(imageTaskFactory.create(binItem, xmlParser.getElementText()));
            } else {
                imageTaskFactory.create(binItem, xmlParser.getElementText()).run();
            }
        }
    }
}

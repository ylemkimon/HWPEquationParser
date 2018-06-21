package kim.ylem.hmlparser;

import kim.ylem.ParserException;
import kim.ylem.xmlparser.XMLDOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class HMLDOMCloner implements HMLCloner { // TODO: profile
    private final XMLDOMParser parser;
    private final String path;
    private final Document document;
    private final Element body;
    private final Element binDataList;
    private final Element binDataStorage;
    private final Transformer transformer;

    private Element section;
    private int binCount;

    public HMLDOMCloner(XMLDOMParser parser, String path) throws ParserException {
        this.parser = parser;
        this.path = path;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new ParserException(e);
        }
        Node root = document.importNode(parser.getRoot(), true);
        document.appendChild(root);

        body = find(root, "BODY");
        binDataList = find(root, "BINDATALIST");
        binDataStorage = find(root, "BINDATASTORAGE");
        reset();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new ParserException(e);
        }
    }

    private static Element find(Node element, String name) {
        return (Element) ((Element) element).getElementsByTagName(name).item(0);
    }

    private static void clear(Node node) {
        if (node == null) {
            return;
        }
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }

    private void importBin(String name, String childName, String idAttr, int id, Node list) {
        Element e = (Element) document.importNode(find(parser.getRoot(), name).getElementsByTagName(childName).item(id),
                true);
        e.setAttribute(idAttr, Integer.toString(binCount));
        list.appendChild(e);
    }

    @Override
    public void reset() {
        clear(body);
        clear(binDataList);
        clear(binDataStorage);

        section = document.createElement("SECTION");
        section.setAttribute("Id", "0");
        body.appendChild(section);

        binCount = 0;
    }

    @Override
    public void extract(String name) throws ParserException {
        if (binDataList != null) {
            binDataList.setAttribute("Count", Integer.toString(binCount));
        }

        Source source = new DOMSource(document);
        Result result = new StreamResult(new File(path + name + ".hml"));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public void cloneParagraph() {
        Element current = parser.getCurrent();
        if ("SECTION".equals(((Element) current.getParentNode()).getTagName())) {
            section.appendChild(document.importNode(current, true));
        }
    }

    @Override
    public void cloneImage() {
        int id = parser.getIntAttribute("BinItem") - 1;
        parser.getCurrent().setAttribute("BinItem", Integer.toString(++binCount));
        importBin("BINDATALIST", "BINITEM", "BinData", id, binDataList);
        importBin("BINDATASTORAGE", "BINDATA", "Id", id, binDataStorage);
    }
}

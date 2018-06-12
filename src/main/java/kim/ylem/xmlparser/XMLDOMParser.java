package kim.ylem.xmlparser;

import kim.ylem.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class XMLDOMParser implements XMLParser {
    private final Document document;
    private Element current;
    private boolean visited;

    protected XMLDOMParser(Document document) {
        this.document = document;
        current = document.getDocumentElement();
    }

    public static XMLDOMParser newInstance(String filepath) throws ParserException {
        try {
            InputStream is = new BOMInputStream(new FileInputStream(filepath));
            return newInstance(is, true);
        } catch (FileNotFoundException e) {
            throw new ParserException(e);
        }
    }

    public static XMLDOMParser newInstance(InputStream stream, boolean close) throws ParserException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            if (close) {
                stream.close();
            }
            return new XMLDOMParser(document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParserException(e);
        }
    }

    public static XMLDOMParser newInstance(InputStream stream) throws ParserException {
        return newInstance(stream, false);
    }

    @Override
    public String getAttribute(String name) {
        return current.getAttribute(name);
    }

    @Override
    public String getElementText() {
        return current.getTextContent();
    }

    private Element getNextElement(Consumer<String> textConsumer, boolean child) {
        Node node = child ? current.getFirstChild() : current.getNextSibling();
        while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
            if (textConsumer != null && node.getNodeType() == Node.TEXT_NODE) {
                textConsumer.accept(node.getNodeValue());
            }
            node = node.getNextSibling();
        }
        return (Element) node;
    }

    private Element next(String ancestor, Consumer<String> textConsumer, Counter nestCounter) {
        Element next;
        Node parent;
        for (next = getNextElement(textConsumer, !visited), parent = current;
             next == null && parent.getNodeType() == Node.ELEMENT_NODE;
             next = getNextElement(textConsumer, false), parent = current.getParentNode()) {
            current = (Element) parent;
            if (ancestor.equals(current.getTagName()) && nestCounter.decrement() <= 0) {
                return null;
            }
        }
        visited = false;
        return next;
    }

    @Override
    public void forEach(String ancestor, ElementProcessor elementProcessor, Consumer<String> textConsumer)
            throws ParserException {
        Counter nestCounter = new Counter();
        for (Element next = getNextElement(textConsumer, true); next != null;
             next = next(ancestor, textConsumer, nestCounter)) {
            current = next;
            String tagName = current.getTagName();
            if (ancestor.equals(tagName)) {
                nestCounter.increment();
            }
            elementProcessor.process(tagName);
        }
        visited = true;
    }

    @Override
    public void close() {
    }

    public Element getRoot() {
        return document.getDocumentElement();
    }

    public Element getCurrent() {
        return current;
    }

    private static class Counter {
        private int count;

        int increment() {
            return ++count;
        }

        int decrement() {
            return --count;
        }
    }
}

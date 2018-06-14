package kim.ylem.xmlparser;

import kim.ylem.ParserException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.function.Consumer;

public class XMLStreamParser implements XMLParser {
    private final XMLStreamReader streamReader;
    private final InputStream stream;

    private XMLStreamParser(XMLStreamReader streamReader, InputStream stream) throws ParserException {
        this.streamReader = streamReader;
        this.stream = stream;

        try {
            //noinspection StatementWithEmptyBody
            while (streamReader.hasNext() && streamReader.next() != XMLStreamConstants.START_ELEMENT) ;
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }
    }

    private XMLStreamParser(XMLStreamReader streamReader) throws ParserException {
        this(streamReader, null);
    }

    public static XMLStreamParser newInstance(String filepath) throws ParserException {
        try {
            InputStream is = new BOMInputStream(new FileInputStream(filepath));
            return newInstance(is, true);
        } catch (FileNotFoundException e) {
            throw new ParserException(e);
        }
    }

    public static XMLStreamParser newInstance(InputStream stream, boolean close) throws ParserException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(stream);
            return close ? new XMLStreamParser(streamReader, stream) : new XMLStreamParser(streamReader);
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }
    }

    public static XMLStreamParser newInstance(InputStream stream) throws ParserException {
        return newInstance(stream, false);
    }

    public static XMLStreamParser newInstance(Reader reader) throws ParserException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(reader);
            return new XMLStreamParser(streamReader);
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public String getAttribute(String name) {
        return streamReader.getAttributeValue(null, name);
    }

    @Override
    public String getElementText() throws ParserException {
        try {
            return streamReader.getElementText();
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public void forEach(String ancestor, ElementProcessor elementProcessor, Consumer<String> textConsumer)
            throws ParserException {
        try {
            int nestCount = 0;
            while (streamReader.hasNext()) {
                switch (streamReader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        String tagName = streamReader.getLocalName();
                        if (ancestor.equals(tagName)) {
                            nestCount++;
                        }
                        elementProcessor.process(tagName);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (ancestor.equals(streamReader.getLocalName()) && --nestCount <= 0) {
                            return;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (textConsumer != null) {
                            textConsumer.accept(streamReader.getText());
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }
    }

    @Override
    public void close() throws ParserException {
        try {
            streamReader.close();
            if (stream != null) {
                stream.close();
            }
        } catch (XMLStreamException | IOException e) {
            throw new ParserException(e);
        }
    }
}

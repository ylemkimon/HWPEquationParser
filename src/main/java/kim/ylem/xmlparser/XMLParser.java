package kim.ylem.xmlparser;

import kim.ylem.ParserException;

import java.util.function.Consumer;

public interface XMLParser extends AutoCloseable {
    /**
     * Retrieves an attribute value of the current element by name.
     * @param name The name of the attribute to retrieve.
     * @return The attribute value as a string, or the empty string or null
     * if that attribute does not have a specified or default value.
     */
    String getAttribute(String name);

    /**
     * Retrieves an integer attribute value of the current element by name.
     * @param name The name of the attribute to retrieve.
     * @return The attribute value as an integer.
     * @throws NumberFormatException if the value does not contain a parsable
     * integer or that attribute does not have a specified or default value.
     * @see Integer#parseInt(String)
     */
    default int getIntAttribute(String name) {
        return Integer.parseInt(getAttribute(name));
    }

    /**
     * Reads the text content of the current element.
     * @return The text content as a string.
     * @throws ParserException may be thrown if the current element is not a
     * text-only element.
     */
    String getElementText() throws ParserException;

    /**
     * Processes each
     * @param ancestor
     * @param elementProcessor
     * @param textConsumer
     * @throws ParserException
     */
    void forEach(String ancestor, ElementProcessor elementProcessor,
                 Consumer<String> textConsumer) throws ParserException;

    default void forEach(String ancestor, ElementProcessor elementProcessor) throws ParserException {
        forEach(ancestor, elementProcessor, null);
    }

    default void join(String ancestor, String child,
                      StringBuilder sb, ElementParser elementParser) throws ParserException {
        forEach(ancestor, name -> {
            if (child.equals(name)) {
                elementParser.parse(sb);
            }
        });
    }

    @Override
    void close() throws ParserException;
}

package kim.ylem.xmlparser;

import kim.ylem.ParserException;

import java.util.function.Consumer;

public interface XMLParser extends AutoCloseable {
    String getAttribute(String name);

    default int getIntAttribute(String name) {
        return Integer.parseInt(getAttribute(name));
    }

    String getElementText() throws ParserException;

    void forEach(String ancestor, ElementProcessor elementProcessor,
                 Consumer<String> textConsumer, boolean inAncestor) throws ParserException;

    default void forEach(String ancestor, ElementProcessor elementProcessor) throws ParserException {
        forEach(ancestor, elementProcessor, null, true);
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

package kim.ylem.xmlparser;

import kim.ylem.ParserException;

@FunctionalInterface
public interface ElementProcessor {
    void process(String name) throws ParserException;
}

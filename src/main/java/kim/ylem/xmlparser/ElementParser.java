package kim.ylem.xmlparser;

import kim.ylem.ParserException;

/**
 *
 */
@FunctionalInterface
public interface ElementParser {
    void parse(StringBuilder sb) throws ParserException;
}

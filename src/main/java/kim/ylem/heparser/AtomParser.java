package kim.ylem.heparser;

import kim.ylem.ParserException;

@FunctionalInterface
public interface AtomParser {
    Atom parse(HEParser parser, String function) throws ParserException;
}

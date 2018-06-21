package kim.ylem.hmlparser;

import kim.ylem.ParserException;

public interface HMLCloner {
    void reset();

    void extract(String name) throws ParserException;

    void cloneParagraph();

    void cloneImage();
}

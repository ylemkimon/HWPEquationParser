package kim.ylem.hmlparser;

import kim.ylem.ParserException;

public interface HMLCloner {
    void reset();

    void extract(String name) throws ParserException;

    void cloneParagraph();

    void cloneImage();
    
    // add: 2018.06.20 @leria95 -- extract last q.hml
    int getParaCount();
}

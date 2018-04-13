package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;

@FunctionalInterface
public interface ImageTaskFactory {
    Runnable create(BinItem binItem, String base64);
}

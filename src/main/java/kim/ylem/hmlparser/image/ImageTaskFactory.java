package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;

@FunctionalInterface
public interface ImageTaskFactory {
    ImageTask create(BinItem binItem, String base64);
}

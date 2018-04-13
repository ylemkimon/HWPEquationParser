package kim.ylem.hmlparser;

import java.util.ArrayList;
import java.util.List;

public class BinItem {
    private final String format;
    private final List<ImageData> imageDataList = new ArrayList<>();
    private boolean compressed;

    BinItem(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public List<ImageData> getImageDataList() {
        return imageDataList;
    }

    boolean addImageData(ImageData imageData) {
        return imageDataList.add(imageData);
    }
}

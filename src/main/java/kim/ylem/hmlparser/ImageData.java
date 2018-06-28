package kim.ylem.hmlparser;

import java.util.UUID;

public class ImageData {
    private final String uuid;
    private final Updatable updatable;

    private int width;
    private int height;
    private int left;
    private int top;
    private int right;
    private int bottom;

    ImageData(Updatable updatable) {
        uuid = UUID.randomUUID().toString();
        this.updatable = updatable;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "@[" + uuid + ']';
    }

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void setCrop(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void updateUpdatable(String replacement) {
        updatable.update(toString(), replacement);
    }
}

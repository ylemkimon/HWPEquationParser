package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class ImageIOTask extends ImageTask {
    private static final Logger logger = LogManager.getLogger();

    private BufferedImage image;

    protected ImageIOTask(BinItem binItem, String base64) {
        super(binItem, base64);
    }

    @Override
    protected void read(byte[] data) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            image = ImageIO.read(is);
        } catch (IOException e) {
            logger.error("Exception during reading image", e);
        }
    }

    @Override
    protected void write(ImageData data) {
        if (image != null) {
            BufferedImage subimage = image.getSubimage(data.getLeft(), data.getTop(),
                    data.getRight() - data.getLeft(), data.getBottom() - data.getTop());
            writeBufferedImage(subimage, data);
        }
    }

    protected abstract void writeBufferedImage(BufferedImage subimage, ImageData data);
}

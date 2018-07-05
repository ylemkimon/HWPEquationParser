package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class ImageToBase64Task extends ImageIOTask {
    private static final Logger logger = LogManager.getLogger();

    public ImageToBase64Task(BinItem binItem, String base64) {
        super(binItem, base64);
    }

    @Override
    protected void writeBufferedImage(BufferedImage subimage, ImageData data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStream os = Base64.getEncoder().wrap(baos)) {
            ImageIO.write(subimage, "png", os);
            //noinspection StringConcatenationMissingWhitespace
            data.updateUpdatable("<img src=\"data:image/png;base64," + baos.toString("ISO-8859-1")
                    + "\" style=\"width:" + data.getWidth() + "px;height:" + data.getHeight() + "px;\">");
        } catch (IOException e) {
            logger.error("Exception during writing BufferedImage", e);
        }
    }
}

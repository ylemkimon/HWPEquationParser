package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToFileTask extends ImageIOTask {
    private static final Logger logger = LogManager.getLogger();

    private final File filepath;
    private final String publicPath;

    // add: 2018.06.20 @leria95 -- select img tag (local/proc)
    private final boolean procMode;

    public ImageToFileTask(BinItem bin, String base64, File filepath, String publicPath, boolean procMode) {
        super(bin, base64);
        this.filepath = filepath;
        this.publicPath = publicPath;
        this.procMode = procMode;
    }

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    @Override
    protected void writeBufferedImage(BufferedImage subimage, ImageData data) {
        try {
            File output = new File(filepath, data.getUuid() + ".png");
            ImageIO.write(subimage, "png", output);

            // edit: 2018.06.20 @leria95 -- select img tag (local/proc)
            if(procMode) {
                data.updateUpdatable("<div class=\"ext-resource center-block\" data-imgLink=\"" + publicPath +
                    output.getName() + "\" data-img-width=\"" + data.getWidth() + "\" data-img-height=\"" +
                    data.getHeight() + "\"/>");
            } else {
                data.updateUpdatable("<img src=\"" + publicPath + output.getName()
                    + "\" style=\"width:" + data.getWidth() + "px;height:" + data.getHeight() + "px;\">");
            }
        } catch (IOException e) {
            logger.error("Exception during writing BufferedImage", e);
        }
    }
}

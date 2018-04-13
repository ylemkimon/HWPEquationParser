package kim.ylem.hmlparser.image.imageio;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ToFileTask extends ImageIOTask {
    private final File filepath;
    private final String publicPath;

    public ToFileTask(BinItem bin, String base64, File filepath, String publicPath) {
        super(bin, base64);
        this.filepath = filepath;
        this.publicPath = publicPath;
    }

    @Override
    protected void writeBufferedImage(BufferedImage subimage, ImageData data) {
        try {
            File output = new File(filepath, data.getUuid() + ".png");
            ImageIO.write(subimage, "png", output);
            data.updateUpdatable("<img src=\"" + publicPath + output.getName()
                    + "\" style=\"width:" + data.getWidth() + "px;height:" + data.getHeight() + "px;\">");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

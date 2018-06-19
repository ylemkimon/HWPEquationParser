package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToFileTask extends ImageIOTask {
    private final File filepath;
    private final String publicPath;

    public ImageToFileTask(BinItem bin, String base64, File filepath, String publicPath) {
        super(bin, base64);
        this.filepath = filepath;
        this.publicPath = publicPath;
    }

    @Override
    protected void writeBufferedImage(BufferedImage subimage, ImageData data) {
        try {
            File output = new File(filepath, data.getUuid() + ".png");
            ImageIO.write(subimage, "png", output);
            /*
            data.updateUpdatable("<img src=\"" + publicPath + output.getName()
                    + "\" style=\"width:" + data.getWidth() + "px;height:" + data.getHeight() + "px;\">");
            */
            data.updateUpdatable(" \n <div class=\'ext-resource center-block\' data-imgLink=\'" + publicPath + output.getName() + "\'" + 
            		" data-img-width=\'" + data.getWidth() + "\' data-img-height=\'" + data.getHeight() + "\'" + 
            		"/> \n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

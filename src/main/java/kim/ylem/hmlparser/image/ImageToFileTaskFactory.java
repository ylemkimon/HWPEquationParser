package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;

import java.io.File;
import java.nio.file.Path;

public class ImageToFileTaskFactory implements ImageTaskFactory {
    private final File filepath;
    private String publicPath;

    public ImageToFileTaskFactory(File filepath) {
        this.filepath = filepath;
        publicPath = filepath.toURI().toString();
    }

    public ImageToFileTaskFactory(Path filepath) {
        this(filepath.toFile());
    }

    public ImageToFileTaskFactory(String filepath) {
        this(new File(filepath));
    }

    public void setPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }

    @Override
    public ImageTask create(BinItem binItem, String base64) {
        return new ImageToFileTask(binItem, base64, filepath, publicPath);
    }
}

package kim.ylem.hmlparser.image.imageio;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.image.ImageTaskFactory;

import java.io.File;
import java.nio.file.Path;

public class ToFileTaskFactory implements ImageTaskFactory {
    private final File filepath;
    private String publicPath;

    public ToFileTaskFactory(File filepath) {
        this.filepath = filepath;
        publicPath = filepath.toURI().toString();
    }

    public ToFileTaskFactory(Path filepath) {
        this(filepath.toFile());
    }

    public ToFileTaskFactory(String filepath) {
        this(new File(filepath));
    }

    public void withPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }

    @Override
    public Runnable create(BinItem binItem, String base64) {
        return new ToFileTask(binItem, base64, filepath, publicPath);
    }
}

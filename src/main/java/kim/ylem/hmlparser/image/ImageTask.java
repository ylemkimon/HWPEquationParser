package kim.ylem.hmlparser.image;

import kim.ylem.hmlparser.BinItem;
import kim.ylem.hmlparser.ImageData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@SuppressWarnings("WeakerAccess")
public abstract class ImageTask implements Runnable {
    protected final BinItem binItem;
    protected final String base64;

    protected ImageTask(BinItem binItem, String base64) {
        this.binItem = binItem;
        this.base64 = base64;
    }

    protected static byte[] decode(String base64) {
        return Base64.getMimeDecoder().decode(base64);
    }

    protected static byte[] decompress(byte[] data) {
        Inflater inflater = new Inflater(true);
        inflater.setInput(data);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int len = inflater.inflate(buffer);
                os.write(buffer, 0, len);
            }
            return os.toByteArray();
        } catch (DataFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            inflater.end();
        }
        return null;
    }

    protected abstract void read(byte[] data);

    protected abstract void write(ImageData data);

    @Override
    public void run() {
        byte[] data = binItem.isCompressed() ? decompress(decode(base64)) : decode(base64);
        if (data != null) {
            read(data);
            for (ImageData imageData : binItem.getImageDataList()) {
                write(imageData);
            }
        }
    }
}

/*
 * Apache Commons IO
 * Copyright 2002-2017 The Apache Software Foundation
 *
 * This product includes software developed at
 * The Apache Software Foundation (http://www.apache.org/).
 *
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kim.ylem.xmlparser;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to wrap a stream that includes an encoded UTF-8 byte order mark as its first bytes.
 * <p>
 * This class detects these bytes, automatically skips them, and return the subsequent byte as the first byte
 * in the stream.
 */
public class BOMInputStream extends FilterInputStream {
    /**
     * UTF-8 BOM
     */
    private static final int[] UTF_8_BOM = {0xEF, 0xBB, 0xBF};

    /**
     * Represents the end-of-file (or stream).
     */
    private static final int EOF = -1;

    private int[] firstBytes;
    private int fbLength;
    private int fbIndex;
    private int markFbIndex;
    private boolean markedAtStart;

    /**
     * Constructs a new BOM InputStream that excludes a UTF-8 BOM.
     *
     * @param delegate the InputStream to delegate to
     */
    public BOMInputStream(InputStream delegate) {
        super(delegate);
    }

    /**
     * This method reads and either preserves or skips the first bytes in the stream. It behaves like the single-byte
     * {@code read()} method, either returning a valid byte or -1 to indicate that the initial bytes have been
     * processed already.
     *
     * @return the byte read (excluding BOM) or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    private int readFirstBytes() throws IOException {
        if (firstBytes == null) {
            firstBytes = new int[UTF_8_BOM.length];
            boolean hasBOM = true;
            for (int i = 0; i < UTF_8_BOM.length; i++) {
                firstBytes[i] = in.read();
                fbLength++;
                if (firstBytes[i] != UTF_8_BOM[i]) {
                    hasBOM = false;
                    break;
                }
            }
            if (hasBOM) {
                fbLength = 0;
            }
        }
        return fbIndex < fbLength ? firstBytes[fbIndex++] : EOF;
    }

    /**
     * Invokes the delegate's {@code read()} method, detecting and skipping BOM.
     *
     * @return the byte read (excluding BOM) or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        int b = readFirstBytes();
        return b >= 0 ? b : in.read();
    }

    /**
     * Invokes the delegate's {@code read(byte[], int, int)} method, detecting and skipping BOM.
     *
     * @param buf the buffer to read the bytes into
     * @param off The start offset
     * @param len The number of bytes to read (excluding BOM)
     * @return the number of bytes read or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        int firstCount = 0;
        int b = 0;
        while (len > 0 && b >= 0) {
            b = readFirstBytes();
            if (b >= 0) {
                buf[off++] = (byte) (b & 0xFF);
                len--;
                firstCount++;
            }
        }
        int secondCount = in.read(buf, off, len);
        return secondCount < 0 ? firstCount > 0 ? firstCount : EOF : firstCount + secondCount;
    }

    /**
     * Invokes the delegate's {@code read(byte[])} method, detecting and skipping BOM.
     *
     * @param buf the buffer to read the bytes into
     * @return the number of bytes read (excluding BOM) or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    /**
     * Invokes the delegate's {@code mark(int)} method.
     *
     * @param readlimit read ahead limit
     */
    @Override
    public synchronized void mark(int readlimit) {
        markFbIndex = fbIndex;
        markedAtStart = firstBytes == null;
        in.mark(readlimit);
    }

    /**
     * Invokes the delegate's {@code reset()} method.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void reset() throws IOException {
        fbIndex = markFbIndex;
        if (markedAtStart) {
            firstBytes = null;
        }
        in.reset();
    }

    /**
     * Invokes the delegate's {@code skip(long)} method, detecting and skipping BOM.
     *
     * @param n the number of bytes to skip
     * @return the number of bytes to skipped or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long skip(long n) throws IOException {
        int skipped = 0;
        while ((n > skipped) && (readFirstBytes() >= 0)) {
            skipped++;
        }
        return in.skip(n - skipped) + skipped;
    }
}

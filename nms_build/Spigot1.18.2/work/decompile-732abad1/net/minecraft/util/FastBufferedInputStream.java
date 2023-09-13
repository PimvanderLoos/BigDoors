package net.minecraft.util;

import java.io.IOException;
import java.io.InputStream;

public class FastBufferedInputStream extends InputStream {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final InputStream in;
    private final byte[] buffer;
    private int limit;
    private int position;

    public FastBufferedInputStream(InputStream inputstream) {
        this(inputstream, 8192);
    }

    public FastBufferedInputStream(InputStream inputstream, int i) {
        this.in = inputstream;
        this.buffer = new byte[i];
    }

    public int read() throws IOException {
        if (this.position >= this.limit) {
            this.fill();
            if (this.position >= this.limit) {
                return -1;
            }
        }

        return Byte.toUnsignedInt(this.buffer[this.position++]);
    }

    public int read(byte[] abyte, int i, int j) throws IOException {
        int k = this.bytesInBuffer();

        if (k <= 0) {
            if (j >= this.buffer.length) {
                return this.in.read(abyte, i, j);
            }

            this.fill();
            k = this.bytesInBuffer();
            if (k <= 0) {
                return -1;
            }
        }

        if (j > k) {
            j = k;
        }

        System.arraycopy(this.buffer, this.position, abyte, i, j);
        this.position += j;
        return j;
    }

    public long skip(long i) throws IOException {
        if (i <= 0L) {
            return 0L;
        } else {
            long j = (long) this.bytesInBuffer();

            if (j <= 0L) {
                return this.in.skip(i);
            } else {
                if (i > j) {
                    i = j;
                }

                this.position = (int) ((long) this.position + i);
                return i;
            }
        }
    }

    public int available() throws IOException {
        return this.bytesInBuffer() + this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    private int bytesInBuffer() {
        return this.limit - this.position;
    }

    private void fill() throws IOException {
        this.limit = 0;
        this.position = 0;
        int i = this.in.read(this.buffer, 0, this.buffer.length);

        if (i > 0) {
            this.limit = i;
        }

    }
}

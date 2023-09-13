package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.VisibleForDebug;

public final class NibbleArray {

    public static final int LAYER_COUNT = 16;
    public static final int LAYER_SIZE = 128;
    public static final int SIZE = 2048;
    private static final int NIBBLE_SIZE = 4;
    @Nullable
    protected byte[] data;

    public NibbleArray() {}

    public NibbleArray(byte[] abyte) {
        this.data = abyte;
        if (abyte.length != 2048) {
            throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("DataLayer should be 2048 bytes not: " + abyte.length)));
        }
    }

    protected NibbleArray(int i) {
        this.data = new byte[i];
    }

    public int a(int i, int j, int k) {
        return this.b(b(i, j, k));
    }

    public void a(int i, int j, int k, int l) {
        this.a(b(i, j, k), l);
    }

    private static int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int b(int i) {
        if (this.data == null) {
            return 0;
        } else {
            int j = d(i);
            int k = c(i);

            return this.data[j] >> 4 * k & 15;
        }
    }

    private void a(int i, int j) {
        if (this.data == null) {
            this.data = new byte[2048];
        }

        int k = d(i);
        int l = c(i);
        int i1 = ~(15 << 4 * l);
        int j1 = (j & 15) << 4 * l;

        this.data[k] = (byte) (this.data[k] & i1 | j1);
    }

    private static int c(int i) {
        return i & 1;
    }

    private static int d(int i) {
        return i >> 1;
    }

    public byte[] asBytes() {
        if (this.data == null) {
            this.data = new byte[2048];
        }

        return this.data;
    }

    public NibbleArray b() {
        return this.data == null ? new NibbleArray() : new NibbleArray((byte[]) this.data.clone());
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < 4096; ++i) {
            stringbuilder.append(Integer.toHexString(this.b(i)));
            if ((i & 15) == 15) {
                stringbuilder.append("\n");
            }

            if ((i & 255) == 255) {
                stringbuilder.append("\n");
            }
        }

        return stringbuilder.toString();
    }

    @VisibleForDebug
    public String a(int i) {
        StringBuilder stringbuilder = new StringBuilder();

        for (int j = 0; j < 256; ++j) {
            stringbuilder.append(Integer.toHexString(this.b(j)));
            if ((j & 15) == 15) {
                stringbuilder.append("\n");
            }
        }

        return stringbuilder.toString();
    }

    public boolean c() {
        return this.data == null;
    }
}

package net.minecraft.util.datafix;

import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class DataBitsPacked {

    private static final int BIT_TO_LONG_SHIFT = 6;
    private final long[] data;
    private final int bits;
    private final long mask;
    private final int size;

    public DataBitsPacked(int i, int j) {
        this(i, j, new long[MathHelper.roundToward(j * i, 64) / 64]);
    }

    public DataBitsPacked(int i, int j, long[] along) {
        Validate.inclusiveBetween(1L, 32L, (long) i);
        this.size = j;
        this.bits = i;
        this.data = along;
        this.mask = (1L << i) - 1L;
        int k = MathHelper.roundToward(j * i, 64) / 64;

        if (along.length != k) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + along.length + " but expected: " + k);
        }
    }

    public void set(int i, int j) {
        Validate.inclusiveBetween(0L, (long) (this.size - 1), (long) i);
        Validate.inclusiveBetween(0L, this.mask, (long) j);
        int k = i * this.bits;
        int l = k >> 6;
        int i1 = (i + 1) * this.bits - 1 >> 6;
        int j1 = k ^ l << 6;

        this.data[l] = this.data[l] & ~(this.mask << j1) | ((long) j & this.mask) << j1;
        if (l != i1) {
            int k1 = 64 - j1;
            int l1 = this.bits - k1;

            this.data[i1] = this.data[i1] >>> l1 << l1 | ((long) j & this.mask) >> k1;
        }

    }

    public int get(int i) {
        Validate.inclusiveBetween(0L, (long) (this.size - 1), (long) i);
        int j = i * this.bits;
        int k = j >> 6;
        int l = (i + 1) * this.bits - 1 >> 6;
        int i1 = j ^ k << 6;

        if (k == l) {
            return (int) (this.data[k] >>> i1 & this.mask);
        } else {
            int j1 = 64 - i1;

            return (int) ((this.data[k] >>> i1 | this.data[l] << j1) & this.mask);
        }
    }

    public long[] getRaw() {
        return this.data;
    }

    public int getBits() {
        return this.bits;
    }
}

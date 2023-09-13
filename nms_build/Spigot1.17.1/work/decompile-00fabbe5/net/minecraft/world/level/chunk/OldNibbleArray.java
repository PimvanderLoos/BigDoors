package net.minecraft.world.level.chunk;

public class OldNibbleArray {

    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public OldNibbleArray(byte[] abyte, int i) {
        this.data = abyte;
        this.depthBits = i;
        this.depthBitsPlusFour = i + 4;
    }

    public int a(int i, int j, int k) {
        int l = i << this.depthBitsPlusFour | k << this.depthBits | j;
        int i1 = l >> 1;
        int j1 = l & 1;

        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }
}

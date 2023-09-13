package net.minecraft.world.level.chunk.storage;

import java.util.BitSet;

public class RegionFileBitSet {

    private final BitSet a = new BitSet();

    public RegionFileBitSet() {}

    public void a(int i, int j) {
        this.a.set(i, i + j);
    }

    public void b(int i, int j) {
        this.a.clear(i, i + j);
    }

    public int a(int i) {
        int j = 0;

        while (true) {
            int k = this.a.nextClearBit(j);
            int l = this.a.nextSetBit(k);

            if (l == -1 || l - k >= i) {
                this.a(k, i);
                return k;
            }

            j = l;
        }
    }
}

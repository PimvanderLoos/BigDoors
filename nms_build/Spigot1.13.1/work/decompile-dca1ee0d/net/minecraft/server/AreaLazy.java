package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public final class AreaLazy implements Area {

    private final AreaTransformer8 a;
    private final Long2IntLinkedOpenHashMap b;
    private final int c;
    private final AreaDimension d;

    public AreaLazy(Long2IntLinkedOpenHashMap long2intlinkedopenhashmap, int i, AreaDimension areadimension, AreaTransformer8 areatransformer8) {
        this.b = long2intlinkedopenhashmap;
        this.c = i;
        this.d = areadimension;
        this.a = areatransformer8;
    }

    public int a(int i, int j) {
        long k = this.b(i, j);
        Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = this.b;

        synchronized (this.b) {
            int l = this.b.get(k);

            if (l != Integer.MIN_VALUE) {
                return l;
            } else {
                int i1 = this.a.apply(i, j);

                this.b.put(k, i1);
                if (this.b.size() > this.c) {
                    for (int j1 = 0; j1 < this.c / 16; ++j1) {
                        this.b.removeFirstInt();
                    }
                }

                return i1;
            }
        }
    }

    private long b(int i, int j) {
        long k = 1L;

        k <<= 26;
        k |= (long) (i + this.d.a()) & 67108863L;
        k <<= 26;
        k |= (long) (j + this.d.b()) & 67108863L;
        return k;
    }

    public int a() {
        return this.c;
    }
}

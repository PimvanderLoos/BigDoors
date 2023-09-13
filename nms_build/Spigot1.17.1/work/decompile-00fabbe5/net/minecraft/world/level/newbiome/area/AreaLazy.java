package net.minecraft.world.level.newbiome.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer8;

public final class AreaLazy implements Area {

    private final AreaTransformer8 transformer;
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;

    public AreaLazy(Long2IntLinkedOpenHashMap long2intlinkedopenhashmap, int i, AreaTransformer8 areatransformer8) {
        this.cache = long2intlinkedopenhashmap;
        this.maxCache = i;
        this.transformer = areatransformer8;
    }

    @Override
    public int a(int i, int j) {
        long k = ChunkCoordIntPair.pair(i, j);
        Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = this.cache;

        synchronized (this.cache) {
            int l = this.cache.get(k);

            if (l != Integer.MIN_VALUE) {
                return l;
            } else {
                int i1 = this.transformer.apply(i, j);

                this.cache.put(k, i1);
                if (this.cache.size() > this.maxCache) {
                    for (int j1 = 0; j1 < this.maxCache / 16; ++j1) {
                        this.cache.removeFirstInt();
                    }
                }

                return i1;
            }
        }
    }

    public int a() {
        return this.maxCache;
    }
}

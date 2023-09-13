package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.NibbleArray;

public abstract class LightEngineStorageArray<M extends LightEngineStorageArray<M>> {

    private static final int CACHE_SIZE = 2;
    private final long[] lastSectionKeys = new long[2];
    private final NibbleArray[] lastSections = new NibbleArray[2];
    private boolean cacheEnabled;
    protected final Long2ObjectOpenHashMap<NibbleArray> map;

    protected LightEngineStorageArray(Long2ObjectOpenHashMap<NibbleArray> long2objectopenhashmap) {
        this.map = long2objectopenhashmap;
        this.c();
        this.cacheEnabled = true;
    }

    public abstract M b();

    public void a(long i) {
        this.map.put(i, ((NibbleArray) this.map.get(i)).b());
        this.c();
    }

    public boolean b(long i) {
        return this.map.containsKey(i);
    }

    @Nullable
    public NibbleArray c(long i) {
        if (this.cacheEnabled) {
            for (int j = 0; j < 2; ++j) {
                if (i == this.lastSectionKeys[j]) {
                    return this.lastSections[j];
                }
            }
        }

        NibbleArray nibblearray = (NibbleArray) this.map.get(i);

        if (nibblearray == null) {
            return null;
        } else {
            if (this.cacheEnabled) {
                for (int k = 1; k > 0; --k) {
                    this.lastSectionKeys[k] = this.lastSectionKeys[k - 1];
                    this.lastSections[k] = this.lastSections[k - 1];
                }

                this.lastSectionKeys[0] = i;
                this.lastSections[0] = nibblearray;
            }

            return nibblearray;
        }
    }

    @Nullable
    public NibbleArray d(long i) {
        return (NibbleArray) this.map.remove(i);
    }

    public void a(long i, NibbleArray nibblearray) {
        this.map.put(i, nibblearray);
    }

    public void c() {
        for (int i = 0; i < 2; ++i) {
            this.lastSectionKeys[i] = Long.MAX_VALUE;
            this.lastSections[i] = null;
        }

    }

    public void d() {
        this.cacheEnabled = false;
    }
}

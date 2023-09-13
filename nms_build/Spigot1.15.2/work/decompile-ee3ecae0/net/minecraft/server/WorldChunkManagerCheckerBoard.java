package net.minecraft.server;

import com.google.common.collect.ImmutableSet;

public class WorldChunkManagerCheckerBoard extends WorldChunkManager {

    private final BiomeBase[] d;
    private final int e;

    public WorldChunkManagerCheckerBoard(BiomeLayoutCheckerboardConfiguration biomelayoutcheckerboardconfiguration) {
        super(ImmutableSet.copyOf(biomelayoutcheckerboardconfiguration.a()));
        this.d = biomelayoutcheckerboardconfiguration.a();
        this.e = biomelayoutcheckerboardconfiguration.b() + 2;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return this.d[Math.abs(((i >> this.e) + (k >> this.e)) % this.d.length)];
    }
}

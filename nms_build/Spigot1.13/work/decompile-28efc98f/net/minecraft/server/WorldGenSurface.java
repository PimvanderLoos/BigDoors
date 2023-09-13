package net.minecraft.server;

import java.util.Random;

public interface WorldGenSurface<C extends WorldGenSurfaceConfiguration> {

    void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, C c0);

    default void a(long i) {}
}

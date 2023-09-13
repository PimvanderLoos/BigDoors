package net.minecraft.server;

import java.util.Random;

public class WorldGenSurfaceComposite<C extends WorldGenSurfaceConfiguration> implements WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    private final WorldGenSurface<C> a;
    private final C b;

    public WorldGenSurfaceComposite(WorldGenSurface<C> worldgensurface, C c0) {
        this.a = worldgensurface;
        this.b = c0;
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        this.a.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, this.b);
    }

    public void a(long i) {
        this.a.a(i);
    }

    public C a() {
        return this.b;
    }
}

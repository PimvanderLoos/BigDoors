package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenSurfaceComposite<SC extends WorldGenSurfaceConfiguration> {

    public static final Codec<WorldGenSurfaceComposite<?>> a = IRegistry.SURFACE_BUILDER.dispatch("name", (worldgensurfacecomposite) -> {
        return worldgensurfacecomposite.b;
    }, WorldGenSurface::d);
    public final WorldGenSurface<SC> b;
    public final SC c;

    public WorldGenSurfaceComposite(WorldGenSurface<SC> worldgensurface, SC sc) {
        this.b = worldgensurface;
        this.c = sc;
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1) {
        this.b.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, this.c);
    }

    public void a(long i) {
        this.b.a(i);
    }

    public SC a() {
        return this.c;
    }
}

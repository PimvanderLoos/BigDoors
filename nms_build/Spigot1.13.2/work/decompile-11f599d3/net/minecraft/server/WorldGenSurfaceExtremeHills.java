package net.minecraft.server;

import java.util.Random;

public class WorldGenSurfaceExtremeHills implements WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceExtremeHills() {}

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        if (d0 > 1.0D) {
            BiomeBase.au.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, BiomeBase.aj);
        } else {
            BiomeBase.au.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, BiomeBase.ai);
        }

    }
}

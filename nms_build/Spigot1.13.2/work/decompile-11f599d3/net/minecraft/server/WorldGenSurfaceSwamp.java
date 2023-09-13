package net.minecraft.server;

import java.util.Random;

public class WorldGenSurfaceSwamp implements WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceSwamp() {}

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = BiomeBase.aJ.a((double) i * 0.25D, (double) j * 0.25D);

        if (d1 > 0.0D) {
            int j1 = i & 15;
            int k1 = j & 15;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int l1 = k; l1 >= 0; --l1) {
                blockposition_mutableblockposition.c(j1, l1, k1);
                if (!ichunkaccess.getType(blockposition_mutableblockposition).isAir()) {
                    if (l1 == 62 && ichunkaccess.getType(blockposition_mutableblockposition).getBlock() != iblockdata1.getBlock()) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata1, false);
                        if (d1 < 0.12D) {
                            ichunkaccess.setType(blockposition_mutableblockposition.d(0, 1, 0), Blocks.LILY_PAD.getBlockData(), false);
                        }
                    }
                    break;
                }
            }
        }

        BiomeBase.au.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, worldgensurfaceconfigurationbase);
    }
}

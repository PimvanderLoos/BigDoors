package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenSurfaceEmpty extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceEmpty(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {}
}

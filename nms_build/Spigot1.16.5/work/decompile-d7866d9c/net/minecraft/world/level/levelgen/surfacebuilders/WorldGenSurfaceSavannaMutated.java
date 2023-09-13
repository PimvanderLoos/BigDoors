package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceSavannaMutated extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceSavannaMutated(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        if (d0 > 1.75D) {
            WorldGenSurface.v.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.i);
        } else if (d0 > -0.5D) {
            WorldGenSurface.v.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.j);
        } else {
            WorldGenSurface.v.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.h);
        }

    }
}

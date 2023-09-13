package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceSwamp extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceSwamp(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = BiomeBase.BIOME_INFO_NOISE.a((double) i * 0.25D, (double) j * 0.25D, false);

        if (d1 > 0.0D) {
            int k1 = i & 15;
            int l1 = j & 15;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i2 = k; i2 >= i1; --i2) {
                blockposition_mutableblockposition.d(k1, i2, l1);
                if (!ichunkaccess.getType(blockposition_mutableblockposition).isAir()) {
                    if (i2 == 62 && !ichunkaccess.getType(blockposition_mutableblockposition).a(iblockdata1.getBlock())) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata1, false);
                    }
                    break;
                }
            }
        }

        WorldGenSurface.DEFAULT.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, j1, worldgensurfaceconfigurationbase);
    }
}

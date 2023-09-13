package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves;

public class WorldGenSurfaceNetherForest extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    private static final IBlockData AIR = Blocks.CAVE_AIR.getBlockData();
    protected long seed;
    private NoiseGeneratorOctaves decorationNoise;

    public WorldGenSurfaceNetherForest(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int k1 = l;
        int l1 = i & 15;
        int i2 = j & 15;
        double d1 = this.decorationNoise.a((double) i * 0.1D, (double) l, (double) j * 0.1D);
        boolean flag = d1 > 0.15D + random.nextDouble() * 0.35D;
        double d2 = this.decorationNoise.a((double) i * 0.1D, 109.0D, (double) j * 0.1D);
        boolean flag1 = d2 > 0.25D + random.nextDouble() * 0.9D;
        int j2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int k2 = -1;
        IBlockData iblockdata2 = worldgensurfaceconfigurationbase.b();

        for (int l2 = 127; l2 >= i1; --l2) {
            blockposition_mutableblockposition.d(l1, l2, i2);
            IBlockData iblockdata3 = worldgensurfaceconfigurationbase.a();
            IBlockData iblockdata4 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata4.isAir()) {
                k2 = -1;
            } else if (iblockdata4.a(iblockdata.getBlock())) {
                if (k2 == -1) {
                    boolean flag2 = false;

                    if (j2 <= 0) {
                        flag2 = true;
                        iblockdata2 = worldgensurfaceconfigurationbase.b();
                    }

                    if (flag) {
                        iblockdata3 = worldgensurfaceconfigurationbase.b();
                    } else if (flag1) {
                        iblockdata3 = worldgensurfaceconfigurationbase.c();
                    }

                    if (l2 < k1 && flag2) {
                        iblockdata3 = iblockdata1;
                    }

                    k2 = j2;
                    if (l2 >= k1 - 1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata3, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata2, false);
                    }
                } else if (k2 > 0) {
                    --k2;
                    ichunkaccess.setType(blockposition_mutableblockposition, iblockdata2, false);
                }
            }
        }

    }

    @Override
    public void a(long i) {
        if (this.seed != i || this.decorationNoise == null) {
            this.decorationNoise = new NoiseGeneratorOctaves(new SeededRandom(i), ImmutableList.of(0));
        }

        this.seed = i;
    }
}

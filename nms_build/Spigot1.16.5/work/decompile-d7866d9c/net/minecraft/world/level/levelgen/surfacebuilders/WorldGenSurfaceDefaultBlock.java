package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceDefaultBlock extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceDefaultBlock(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        this.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, worldgensurfaceconfigurationbase.a(), worldgensurfaceconfigurationbase.b(), worldgensurfaceconfigurationbase.c(), l);
    }

    protected void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3, IBlockData iblockdata4, int l) {
        IBlockData iblockdata5 = iblockdata2;
        IBlockData iblockdata6 = iblockdata3;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i1 = -1;
        int j1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int k1 = i & 15;
        int l1 = j & 15;

        for (int i2 = k; i2 >= 0; --i2) {
            blockposition_mutableblockposition.d(k1, i2, l1);
            IBlockData iblockdata7 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata7.isAir()) {
                i1 = -1;
            } else if (iblockdata7.a(iblockdata.getBlock())) {
                if (i1 == -1) {
                    if (j1 <= 0) {
                        iblockdata5 = Blocks.AIR.getBlockData();
                        iblockdata6 = iblockdata;
                    } else if (i2 >= l - 4 && i2 <= l + 1) {
                        iblockdata5 = iblockdata2;
                        iblockdata6 = iblockdata3;
                    }

                    if (i2 < l && (iblockdata5 == null || iblockdata5.isAir())) {
                        if (biomebase.getAdjustedTemperature(blockposition_mutableblockposition.d(i, i2, j)) < 0.15F) {
                            iblockdata5 = Blocks.ICE.getBlockData();
                        } else {
                            iblockdata5 = iblockdata1;
                        }

                        blockposition_mutableblockposition.d(k1, i2, l1);
                    }

                    i1 = j1;
                    if (i2 >= l - 1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                    } else if (i2 < l - 7 - j1) {
                        iblockdata5 = Blocks.AIR.getBlockData();
                        iblockdata6 = iblockdata;
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata6, false);
                    }
                } else if (i1 > 0) {
                    --i1;
                    ichunkaccess.setType(blockposition_mutableblockposition, iblockdata6, false);
                    if (i1 == 0 && iblockdata6.a(Blocks.SAND) && j1 > 1) {
                        i1 = random.nextInt(4) + Math.max(0, i2 - 63);
                        iblockdata6 = iblockdata6.a(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();
                    }
                }
            }
        }

    }
}

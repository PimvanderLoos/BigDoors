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

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        this.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, worldgensurfaceconfigurationbase.a(), worldgensurfaceconfigurationbase.b(), worldgensurfaceconfigurationbase.c(), l, i1);
    }

    protected void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3, IBlockData iblockdata4, int l, int i1) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int j1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int k1;
        IBlockData iblockdata5;

        if (j1 == 0) {
            boolean flag = false;

            for (k1 = k; k1 >= i1; --k1) {
                blockposition_mutableblockposition.d(i, k1, j);
                IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

                if (iblockdata6.isAir()) {
                    flag = false;
                } else if (iblockdata6.a(iblockdata.getBlock())) {
                    if (!flag) {
                        if (k1 >= l) {
                            iblockdata5 = Blocks.AIR.getBlockData();
                        } else if (k1 == l - 1) {
                            iblockdata5 = biomebase.getAdjustedTemperature(blockposition_mutableblockposition) < 0.15F ? Blocks.ICE.getBlockData() : iblockdata1;
                        } else if (k1 >= l - (7 + j1)) {
                            iblockdata5 = iblockdata;
                        } else {
                            iblockdata5 = iblockdata4;
                        }

                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                    }

                    flag = true;
                }
            }
        } else {
            IBlockData iblockdata7 = iblockdata3;

            k1 = -1;

            for (int l1 = k; l1 >= i1; --l1) {
                blockposition_mutableblockposition.d(i, l1, j);
                iblockdata5 = ichunkaccess.getType(blockposition_mutableblockposition);
                if (iblockdata5.isAir()) {
                    k1 = -1;
                } else if (iblockdata5.a(iblockdata.getBlock())) {
                    if (k1 == -1) {
                        k1 = j1;
                        IBlockData iblockdata8;

                        if (l1 >= l + 2) {
                            iblockdata8 = iblockdata2;
                        } else if (l1 >= l - 1) {
                            iblockdata7 = iblockdata3;
                            iblockdata8 = iblockdata2;
                        } else if (l1 >= l - 4) {
                            iblockdata7 = iblockdata3;
                            iblockdata8 = iblockdata3;
                        } else if (l1 >= l - (7 + j1)) {
                            iblockdata8 = iblockdata7;
                        } else {
                            iblockdata7 = iblockdata;
                            iblockdata8 = iblockdata4;
                        }

                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata8, false);
                    } else if (k1 > 0) {
                        --k1;
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                        if (k1 == 0 && iblockdata7.a(Blocks.SAND) && j1 > 1) {
                            k1 = random.nextInt(4) + Math.max(0, l1 - l);
                            iblockdata7 = iblockdata7.a(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();
                        }
                    }
                }
            }
        }

    }
}

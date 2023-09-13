package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Material;

public class WorldGenFeatureHugeFungi extends WorldGenerator<WorldGenFeatureHugeFungiConfiguration> {

    public WorldGenFeatureHugeFungi(Codec<WorldGenFeatureHugeFungiConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration) {
        Block block = worldgenfeaturehugefungiconfiguration.f.getBlock();
        BlockPosition blockposition1 = null;
        Block block1 = generatoraccessseed.getType(blockposition.down()).getBlock();

        if (block1 == block) {
            blockposition1 = blockposition;
        }

        if (blockposition1 == null) {
            return false;
        } else {
            int i = MathHelper.nextInt(random, 4, 13);

            if (random.nextInt(12) == 0) {
                i *= 2;
            }

            if (!worldgenfeaturehugefungiconfiguration.j) {
                int j = chunkgenerator.getGenerationDepth();

                if (blockposition1.getY() + i + 1 >= j) {
                    return false;
                }
            }

            boolean flag = !worldgenfeaturehugefungiconfiguration.j && random.nextFloat() < 0.06F;

            generatoraccessseed.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 4);
            this.a(generatoraccessseed, random, worldgenfeaturehugefungiconfiguration, blockposition1, i, flag);
            this.b(generatoraccessseed, random, worldgenfeaturehugefungiconfiguration, blockposition1, i, flag);
            return true;
        }
    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        return generatoraccess.a(blockposition, (iblockdata) -> {
            Material material = iblockdata.getMaterial();

            return iblockdata.getMaterial().isReplaceable() || flag && material == Material.PLANT;
        });
    }

    private void a(GeneratorAccess generatoraccess, Random random, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        IBlockData iblockdata = worldgenfeaturehugefungiconfiguration.g;
        int j = flag ? 1 : 0;

        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean flag1 = flag && MathHelper.a(k) == j && MathHelper.a(l) == j;

                for (int i1 = 0; i1 < i; ++i1) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, k, i1, l);
                    if (a(generatoraccess, blockposition_mutableblockposition, true)) {
                        if (worldgenfeaturehugefungiconfiguration.j) {
                            if (!generatoraccess.getType(blockposition_mutableblockposition.down()).isAir()) {
                                generatoraccess.b(blockposition_mutableblockposition, true);
                            }

                            generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata, 3);
                        } else if (flag1) {
                            if (random.nextFloat() < 0.1F) {
                                this.a((IWorldWriter) generatoraccess, (BlockPosition) blockposition_mutableblockposition, iblockdata);
                            }
                        } else {
                            this.a((IWorldWriter) generatoraccess, (BlockPosition) blockposition_mutableblockposition, iblockdata);
                        }
                    }
                }
            }
        }

    }

    private void b(GeneratorAccess generatoraccess, Random random, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        boolean flag1 = worldgenfeaturehugefungiconfiguration.h.a(Blocks.NETHER_WART_BLOCK);
        int j = Math.min(random.nextInt(1 + i / 3) + 5, i);
        int k = i - j;

        for (int l = k; l <= i; ++l) {
            int i1 = l < i - random.nextInt(3) ? 2 : 1;

            if (j > 8 && l < k + 4) {
                i1 = 3;
            }

            if (flag) {
                ++i1;
            }

            for (int j1 = -i1; j1 <= i1; ++j1) {
                for (int k1 = -i1; k1 <= i1; ++k1) {
                    boolean flag2 = j1 == -i1 || j1 == i1;
                    boolean flag3 = k1 == -i1 || k1 == i1;
                    boolean flag4 = !flag2 && !flag3 && l != i;
                    boolean flag5 = flag2 && flag3;
                    boolean flag6 = l < k + 3;

                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, j1, l, k1);
                    if (a(generatoraccess, blockposition_mutableblockposition, false)) {
                        if (worldgenfeaturehugefungiconfiguration.j && !generatoraccess.getType(blockposition_mutableblockposition.down()).isAir()) {
                            generatoraccess.b(blockposition_mutableblockposition, true);
                        }

                        if (flag6) {
                            if (!flag4) {
                                this.a(generatoraccess, random, blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.h, flag1);
                            }
                        } else if (flag4) {
                            this.a(generatoraccess, random, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 0.1F, 0.2F, flag1 ? 0.1F : 0.0F);
                        } else if (flag5) {
                            this.a(generatoraccess, random, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 0.01F, 0.7F, flag1 ? 0.083F : 0.0F);
                        } else {
                            this.a(generatoraccess, random, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 5.0E-4F, 0.98F, flag1 ? 0.07F : 0.0F);
                        }
                    }
                }
            }
        }

    }

    private void a(GeneratorAccess generatoraccess, Random random, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, float f, float f1, float f2) {
        if (random.nextFloat() < f) {
            this.a((IWorldWriter) generatoraccess, (BlockPosition) blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.i);
        } else if (random.nextFloat() < f1) {
            this.a((IWorldWriter) generatoraccess, (BlockPosition) blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.h);
            if (random.nextFloat() < f2) {
                a((BlockPosition) blockposition_mutableblockposition, generatoraccess, random);
            }
        }

    }

    private void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (generatoraccess.getType(blockposition.down()).a(iblockdata.getBlock())) {
            this.a((IWorldWriter) generatoraccess, blockposition, iblockdata);
        } else if ((double) random.nextFloat() < 0.15D) {
            this.a((IWorldWriter) generatoraccess, blockposition, iblockdata);
            if (flag && random.nextInt(11) == 0) {
                a(blockposition, generatoraccess, random);
            }
        }

    }

    private static void a(BlockPosition blockposition, GeneratorAccess generatoraccess, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i().c(EnumDirection.DOWN);

        if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
            int i = MathHelper.nextInt(random, 1, 5);

            if (random.nextInt(7) == 0) {
                i *= 2;
            }

            boolean flag = true;
            boolean flag1 = true;

            WorldGenFeatureWeepingVines.a(generatoraccess, random, blockposition_mutableblockposition, i, 23, 25);
        }
    }
}

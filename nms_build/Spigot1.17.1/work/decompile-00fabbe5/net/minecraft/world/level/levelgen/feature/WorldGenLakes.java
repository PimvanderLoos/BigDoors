package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.material.Material;

public class WorldGenLakes extends WorldGenerator<WorldGenFeatureLakeConfiguration> {

    private static final IBlockData AIR = Blocks.CAVE_AIR.getBlockData();

    public WorldGenLakes(Codec<WorldGenFeatureLakeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureLakeConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();

        WorldGenFeatureLakeConfiguration worldgenfeaturelakeconfiguration;

        for (worldgenfeaturelakeconfiguration = (WorldGenFeatureLakeConfiguration) featureplacecontext.e(); blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 5 && generatoraccessseed.isEmpty(blockposition); blockposition = blockposition.down()) {
            ;
        }

        if (blockposition.getY() <= generatoraccessseed.getMinBuildHeight() + 4) {
            return false;
        } else {
            blockposition = blockposition.down(4);
            if (generatoraccessseed.a(SectionPosition.a(blockposition), StructureGenerator.VILLAGE).findAny().isPresent()) {
                return false;
            } else {
                boolean[] aboolean = new boolean[2048];
                int i = random.nextInt(4) + 4;

                int j;

                for (j = 0; j < i; ++j) {
                    double d0 = random.nextDouble() * 6.0D + 3.0D;
                    double d1 = random.nextDouble() * 4.0D + 2.0D;
                    double d2 = random.nextDouble() * 6.0D + 3.0D;
                    double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                    double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                    double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                    for (int k = 1; k < 15; ++k) {
                        for (int l = 1; l < 15; ++l) {
                            for (int i1 = 1; i1 < 7; ++i1) {
                                double d6 = ((double) k - d3) / (d0 / 2.0D);
                                double d7 = ((double) i1 - d4) / (d1 / 2.0D);
                                double d8 = ((double) l - d5) / (d2 / 2.0D);
                                double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                                if (d9 < 1.0D) {
                                    aboolean[(k * 16 + l) * 8 + i1] = true;
                                }
                            }
                        }
                    }
                }

                int j1;
                int k1;

                for (j = 0; j < 16; ++j) {
                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 0; j1 < 8; ++j1) {
                            boolean flag = !aboolean[(j * 16 + k1) * 8 + j1] && (j < 15 && aboolean[((j + 1) * 16 + k1) * 8 + j1] || j > 0 && aboolean[((j - 1) * 16 + k1) * 8 + j1] || k1 < 15 && aboolean[(j * 16 + k1 + 1) * 8 + j1] || k1 > 0 && aboolean[(j * 16 + (k1 - 1)) * 8 + j1] || j1 < 7 && aboolean[(j * 16 + k1) * 8 + j1 + 1] || j1 > 0 && aboolean[(j * 16 + k1) * 8 + (j1 - 1)]);

                            if (flag) {
                                Material material = generatoraccessseed.getType(blockposition.c(j, j1, k1)).getMaterial();

                                if (j1 >= 4 && material.isLiquid()) {
                                    return false;
                                }

                                if (j1 < 4 && !material.isBuildable() && generatoraccessseed.getType(blockposition.c(j, j1, k1)) != worldgenfeaturelakeconfiguration.state) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                BlockPosition blockposition1;
                boolean flag1;

                for (j = 0; j < 16; ++j) {
                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 0; j1 < 8; ++j1) {
                            if (aboolean[(j * 16 + k1) * 8 + j1]) {
                                blockposition1 = blockposition.c(j, j1, k1);
                                flag1 = j1 >= 4;
                                generatoraccessseed.setTypeAndData(blockposition1, flag1 ? WorldGenLakes.AIR : worldgenfeaturelakeconfiguration.state, 2);
                                if (flag1) {
                                    generatoraccessseed.getBlockTickList().a(blockposition1, WorldGenLakes.AIR.getBlock(), 0);
                                    this.a(generatoraccessseed, blockposition1);
                                }
                            }
                        }
                    }
                }

                for (j = 0; j < 16; ++j) {
                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 4; j1 < 8; ++j1) {
                            if (aboolean[(j * 16 + k1) * 8 + j1]) {
                                blockposition1 = blockposition.c(j, j1 - 1, k1);
                                if (b(generatoraccessseed.getType(blockposition1)) && generatoraccessseed.getBrightness(EnumSkyBlock.SKY, blockposition.c(j, j1, k1)) > 0) {
                                    BiomeBase biomebase = generatoraccessseed.getBiome(blockposition1);

                                    if (biomebase.e().e().a().a(Blocks.MYCELIUM)) {
                                        generatoraccessseed.setTypeAndData(blockposition1, Blocks.MYCELIUM.getBlockData(), 2);
                                    } else {
                                        generatoraccessseed.setTypeAndData(blockposition1, Blocks.GRASS_BLOCK.getBlockData(), 2);
                                    }
                                }
                            }
                        }
                    }
                }

                if (worldgenfeaturelakeconfiguration.state.getMaterial() == Material.LAVA) {
                    BaseStoneSource basestonesource = featureplacecontext.b().g();

                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 0; j1 < 16; ++j1) {
                            for (int l1 = 0; l1 < 8; ++l1) {
                                flag1 = !aboolean[(k1 * 16 + j1) * 8 + l1] && (k1 < 15 && aboolean[((k1 + 1) * 16 + j1) * 8 + l1] || k1 > 0 && aboolean[((k1 - 1) * 16 + j1) * 8 + l1] || j1 < 15 && aboolean[(k1 * 16 + j1 + 1) * 8 + l1] || j1 > 0 && aboolean[(k1 * 16 + (j1 - 1)) * 8 + l1] || l1 < 7 && aboolean[(k1 * 16 + j1) * 8 + l1 + 1] || l1 > 0 && aboolean[(k1 * 16 + j1) * 8 + (l1 - 1)]);
                                if (flag1 && (l1 < 4 || random.nextInt(2) != 0)) {
                                    IBlockData iblockdata = generatoraccessseed.getType(blockposition.c(k1, l1, j1));

                                    if (iblockdata.getMaterial().isBuildable() && !iblockdata.a((Tag) TagsBlock.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                                        BlockPosition blockposition2 = blockposition.c(k1, l1, j1);

                                        generatoraccessseed.setTypeAndData(blockposition2, basestonesource.a(blockposition2), 2);
                                        this.a(generatoraccessseed, blockposition2);
                                    }
                                }
                            }
                        }
                    }
                }

                if (worldgenfeaturelakeconfiguration.state.getMaterial() == Material.WATER) {
                    for (j = 0; j < 16; ++j) {
                        for (k1 = 0; k1 < 16; ++k1) {
                            boolean flag2 = true;

                            blockposition1 = blockposition.c(j, 4, k1);
                            if (generatoraccessseed.getBiome(blockposition1).a(generatoraccessseed, blockposition1, false)) {
                                generatoraccessseed.setTypeAndData(blockposition1, Blocks.ICE.getBlockData(), 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
    }
}

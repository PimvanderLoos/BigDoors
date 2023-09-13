package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.material.Fluid;

public class GeodeFeature extends WorldGenerator<GeodeConfiguration> {

    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();

    public GeodeFeature(Codec<GeodeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<GeodeConfiguration> featureplacecontext) {
        GeodeConfiguration geodeconfiguration = (GeodeConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        int i = geodeconfiguration.minGenOffset;
        int j = geodeconfiguration.maxGenOffset;
        List<Pair<BlockPosition, Integer>> list = Lists.newLinkedList();
        int k = geodeconfiguration.distributionPoints.a(random);
        SeededRandom seededrandom = new SeededRandom(generatoraccessseed.getSeed());
        NoiseGeneratorNormal noisegeneratornormal = NoiseGeneratorNormal.a(seededrandom, -4, 1.0D);
        List<BlockPosition> list1 = Lists.newLinkedList();
        double d0 = (double) k / (double) geodeconfiguration.outerWallDistance.b();
        GeodeLayerSettings geodelayersettings = geodeconfiguration.geodeLayerSettings;
        GeodeBlockSettings geodeblocksettings = geodeconfiguration.geodeBlockSettings;
        GeodeCrackSettings geodecracksettings = geodeconfiguration.geodeCrackSettings;
        double d1 = 1.0D / Math.sqrt(geodelayersettings.filling);
        double d2 = 1.0D / Math.sqrt(geodelayersettings.innerLayer + d0);
        double d3 = 1.0D / Math.sqrt(geodelayersettings.middleLayer + d0);
        double d4 = 1.0D / Math.sqrt(geodelayersettings.outerLayer + d0);
        double d5 = 1.0D / Math.sqrt(geodecracksettings.baseCrackSize + random.nextDouble() / 2.0D + (k > 3 ? d0 : 0.0D));
        boolean flag = (double) random.nextFloat() < geodecracksettings.generateCrackChance;
        int l = 0;

        int i1;
        BlockPosition blockposition1;
        IBlockData iblockdata;
        int j1;

        for (j1 = 0; j1 < k; ++j1) {
            i1 = geodeconfiguration.outerWallDistance.a(random);
            int k1 = geodeconfiguration.outerWallDistance.a(random);
            int l1 = geodeconfiguration.outerWallDistance.a(random);

            blockposition1 = blockposition.c(i1, k1, l1);
            iblockdata = generatoraccessseed.getType(blockposition1);
            if (iblockdata.isAir() || iblockdata.a((Tag) TagsBlock.GEODE_INVALID_BLOCKS)) {
                ++l;
                if (l > geodeconfiguration.invalidBlocksThreshold) {
                    return false;
                }
            }

            list.add(Pair.of(blockposition1, geodeconfiguration.pointOffset.a(random)));
        }

        if (flag) {
            j1 = random.nextInt(4);
            i1 = k * 2 + 1;
            if (j1 == 0) {
                list1.add(blockposition.c(i1, 7, 0));
                list1.add(blockposition.c(i1, 5, 0));
                list1.add(blockposition.c(i1, 1, 0));
            } else if (j1 == 1) {
                list1.add(blockposition.c(0, 7, i1));
                list1.add(blockposition.c(0, 5, i1));
                list1.add(blockposition.c(0, 1, i1));
            } else if (j1 == 2) {
                list1.add(blockposition.c(i1, 7, i1));
                list1.add(blockposition.c(i1, 5, i1));
                list1.add(blockposition.c(i1, 1, i1));
            } else {
                list1.add(blockposition.c(0, 7, 0));
                list1.add(blockposition.c(0, 5, 0));
                list1.add(blockposition.c(0, 1, 0));
            }
        }

        List<BlockPosition> list2 = Lists.newArrayList();
        Predicate<IBlockData> predicate = a(geodeconfiguration.geodeBlockSettings.cannotReplace);
        Iterator iterator = BlockPosition.a(blockposition.c(i, i, i), blockposition.c(j, j, j)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition2 = (BlockPosition) iterator.next();
            double d6 = noisegeneratornormal.a((double) blockposition2.getX(), (double) blockposition2.getY(), (double) blockposition2.getZ()) * geodeconfiguration.noiseMultiplier;
            double d7 = 0.0D;
            double d8 = 0.0D;

            Pair pair;
            Iterator iterator1;

            for (iterator1 = list.iterator(); iterator1.hasNext(); d7 += MathHelper.h(blockposition2.j((BaseBlockPosition) pair.getFirst()) + (double) (Integer) pair.getSecond()) + d6) {
                pair = (Pair) iterator1.next();
            }

            BlockPosition blockposition3;

            for (iterator1 = list1.iterator(); iterator1.hasNext(); d8 += MathHelper.h(blockposition2.j(blockposition3) + (double) geodecracksettings.crackPointOffset) + d6) {
                blockposition3 = (BlockPosition) iterator1.next();
            }

            if (d7 >= d4) {
                if (flag && d8 >= d5 && d7 < d1) {
                    this.a(generatoraccessseed, blockposition2, Blocks.AIR.getBlockData(), predicate);
                    EnumDirection[] aenumdirection = GeodeFeature.DIRECTIONS;
                    int i2 = aenumdirection.length;

                    for (int j2 = 0; j2 < i2; ++j2) {
                        EnumDirection enumdirection = aenumdirection[j2];
                        BlockPosition blockposition4 = blockposition2.shift(enumdirection);
                        Fluid fluid = generatoraccessseed.getFluid(blockposition4);

                        if (!fluid.isEmpty()) {
                            generatoraccessseed.getFluidTickList().a(blockposition4, fluid.getType(), 0);
                        }
                    }
                } else if (d7 >= d1) {
                    this.a(generatoraccessseed, blockposition2, geodeblocksettings.fillingProvider.a(random, blockposition2), predicate);
                } else if (d7 >= d2) {
                    boolean flag1 = (double) random.nextFloat() < geodeconfiguration.useAlternateLayer0Chance;

                    if (flag1) {
                        this.a(generatoraccessseed, blockposition2, geodeblocksettings.alternateInnerLayerProvider.a(random, blockposition2), predicate);
                    } else {
                        this.a(generatoraccessseed, blockposition2, geodeblocksettings.innerLayerProvider.a(random, blockposition2), predicate);
                    }

                    if ((!geodeconfiguration.placementsRequireLayer0Alternate || flag1) && (double) random.nextFloat() < geodeconfiguration.usePotentialPlacementsChance) {
                        list2.add(blockposition2.immutableCopy());
                    }
                } else if (d7 >= d3) {
                    this.a(generatoraccessseed, blockposition2, geodeblocksettings.middleLayerProvider.a(random, blockposition2), predicate);
                } else if (d7 >= d4) {
                    this.a(generatoraccessseed, blockposition2, geodeblocksettings.outerLayerProvider.a(random, blockposition2), predicate);
                }
            }
        }

        List<IBlockData> list3 = geodeblocksettings.innerPlacements;
        Iterator iterator2 = list2.iterator();

        while (iterator2.hasNext()) {
            blockposition1 = (BlockPosition) iterator2.next();
            iblockdata = (IBlockData) SystemUtils.a(list3, random);
            EnumDirection[] aenumdirection1 = GeodeFeature.DIRECTIONS;
            int k2 = aenumdirection1.length;

            for (int l2 = 0; l2 < k2; ++l2) {
                EnumDirection enumdirection1 = aenumdirection1[l2];

                if (iblockdata.b(BlockProperties.FACING)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockProperties.FACING, enumdirection1);
                }

                BlockPosition blockposition5 = blockposition1.shift(enumdirection1);
                IBlockData iblockdata1 = generatoraccessseed.getType(blockposition5);

                if (iblockdata.b(BlockProperties.WATERLOGGED)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockProperties.WATERLOGGED, iblockdata1.getFluid().isSource());
                }

                if (BuddingAmethystBlock.g(iblockdata1)) {
                    this.a(generatoraccessseed, blockposition5, iblockdata, predicate);
                    break;
                }
            }
        }

        return true;
    }
}

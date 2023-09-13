package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;

public class GlowLichenFeature extends WorldGenerator<GlowLichenConfiguration> {

    public GlowLichenFeature(Codec<GlowLichenConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GlowLichenConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        Random random = featureplacecontext.random();
        GlowLichenConfiguration glowlichenconfiguration = (GlowLichenConfiguration) featureplacecontext.config();

        if (!isAirOrWater(generatoraccessseed.getBlockState(blockposition))) {
            return false;
        } else {
            List<EnumDirection> list = getShuffledDirections(glowlichenconfiguration, random);

            if (placeGlowLichenIfPossible(generatoraccessseed, blockposition, generatoraccessseed.getBlockState(blockposition), glowlichenconfiguration, random, list)) {
                return true;
            } else {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    blockposition_mutableblockposition.set(blockposition);
                    List<EnumDirection> list1 = getShuffledDirectionsExcept(glowlichenconfiguration, random, enumdirection.getOpposite());

                    for (int i = 0; i < glowlichenconfiguration.searchRange; ++i) {
                        blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition);

                        if (!isAirOrWater(iblockdata) && !iblockdata.is(Blocks.GLOW_LICHEN)) {
                            break;
                        }

                        if (placeGlowLichenIfPossible(generatoraccessseed, blockposition_mutableblockposition, iblockdata, glowlichenconfiguration, random, list1)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean placeGlowLichenIfPossible(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, IBlockData iblockdata, GlowLichenConfiguration glowlichenconfiguration, Random random, List<EnumDirection> list) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        Iterator iterator = list.iterator();

        EnumDirection enumdirection;
        IBlockData iblockdata1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
            iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection));
        } while (!glowlichenconfiguration.canBePlacedOn.contains(iblockdata1.getBlock()));

        GlowLichenBlock glowlichenblock = (GlowLichenBlock) Blocks.GLOW_LICHEN;
        IBlockData iblockdata2 = glowlichenblock.getStateForPlacement(iblockdata, generatoraccessseed, blockposition, enumdirection);

        if (iblockdata2 == null) {
            return false;
        } else {
            generatoraccessseed.setBlock(blockposition, iblockdata2, 3);
            generatoraccessseed.getChunk(blockposition).markPosForPostprocessing(blockposition);
            if (random.nextFloat() < glowlichenconfiguration.chanceOfSpreading) {
                glowlichenblock.spreadFromFaceTowardRandomDirection(iblockdata2, generatoraccessseed, blockposition, enumdirection, random, true);
            }

            return true;
        }
    }

    public static List<EnumDirection> getShuffledDirections(GlowLichenConfiguration glowlichenconfiguration, Random random) {
        List<EnumDirection> list = Lists.newArrayList(glowlichenconfiguration.validDirections);

        Collections.shuffle(list, random);
        return list;
    }

    public static List<EnumDirection> getShuffledDirectionsExcept(GlowLichenConfiguration glowlichenconfiguration, Random random, EnumDirection enumdirection) {
        List<EnumDirection> list = (List) glowlichenconfiguration.validDirections.stream().filter((enumdirection1) -> {
            return enumdirection1 != enumdirection;
        }).collect(Collectors.toList());

        Collections.shuffle(list, random);
        return list;
    }

    private static boolean isAirOrWater(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER);
    }
}

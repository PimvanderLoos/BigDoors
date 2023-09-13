package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class MultifaceGrowthFeature extends WorldGenerator<MultifaceGrowthConfiguration> {

    public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MultifaceGrowthConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();
        MultifaceGrowthConfiguration multifacegrowthconfiguration = (MultifaceGrowthConfiguration) featureplacecontext.config();

        if (!isAirOrWater(generatoraccessseed.getBlockState(blockposition))) {
            return false;
        } else {
            List<EnumDirection> list = multifacegrowthconfiguration.getShuffledDirections(randomsource);

            if (placeGrowthIfPossible(generatoraccessseed, blockposition, generatoraccessseed.getBlockState(blockposition), multifacegrowthconfiguration, randomsource, list)) {
                return true;
            } else {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    blockposition_mutableblockposition.set(blockposition);
                    List<EnumDirection> list1 = multifacegrowthconfiguration.getShuffledDirectionsExcept(randomsource, enumdirection.getOpposite());

                    for (int i = 0; i < multifacegrowthconfiguration.searchRange; ++i) {
                        blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition);

                        if (!isAirOrWater(iblockdata) && !iblockdata.is((Block) multifacegrowthconfiguration.placeBlock)) {
                            break;
                        }

                        if (placeGrowthIfPossible(generatoraccessseed, blockposition_mutableblockposition, iblockdata, multifacegrowthconfiguration, randomsource, list1)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean placeGrowthIfPossible(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, IBlockData iblockdata, MultifaceGrowthConfiguration multifacegrowthconfiguration, RandomSource randomsource, List<EnumDirection> list) {
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
        } while (!iblockdata1.is(multifacegrowthconfiguration.canBePlacedOn));

        IBlockData iblockdata2 = multifacegrowthconfiguration.placeBlock.getStateForPlacement(iblockdata, generatoraccessseed, blockposition, enumdirection);

        if (iblockdata2 == null) {
            return false;
        } else {
            generatoraccessseed.setBlock(blockposition, iblockdata2, 3);
            generatoraccessseed.getChunk(blockposition).markPosForPostprocessing(blockposition);
            if (randomsource.nextFloat() < multifacegrowthconfiguration.chanceOfSpreading) {
                multifacegrowthconfiguration.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection(iblockdata2, generatoraccessseed, blockposition, enumdirection, randomsource, true);
            }

            return true;
        }
    }

    private static boolean isAirOrWater(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER);
    }
}

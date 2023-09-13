package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {

    public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    protected Set<BlockPosition> placeGroundPatch(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, RandomSource randomsource, BlockPosition blockposition, Predicate<IBlockData> predicate, int i, int j) {
        Set<BlockPosition> set = super.placeGroundPatch(generatoraccessseed, vegetationpatchconfiguration, randomsource, blockposition, predicate, i, j);
        Set<BlockPosition> set1 = new HashSet();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = set.iterator();

        BlockPosition blockposition1;

        while (iterator.hasNext()) {
            blockposition1 = (BlockPosition) iterator.next();
            if (!isExposed(generatoraccessseed, set, blockposition1, blockposition_mutableblockposition)) {
                set1.add(blockposition1);
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            blockposition1 = (BlockPosition) iterator.next();
            generatoraccessseed.setBlock(blockposition1, Blocks.WATER.defaultBlockState(), 2);
        }

        return set1;
    }

    private static boolean isExposed(GeneratorAccessSeed generatoraccessseed, Set<BlockPosition> set, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        return isExposedDirection(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.NORTH) || isExposedDirection(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.EAST) || isExposedDirection(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.SOUTH) || isExposedDirection(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.WEST) || isExposedDirection(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.DOWN);
    }

    private static boolean isExposedDirection(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection) {
        blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
        return !generatoraccessseed.getBlockState(blockposition_mutableblockposition).isFaceSturdy(generatoraccessseed, blockposition_mutableblockposition, enumdirection.getOpposite());
    }

    @Override
    protected boolean placeVegetation(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        if (super.placeVegetation(generatoraccessseed, vegetationpatchconfiguration, chunkgenerator, randomsource, blockposition.below())) {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition);

            if (iblockdata.hasProperty(BlockProperties.WATERLOGGED) && !(Boolean) iblockdata.getValue(BlockProperties.WATERLOGGED)) {
                generatoraccessseed.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, true), 2);
            }

            return true;
        } else {
            return false;
        }
    }
}

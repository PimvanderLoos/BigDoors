package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
    protected Set<BlockPosition> a(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Random random, BlockPosition blockposition, Predicate<IBlockData> predicate, int i, int j) {
        Set<BlockPosition> set = super.a(generatoraccessseed, vegetationpatchconfiguration, random, blockposition, predicate, i, j);
        Set<BlockPosition> set1 = new HashSet();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = set.iterator();

        BlockPosition blockposition1;

        while (iterator.hasNext()) {
            blockposition1 = (BlockPosition) iterator.next();
            if (!a(generatoraccessseed, set, blockposition1, blockposition_mutableblockposition)) {
                set1.add(blockposition1);
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            blockposition1 = (BlockPosition) iterator.next();
            generatoraccessseed.setTypeAndData(blockposition1, Blocks.WATER.getBlockData(), 2);
        }

        return set1;
    }

    private static boolean a(GeneratorAccessSeed generatoraccessseed, Set<BlockPosition> set, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        return a(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.NORTH) || a(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.EAST) || a(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.SOUTH) || a(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.WEST) || a(generatoraccessseed, blockposition, blockposition_mutableblockposition, EnumDirection.DOWN);
    }

    private static boolean a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection) {
        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
        return !generatoraccessseed.getType(blockposition_mutableblockposition).d(generatoraccessseed, blockposition_mutableblockposition, enumdirection.opposite());
    }

    @Override
    protected boolean a(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        if (super.a(generatoraccessseed, vegetationpatchconfiguration, chunkgenerator, random, blockposition.down())) {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition);

            if (iblockdata.b(BlockProperties.WATERLOGGED) && !(Boolean) iblockdata.get(BlockProperties.WATERLOGGED)) {
                generatoraccessseed.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.WATERLOGGED, true), 2);
            }

            return true;
        } else {
            return false;
        }
    }
}

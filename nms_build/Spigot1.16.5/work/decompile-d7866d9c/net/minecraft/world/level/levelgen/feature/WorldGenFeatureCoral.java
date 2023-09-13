package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCoralFanWallAbstract;
import net.minecraft.world.level.block.BlockSeaPickle;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public abstract class WorldGenFeatureCoral extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureCoral(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        IBlockData iblockdata = ((Block) TagsBlock.CORAL_BLOCKS.a(random)).getBlockData();

        return this.a(generatoraccessseed, random, blockposition, iblockdata);
    }

    protected abstract boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata);

    protected boolean b(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = generatoraccess.getType(blockposition);

        if ((iblockdata1.a(Blocks.WATER) || iblockdata1.a((Tag) TagsBlock.CORALS)) && generatoraccess.getType(blockposition1).a(Blocks.WATER)) {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
            if (random.nextFloat() < 0.25F) {
                generatoraccess.setTypeAndData(blockposition1, ((Block) TagsBlock.CORALS.a(random)).getBlockData(), 2);
            } else if (random.nextFloat() < 0.05F) {
                generatoraccess.setTypeAndData(blockposition1, (IBlockData) Blocks.SEA_PICKLE.getBlockData().set(BlockSeaPickle.a, random.nextInt(4) + 1), 2);
            }

            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (random.nextFloat() < 0.2F) {
                    BlockPosition blockposition2 = blockposition.shift(enumdirection);

                    if (generatoraccess.getType(blockposition2).a(Blocks.WATER)) {
                        IBlockData iblockdata2 = (IBlockData) ((Block) TagsBlock.WALL_CORALS.a(random)).getBlockData().set(BlockCoralFanWallAbstract.a, enumdirection);

                        generatoraccess.setTypeAndData(blockposition2, iblockdata2, 2);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}

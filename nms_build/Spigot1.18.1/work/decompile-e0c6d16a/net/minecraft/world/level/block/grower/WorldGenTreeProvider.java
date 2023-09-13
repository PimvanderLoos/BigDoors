package net.minecraft.world.level.block.grower;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public abstract class WorldGenTreeProvider {

    public WorldGenTreeProvider() {}

    @Nullable
    protected abstract WorldGenFeatureConfigured<?, ?> getConfiguredFeature(Random random, boolean flag);

    public boolean growTree(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = this.getConfiguredFeature(random, this.hasFlowers(worldserver, blockposition));

        if (worldgenfeatureconfigured == null) {
            return false;
        } else {
            worldserver.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 4);
            if (worldgenfeatureconfigured.place(worldserver, chunkgenerator, random, blockposition)) {
                return true;
            } else {
                worldserver.setBlock(blockposition, iblockdata, 4);
                return false;
            }
        }
    }

    private boolean hasFlowers(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.MutableBlockPosition.betweenClosed(blockposition.below().north(2).west(2), blockposition.above().south(2).east(2)).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!generatoraccess.getBlockState(blockposition1).is((Tag) TagsBlock.FLOWERS));

        return true;
    }
}

package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public abstract class WorldGenMegaTreeProvider extends WorldGenTreeProvider {

    public WorldGenMegaTreeProvider() {}

    @Override
    public boolean growTree(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (isTwoByTwoSapling(iblockdata, worldserver, blockposition, i, j)) {
                    return this.placeMega(worldserver, chunkgenerator, blockposition, iblockdata, random, i, j);
                }
            }
        }

        return super.growTree(worldserver, chunkgenerator, blockposition, iblockdata, random);
    }

    @Nullable
    protected abstract WorldGenFeatureConfigured<?, ?> getConfiguredMegaFeature(Random random);

    public boolean placeMega(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random, int i, int j) {
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = this.getConfiguredMegaFeature(random);

        if (worldgenfeatureconfigured == null) {
            return false;
        } else {
            IBlockData iblockdata1 = Blocks.AIR.defaultBlockState();

            worldserver.setBlock(blockposition.offset(i, 0, j), iblockdata1, 4);
            worldserver.setBlock(blockposition.offset(i + 1, 0, j), iblockdata1, 4);
            worldserver.setBlock(blockposition.offset(i, 0, j + 1), iblockdata1, 4);
            worldserver.setBlock(blockposition.offset(i + 1, 0, j + 1), iblockdata1, 4);
            if (worldgenfeatureconfigured.place(worldserver, chunkgenerator, random, blockposition.offset(i, 0, j))) {
                return true;
            } else {
                worldserver.setBlock(blockposition.offset(i, 0, j), iblockdata, 4);
                worldserver.setBlock(blockposition.offset(i + 1, 0, j), iblockdata, 4);
                worldserver.setBlock(blockposition.offset(i, 0, j + 1), iblockdata, 4);
                worldserver.setBlock(blockposition.offset(i + 1, 0, j + 1), iblockdata, 4);
                return false;
            }
        }
    }

    public static boolean isTwoByTwoSapling(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, int i, int j) {
        Block block = iblockdata.getBlock();

        return iblockaccess.getBlockState(blockposition.offset(i, 0, j)).is(block) && iblockaccess.getBlockState(blockposition.offset(i + 1, 0, j)).is(block) && iblockaccess.getBlockState(blockposition.offset(i, 0, j + 1)).is(block) && iblockaccess.getBlockState(blockposition.offset(i + 1, 0, j + 1)).is(block);
    }
}

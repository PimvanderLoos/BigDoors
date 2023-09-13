package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public abstract class WorldGenMegaTreeProvider extends WorldGenTreeProvider {

    public WorldGenMegaTreeProvider() {}

    @Override
    public boolean growTree(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, RandomSource randomsource) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (isTwoByTwoSapling(iblockdata, worldserver, blockposition, i, j)) {
                    return this.placeMega(worldserver, chunkgenerator, blockposition, iblockdata, randomsource, i, j);
                }
            }
        }

        return super.growTree(worldserver, chunkgenerator, blockposition, iblockdata, randomsource);
    }

    @Nullable
    protected abstract ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredMegaFeature(RandomSource randomsource);

    public boolean placeMega(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, RandomSource randomsource, int i, int j) {
        ResourceKey<WorldGenFeatureConfigured<?, ?>> resourcekey = this.getConfiguredMegaFeature(randomsource);

        if (resourcekey == null) {
            return false;
        } else {
            Holder<WorldGenFeatureConfigured<?, ?>> holder = (Holder) worldserver.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Object) null);

            if (holder == null) {
                return false;
            } else {
                WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) holder.value();
                IBlockData iblockdata1 = Blocks.AIR.defaultBlockState();

                worldserver.setBlock(blockposition.offset(i, 0, j), iblockdata1, 4);
                worldserver.setBlock(blockposition.offset(i + 1, 0, j), iblockdata1, 4);
                worldserver.setBlock(blockposition.offset(i, 0, j + 1), iblockdata1, 4);
                worldserver.setBlock(blockposition.offset(i + 1, 0, j + 1), iblockdata1, 4);
                if (worldgenfeatureconfigured.place(worldserver, chunkgenerator, randomsource, blockposition.offset(i, 0, j))) {
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
    }

    public static boolean isTwoByTwoSapling(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, int i, int j) {
        Block block = iblockdata.getBlock();

        return iblockaccess.getBlockState(blockposition.offset(i, 0, j)).is(block) && iblockaccess.getBlockState(blockposition.offset(i + 1, 0, j)).is(block) && iblockaccess.getBlockState(blockposition.offset(i, 0, j + 1)).is(block) && iblockaccess.getBlockState(blockposition.offset(i + 1, 0, j + 1)).is(block);
    }
}

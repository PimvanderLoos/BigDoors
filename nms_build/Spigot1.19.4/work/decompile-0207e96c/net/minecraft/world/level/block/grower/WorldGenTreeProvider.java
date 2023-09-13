package net.minecraft.world.level.block.grower;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public abstract class WorldGenTreeProvider {

    public WorldGenTreeProvider() {}

    @Nullable
    protected abstract ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag);

    public boolean growTree(WorldServer worldserver, ChunkGenerator chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, RandomSource randomsource) {
        ResourceKey<WorldGenFeatureConfigured<?, ?>> resourcekey = this.getConfiguredFeature(randomsource, this.hasFlowers(worldserver, blockposition));

        if (resourcekey == null) {
            return false;
        } else {
            Holder<WorldGenFeatureConfigured<?, ?>> holder = (Holder) worldserver.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Object) null);

            if (holder == null) {
                return false;
            } else {
                WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) holder.value();
                IBlockData iblockdata1 = worldserver.getFluidState(blockposition).createLegacyBlock();

                worldserver.setBlock(blockposition, iblockdata1, 4);
                if (worldgenfeatureconfigured.place(worldserver, chunkgenerator, randomsource, blockposition)) {
                    if (worldserver.getBlockState(blockposition) == iblockdata1) {
                        worldserver.sendBlockUpdated(blockposition, iblockdata, iblockdata1, 2);
                    }

                    return true;
                } else {
                    worldserver.setBlock(blockposition, iblockdata, 4);
                    return false;
                }
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
        } while (!generatoraccess.getBlockState(blockposition1).is(TagsBlock.FLOWERS));

        return true;
    }
}

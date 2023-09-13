package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.HeightMap;

public class WorldProviderNormal {

    @Nullable
    protected static BlockPosition a(WorldServer worldserver, int i, int j, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, 0, j);
        BiomeBase biomebase = worldserver.getBiome(blockposition_mutableblockposition);
        boolean flag1 = worldserver.getDimensionManager().hasCeiling();
        IBlockData iblockdata = biomebase.e().e().a();

        if (flag && !iblockdata.getBlock().a((Tag) TagsBlock.VALID_SPAWN)) {
            return null;
        } else {
            Chunk chunk = worldserver.getChunkAt(i >> 4, j >> 4);
            int k = flag1 ? worldserver.getChunkProvider().getChunkGenerator().getSpawnHeight() : chunk.getHighestBlock(HeightMap.Type.MOTION_BLOCKING, i & 15, j & 15);

            if (k < 0) {
                return null;
            } else {
                int l = chunk.getHighestBlock(HeightMap.Type.WORLD_SURFACE, i & 15, j & 15);

                if (l <= k && l > chunk.getHighestBlock(HeightMap.Type.OCEAN_FLOOR, i & 15, j & 15)) {
                    return null;
                } else {
                    for (int i1 = k + 1; i1 >= 0; --i1) {
                        blockposition_mutableblockposition.d(i, i1, j);
                        IBlockData iblockdata1 = worldserver.getType(blockposition_mutableblockposition);

                        if (!iblockdata1.getFluid().isEmpty()) {
                            break;
                        }

                        if (iblockdata1.equals(iblockdata)) {
                            return blockposition_mutableblockposition.up().immutableCopy();
                        }
                    }

                    return null;
                }
            }
        }
    }

    @Nullable
    public static BlockPosition a(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        for (int i = chunkcoordintpair.d(); i <= chunkcoordintpair.f(); ++i) {
            for (int j = chunkcoordintpair.e(); j <= chunkcoordintpair.g(); ++j) {
                BlockPosition blockposition = a(worldserver, i, j, flag);

                if (blockposition != null) {
                    return blockposition;
                }
            }
        }

        return null;
    }
}

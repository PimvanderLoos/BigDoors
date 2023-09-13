package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.HeightMap;

public class WorldProviderNormal {

    public WorldProviderNormal() {}

    @Nullable
    protected static BlockPosition getOverworldRespawnPos(WorldServer worldserver, int i, int j) {
        boolean flag = worldserver.dimensionType().hasCeiling();
        Chunk chunk = worldserver.getChunk(SectionPosition.blockToSectionCoord(i), SectionPosition.blockToSectionCoord(j));
        int k = flag ? worldserver.getChunkSource().getGenerator().getSpawnHeight(worldserver) : chunk.getHeight(HeightMap.Type.MOTION_BLOCKING, i & 15, j & 15);

        if (k < worldserver.getMinBuildHeight()) {
            return null;
        } else {
            int l = chunk.getHeight(HeightMap.Type.WORLD_SURFACE, i & 15, j & 15);

            if (l <= k && l > chunk.getHeight(HeightMap.Type.OCEAN_FLOOR, i & 15, j & 15)) {
                return null;
            } else {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int i1 = k + 1; i1 >= worldserver.getMinBuildHeight(); --i1) {
                    blockposition_mutableblockposition.set(i, i1, j);
                    IBlockData iblockdata = worldserver.getBlockState(blockposition_mutableblockposition);

                    if (!iblockdata.getFluidState().isEmpty()) {
                        break;
                    }

                    if (Block.isFaceFull(iblockdata.getCollisionShape(worldserver, blockposition_mutableblockposition), EnumDirection.UP)) {
                        return blockposition_mutableblockposition.above().immutable();
                    }
                }

                return null;
            }
        }
    }

    @Nullable
    public static BlockPosition getSpawnPosInChunk(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair) {
        if (SharedConstants.debugVoidTerrain(chunkcoordintpair)) {
            return null;
        } else {
            for (int i = chunkcoordintpair.getMinBlockX(); i <= chunkcoordintpair.getMaxBlockX(); ++i) {
                for (int j = chunkcoordintpair.getMinBlockZ(); j <= chunkcoordintpair.getMaxBlockZ(); ++j) {
                    BlockPosition blockposition = getOverworldRespawnPos(worldserver, i, j);

                    if (blockposition != null) {
                        return blockposition;
                    }
                }
            }

            return null;
        }
    }
}

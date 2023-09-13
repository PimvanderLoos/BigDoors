package net.minecraft.world.level;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;

public interface IWorldReader extends IBlockLightAccess, ICollisionAccess, BiomeManager.Provider {

    @Nullable
    IChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus, boolean flag);

    /** @deprecated */
    @Deprecated
    boolean hasChunk(int i, int j);

    int getHeight(HeightMap.Type heightmap_type, int i, int j);

    int getSkyDarken();

    BiomeManager getBiomeManager();

    default BiomeBase getBiome(BlockPosition blockposition) {
        return this.getBiomeManager().getBiome(blockposition);
    }

    default Stream<IBlockData> getBlockStatesIfLoaded(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.floor(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.floor(axisalignedbb.maxZ);

        return this.hasChunksAt(i, k, i1, j, l, j1) ? this.getBlockStates(axisalignedbb) : Stream.empty();
    }

    @Override
    default int getBlockTint(BlockPosition blockposition, ColorResolver colorresolver) {
        return colorresolver.getColor(this.getBiome(blockposition), (double) blockposition.getX(), (double) blockposition.getZ());
    }

    @Override
    default BiomeBase getNoiseBiome(int i, int j, int k) {
        IChunkAccess ichunkaccess = this.getChunk(QuartPos.toSection(i), QuartPos.toSection(k), ChunkStatus.BIOMES, false);

        return ichunkaccess != null ? ichunkaccess.getNoiseBiome(i, j, k) : this.getUncachedNoiseBiome(i, j, k);
    }

    BiomeBase getUncachedNoiseBiome(int i, int j, int k);

    boolean isClientSide();

    /** @deprecated */
    @Deprecated
    int getSeaLevel();

    DimensionManager dimensionType();

    @Override
    default int getMinBuildHeight() {
        return this.dimensionType().minY();
    }

    @Override
    default int getHeight() {
        return this.dimensionType().height();
    }

    default BlockPosition getHeightmapPos(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return new BlockPosition(blockposition.getX(), this.getHeight(heightmap_type, blockposition.getX(), blockposition.getZ()), blockposition.getZ());
    }

    default boolean isEmptyBlock(BlockPosition blockposition) {
        return this.getBlockState(blockposition).isAir();
    }

    default boolean canSeeSkyFromBelowWater(BlockPosition blockposition) {
        if (blockposition.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(blockposition);
        } else {
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.getSeaLevel(), blockposition.getZ());

            if (!this.canSeeSky(blockposition1)) {
                return false;
            } else {
                for (blockposition1 = blockposition1.below(); blockposition1.getY() > blockposition.getY(); blockposition1 = blockposition1.below()) {
                    IBlockData iblockdata = this.getBlockState(blockposition1);

                    if (iblockdata.getLightBlock(this, blockposition1) > 0 && !iblockdata.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    default float getBrightness(BlockPosition blockposition) {
        return this.dimensionType().brightness(this.getMaxLocalRawBrightness(blockposition));
    }

    default int getDirectSignal(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlockState(blockposition).getDirectSignal(this, blockposition, enumdirection);
    }

    default IChunkAccess getChunk(BlockPosition blockposition) {
        return this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
    }

    default IChunkAccess getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, true);
    }

    default IChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus) {
        return this.getChunk(i, j, chunkstatus, true);
    }

    @Nullable
    @Override
    default IBlockAccess getChunkForCollisions(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    default boolean isWaterAt(BlockPosition blockposition) {
        return this.getFluidState(blockposition).is((Tag) TagsFluid.WATER);
    }

    default boolean containsAnyLiquid(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    IBlockData iblockdata = this.getBlockState(blockposition_mutableblockposition.set(k1, l1, i2));

                    if (!iblockdata.getFluidState().isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    default int getMaxLocalRawBrightness(BlockPosition blockposition) {
        return this.getMaxLocalRawBrightness(blockposition, this.getSkyDarken());
    }

    default int getMaxLocalRawBrightness(BlockPosition blockposition, int i) {
        return blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000 ? this.getRawBrightness(blockposition, i) : 15;
    }

    /** @deprecated */
    @Deprecated
    default boolean hasChunkAt(int i, int j) {
        return this.hasChunk(SectionPosition.blockToSectionCoord(i), SectionPosition.blockToSectionCoord(j));
    }

    /** @deprecated */
    @Deprecated
    default boolean hasChunkAt(BlockPosition blockposition) {
        return this.hasChunkAt(blockposition.getX(), blockposition.getZ());
    }

    /** @deprecated */
    @Deprecated
    default boolean hasChunksAt(BlockPosition blockposition, BlockPosition blockposition1) {
        return this.hasChunksAt(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
    }

    /** @deprecated */
    @Deprecated
    default boolean hasChunksAt(int i, int j, int k, int l, int i1, int j1) {
        return i1 >= this.getMinBuildHeight() && j < this.getMaxBuildHeight() ? this.hasChunksAt(i, k, l, j1) : false;
    }

    /** @deprecated */
    @Deprecated
    default boolean hasChunksAt(int i, int j, int k, int l) {
        int i1 = SectionPosition.blockToSectionCoord(i);
        int j1 = SectionPosition.blockToSectionCoord(k);
        int k1 = SectionPosition.blockToSectionCoord(j);
        int l1 = SectionPosition.blockToSectionCoord(l);

        for (int i2 = i1; i2 <= j1; ++i2) {
            for (int j2 = k1; j2 <= l1; ++j2) {
                if (!this.hasChunk(i2, j2)) {
                    return false;
                }
            }
        }

        return true;
    }
}

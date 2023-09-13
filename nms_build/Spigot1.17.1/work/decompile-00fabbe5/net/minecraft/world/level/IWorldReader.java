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
    IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag);

    @Deprecated
    boolean isChunkLoaded(int i, int j);

    int a(HeightMap.Type heightmap_type, int i, int j);

    int n_();

    BiomeManager r_();

    default BiomeBase getBiome(BlockPosition blockposition) {
        return this.r_().a(blockposition);
    }

    default Stream<IBlockData> c(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.floor(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.floor(axisalignedbb.maxZ);

        return this.isAreaLoaded(i, k, i1, j, l, j1) ? this.a(axisalignedbb) : Stream.empty();
    }

    @Override
    default int a(BlockPosition blockposition, ColorResolver colorresolver) {
        return colorresolver.getColor(this.getBiome(blockposition), (double) blockposition.getX(), (double) blockposition.getZ());
    }

    @Override
    default BiomeBase getBiome(int i, int j, int k) {
        IChunkAccess ichunkaccess = this.getChunkAt(QuartPos.d(i), QuartPos.d(k), ChunkStatus.BIOMES, false);

        return ichunkaccess != null && ichunkaccess.getBiomeIndex() != null ? ichunkaccess.getBiomeIndex().getBiome(i, j, k) : this.a(i, j, k);
    }

    BiomeBase a(int i, int j, int k);

    boolean isClientSide();

    @Deprecated
    int getSeaLevel();

    DimensionManager getDimensionManager();

    @Override
    default int getMinBuildHeight() {
        return this.getDimensionManager().getMinY();
    }

    @Override
    default int getHeight() {
        return this.getDimensionManager().getHeight();
    }

    default BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return new BlockPosition(blockposition.getX(), this.a(heightmap_type, blockposition.getX(), blockposition.getZ()), blockposition.getZ());
    }

    default boolean isEmpty(BlockPosition blockposition) {
        return this.getType(blockposition).isAir();
    }

    default boolean y(BlockPosition blockposition) {
        if (blockposition.getY() >= this.getSeaLevel()) {
            return this.g(blockposition);
        } else {
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.getSeaLevel(), blockposition.getZ());

            if (!this.g(blockposition1)) {
                return false;
            } else {
                for (blockposition1 = blockposition1.down(); blockposition1.getY() > blockposition.getY(); blockposition1 = blockposition1.down()) {
                    IBlockData iblockdata = this.getType(blockposition1);

                    if (iblockdata.b((IBlockAccess) this, blockposition1) > 0 && !iblockdata.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    @Deprecated
    default float z(BlockPosition blockposition) {
        return this.getDimensionManager().a(this.getLightLevel(blockposition));
    }

    default int c(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getType(blockposition).c(this, blockposition, enumdirection);
    }

    default IChunkAccess A(BlockPosition blockposition) {
        return this.getChunkAt(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()));
    }

    default IChunkAccess getChunkAt(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.FULL, true);
    }

    default IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus) {
        return this.getChunkAt(i, j, chunkstatus, true);
    }

    @Nullable
    @Override
    default IBlockAccess c(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.EMPTY, false);
    }

    default boolean B(BlockPosition blockposition) {
        return this.getFluid(blockposition).a((Tag) TagsFluid.WATER);
    }

    default boolean containsLiquid(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.e(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.e(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.e(axisalignedbb.maxZ);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    IBlockData iblockdata = this.getType(blockposition_mutableblockposition.d(k1, l1, i2));

                    if (!iblockdata.getFluid().isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    default int getLightLevel(BlockPosition blockposition) {
        return this.c(blockposition, this.n_());
    }

    default int c(BlockPosition blockposition, int i) {
        return blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000 ? this.getLightLevel(blockposition, i) : 15;
    }

    @Deprecated
    default boolean e(int i, int j) {
        return this.isChunkLoaded(SectionPosition.a(i), SectionPosition.a(j));
    }

    @Deprecated
    default boolean isLoaded(BlockPosition blockposition) {
        return this.e(blockposition.getX(), blockposition.getZ());
    }

    @Deprecated
    default boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
        return this.isAreaLoaded(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
    }

    @Deprecated
    default boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1) {
        return i1 >= this.getMinBuildHeight() && j < this.getMaxBuildHeight() ? this.b(i, k, l, j1) : false;
    }

    @Deprecated
    default boolean b(int i, int j, int k, int l) {
        int i1 = SectionPosition.a(i);
        int j1 = SectionPosition.a(k);
        int k1 = SectionPosition.a(j);
        int l1 = SectionPosition.a(l);

        for (int i2 = i1; i2 <= j1; ++i2) {
            for (int j2 = k1; j2 <= l1; ++j2) {
                if (!this.isChunkLoaded(i2, j2)) {
                    return false;
                }
            }
        }

        return true;
    }
}

package net.minecraft.world.level;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkEmpty;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChunkCache implements IBlockAccess, ICollisionAccess {

    protected final int centerX;
    protected final int centerZ;
    protected final IChunkAccess[][] chunks;
    protected boolean allEmpty;
    protected final World level;
    private final Supplier<Holder<BiomeBase>> plains;

    public ChunkCache(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        this.level = world;
        this.plains = Suppliers.memoize(() -> {
            return world.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
        });
        this.centerX = SectionPosition.blockToSectionCoord(blockposition.getX());
        this.centerZ = SectionPosition.blockToSectionCoord(blockposition.getZ());
        int i = SectionPosition.blockToSectionCoord(blockposition1.getX());
        int j = SectionPosition.blockToSectionCoord(blockposition1.getZ());

        this.chunks = new IChunkAccess[i - this.centerX + 1][j - this.centerZ + 1];
        IChunkProvider ichunkprovider = world.getChunkSource();

        this.allEmpty = true;

        int k;
        int l;

        for (k = this.centerX; k <= i; ++k) {
            for (l = this.centerZ; l <= j; ++l) {
                this.chunks[k - this.centerX][l - this.centerZ] = ichunkprovider.getChunkNow(k, l);
            }
        }

        for (k = SectionPosition.blockToSectionCoord(blockposition.getX()); k <= SectionPosition.blockToSectionCoord(blockposition1.getX()); ++k) {
            for (l = SectionPosition.blockToSectionCoord(blockposition.getZ()); l <= SectionPosition.blockToSectionCoord(blockposition1.getZ()); ++l) {
                IChunkAccess ichunkaccess = this.chunks[k - this.centerX][l - this.centerZ];

                if (ichunkaccess != null && !ichunkaccess.isYSpaceEmpty(blockposition.getY(), blockposition1.getY())) {
                    this.allEmpty = false;
                    return;
                }
            }
        }

    }

    private IChunkAccess getChunk(BlockPosition blockposition) {
        return this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
    }

    private IChunkAccess getChunk(int i, int j) {
        int k = i - this.centerX;
        int l = j - this.centerZ;

        if (k >= 0 && k < this.chunks.length && l >= 0 && l < this.chunks[k].length) {
            IChunkAccess ichunkaccess = this.chunks[k][l];

            return (IChunkAccess) (ichunkaccess != null ? ichunkaccess : new ChunkEmpty(this.level, new ChunkCoordIntPair(i, j), (Holder) this.plains.get()));
        } else {
            return new ChunkEmpty(this.level, new ChunkCoordIntPair(i, j), (Holder) this.plains.get());
        }
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public IBlockAccess getChunkForCollisions(int i, int j) {
        return this.getChunk(i, j);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return List.of();
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.getChunk(blockposition);

        return ichunkaccess.getBlockEntity(blockposition);
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            IChunkAccess ichunkaccess = this.getChunk(blockposition);

            return ichunkaccess.getBlockState(blockposition);
        }
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return FluidTypes.EMPTY.defaultFluidState();
        } else {
            IChunkAccess ichunkaccess = this.getChunk(blockposition);

            return ichunkaccess.getFluidState(blockposition);
        }
    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }

    public GameProfilerFiller getProfiler() {
        return this.level.getProfiler();
    }
}

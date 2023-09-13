package net.minecraft.world.level;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
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

    public ChunkCache(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        this.level = world;
        this.centerX = SectionPosition.a(blockposition.getX());
        this.centerZ = SectionPosition.a(blockposition.getZ());
        int i = SectionPosition.a(blockposition1.getX());
        int j = SectionPosition.a(blockposition1.getZ());

        this.chunks = new IChunkAccess[i - this.centerX + 1][j - this.centerZ + 1];
        IChunkProvider ichunkprovider = world.getChunkProvider();

        this.allEmpty = true;

        int k;
        int l;

        for (k = this.centerX; k <= i; ++k) {
            for (l = this.centerZ; l <= j; ++l) {
                this.chunks[k - this.centerX][l - this.centerZ] = ichunkprovider.a(k, l);
            }
        }

        for (k = SectionPosition.a(blockposition.getX()); k <= SectionPosition.a(blockposition1.getX()); ++k) {
            for (l = SectionPosition.a(blockposition.getZ()); l <= SectionPosition.a(blockposition1.getZ()); ++l) {
                IChunkAccess ichunkaccess = this.chunks[k - this.centerX][l - this.centerZ];

                if (ichunkaccess != null && !ichunkaccess.a(blockposition.getY(), blockposition1.getY())) {
                    this.allEmpty = false;
                    return;
                }
            }
        }

    }

    private IChunkAccess d(BlockPosition blockposition) {
        return this.a(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()));
    }

    private IChunkAccess a(int i, int j) {
        int k = i - this.centerX;
        int l = j - this.centerZ;

        if (k >= 0 && k < this.chunks.length && l >= 0 && l < this.chunks[k].length) {
            IChunkAccess ichunkaccess = this.chunks[k][l];

            return (IChunkAccess) (ichunkaccess != null ? ichunkaccess : new ChunkEmpty(this.level, new ChunkCoordIntPair(i, j)));
        } else {
            return new ChunkEmpty(this.level, new ChunkCoordIntPair(i, j));
        }
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public IBlockAccess c(int i, int j) {
        return this.a(i, j);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.d(blockposition);

        return ichunkaccess.getTileEntity(blockposition);
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        if (this.isOutsideWorld(blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            IChunkAccess ichunkaccess = this.d(blockposition);

            return ichunkaccess.getType(blockposition);
        }
    }

    @Override
    public Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return Stream.empty();
    }

    @Override
    public Stream<VoxelShape> d(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return this.b(entity, axisalignedbb);
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        if (this.isOutsideWorld(blockposition)) {
            return FluidTypes.EMPTY.h();
        } else {
            IChunkAccess ichunkaccess = this.d(blockposition);

            return ichunkaccess.getFluid(blockposition);
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

    public GameProfilerFiller a() {
        return this.level.getMethodProfiler();
    }
}

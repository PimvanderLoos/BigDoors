package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;

public class ChunkEmpty extends Chunk {

    private final Holder<BiomeBase> biome;

    public ChunkEmpty(World world, ChunkCoordIntPair chunkcoordintpair, Holder<BiomeBase> holder) {
        super(world, chunkcoordintpair);
        this.biome = holder;
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        return Blocks.VOID_AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public IBlockData setBlockState(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return null;
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        return FluidTypes.EMPTY.defaultFluidState();
    }

    @Override
    public int getLightEmission(BlockPosition blockposition) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        return null;
    }

    @Override
    public void addAndRegisterBlockEntity(TileEntity tileentity) {}

    @Override
    public void setBlockEntity(TileEntity tileentity) {}

    @Override
    public void removeBlockEntity(BlockPosition blockposition) {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isYSpaceEmpty(int i, int j) {
        return true;
    }

    @Override
    public PlayerChunk.State getFullStatus() {
        return PlayerChunk.State.BORDER;
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k) {
        return this.biome;
    }
}

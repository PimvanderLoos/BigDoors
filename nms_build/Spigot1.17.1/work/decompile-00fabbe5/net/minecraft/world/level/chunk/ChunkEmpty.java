package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
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

    public ChunkEmpty(World world, ChunkCoordIntPair chunkcoordintpair) {
        super(world, chunkcoordintpair, (BiomeStorage) (new ChunkEmpty.a(world)));
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        return Blocks.VOID_AIR.getBlockData();
    }

    @Nullable
    @Override
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return null;
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return FluidTypes.EMPTY.h();
    }

    @Override
    public int h(BlockPosition blockposition) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        return null;
    }

    @Override
    public void b(TileEntity tileentity) {}

    @Override
    public void setTileEntity(TileEntity tileentity) {}

    @Override
    public void removeTileEntity(BlockPosition blockposition) {}

    @Override
    public void markDirty() {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean a(int i, int j) {
        return true;
    }

    @Override
    public PlayerChunk.State getState() {
        return PlayerChunk.State.BORDER;
    }

    private static class a extends BiomeStorage {

        private static final BiomeBase[] EMPTY_BIOMES = new BiomeBase[0];

        public a(World world) {
            super(world.t().d(IRegistry.BIOME_REGISTRY), world, ChunkEmpty.a.EMPTY_BIOMES);
        }

        @Override
        public int[] a() {
            throw new UnsupportedOperationException("Can not write biomes of an empty chunk");
        }

        @Override
        public BiomeBase getBiome(int i, int j, int k) {
            return BiomeRegistry.PLAINS;
        }
    }
}

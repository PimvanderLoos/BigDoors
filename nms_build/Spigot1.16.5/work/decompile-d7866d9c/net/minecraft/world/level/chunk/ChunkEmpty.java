package net.minecraft.world.level.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.AxisAlignedBB;

public class ChunkEmpty extends Chunk {

    private static final BiomeBase[] b = (BiomeBase[]) SystemUtils.a((Object) (new BiomeBase[BiomeStorage.a]), (abiomebase) -> {
        Arrays.fill(abiomebase, BiomeRegistry.a);
    });

    public ChunkEmpty(World world, ChunkCoordIntPair chunkcoordintpair) {
        super(world, chunkcoordintpair, new BiomeStorage(world.r().b(IRegistry.ay), ChunkEmpty.b));
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

    @Nullable
    @Override
    public LightEngine e() {
        return null;
    }

    @Override
    public int g(BlockPosition blockposition) {
        return 0;
    }

    @Override
    public void a(Entity entity) {}

    @Override
    public void b(Entity entity) {}

    @Override
    public void a(Entity entity, int i) {}

    @Nullable
    @Override
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        return null;
    }

    @Override
    public void a(TileEntity tileentity) {}

    @Override
    public void setTileEntity(BlockPosition blockposition, TileEntity tileentity) {}

    @Override
    public void removeTileEntity(BlockPosition blockposition) {}

    @Override
    public void markDirty() {}

    @Override
    public void a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {}

    @Override
    public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, Predicate<? super T> predicate) {}

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
}

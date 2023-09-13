package net.minecraft.world.level.chunk;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.lighting.LightEngine;

public abstract class IChunkProvider implements ILightAccess, AutoCloseable {

    public IChunkProvider() {}

    @Nullable
    public Chunk getChunkAt(int i, int j, boolean flag) {
        return (Chunk) this.getChunkAt(i, j, ChunkStatus.FULL, flag);
    }

    @Nullable
    public Chunk a(int i, int j) {
        return this.getChunkAt(i, j, false);
    }

    @Nullable
    @Override
    public IBlockAccess c(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.EMPTY, false);
    }

    public boolean b(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag);

    public abstract String getName();

    public void close() throws IOException {}

    public abstract LightEngine getLightEngine();

    public void a(boolean flag, boolean flag1) {}

    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {}

    public boolean a(Entity entity) {
        return true;
    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair) {
        return true;
    }

    public boolean a(BlockPosition blockposition) {
        return true;
    }
}

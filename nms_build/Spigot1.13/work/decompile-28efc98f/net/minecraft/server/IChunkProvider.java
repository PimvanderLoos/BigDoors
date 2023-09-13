package net.minecraft.server;

import javax.annotation.Nullable;

public interface IChunkProvider extends AutoCloseable {

    @Nullable
    Chunk getLoadedChunkAt(int i, int j);

    Chunk getChunkAt(int i, int j);

    IChunkAccess d(int i, int j);

    boolean unloadChunks();

    String getName();

    ChunkGenerator<?> getChunkGenerator();

    boolean f(int i, int j);

    default void close() {}
}

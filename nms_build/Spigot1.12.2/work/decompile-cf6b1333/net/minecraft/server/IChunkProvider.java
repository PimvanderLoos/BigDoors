package net.minecraft.server;

import javax.annotation.Nullable;

public interface IChunkProvider {

    @Nullable
    Chunk getLoadedChunkAt(int i, int j);

    Chunk getChunkAt(int i, int j);

    boolean unloadChunks();

    String getName();

    boolean e(int i, int j);
}

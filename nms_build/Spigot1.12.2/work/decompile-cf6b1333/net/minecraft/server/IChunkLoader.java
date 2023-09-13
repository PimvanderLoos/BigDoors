package net.minecraft.server;

import java.io.IOException;
import javax.annotation.Nullable;

public interface IChunkLoader {

    @Nullable
    Chunk a(World world, int i, int j) throws IOException;

    void saveChunk(World world, Chunk chunk) throws IOException, ExceptionWorldConflict;

    void b(World world, Chunk chunk) throws IOException;

    void b();

    void c();

    boolean chunkExists(int i, int j);
}

package net.minecraft.world.level.entity;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.ChunkCoordIntPair;

public interface EntityPersistentStorage<T> extends AutoCloseable {

    CompletableFuture<ChunkEntities<T>> loadEntities(ChunkCoordIntPair chunkcoordintpair);

    void storeEntities(ChunkEntities<T> chunkentities);

    void flush(boolean flag);

    default void close() throws IOException {}
}

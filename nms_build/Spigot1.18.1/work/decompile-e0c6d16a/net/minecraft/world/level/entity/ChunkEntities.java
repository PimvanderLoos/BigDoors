package net.minecraft.world.level.entity;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.level.ChunkCoordIntPair;

public class ChunkEntities<T> {

    private final ChunkCoordIntPair pos;
    private final List<T> entities;

    public ChunkEntities(ChunkCoordIntPair chunkcoordintpair, List<T> list) {
        this.pos = chunkcoordintpair;
        this.entities = list;
    }

    public ChunkCoordIntPair getPos() {
        return this.pos;
    }

    public Stream<T> getEntities() {
        return this.entities.stream();
    }

    public boolean isEmpty() {
        return this.entities.isEmpty();
    }
}

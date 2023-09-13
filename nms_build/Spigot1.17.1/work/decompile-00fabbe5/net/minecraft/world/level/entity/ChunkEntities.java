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

    public ChunkCoordIntPair a() {
        return this.pos;
    }

    public Stream<T> b() {
        return this.entities.stream();
    }

    public boolean c() {
        return this.entities.isEmpty();
    }
}

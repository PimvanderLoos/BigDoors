package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class BulkSectionAccess implements AutoCloseable {

    private final GeneratorAccess level;
    private final Long2ObjectMap<ChunkSection> acquiredSections = new Long2ObjectOpenHashMap();
    @Nullable
    private ChunkSection lastSection;
    private long lastSectionKey;

    public BulkSectionAccess(GeneratorAccess generatoraccess) {
        this.level = generatoraccess;
    }

    @Nullable
    public ChunkSection a(BlockPosition blockposition) {
        int i = this.level.getSectionIndex(blockposition.getY());

        if (i >= 0 && i < this.level.getSectionsCount()) {
            long j = SectionPosition.c(blockposition);

            if (this.lastSection == null || this.lastSectionKey != j) {
                this.lastSection = (ChunkSection) this.acquiredSections.computeIfAbsent(j, (k) -> {
                    IChunkAccess ichunkaccess = this.level.getChunkAt(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()));
                    ChunkSection chunksection = ichunkaccess.b(i);

                    chunksection.a();
                    return chunksection;
                });
                this.lastSectionKey = j;
            }

            return this.lastSection;
        } else {
            return Chunk.EMPTY_SECTION;
        }
    }

    public IBlockData b(BlockPosition blockposition) {
        ChunkSection chunksection = this.a(blockposition);

        if (chunksection == Chunk.EMPTY_SECTION) {
            return Blocks.AIR.getBlockData();
        } else {
            int i = SectionPosition.b(blockposition.getX());
            int j = SectionPosition.b(blockposition.getY());
            int k = SectionPosition.b(blockposition.getZ());

            return chunksection.getType(i, j, k);
        }
    }

    public void close() {
        ObjectIterator objectiterator = this.acquiredSections.values().iterator();

        while (objectiterator.hasNext()) {
            ChunkSection chunksection = (ChunkSection) objectiterator.next();

            chunksection.b();
        }

    }
}

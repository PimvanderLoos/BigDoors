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
    public ChunkSection getSection(BlockPosition blockposition) {
        int i = this.level.getSectionIndex(blockposition.getY());

        if (i >= 0 && i < this.level.getSectionsCount()) {
            long j = SectionPosition.asLong(blockposition);

            if (this.lastSection == null || this.lastSectionKey != j) {
                this.lastSection = (ChunkSection) this.acquiredSections.computeIfAbsent(j, (k) -> {
                    IChunkAccess ichunkaccess = this.level.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
                    ChunkSection chunksection = ichunkaccess.getSection(i);

                    chunksection.acquire();
                    return chunksection;
                });
                this.lastSectionKey = j;
            }

            return this.lastSection;
        } else {
            return null;
        }
    }

    public IBlockData getBlockState(BlockPosition blockposition) {
        ChunkSection chunksection = this.getSection(blockposition);

        if (chunksection == null) {
            return Blocks.AIR.defaultBlockState();
        } else {
            int i = SectionPosition.sectionRelative(blockposition.getX());
            int j = SectionPosition.sectionRelative(blockposition.getY());
            int k = SectionPosition.sectionRelative(blockposition.getZ());

            return chunksection.getBlockState(i, j, k);
        }
    }

    public void close() {
        ObjectIterator objectiterator = this.acquiredSections.values().iterator();

        while (objectiterator.hasNext()) {
            ChunkSection chunksection = (ChunkSection) objectiterator.next();

            chunksection.release();
        }

    }
}

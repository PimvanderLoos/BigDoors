package net.minecraft.server.level;

import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;

public record BlockPosition2D(int x, int z) {

    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 4294967295L;

    public ChunkCoordIntPair toChunkPos() {
        return new ChunkCoordIntPair(SectionPosition.blockToSectionCoord(this.x), SectionPosition.blockToSectionCoord(this.z));
    }

    public long toLong() {
        return asLong(this.x, this.z);
    }

    public static long asLong(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public static int getX(long i) {
        return (int) (i & 4294967295L);
    }

    public static int getZ(long i) {
        return (int) (i >>> 32 & 4294967295L);
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public int hashCode() {
        return ChunkCoordIntPair.hash(this.x, this.z);
    }
}

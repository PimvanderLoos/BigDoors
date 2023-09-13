package net.minecraft.server.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;

public class BlockPosition2D {

    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 4294967295L;
    private static final int HASH_A = 1664525;
    private static final int HASH_C = 1013904223;
    private static final int HASH_Z_XOR = -559038737;
    public final int x;
    public final int z;

    public BlockPosition2D(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public BlockPosition2D(BlockPosition blockposition) {
        this.x = blockposition.getX();
        this.z = blockposition.getZ();
    }

    public ChunkCoordIntPair a() {
        return new ChunkCoordIntPair(SectionPosition.a(this.x), SectionPosition.a(this.z));
    }

    public long b() {
        return a(this.x, this.z);
    }

    public static long a(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public int hashCode() {
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ -559038737) + 1013904223;

        return i ^ j;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof BlockPosition2D)) {
            return false;
        } else {
            BlockPosition2D blockposition2d = (BlockPosition2D) object;

            return this.x == blockposition2d.x && this.z == blockposition2d.z;
        }
    }
}

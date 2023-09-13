package net.minecraft.world.level;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;

public class ChunkCoordIntPair {

    private static final int SAFETY_MARGIN = 1056;
    public static final long INVALID_CHUNK_POS = asLong(1875066, 1875066);
    public static final ChunkCoordIntPair ZERO = new ChunkCoordIntPair(0, 0);
    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 4294967295L;
    private static final int REGION_BITS = 5;
    public static final int REGION_SIZE = 32;
    private static final int REGION_MASK = 31;
    public static final int REGION_MAX_INDEX = 31;
    public final int x;
    public final int z;
    private static final int HASH_A = 1664525;
    private static final int HASH_C = 1013904223;
    private static final int HASH_Z_XOR = -559038737;

    public ChunkCoordIntPair(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public ChunkCoordIntPair(BlockPosition blockposition) {
        this.x = SectionPosition.blockToSectionCoord(blockposition.getX());
        this.z = SectionPosition.blockToSectionCoord(blockposition.getZ());
    }

    public ChunkCoordIntPair(long i) {
        this.x = (int) i;
        this.z = (int) (i >> 32);
    }

    public static ChunkCoordIntPair minFromRegion(int i, int j) {
        return new ChunkCoordIntPair(i << 5, j << 5);
    }

    public static ChunkCoordIntPair maxFromRegion(int i, int j) {
        return new ChunkCoordIntPair((i << 5) + 31, (j << 5) + 31);
    }

    public long toLong() {
        return asLong(this.x, this.z);
    }

    public static long asLong(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public static long asLong(BlockPosition blockposition) {
        return asLong(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
    }

    public static int getX(long i) {
        return (int) (i & 4294967295L);
    }

    public static int getZ(long i) {
        return (int) (i >>> 32 & 4294967295L);
    }

    public int hashCode() {
        return hash(this.x, this.z);
    }

    public static int hash(int i, int j) {
        int k = 1664525 * i + 1013904223;
        int l = 1664525 * (j ^ -559038737) + 1013904223;

        return k ^ l;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChunkCoordIntPair)) {
            return false;
        } else {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) object;

            return this.x == chunkcoordintpair.x && this.z == chunkcoordintpair.z;
        }
    }

    public int getMiddleBlockX() {
        return this.getBlockX(8);
    }

    public int getMiddleBlockZ() {
        return this.getBlockZ(8);
    }

    public int getMinBlockX() {
        return SectionPosition.sectionToBlockCoord(this.x);
    }

    public int getMinBlockZ() {
        return SectionPosition.sectionToBlockCoord(this.z);
    }

    public int getMaxBlockX() {
        return this.getBlockX(15);
    }

    public int getMaxBlockZ() {
        return this.getBlockZ(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionLocalX() {
        return this.x & 31;
    }

    public int getRegionLocalZ() {
        return this.z & 31;
    }

    public BlockPosition getBlockAt(int i, int j, int k) {
        return new BlockPosition(this.getBlockX(i), j, this.getBlockZ(k));
    }

    public int getBlockX(int i) {
        return SectionPosition.sectionToBlockCoord(this.x, i);
    }

    public int getBlockZ(int i) {
        return SectionPosition.sectionToBlockCoord(this.z, i);
    }

    public BlockPosition getMiddleBlockPosition(int i) {
        return new BlockPosition(this.getMiddleBlockX(), i, this.getMiddleBlockZ());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPosition getWorldPosition() {
        return new BlockPosition(this.getMinBlockX(), 0, this.getMinBlockZ());
    }

    public int getChessboardDistance(ChunkCoordIntPair chunkcoordintpair) {
        return Math.max(Math.abs(this.x - chunkcoordintpair.x), Math.abs(this.z - chunkcoordintpair.z));
    }

    public static Stream<ChunkCoordIntPair> rangeClosed(ChunkCoordIntPair chunkcoordintpair, int i) {
        return rangeClosed(new ChunkCoordIntPair(chunkcoordintpair.x - i, chunkcoordintpair.z - i), new ChunkCoordIntPair(chunkcoordintpair.x + i, chunkcoordintpair.z + i));
    }

    public static Stream<ChunkCoordIntPair> rangeClosed(final ChunkCoordIntPair chunkcoordintpair, final ChunkCoordIntPair chunkcoordintpair1) {
        int i = Math.abs(chunkcoordintpair.x - chunkcoordintpair1.x) + 1;
        int j = Math.abs(chunkcoordintpair.z - chunkcoordintpair1.z) + 1;
        final int k = chunkcoordintpair.x < chunkcoordintpair1.x ? 1 : -1;
        final int l = chunkcoordintpair.z < chunkcoordintpair1.z ? 1 : -1;

        return StreamSupport.stream(new AbstractSpliterator<ChunkCoordIntPair>((long) (i * j), 64) {
            @Nullable
            private ChunkCoordIntPair pos;

            public boolean tryAdvance(Consumer<? super ChunkCoordIntPair> consumer) {
                if (this.pos == null) {
                    this.pos = chunkcoordintpair;
                } else {
                    int i1 = this.pos.x;
                    int j1 = this.pos.z;

                    if (i1 == chunkcoordintpair1.x) {
                        if (j1 == chunkcoordintpair1.z) {
                            return false;
                        }

                        this.pos = new ChunkCoordIntPair(chunkcoordintpair.x, j1 + l);
                    } else {
                        this.pos = new ChunkCoordIntPair(i1 + k, j1);
                    }
                }

                consumer.accept(this.pos);
                return true;
            }
        }, false);
    }
}

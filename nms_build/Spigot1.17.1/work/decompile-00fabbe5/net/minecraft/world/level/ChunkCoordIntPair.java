package net.minecraft.world.level;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;

public class ChunkCoordIntPair {

    public static final long INVALID_CHUNK_POS = pair(1875016, 1875016);
    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 4294967295L;
    private static final int REGION_BITS = 5;
    private static final int REGION_MASK = 31;
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
        this.x = SectionPosition.a(blockposition.getX());
        this.z = SectionPosition.a(blockposition.getZ());
    }

    public ChunkCoordIntPair(long i) {
        this.x = (int) i;
        this.z = (int) (i >> 32);
    }

    public long pair() {
        return pair(this.x, this.z);
    }

    public static long pair(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public static long a(BlockPosition blockposition) {
        return pair(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()));
    }

    public static int getX(long i) {
        return (int) (i & 4294967295L);
    }

    public static int getZ(long i) {
        return (int) (i >>> 32 & 4294967295L);
    }

    public int hashCode() {
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ -559038737) + 1013904223;

        return i ^ j;
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

    public int b() {
        return this.a(8);
    }

    public int c() {
        return this.b(8);
    }

    public int d() {
        return SectionPosition.c(this.x);
    }

    public int e() {
        return SectionPosition.c(this.z);
    }

    public int f() {
        return this.a(15);
    }

    public int g() {
        return this.b(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int j() {
        return this.x & 31;
    }

    public int k() {
        return this.z & 31;
    }

    public BlockPosition a(int i, int j, int k) {
        return new BlockPosition(this.a(i), j, this.b(k));
    }

    public int a(int i) {
        return SectionPosition.a(this.x, i);
    }

    public int b(int i) {
        return SectionPosition.a(this.z, i);
    }

    public BlockPosition c(int i) {
        return new BlockPosition(this.b(), i, this.c());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPosition l() {
        return new BlockPosition(this.d(), 0, this.e());
    }

    public int a(ChunkCoordIntPair chunkcoordintpair) {
        return Math.max(Math.abs(this.x - chunkcoordintpair.x), Math.abs(this.z - chunkcoordintpair.z));
    }

    public static Stream<ChunkCoordIntPair> a(ChunkCoordIntPair chunkcoordintpair, int i) {
        return a(new ChunkCoordIntPair(chunkcoordintpair.x - i, chunkcoordintpair.z - i), new ChunkCoordIntPair(chunkcoordintpair.x + i, chunkcoordintpair.z + i));
    }

    public static Stream<ChunkCoordIntPair> a(final ChunkCoordIntPair chunkcoordintpair, final ChunkCoordIntPair chunkcoordintpair1) {
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

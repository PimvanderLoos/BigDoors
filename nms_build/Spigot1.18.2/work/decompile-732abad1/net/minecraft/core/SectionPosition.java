package net.minecraft.core;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.IChunkAccess;

public class SectionPosition extends BaseBlockPosition {

    public static final int SECTION_BITS = 4;
    public static final int SECTION_SIZE = 16;
    public static final int SECTION_MASK = 15;
    public static final int SECTION_HALF_SIZE = 8;
    public static final int SECTION_MAX_INDEX = 15;
    private static final int PACKED_X_LENGTH = 22;
    private static final int PACKED_Y_LENGTH = 20;
    private static final int PACKED_Z_LENGTH = 22;
    private static final long PACKED_X_MASK = 4194303L;
    private static final long PACKED_Y_MASK = 1048575L;
    private static final long PACKED_Z_MASK = 4194303L;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET = 20;
    private static final int X_OFFSET = 42;
    private static final int RELATIVE_X_SHIFT = 8;
    private static final int RELATIVE_Y_SHIFT = 0;
    private static final int RELATIVE_Z_SHIFT = 4;

    SectionPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public static SectionPosition of(int i, int j, int k) {
        return new SectionPosition(i, j, k);
    }

    public static SectionPosition of(BlockPosition blockposition) {
        return new SectionPosition(blockToSectionCoord(blockposition.getX()), blockToSectionCoord(blockposition.getY()), blockToSectionCoord(blockposition.getZ()));
    }

    public static SectionPosition of(ChunkCoordIntPair chunkcoordintpair, int i) {
        return new SectionPosition(chunkcoordintpair.x, i, chunkcoordintpair.z);
    }

    public static SectionPosition of(Entity entity) {
        return new SectionPosition(blockToSectionCoord(entity.getBlockX()), blockToSectionCoord(entity.getBlockY()), blockToSectionCoord(entity.getBlockZ()));
    }

    public static SectionPosition of(long i) {
        return new SectionPosition(x(i), y(i), z(i));
    }

    public static SectionPosition bottomOf(IChunkAccess ichunkaccess) {
        return of(ichunkaccess.getPos(), ichunkaccess.getMinSection());
    }

    public static long offset(long i, EnumDirection enumdirection) {
        return offset(i, enumdirection.getStepX(), enumdirection.getStepY(), enumdirection.getStepZ());
    }

    public static long offset(long i, int j, int k, int l) {
        return asLong(x(i) + j, y(i) + k, z(i) + l);
    }

    public static int posToSectionCoord(double d0) {
        return blockToSectionCoord(MathHelper.floor(d0));
    }

    public static int blockToSectionCoord(int i) {
        return i >> 4;
    }

    public static int sectionRelative(int i) {
        return i & 15;
    }

    public static short sectionRelativePos(BlockPosition blockposition) {
        int i = sectionRelative(blockposition.getX());
        int j = sectionRelative(blockposition.getY());
        int k = sectionRelative(blockposition.getZ());

        return (short) (i << 8 | k << 4 | j << 0);
    }

    public static int sectionRelativeX(short short0) {
        return short0 >>> 8 & 15;
    }

    public static int sectionRelativeY(short short0) {
        return short0 >>> 0 & 15;
    }

    public static int sectionRelativeZ(short short0) {
        return short0 >>> 4 & 15;
    }

    public int relativeToBlockX(short short0) {
        return this.minBlockX() + sectionRelativeX(short0);
    }

    public int relativeToBlockY(short short0) {
        return this.minBlockY() + sectionRelativeY(short0);
    }

    public int relativeToBlockZ(short short0) {
        return this.minBlockZ() + sectionRelativeZ(short0);
    }

    public BlockPosition relativeToBlockPos(short short0) {
        return new BlockPosition(this.relativeToBlockX(short0), this.relativeToBlockY(short0), this.relativeToBlockZ(short0));
    }

    public static int sectionToBlockCoord(int i) {
        return i << 4;
    }

    public static int sectionToBlockCoord(int i, int j) {
        return sectionToBlockCoord(i) + j;
    }

    public static int x(long i) {
        return (int) (i << 0 >> 42);
    }

    public static int y(long i) {
        return (int) (i << 44 >> 44);
    }

    public static int z(long i) {
        return (int) (i << 22 >> 42);
    }

    public int x() {
        return this.getX();
    }

    public int y() {
        return this.getY();
    }

    public int z() {
        return this.getZ();
    }

    public int minBlockX() {
        return sectionToBlockCoord(this.x());
    }

    public int minBlockY() {
        return sectionToBlockCoord(this.y());
    }

    public int minBlockZ() {
        return sectionToBlockCoord(this.z());
    }

    public int maxBlockX() {
        return sectionToBlockCoord(this.x(), 15);
    }

    public int maxBlockY() {
        return sectionToBlockCoord(this.y(), 15);
    }

    public int maxBlockZ() {
        return sectionToBlockCoord(this.z(), 15);
    }

    public static long blockToSection(long i) {
        return asLong(blockToSectionCoord(BlockPosition.getX(i)), blockToSectionCoord(BlockPosition.getY(i)), blockToSectionCoord(BlockPosition.getZ(i)));
    }

    public static long getZeroNode(long i) {
        return i & -1048576L;
    }

    public BlockPosition origin() {
        return new BlockPosition(sectionToBlockCoord(this.x()), sectionToBlockCoord(this.y()), sectionToBlockCoord(this.z()));
    }

    public BlockPosition center() {
        boolean flag = true;

        return this.origin().offset(8, 8, 8);
    }

    public ChunkCoordIntPair chunk() {
        return new ChunkCoordIntPair(this.x(), this.z());
    }

    public static long asLong(BlockPosition blockposition) {
        return asLong(blockToSectionCoord(blockposition.getX()), blockToSectionCoord(blockposition.getY()), blockToSectionCoord(blockposition.getZ()));
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;

        l |= ((long) i & 4194303L) << 42;
        l |= ((long) j & 1048575L) << 0;
        l |= ((long) k & 4194303L) << 20;
        return l;
    }

    public long asLong() {
        return asLong(this.x(), this.y(), this.z());
    }

    @Override
    public SectionPosition offset(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new SectionPosition(this.x() + i, this.y() + j, this.z() + k);
    }

    public Stream<BlockPosition> blocksInside() {
        return BlockPosition.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
    }

    public static Stream<SectionPosition> cube(SectionPosition sectionposition, int i) {
        int j = sectionposition.x();
        int k = sectionposition.y();
        int l = sectionposition.z();

        return betweenClosedStream(j - i, k - i, l - i, j + i, k + i, l + i);
    }

    public static Stream<SectionPosition> aroundChunk(ChunkCoordIntPair chunkcoordintpair, int i, int j, int k) {
        int l = chunkcoordintpair.x;
        int i1 = chunkcoordintpair.z;

        return betweenClosedStream(l - i, j, i1 - i, l + i, k - 1, i1 + i);
    }

    public static Stream<SectionPosition> betweenClosedStream(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        return StreamSupport.stream(new AbstractSpliterator<SectionPosition>((long) ((l - i + 1) * (i1 - j + 1) * (j1 - k + 1)), 64) {
            final CursorPosition cursor = new CursorPosition(i, j, k, l, i1, j1);

            public boolean tryAdvance(Consumer<? super SectionPosition> consumer) {
                if (this.cursor.advance()) {
                    consumer.accept(new SectionPosition(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
                    return true;
                } else {
                    return false;
                }
            }
        }, false);
    }

    public static void aroundAndAtBlockPos(BlockPosition blockposition, LongConsumer longconsumer) {
        aroundAndAtBlockPos(blockposition.getX(), blockposition.getY(), blockposition.getZ(), longconsumer);
    }

    public static void aroundAndAtBlockPos(long i, LongConsumer longconsumer) {
        aroundAndAtBlockPos(BlockPosition.getX(i), BlockPosition.getY(i), BlockPosition.getZ(i), longconsumer);
    }

    public static void aroundAndAtBlockPos(int i, int j, int k, LongConsumer longconsumer) {
        int l = blockToSectionCoord(i - 1);
        int i1 = blockToSectionCoord(i + 1);
        int j1 = blockToSectionCoord(j - 1);
        int k1 = blockToSectionCoord(j + 1);
        int l1 = blockToSectionCoord(k - 1);
        int i2 = blockToSectionCoord(k + 1);

        if (l == i1 && j1 == k1 && l1 == i2) {
            longconsumer.accept(asLong(l, j1, l1));
        } else {
            for (int j2 = l; j2 <= i1; ++j2) {
                for (int k2 = j1; k2 <= k1; ++k2) {
                    for (int l2 = l1; l2 <= i2; ++l2) {
                        longconsumer.accept(asLong(j2, k2, l2));
                    }
                }
            }
        }

    }
}

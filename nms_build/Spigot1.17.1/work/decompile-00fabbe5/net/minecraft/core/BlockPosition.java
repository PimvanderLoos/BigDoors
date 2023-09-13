package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.SystemUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPosition extends BaseBlockPosition {

    public static final Codec<BlockPosition> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.a(intstream, 3).map((aint) -> {
            return new BlockPosition(aint[0], aint[1], aint[2]);
        });
    }, (blockposition) -> {
        return IntStream.of(new int[]{blockposition.getX(), blockposition.getY(), blockposition.getZ()});
    }).stable();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    private static final int PACKED_X_LENGTH = 1 + MathHelper.f(MathHelper.c(30000000));
    private static final int PACKED_Z_LENGTH = BlockPosition.PACKED_X_LENGTH;
    public static final int PACKED_Y_LENGTH = 64 - BlockPosition.PACKED_X_LENGTH - BlockPosition.PACKED_Z_LENGTH;
    private static final long PACKED_X_MASK = (1L << BlockPosition.PACKED_X_LENGTH) - 1L;
    private static final long PACKED_Y_MASK = (1L << BlockPosition.PACKED_Y_LENGTH) - 1L;
    private static final long PACKED_Z_MASK = (1L << BlockPosition.PACKED_Z_LENGTH) - 1L;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET = BlockPosition.PACKED_Y_LENGTH;
    private static final int X_OFFSET = BlockPosition.PACKED_Y_LENGTH + BlockPosition.PACKED_Z_LENGTH;

    public BlockPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPosition(double d0, double d1, double d2) {
        super(d0, d1, d2);
    }

    public BlockPosition(Vec3D vec3d) {
        this(vec3d.x, vec3d.y, vec3d.z);
    }

    public BlockPosition(IPosition iposition) {
        this(iposition.getX(), iposition.getY(), iposition.getZ());
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static long a(long i, EnumDirection enumdirection) {
        return a(i, enumdirection.getAdjacentX(), enumdirection.getAdjacentY(), enumdirection.getAdjacentZ());
    }

    public static long a(long i, int j, int k, int l) {
        return a(a(i) + j, b(i) + k, c(i) + l);
    }

    public static int a(long i) {
        return (int) (i << 64 - BlockPosition.X_OFFSET - BlockPosition.PACKED_X_LENGTH >> 64 - BlockPosition.PACKED_X_LENGTH);
    }

    public static int b(long i) {
        return (int) (i << 64 - BlockPosition.PACKED_Y_LENGTH >> 64 - BlockPosition.PACKED_Y_LENGTH);
    }

    public static int c(long i) {
        return (int) (i << 64 - BlockPosition.Z_OFFSET - BlockPosition.PACKED_Z_LENGTH >> 64 - BlockPosition.PACKED_Z_LENGTH);
    }

    public static BlockPosition fromLong(long i) {
        return new BlockPosition(a(i), b(i), c(i));
    }

    public long asLong() {
        return a(this.getX(), this.getY(), this.getZ());
    }

    public static long a(int i, int j, int k) {
        long l = 0L;

        l |= ((long) i & BlockPosition.PACKED_X_MASK) << BlockPosition.X_OFFSET;
        l |= ((long) j & BlockPosition.PACKED_Y_MASK) << 0;
        l |= ((long) k & BlockPosition.PACKED_Z_MASK) << BlockPosition.Z_OFFSET;
        return l;
    }

    public static long e(long i) {
        return i & -16L;
    }

    @Override
    public BlockPosition b(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    @Override
    public BlockPosition c(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    @Override
    public BlockPosition f(BaseBlockPosition baseblockposition) {
        return this.c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    @Override
    public BlockPosition e(BaseBlockPosition baseblockposition) {
        return this.c(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    @Override
    public BlockPosition o(int i) {
        return i == 1 ? this : (i == 0 ? BlockPosition.ZERO : new BlockPosition(this.getX() * i, this.getY() * i, this.getZ() * i));
    }

    @Override
    public BlockPosition up() {
        return this.shift(EnumDirection.UP);
    }

    @Override
    public BlockPosition up(int i) {
        return this.shift(EnumDirection.UP, i);
    }

    @Override
    public BlockPosition down() {
        return this.shift(EnumDirection.DOWN);
    }

    @Override
    public BlockPosition down(int i) {
        return this.shift(EnumDirection.DOWN, i);
    }

    @Override
    public BlockPosition north() {
        return this.shift(EnumDirection.NORTH);
    }

    @Override
    public BlockPosition north(int i) {
        return this.shift(EnumDirection.NORTH, i);
    }

    @Override
    public BlockPosition south() {
        return this.shift(EnumDirection.SOUTH);
    }

    @Override
    public BlockPosition south(int i) {
        return this.shift(EnumDirection.SOUTH, i);
    }

    @Override
    public BlockPosition west() {
        return this.shift(EnumDirection.WEST);
    }

    @Override
    public BlockPosition west(int i) {
        return this.shift(EnumDirection.WEST, i);
    }

    @Override
    public BlockPosition east() {
        return this.shift(EnumDirection.EAST);
    }

    @Override
    public BlockPosition east(int i) {
        return this.shift(EnumDirection.EAST, i);
    }

    @Override
    public BlockPosition shift(EnumDirection enumdirection) {
        return new BlockPosition(this.getX() + enumdirection.getAdjacentX(), this.getY() + enumdirection.getAdjacentY(), this.getZ() + enumdirection.getAdjacentZ());
    }

    @Override
    public BlockPosition shift(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
    }

    @Override
    public BlockPosition b(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        if (i == 0) {
            return this;
        } else {
            int j = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? i : 0;
            int k = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? i : 0;
            int l = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? i : 0;

            return new BlockPosition(this.getX() + j, this.getY() + k, this.getZ() + l);
        }
    }

    public BlockPosition a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new BlockPosition(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180:
                return new BlockPosition(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPosition(this.getZ(), this.getY(), -this.getX());
        }
    }

    @Override
    public BlockPosition d(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public BlockPosition h(int i) {
        return new BlockPosition(this.getX(), i, this.getZ());
    }

    public BlockPosition immutableCopy() {
        return this;
    }

    public BlockPosition.MutableBlockPosition i() {
        return new BlockPosition.MutableBlockPosition(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPosition> a(Random random, int i, BlockPosition blockposition, int j) {
        return a(random, i, blockposition.getX() - j, blockposition.getY() - j, blockposition.getZ() - j, blockposition.getX() + j, blockposition.getY() + j, blockposition.getZ() + j);
    }

    public static Iterable<BlockPosition> a(Random random, int i, int j, int k, int l, int i1, int j1, int k1) {
        int l1 = i1 - j + 1;
        int i2 = j1 - k + 1;
        int j2 = k1 - l + 1;

        return () -> {
            return new AbstractIterator<BlockPosition>() {
                final BlockPosition.MutableBlockPosition nextPos = new BlockPosition.MutableBlockPosition();
                int counter = i;

                protected BlockPosition computeNext() {
                    if (this.counter <= 0) {
                        return (BlockPosition) this.endOfData();
                    } else {
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.nextPos.d(j + random.nextInt(l1), k + random.nextInt(i2), l + random.nextInt(j2));

                        --this.counter;
                        return blockposition_mutableblockposition;
                    }
                }
            };
        };
    }

    public static Iterable<BlockPosition> a(BlockPosition blockposition, int i, int j, int k) {
        int l = i + j + k;
        int i1 = blockposition.getX();
        int j1 = blockposition.getY();
        int k1 = blockposition.getZ();

        return () -> {
            return new AbstractIterator<BlockPosition>() {
                private final BlockPosition.MutableBlockPosition cursor = new BlockPosition.MutableBlockPosition();
                private int currentDepth;
                private int maxX;
                private int maxY;
                private int x;
                private int y;
                private boolean zMirror;

                protected BlockPosition computeNext() {
                    if (this.zMirror) {
                        this.zMirror = false;
                        this.cursor.s(k1 - (this.cursor.getZ() - k1));
                        return this.cursor;
                    } else {
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition;

                        for (blockposition_mutableblockposition = null; blockposition_mutableblockposition == null; ++this.y) {
                            if (this.y > this.maxY) {
                                ++this.x;
                                if (this.x > this.maxX) {
                                    ++this.currentDepth;
                                    if (this.currentDepth > l) {
                                        return (BlockPosition) this.endOfData();
                                    }

                                    this.maxX = Math.min(i, this.currentDepth);
                                    this.x = -this.maxX;
                                }

                                this.maxY = Math.min(j, this.currentDepth - Math.abs(this.x));
                                this.y = -this.maxY;
                            }

                            int l1 = this.x;
                            int i2 = this.y;
                            int j2 = this.currentDepth - Math.abs(l1) - Math.abs(i2);

                            if (j2 <= k) {
                                this.zMirror = j2 != 0;
                                blockposition_mutableblockposition = this.cursor.d(i1 + l1, j1 + i2, k1 + j2);
                            }
                        }

                        return blockposition_mutableblockposition;
                    }
                }
            };
        };
    }

    public static Optional<BlockPosition> a(BlockPosition blockposition, int i, int j, Predicate<BlockPosition> predicate) {
        return b(blockposition, i, j, i).filter(predicate).findFirst();
    }

    public static Stream<BlockPosition> b(BlockPosition blockposition, int i, int j, int k) {
        return StreamSupport.stream(a(blockposition, i, j, k).spliterator(), false);
    }

    public static Iterable<BlockPosition> a(BlockPosition blockposition, BlockPosition blockposition1) {
        return b(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Stream<BlockPosition> b(BlockPosition blockposition, BlockPosition blockposition1) {
        return StreamSupport.stream(a(blockposition, blockposition1).spliterator(), false);
    }

    public static Stream<BlockPosition> a(StructureBoundingBox structureboundingbox) {
        return a(Math.min(structureboundingbox.g(), structureboundingbox.j()), Math.min(structureboundingbox.h(), structureboundingbox.k()), Math.min(structureboundingbox.i(), structureboundingbox.l()), Math.max(structureboundingbox.g(), structureboundingbox.j()), Math.max(structureboundingbox.h(), structureboundingbox.k()), Math.max(structureboundingbox.i(), structureboundingbox.l()));
    }

    public static Stream<BlockPosition> a(AxisAlignedBB axisalignedbb) {
        return a(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ));
    }

    public static Stream<BlockPosition> a(int i, int j, int k, int l, int i1, int j1) {
        return StreamSupport.stream(b(i, j, k, l, i1, j1).spliterator(), false);
    }

    public static Iterable<BlockPosition> b(int i, int j, int k, int l, int i1, int j1) {
        int k1 = l - i + 1;
        int l1 = i1 - j + 1;
        int i2 = j1 - k + 1;
        int j2 = k1 * l1 * i2;

        return () -> {
            return new AbstractIterator<BlockPosition>() {
                private final BlockPosition.MutableBlockPosition cursor = new BlockPosition.MutableBlockPosition();
                private int index;

                protected BlockPosition computeNext() {
                    if (this.index == j2) {
                        return (BlockPosition) this.endOfData();
                    } else {
                        int k2 = this.index % k1;
                        int l2 = this.index / k1;
                        int i3 = l2 % l1;
                        int j3 = l2 / l1;

                        ++this.index;
                        return this.cursor.d(i + k2, j + i3, k + j3);
                    }
                }
            };
        };
    }

    public static Iterable<BlockPosition.MutableBlockPosition> a(BlockPosition blockposition, int i, EnumDirection enumdirection, EnumDirection enumdirection1) {
        Validate.validState(enumdirection.n() != enumdirection1.n(), "The two directions cannot be on the same axis", new Object[0]);
        return () -> {
            return new AbstractIterator<BlockPosition.MutableBlockPosition>() {
                private final EnumDirection[] directions = new EnumDirection[]{enumdirection, enumdirection1, enumdirection.opposite(), enumdirection1.opposite()};
                private final BlockPosition.MutableBlockPosition cursor = blockposition.i().c(enumdirection1);
                private final int legs = 4 * i;
                private int leg = -1;
                private int legSize;
                private int legIndex;
                private int lastX;
                private int lastY;
                private int lastZ;

                {
                    this.lastX = this.cursor.getX();
                    this.lastY = this.cursor.getY();
                    this.lastZ = this.cursor.getZ();
                }

                protected BlockPosition.MutableBlockPosition computeNext() {
                    this.cursor.d(this.lastX, this.lastY, this.lastZ).c(this.directions[(this.leg + 4) % 4]);
                    this.lastX = this.cursor.getX();
                    this.lastY = this.cursor.getY();
                    this.lastZ = this.cursor.getZ();
                    if (this.legIndex >= this.legSize) {
                        if (this.leg >= this.legs) {
                            return (BlockPosition.MutableBlockPosition) this.endOfData();
                        }

                        ++this.leg;
                        this.legIndex = 0;
                        this.legSize = this.leg / 2 + 1;
                    }

                    ++this.legIndex;
                    return this.cursor;
                }
            };
        };
    }

    public static class MutableBlockPosition extends BlockPosition {

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(int i, int j, int k) {
            super(i, j, k);
        }

        public MutableBlockPosition(double d0, double d1, double d2) {
            this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        @Override
        public BlockPosition b(double d0, double d1, double d2) {
            return super.b(d0, d1, d2).immutableCopy();
        }

        @Override
        public BlockPosition c(int i, int j, int k) {
            return super.c(i, j, k).immutableCopy();
        }

        @Override
        public BlockPosition o(int i) {
            return super.o(i).immutableCopy();
        }

        @Override
        public BlockPosition shift(EnumDirection enumdirection, int i) {
            return super.shift(enumdirection, i).immutableCopy();
        }

        @Override
        public BlockPosition b(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
            return super.b(enumdirection_enumaxis, i).immutableCopy();
        }

        @Override
        public BlockPosition a(EnumBlockRotation enumblockrotation) {
            return super.a(enumblockrotation).immutableCopy();
        }

        public BlockPosition.MutableBlockPosition d(int i, int j, int k) {
            this.u(i);
            this.t(j);
            this.s(k);
            return this;
        }

        public BlockPosition.MutableBlockPosition c(double d0, double d1, double d2) {
            return this.d(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public BlockPosition.MutableBlockPosition g(BaseBlockPosition baseblockposition) {
            return this.d(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition f(long i) {
            return this.d(a(i), b(i), c(i));
        }

        public BlockPosition.MutableBlockPosition a(EnumAxisCycle enumaxiscycle, int i, int j, int k) {
            return this.d(enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.X), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Y), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Z));
        }

        public BlockPosition.MutableBlockPosition a(BaseBlockPosition baseblockposition, EnumDirection enumdirection) {
            return this.d(baseblockposition.getX() + enumdirection.getAdjacentX(), baseblockposition.getY() + enumdirection.getAdjacentY(), baseblockposition.getZ() + enumdirection.getAdjacentZ());
        }

        public BlockPosition.MutableBlockPosition a(BaseBlockPosition baseblockposition, int i, int j, int k) {
            return this.d(baseblockposition.getX() + i, baseblockposition.getY() + j, baseblockposition.getZ() + k);
        }

        public BlockPosition.MutableBlockPosition a(BaseBlockPosition baseblockposition, BaseBlockPosition baseblockposition1) {
            return this.d(baseblockposition.getX() + baseblockposition1.getX(), baseblockposition.getY() + baseblockposition1.getY(), baseblockposition.getZ() + baseblockposition1.getZ());
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection) {
            return this.c(enumdirection, 1);
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection, int i) {
            return this.d(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
        }

        public BlockPosition.MutableBlockPosition e(int i, int j, int k) {
            return this.d(this.getX() + i, this.getY() + j, this.getZ() + k);
        }

        public BlockPosition.MutableBlockPosition h(BaseBlockPosition baseblockposition) {
            return this.d(this.getX() + baseblockposition.getX(), this.getY() + baseblockposition.getY(), this.getZ() + baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition a(EnumDirection.EnumAxis enumdirection_enumaxis, int i, int j) {
            switch (enumdirection_enumaxis) {
                case X:
                    return this.d(MathHelper.clamp(this.getX(), i, j), this.getY(), this.getZ());
                case Y:
                    return this.d(this.getX(), MathHelper.clamp(this.getY(), i, j), this.getZ());
                case Z:
                    return this.d(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), i, j));
                default:
                    throw new IllegalStateException("Unable to clamp axis " + enumdirection_enumaxis);
            }
        }

        @Override
        public BlockPosition.MutableBlockPosition u(int i) {
            super.u(i);
            return this;
        }

        @Override
        public BlockPosition.MutableBlockPosition t(int i) {
            super.t(i);
            return this;
        }

        @Override
        public BlockPosition.MutableBlockPosition s(int i) {
            super.s(i);
            return this;
        }

        @Override
        public BlockPosition immutableCopy() {
            return new BlockPosition(this);
        }
    }
}

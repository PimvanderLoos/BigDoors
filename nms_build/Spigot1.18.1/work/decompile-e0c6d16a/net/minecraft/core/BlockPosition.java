package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Iterator;
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
        return SystemUtils.fixedSize(intstream, 3).map((aint) -> {
            return new BlockPosition(aint[0], aint[1], aint[2]);
        });
    }, (blockposition) -> {
        return IntStream.of(new int[]{blockposition.getX(), blockposition.getY(), blockposition.getZ()});
    }).stable();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    private static final int PACKED_X_LENGTH = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
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
        this(iposition.x(), iposition.y(), iposition.z());
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static long offset(long i, EnumDirection enumdirection) {
        return offset(i, enumdirection.getStepX(), enumdirection.getStepY(), enumdirection.getStepZ());
    }

    public static long offset(long i, int j, int k, int l) {
        return asLong(getX(i) + j, getY(i) + k, getZ(i) + l);
    }

    public static int getX(long i) {
        return (int) (i << 64 - BlockPosition.X_OFFSET - BlockPosition.PACKED_X_LENGTH >> 64 - BlockPosition.PACKED_X_LENGTH);
    }

    public static int getY(long i) {
        return (int) (i << 64 - BlockPosition.PACKED_Y_LENGTH >> 64 - BlockPosition.PACKED_Y_LENGTH);
    }

    public static int getZ(long i) {
        return (int) (i << 64 - BlockPosition.Z_OFFSET - BlockPosition.PACKED_Z_LENGTH >> 64 - BlockPosition.PACKED_Z_LENGTH);
    }

    public static BlockPosition of(long i) {
        return new BlockPosition(getX(i), getY(i), getZ(i));
    }

    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int i, int j, int k) {
        long l = 0L;

        l |= ((long) i & BlockPosition.PACKED_X_MASK) << BlockPosition.X_OFFSET;
        l |= ((long) j & BlockPosition.PACKED_Y_MASK) << 0;
        l |= ((long) k & BlockPosition.PACKED_Z_MASK) << BlockPosition.Z_OFFSET;
        return l;
    }

    public static long getFlatIndex(long i) {
        return i & -16L;
    }

    @Override
    public BlockPosition offset(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    @Override
    public BlockPosition offset(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    @Override
    public BlockPosition offset(BaseBlockPosition baseblockposition) {
        return this.offset(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    @Override
    public BlockPosition subtract(BaseBlockPosition baseblockposition) {
        return this.offset(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    @Override
    public BlockPosition multiply(int i) {
        return i == 1 ? this : (i == 0 ? BlockPosition.ZERO : new BlockPosition(this.getX() * i, this.getY() * i, this.getZ() * i));
    }

    @Override
    public BlockPosition above() {
        return this.relative(EnumDirection.UP);
    }

    @Override
    public BlockPosition above(int i) {
        return this.relative(EnumDirection.UP, i);
    }

    @Override
    public BlockPosition below() {
        return this.relative(EnumDirection.DOWN);
    }

    @Override
    public BlockPosition below(int i) {
        return this.relative(EnumDirection.DOWN, i);
    }

    @Override
    public BlockPosition north() {
        return this.relative(EnumDirection.NORTH);
    }

    @Override
    public BlockPosition north(int i) {
        return this.relative(EnumDirection.NORTH, i);
    }

    @Override
    public BlockPosition south() {
        return this.relative(EnumDirection.SOUTH);
    }

    @Override
    public BlockPosition south(int i) {
        return this.relative(EnumDirection.SOUTH, i);
    }

    @Override
    public BlockPosition west() {
        return this.relative(EnumDirection.WEST);
    }

    @Override
    public BlockPosition west(int i) {
        return this.relative(EnumDirection.WEST, i);
    }

    @Override
    public BlockPosition east() {
        return this.relative(EnumDirection.EAST);
    }

    @Override
    public BlockPosition east(int i) {
        return this.relative(EnumDirection.EAST, i);
    }

    @Override
    public BlockPosition relative(EnumDirection enumdirection) {
        return new BlockPosition(this.getX() + enumdirection.getStepX(), this.getY() + enumdirection.getStepY(), this.getZ() + enumdirection.getStepZ());
    }

    @Override
    public BlockPosition relative(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + enumdirection.getStepX() * i, this.getY() + enumdirection.getStepY() * i, this.getZ() + enumdirection.getStepZ() * i);
    }

    @Override
    public BlockPosition relative(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        if (i == 0) {
            return this;
        } else {
            int j = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? i : 0;
            int k = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? i : 0;
            int l = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? i : 0;

            return new BlockPosition(this.getX() + j, this.getY() + k, this.getZ() + l);
        }
    }

    public BlockPosition rotate(EnumBlockRotation enumblockrotation) {
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
    public BlockPosition cross(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public BlockPosition atY(int i) {
        return new BlockPosition(this.getX(), i, this.getZ());
    }

    public BlockPosition immutable() {
        return this;
    }

    public BlockPosition.MutableBlockPosition mutable() {
        return new BlockPosition.MutableBlockPosition(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPosition> randomInCube(Random random, int i, BlockPosition blockposition, int j) {
        return randomBetweenClosed(random, i, blockposition.getX() - j, blockposition.getY() - j, blockposition.getZ() - j, blockposition.getX() + j, blockposition.getY() + j, blockposition.getZ() + j);
    }

    public static Iterable<BlockPosition> randomBetweenClosed(Random random, int i, int j, int k, int l, int i1, int j1, int k1) {
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
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.nextPos.set(j + random.nextInt(l1), k + random.nextInt(i2), l + random.nextInt(j2));

                        --this.counter;
                        return blockposition_mutableblockposition;
                    }
                }
            };
        };
    }

    public static Iterable<BlockPosition> withinManhattan(BlockPosition blockposition, int i, int j, int k) {
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
                        this.cursor.setZ(k1 - (this.cursor.getZ() - k1));
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
                                blockposition_mutableblockposition = this.cursor.set(i1 + l1, j1 + i2, k1 + j2);
                            }
                        }

                        return blockposition_mutableblockposition;
                    }
                }
            };
        };
    }

    public static Optional<BlockPosition> findClosestMatch(BlockPosition blockposition, int i, int j, Predicate<BlockPosition> predicate) {
        Iterator iterator = withinManhattan(blockposition, i, j, i).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!predicate.test(blockposition1));

        return Optional.of(blockposition1);
    }

    public static Stream<BlockPosition> withinManhattanStream(BlockPosition blockposition, int i, int j, int k) {
        return StreamSupport.stream(withinManhattan(blockposition, i, j, k).spliterator(), false);
    }

    public static Iterable<BlockPosition> betweenClosed(BlockPosition blockposition, BlockPosition blockposition1) {
        return betweenClosed(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Stream<BlockPosition> betweenClosedStream(BlockPosition blockposition, BlockPosition blockposition1) {
        return StreamSupport.stream(betweenClosed(blockposition, blockposition1).spliterator(), false);
    }

    public static Stream<BlockPosition> betweenClosedStream(StructureBoundingBox structureboundingbox) {
        return betweenClosedStream(Math.min(structureboundingbox.minX(), structureboundingbox.maxX()), Math.min(structureboundingbox.minY(), structureboundingbox.maxY()), Math.min(structureboundingbox.minZ(), structureboundingbox.maxZ()), Math.max(structureboundingbox.minX(), structureboundingbox.maxX()), Math.max(structureboundingbox.minY(), structureboundingbox.maxY()), Math.max(structureboundingbox.minZ(), structureboundingbox.maxZ()));
    }

    public static Stream<BlockPosition> betweenClosedStream(AxisAlignedBB axisalignedbb) {
        return betweenClosedStream(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ));
    }

    public static Stream<BlockPosition> betweenClosedStream(int i, int j, int k, int l, int i1, int j1) {
        return StreamSupport.stream(betweenClosed(i, j, k, l, i1, j1).spliterator(), false);
    }

    public static Iterable<BlockPosition> betweenClosed(int i, int j, int k, int l, int i1, int j1) {
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
                        return this.cursor.set(i + k2, j + i3, k + j3);
                    }
                }
            };
        };
    }

    public static Iterable<BlockPosition.MutableBlockPosition> spiralAround(BlockPosition blockposition, int i, EnumDirection enumdirection, EnumDirection enumdirection1) {
        Validate.validState(enumdirection.getAxis() != enumdirection1.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
        return () -> {
            return new AbstractIterator<BlockPosition.MutableBlockPosition>() {
                private final EnumDirection[] directions = new EnumDirection[]{enumdirection, enumdirection1, enumdirection.getOpposite(), enumdirection1.getOpposite()};
                private final BlockPosition.MutableBlockPosition cursor = blockposition.mutable().move(enumdirection1);
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
                    this.cursor.set(this.lastX, this.lastY, this.lastZ).move(this.directions[(this.leg + 4) % 4]);
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
        public BlockPosition offset(double d0, double d1, double d2) {
            return super.offset(d0, d1, d2).immutable();
        }

        @Override
        public BlockPosition offset(int i, int j, int k) {
            return super.offset(i, j, k).immutable();
        }

        @Override
        public BlockPosition multiply(int i) {
            return super.multiply(i).immutable();
        }

        @Override
        public BlockPosition relative(EnumDirection enumdirection, int i) {
            return super.relative(enumdirection, i).immutable();
        }

        @Override
        public BlockPosition relative(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
            return super.relative(enumdirection_enumaxis, i).immutable();
        }

        @Override
        public BlockPosition rotate(EnumBlockRotation enumblockrotation) {
            return super.rotate(enumblockrotation).immutable();
        }

        public BlockPosition.MutableBlockPosition set(int i, int j, int k) {
            this.setX(i);
            this.setY(j);
            this.setZ(k);
            return this;
        }

        public BlockPosition.MutableBlockPosition set(double d0, double d1, double d2) {
            return this.set(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public BlockPosition.MutableBlockPosition set(BaseBlockPosition baseblockposition) {
            return this.set(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition set(long i) {
            return this.set(getX(i), getY(i), getZ(i));
        }

        public BlockPosition.MutableBlockPosition set(EnumAxisCycle enumaxiscycle, int i, int j, int k) {
            return this.set(enumaxiscycle.cycle(i, j, k, EnumDirection.EnumAxis.X), enumaxiscycle.cycle(i, j, k, EnumDirection.EnumAxis.Y), enumaxiscycle.cycle(i, j, k, EnumDirection.EnumAxis.Z));
        }

        public BlockPosition.MutableBlockPosition setWithOffset(BaseBlockPosition baseblockposition, EnumDirection enumdirection) {
            return this.set(baseblockposition.getX() + enumdirection.getStepX(), baseblockposition.getY() + enumdirection.getStepY(), baseblockposition.getZ() + enumdirection.getStepZ());
        }

        public BlockPosition.MutableBlockPosition setWithOffset(BaseBlockPosition baseblockposition, int i, int j, int k) {
            return this.set(baseblockposition.getX() + i, baseblockposition.getY() + j, baseblockposition.getZ() + k);
        }

        public BlockPosition.MutableBlockPosition setWithOffset(BaseBlockPosition baseblockposition, BaseBlockPosition baseblockposition1) {
            return this.set(baseblockposition.getX() + baseblockposition1.getX(), baseblockposition.getY() + baseblockposition1.getY(), baseblockposition.getZ() + baseblockposition1.getZ());
        }

        public BlockPosition.MutableBlockPosition move(EnumDirection enumdirection) {
            return this.move(enumdirection, 1);
        }

        public BlockPosition.MutableBlockPosition move(EnumDirection enumdirection, int i) {
            return this.set(this.getX() + enumdirection.getStepX() * i, this.getY() + enumdirection.getStepY() * i, this.getZ() + enumdirection.getStepZ() * i);
        }

        public BlockPosition.MutableBlockPosition move(int i, int j, int k) {
            return this.set(this.getX() + i, this.getY() + j, this.getZ() + k);
        }

        public BlockPosition.MutableBlockPosition move(BaseBlockPosition baseblockposition) {
            return this.set(this.getX() + baseblockposition.getX(), this.getY() + baseblockposition.getY(), this.getZ() + baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition clamp(EnumDirection.EnumAxis enumdirection_enumaxis, int i, int j) {
            switch (enumdirection_enumaxis) {
                case X:
                    return this.set(MathHelper.clamp(this.getX(), i, j), this.getY(), this.getZ());
                case Y:
                    return this.set(this.getX(), MathHelper.clamp(this.getY(), i, j), this.getZ());
                case Z:
                    return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), i, j));
                default:
                    throw new IllegalStateException("Unable to clamp axis " + enumdirection_enumaxis);
            }
        }

        @Override
        public BlockPosition.MutableBlockPosition setX(int i) {
            super.setX(i);
            return this;
        }

        @Override
        public BlockPosition.MutableBlockPosition setY(int i) {
            super.setY(i);
            return this;
        }

        @Override
        public BlockPosition.MutableBlockPosition setZ(int i) {
            super.setZ(i);
            return this;
        }

        @Override
        public BlockPosition immutable() {
            return new BlockPosition(this);
        }
    }
}

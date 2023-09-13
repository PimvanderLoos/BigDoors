package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3fa;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;

public enum EnumDirection implements INamable {

    DOWN(0, 1, -1, "down", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, -1, 0)), UP(1, 0, -1, "up", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, 1, 0)), NORTH(2, 3, 2, "north", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, -1)), SOUTH(3, 2, 0, "south", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, 1)), WEST(4, 5, 1, "west", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(-1, 0, 0)), EAST(5, 4, 3, "east", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(1, 0, 0));

    public static final Codec<EnumDirection> CODEC = INamable.fromEnum(EnumDirection::values, EnumDirection::byName);
    public static final Codec<EnumDirection> VERTICAL_CODEC = EnumDirection.CODEC.flatXmap(EnumDirection::verifyVertical, EnumDirection::verifyVertical);
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final EnumDirection.EnumAxis axis;
    private final EnumDirection.EnumAxisDirection axisDirection;
    private final BaseBlockPosition normal;
    private static final EnumDirection[] VALUES = values();
    private static final Map<String, EnumDirection> BY_NAME = (Map) Arrays.stream(EnumDirection.VALUES).collect(Collectors.toMap(EnumDirection::getName, (enumdirection) -> {
        return enumdirection;
    }));
    private static final EnumDirection[] BY_3D_DATA = (EnumDirection[]) Arrays.stream(EnumDirection.VALUES).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.data3d;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });
    private static final EnumDirection[] BY_2D_DATA = (EnumDirection[]) Arrays.stream(EnumDirection.VALUES).filter((enumdirection) -> {
        return enumdirection.getAxis().isHorizontal();
    }).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.data2d;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });
    private static final Long2ObjectMap<EnumDirection> BY_NORMAL = (Long2ObjectMap) Arrays.stream(EnumDirection.VALUES).collect(Collectors.toMap((enumdirection) -> {
        return (new BlockPosition(enumdirection.getNormal())).asLong();
    }, (enumdirection) -> {
        return enumdirection;
    }, (enumdirection, enumdirection1) -> {
        throw new IllegalArgumentException("Duplicate keys");
    }, Long2ObjectOpenHashMap::new));

    private EnumDirection(int i, int j, int k, String s, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis, BaseBlockPosition baseblockposition) {
        this.data3d = i;
        this.data2d = k;
        this.oppositeIndex = j;
        this.name = s;
        this.axis = enumdirection_enumaxis;
        this.axisDirection = enumdirection_enumaxisdirection;
        this.normal = baseblockposition;
    }

    public static EnumDirection[] orderedByNearest(Entity entity) {
        float f = entity.getViewXRot(1.0F) * 0.017453292F;
        float f1 = -entity.getViewYRot(1.0F) * 0.017453292F;
        float f2 = MathHelper.sin(f);
        float f3 = MathHelper.cos(f);
        float f4 = MathHelper.sin(f1);
        float f5 = MathHelper.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        EnumDirection enumdirection = flag ? EnumDirection.EAST : EnumDirection.WEST;
        EnumDirection enumdirection1 = flag1 ? EnumDirection.UP : EnumDirection.DOWN;
        EnumDirection enumdirection2 = flag2 ? EnumDirection.SOUTH : EnumDirection.NORTH;

        return f6 > f8 ? (f7 > f9 ? makeDirectionArray(enumdirection1, enumdirection, enumdirection2) : (f10 > f7 ? makeDirectionArray(enumdirection, enumdirection2, enumdirection1) : makeDirectionArray(enumdirection, enumdirection1, enumdirection2))) : (f7 > f10 ? makeDirectionArray(enumdirection1, enumdirection2, enumdirection) : (f9 > f7 ? makeDirectionArray(enumdirection2, enumdirection, enumdirection1) : makeDirectionArray(enumdirection2, enumdirection1, enumdirection)));
    }

    private static EnumDirection[] makeDirectionArray(EnumDirection enumdirection, EnumDirection enumdirection1, EnumDirection enumdirection2) {
        return new EnumDirection[]{enumdirection, enumdirection1, enumdirection2, enumdirection2.getOpposite(), enumdirection1.getOpposite(), enumdirection.getOpposite()};
    }

    public static EnumDirection rotate(Matrix4f matrix4f, EnumDirection enumdirection) {
        BaseBlockPosition baseblockposition = enumdirection.getNormal();
        Vector4f vector4f = new Vector4f((float) baseblockposition.getX(), (float) baseblockposition.getY(), (float) baseblockposition.getZ(), 0.0F);

        vector4f.transform(matrix4f);
        return getNearest(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public Quaternion getRotation() {
        Quaternion quaternion = Vector3fa.XP.rotationDegrees(90.0F);
        Quaternion quaternion1;

        switch (this) {
            case DOWN:
                quaternion1 = Vector3fa.XP.rotationDegrees(180.0F);
                break;
            case UP:
                quaternion1 = Quaternion.ONE.copy();
                break;
            case NORTH:
                quaternion.mul(Vector3fa.ZP.rotationDegrees(180.0F));
                quaternion1 = quaternion;
                break;
            case SOUTH:
                quaternion1 = quaternion;
                break;
            case WEST:
                quaternion.mul(Vector3fa.ZP.rotationDegrees(90.0F));
                quaternion1 = quaternion;
                break;
            case EAST:
                quaternion.mul(Vector3fa.ZP.rotationDegrees(-90.0F));
                quaternion1 = quaternion;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return quaternion1;
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public EnumDirection.EnumAxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public static EnumDirection getFacingAxis(Entity entity, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection enumdirection;

        switch (enumdirection_enumaxis) {
            case X:
                enumdirection = EnumDirection.EAST.isFacingAngle(entity.getViewYRot(1.0F)) ? EnumDirection.EAST : EnumDirection.WEST;
                break;
            case Z:
                enumdirection = EnumDirection.SOUTH.isFacingAngle(entity.getViewYRot(1.0F)) ? EnumDirection.SOUTH : EnumDirection.NORTH;
                break;
            case Y:
                enumdirection = entity.getViewXRot(1.0F) < 0.0F ? EnumDirection.UP : EnumDirection.DOWN;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return enumdirection;
    }

    public EnumDirection getOpposite() {
        return from3DDataValue(this.oppositeIndex);
    }

    public EnumDirection getClockWise(EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection enumdirection;

        switch (enumdirection_enumaxis) {
            case X:
                enumdirection = this != EnumDirection.WEST && this != EnumDirection.EAST ? this.getClockWiseX() : this;
                break;
            case Z:
                enumdirection = this != EnumDirection.NORTH && this != EnumDirection.SOUTH ? this.getClockWiseZ() : this;
                break;
            case Y:
                enumdirection = this != EnumDirection.UP && this != EnumDirection.DOWN ? this.getClockWise() : this;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return enumdirection;
    }

    public EnumDirection getCounterClockWise(EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection enumdirection;

        switch (enumdirection_enumaxis) {
            case X:
                enumdirection = this != EnumDirection.WEST && this != EnumDirection.EAST ? this.getCounterClockWiseX() : this;
                break;
            case Z:
                enumdirection = this != EnumDirection.NORTH && this != EnumDirection.SOUTH ? this.getCounterClockWiseZ() : this;
                break;
            case Y:
                enumdirection = this != EnumDirection.UP && this != EnumDirection.DOWN ? this.getCounterClockWise() : this;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return enumdirection;
    }

    public EnumDirection getClockWise() {
        EnumDirection enumdirection;

        switch (this) {
            case NORTH:
                enumdirection = EnumDirection.EAST;
                break;
            case SOUTH:
                enumdirection = EnumDirection.WEST;
                break;
            case WEST:
                enumdirection = EnumDirection.NORTH;
                break;
            case EAST:
                enumdirection = EnumDirection.SOUTH;
                break;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }

        return enumdirection;
    }

    private EnumDirection getClockWiseX() {
        EnumDirection enumdirection;

        switch (this) {
            case DOWN:
                enumdirection = EnumDirection.SOUTH;
                break;
            case UP:
                enumdirection = EnumDirection.NORTH;
                break;
            case NORTH:
                enumdirection = EnumDirection.DOWN;
                break;
            case SOUTH:
                enumdirection = EnumDirection.UP;
                break;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return enumdirection;
    }

    private EnumDirection getCounterClockWiseX() {
        EnumDirection enumdirection;

        switch (this) {
            case DOWN:
                enumdirection = EnumDirection.NORTH;
                break;
            case UP:
                enumdirection = EnumDirection.SOUTH;
                break;
            case NORTH:
                enumdirection = EnumDirection.UP;
                break;
            case SOUTH:
                enumdirection = EnumDirection.DOWN;
                break;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return enumdirection;
    }

    private EnumDirection getClockWiseZ() {
        EnumDirection enumdirection;

        switch (this) {
            case DOWN:
                enumdirection = EnumDirection.WEST;
                break;
            case UP:
                enumdirection = EnumDirection.EAST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                enumdirection = EnumDirection.UP;
                break;
            case EAST:
                enumdirection = EnumDirection.DOWN;
        }

        return enumdirection;
    }

    private EnumDirection getCounterClockWiseZ() {
        EnumDirection enumdirection;

        switch (this) {
            case DOWN:
                enumdirection = EnumDirection.EAST;
                break;
            case UP:
                enumdirection = EnumDirection.WEST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                enumdirection = EnumDirection.DOWN;
                break;
            case EAST:
                enumdirection = EnumDirection.UP;
        }

        return enumdirection;
    }

    public EnumDirection getCounterClockWise() {
        EnumDirection enumdirection;

        switch (this) {
            case NORTH:
                enumdirection = EnumDirection.WEST;
                break;
            case SOUTH:
                enumdirection = EnumDirection.EAST;
                break;
            case WEST:
                enumdirection = EnumDirection.SOUTH;
                break;
            case EAST:
                enumdirection = EnumDirection.NORTH;
                break;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }

        return enumdirection;
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3fa step() {
        return new Vector3fa((float) this.getStepX(), (float) this.getStepY(), (float) this.getStepZ());
    }

    public String getName() {
        return this.name;
    }

    public EnumDirection.EnumAxis getAxis() {
        return this.axis;
    }

    @Nullable
    public static EnumDirection byName(@Nullable String s) {
        return s == null ? null : (EnumDirection) EnumDirection.BY_NAME.get(s.toLowerCase(Locale.ROOT));
    }

    public static EnumDirection from3DDataValue(int i) {
        return EnumDirection.BY_3D_DATA[MathHelper.abs(i % EnumDirection.BY_3D_DATA.length)];
    }

    public static EnumDirection from2DDataValue(int i) {
        return EnumDirection.BY_2D_DATA[MathHelper.abs(i % EnumDirection.BY_2D_DATA.length)];
    }

    @Nullable
    public static EnumDirection fromNormal(BlockPosition blockposition) {
        return (EnumDirection) EnumDirection.BY_NORMAL.get(blockposition.asLong());
    }

    @Nullable
    public static EnumDirection fromNormal(int i, int j, int k) {
        return (EnumDirection) EnumDirection.BY_NORMAL.get(BlockPosition.asLong(i, j, k));
    }

    public static EnumDirection fromYRot(double d0) {
        return from2DDataValue(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public static EnumDirection fromAxisAndDirection(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection) {
        EnumDirection enumdirection;

        switch (enumdirection_enumaxis) {
            case X:
                enumdirection = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.EAST : EnumDirection.WEST;
                break;
            case Z:
                enumdirection = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.SOUTH : EnumDirection.NORTH;
                break;
            case Y:
                enumdirection = enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.UP : EnumDirection.DOWN;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return enumdirection;
    }

    public float toYRot() {
        return (float) ((this.data2d & 3) * 90);
    }

    public static EnumDirection getRandom(Random random) {
        return (EnumDirection) SystemUtils.getRandom((Object[]) EnumDirection.VALUES, random);
    }

    public static EnumDirection getNearest(double d0, double d1, double d2) {
        return getNearest((float) d0, (float) d1, (float) d2);
    }

    public static EnumDirection getNearest(float f, float f1, float f2) {
        EnumDirection enumdirection = EnumDirection.NORTH;
        float f3 = Float.MIN_VALUE;
        EnumDirection[] aenumdirection = EnumDirection.VALUES;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];
            float f4 = f * (float) enumdirection1.normal.getX() + f1 * (float) enumdirection1.normal.getY() + f2 * (float) enumdirection1.normal.getZ();

            if (f4 > f3) {
                f3 = f4;
                enumdirection = enumdirection1;
            }
        }

        return enumdirection;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static DataResult<EnumDirection> verifyVertical(EnumDirection enumdirection) {
        return enumdirection.getAxis().isVertical() ? DataResult.success(enumdirection) : DataResult.error("Expected a vertical direction");
    }

    public static EnumDirection get(EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection[] aenumdirection = EnumDirection.VALUES;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.getAxisDirection() == enumdirection_enumaxisdirection && enumdirection.getAxis() == enumdirection_enumaxis) {
                return enumdirection;
            }
        }

        throw new IllegalArgumentException("No such direction: " + enumdirection_enumaxisdirection + " " + enumdirection_enumaxis);
    }

    public BaseBlockPosition getNormal() {
        return this.normal;
    }

    public boolean isFacingAngle(float f) {
        float f1 = f * 0.017453292F;
        float f2 = -MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);

        return (float) this.normal.getX() * f2 + (float) this.normal.getZ() * f3 > 0.0F;
    }

    public static enum EnumAxis implements INamable, Predicate<EnumDirection> {

        X("x") {
            @Override
            public int choose(int i, int j, int k) {
                return i;
            }

            @Override
            public double choose(double d0, double d1, double d2) {
                return d0;
            }
        },
        Y("y") {
            @Override
            public int choose(int i, int j, int k) {
                return j;
            }

            @Override
            public double choose(double d0, double d1, double d2) {
                return d1;
            }
        },
        Z("z") {
            @Override
            public int choose(int i, int j, int k) {
                return k;
            }

            @Override
            public double choose(double d0, double d1, double d2) {
                return d2;
            }
        };

        public static final EnumDirection.EnumAxis[] VALUES = values();
        public static final Codec<EnumDirection.EnumAxis> CODEC = INamable.fromEnum(EnumDirection.EnumAxis::values, EnumDirection.EnumAxis::byName);
        private static final Map<String, EnumDirection.EnumAxis> BY_NAME = (Map) Arrays.stream(EnumDirection.EnumAxis.VALUES).collect(Collectors.toMap(EnumDirection.EnumAxis::getName, (enumdirection_enumaxis) -> {
            return enumdirection_enumaxis;
        }));
        private final String name;

        EnumAxis(String s) {
            this.name = s;
        }

        @Nullable
        public static EnumDirection.EnumAxis byName(String s) {
            return (EnumDirection.EnumAxis) EnumDirection.EnumAxis.BY_NAME.get(s.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == EnumDirection.EnumAxis.Y;
        }

        public boolean isHorizontal() {
            return this == EnumDirection.EnumAxis.X || this == EnumDirection.EnumAxis.Z;
        }

        public String toString() {
            return this.name;
        }

        public static EnumDirection.EnumAxis getRandom(Random random) {
            return (EnumDirection.EnumAxis) SystemUtils.getRandom((Object[]) EnumDirection.EnumAxis.VALUES, random);
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.getAxis() == this;
        }

        public EnumDirection.EnumDirectionLimit getPlane() {
            EnumDirection.EnumDirectionLimit enumdirection_enumdirectionlimit;

            switch (this) {
                case X:
                case Z:
                    enumdirection_enumdirectionlimit = EnumDirection.EnumDirectionLimit.HORIZONTAL;
                    break;
                case Y:
                    enumdirection_enumdirectionlimit = EnumDirection.EnumDirectionLimit.VERTICAL;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return enumdirection_enumdirectionlimit;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int i, int j, int k);

        public abstract double choose(double d0, double d1, double d2);
    }

    public static enum EnumAxisDirection {

        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private EnumAxisDirection(int i, String s) {
            this.step = i;
            this.name = s;
        }

        public int getStep() {
            return this.step;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public EnumDirection.EnumAxisDirection opposite() {
            return this == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.EnumAxisDirection.NEGATIVE : EnumDirection.EnumAxisDirection.POSITIVE;
        }
    }

    public static enum EnumDirectionLimit implements Iterable<EnumDirection>, Predicate<EnumDirection> {

        HORIZONTAL(new EnumDirection[]{EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST}, new EnumDirection.EnumAxis[]{EnumDirection.EnumAxis.X, EnumDirection.EnumAxis.Z}), VERTICAL(new EnumDirection[]{EnumDirection.UP, EnumDirection.DOWN}, new EnumDirection.EnumAxis[]{EnumDirection.EnumAxis.Y});

        private final EnumDirection[] faces;
        private final EnumDirection.EnumAxis[] axis;

        private EnumDirectionLimit(EnumDirection[] aenumdirection, EnumDirection.EnumAxis[] aenumdirection_enumaxis) {
            this.faces = aenumdirection;
            this.axis = aenumdirection_enumaxis;
        }

        public EnumDirection getRandomDirection(Random random) {
            return (EnumDirection) SystemUtils.getRandom((Object[]) this.faces, random);
        }

        public EnumDirection.EnumAxis getRandomAxis(Random random) {
            return (EnumDirection.EnumAxis) SystemUtils.getRandom((Object[]) this.axis, random);
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.getAxis().getPlane() == this;
        }

        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.faces);
        }

        public Stream<EnumDirection> stream() {
            return Arrays.stream(this.faces);
        }
    }
}

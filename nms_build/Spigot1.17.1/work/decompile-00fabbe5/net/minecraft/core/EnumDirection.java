package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3fa;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
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

    public static final Codec<EnumDirection> CODEC = INamable.a(EnumDirection::values, EnumDirection::a);
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final EnumDirection.EnumAxis axis;
    private final EnumDirection.EnumAxisDirection axisDirection;
    private final BaseBlockPosition normal;
    private static final EnumDirection[] VALUES = values();
    private static final Map<String, EnumDirection> BY_NAME = (Map) Arrays.stream(EnumDirection.VALUES).collect(Collectors.toMap(EnumDirection::m, (enumdirection) -> {
        return enumdirection;
    }));
    private static final EnumDirection[] BY_3D_DATA = (EnumDirection[]) Arrays.stream(EnumDirection.VALUES).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.data3d;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });
    private static final EnumDirection[] BY_2D_DATA = (EnumDirection[]) Arrays.stream(EnumDirection.VALUES).filter((enumdirection) -> {
        return enumdirection.n().d();
    }).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.data2d;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });
    private static final Long2ObjectMap<EnumDirection> BY_NORMAL = (Long2ObjectMap) Arrays.stream(EnumDirection.VALUES).collect(Collectors.toMap((enumdirection) -> {
        return (new BlockPosition(enumdirection.p())).asLong();
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

    public static EnumDirection[] a(Entity entity) {
        float f = entity.f(1.0F) * 0.017453292F;
        float f1 = -entity.g(1.0F) * 0.017453292F;
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

        return f6 > f8 ? (f7 > f9 ? a(enumdirection1, enumdirection, enumdirection2) : (f10 > f7 ? a(enumdirection, enumdirection2, enumdirection1) : a(enumdirection, enumdirection1, enumdirection2))) : (f7 > f10 ? a(enumdirection1, enumdirection2, enumdirection) : (f9 > f7 ? a(enumdirection2, enumdirection, enumdirection1) : a(enumdirection2, enumdirection1, enumdirection)));
    }

    private static EnumDirection[] a(EnumDirection enumdirection, EnumDirection enumdirection1, EnumDirection enumdirection2) {
        return new EnumDirection[]{enumdirection, enumdirection1, enumdirection2, enumdirection2.opposite(), enumdirection1.opposite(), enumdirection.opposite()};
    }

    public static EnumDirection a(Matrix4f matrix4f, EnumDirection enumdirection) {
        BaseBlockPosition baseblockposition = enumdirection.p();
        Vector4f vector4f = new Vector4f((float) baseblockposition.getX(), (float) baseblockposition.getY(), (float) baseblockposition.getZ(), 0.0F);

        vector4f.a(matrix4f);
        return a(vector4f.a(), vector4f.b(), vector4f.c());
    }

    public Quaternion a() {
        Quaternion quaternion = Vector3fa.XP.c(90.0F);

        switch (this) {
            case DOWN:
                return Vector3fa.XP.c(180.0F);
            case UP:
                return Quaternion.ONE.k();
            case NORTH:
                quaternion.a(Vector3fa.ZP.c(180.0F));
                return quaternion;
            case SOUTH:
                return quaternion;
            case WEST:
                quaternion.a(Vector3fa.ZP.c(90.0F));
                return quaternion;
            case EAST:
            default:
                quaternion.a(Vector3fa.ZP.c(-90.0F));
                return quaternion;
        }
    }

    public int b() {
        return this.data3d;
    }

    public int get2DRotationValue() {
        return this.data2d;
    }

    public EnumDirection.EnumAxisDirection e() {
        return this.axisDirection;
    }

    public static EnumDirection a(Entity entity, EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case X:
                return EnumDirection.EAST.a(entity.g(1.0F)) ? EnumDirection.EAST : EnumDirection.WEST;
            case Z:
                return EnumDirection.SOUTH.a(entity.g(1.0F)) ? EnumDirection.SOUTH : EnumDirection.NORTH;
            case Y:
            default:
                return entity.f(1.0F) < 0.0F ? EnumDirection.UP : EnumDirection.DOWN;
        }
    }

    public EnumDirection opposite() {
        return fromType1(this.oppositeIndex);
    }

    public EnumDirection a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case X:
                if (this != EnumDirection.WEST && this != EnumDirection.EAST) {
                    return this.q();
                }

                return this;
            case Z:
                if (this != EnumDirection.NORTH && this != EnumDirection.SOUTH) {
                    return this.s();
                }

                return this;
            case Y:
                if (this != EnumDirection.UP && this != EnumDirection.DOWN) {
                    return this.g();
                }

                return this;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + enumdirection_enumaxis);
        }
    }

    public EnumDirection b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case X:
                if (this != EnumDirection.WEST && this != EnumDirection.EAST) {
                    return this.r();
                }

                return this;
            case Z:
                if (this != EnumDirection.NORTH && this != EnumDirection.SOUTH) {
                    return this.t();
                }

                return this;
            case Y:
                if (this != EnumDirection.UP && this != EnumDirection.DOWN) {
                    return this.h();
                }

                return this;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + enumdirection_enumaxis);
        }
    }

    public EnumDirection g() {
        switch (this) {
            case NORTH:
                return EnumDirection.EAST;
            case SOUTH:
                return EnumDirection.WEST;
            case WEST:
                return EnumDirection.NORTH;
            case EAST:
                return EnumDirection.SOUTH;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    private EnumDirection q() {
        switch (this) {
            case DOWN:
                return EnumDirection.SOUTH;
            case UP:
                return EnumDirection.NORTH;
            case NORTH:
                return EnumDirection.DOWN;
            case SOUTH:
                return EnumDirection.UP;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }
    }

    private EnumDirection r() {
        switch (this) {
            case DOWN:
                return EnumDirection.NORTH;
            case UP:
                return EnumDirection.SOUTH;
            case NORTH:
                return EnumDirection.UP;
            case SOUTH:
                return EnumDirection.DOWN;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }
    }

    private EnumDirection s() {
        switch (this) {
            case DOWN:
                return EnumDirection.WEST;
            case UP:
                return EnumDirection.EAST;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                return EnumDirection.UP;
            case EAST:
                return EnumDirection.DOWN;
        }
    }

    private EnumDirection t() {
        switch (this) {
            case DOWN:
                return EnumDirection.EAST;
            case UP:
                return EnumDirection.WEST;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                return EnumDirection.DOWN;
            case EAST:
                return EnumDirection.UP;
        }
    }

    public EnumDirection h() {
        switch (this) {
            case NORTH:
                return EnumDirection.WEST;
            case SOUTH:
                return EnumDirection.EAST;
            case WEST:
                return EnumDirection.SOUTH;
            case EAST:
                return EnumDirection.NORTH;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getAdjacentX() {
        return this.normal.getX();
    }

    public int getAdjacentY() {
        return this.normal.getY();
    }

    public int getAdjacentZ() {
        return this.normal.getZ();
    }

    public Vector3fa l() {
        return new Vector3fa((float) this.getAdjacentX(), (float) this.getAdjacentY(), (float) this.getAdjacentZ());
    }

    public String m() {
        return this.name;
    }

    public EnumDirection.EnumAxis n() {
        return this.axis;
    }

    @Nullable
    public static EnumDirection a(@Nullable String s) {
        return s == null ? null : (EnumDirection) EnumDirection.BY_NAME.get(s.toLowerCase(Locale.ROOT));
    }

    public static EnumDirection fromType1(int i) {
        return EnumDirection.BY_3D_DATA[MathHelper.a(i % EnumDirection.BY_3D_DATA.length)];
    }

    public static EnumDirection fromType2(int i) {
        return EnumDirection.BY_2D_DATA[MathHelper.a(i % EnumDirection.BY_2D_DATA.length)];
    }

    @Nullable
    public static EnumDirection a(BlockPosition blockposition) {
        return (EnumDirection) EnumDirection.BY_NORMAL.get(blockposition.asLong());
    }

    @Nullable
    public static EnumDirection a(int i, int j, int k) {
        return (EnumDirection) EnumDirection.BY_NORMAL.get(BlockPosition.a(i, j, k));
    }

    public static EnumDirection fromAngle(double d0) {
        return fromType2(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public static EnumDirection a(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection) {
        switch (enumdirection_enumaxis) {
            case X:
                return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.EAST : EnumDirection.WEST;
            case Z:
            default:
                return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.SOUTH : EnumDirection.NORTH;
            case Y:
                return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.UP : EnumDirection.DOWN;
        }
    }

    public float o() {
        return (float) ((this.data2d & 3) * 90);
    }

    public static EnumDirection a(Random random) {
        return (EnumDirection) SystemUtils.a((Object[]) EnumDirection.VALUES, random);
    }

    public static EnumDirection a(double d0, double d1, double d2) {
        return a((float) d0, (float) d1, (float) d2);
    }

    public static EnumDirection a(float f, float f1, float f2) {
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
    public String getName() {
        return this.name;
    }

    public static EnumDirection a(EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection[] aenumdirection = EnumDirection.VALUES;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.e() == enumdirection_enumaxisdirection && enumdirection.n() == enumdirection_enumaxis) {
                return enumdirection;
            }
        }

        throw new IllegalArgumentException("No such direction: " + enumdirection_enumaxisdirection + " " + enumdirection_enumaxis);
    }

    public BaseBlockPosition p() {
        return this.normal;
    }

    public boolean a(float f) {
        float f1 = f * 0.017453292F;
        float f2 = -MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);

        return (float) this.normal.getX() * f2 + (float) this.normal.getZ() * f3 > 0.0F;
    }

    public static enum EnumAxis implements INamable, Predicate<EnumDirection> {

        X("x") {
            @Override
            public int a(int i, int j, int k) {
                return i;
            }

            @Override
            public double a(double d0, double d1, double d2) {
                return d0;
            }
        },
        Y("y") {
            @Override
            public int a(int i, int j, int k) {
                return j;
            }

            @Override
            public double a(double d0, double d1, double d2) {
                return d1;
            }
        },
        Z("z") {
            @Override
            public int a(int i, int j, int k) {
                return k;
            }

            @Override
            public double a(double d0, double d1, double d2) {
                return d2;
            }
        };

        public static final EnumDirection.EnumAxis[] VALUES = values();
        public static final Codec<EnumDirection.EnumAxis> CODEC = INamable.a(EnumDirection.EnumAxis::values, EnumDirection.EnumAxis::a);
        private static final Map<String, EnumDirection.EnumAxis> BY_NAME = (Map) Arrays.stream(EnumDirection.EnumAxis.VALUES).collect(Collectors.toMap(EnumDirection.EnumAxis::a, (enumdirection_enumaxis) -> {
            return enumdirection_enumaxis;
        }));
        private final String name;

        EnumAxis(String s) {
            this.name = s;
        }

        @Nullable
        public static EnumDirection.EnumAxis a(String s) {
            return (EnumDirection.EnumAxis) EnumDirection.EnumAxis.BY_NAME.get(s.toLowerCase(Locale.ROOT));
        }

        public String a() {
            return this.name;
        }

        public boolean b() {
            return this == EnumDirection.EnumAxis.Y;
        }

        public boolean d() {
            return this == EnumDirection.EnumAxis.X || this == EnumDirection.EnumAxis.Z;
        }

        public String toString() {
            return this.name;
        }

        public static EnumDirection.EnumAxis a(Random random) {
            return (EnumDirection.EnumAxis) SystemUtils.a((Object[]) EnumDirection.EnumAxis.VALUES, random);
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.n() == this;
        }

        public EnumDirection.EnumDirectionLimit e() {
            switch (this) {
                case X:
                case Z:
                    return EnumDirection.EnumDirectionLimit.HORIZONTAL;
                case Y:
                    return EnumDirection.EnumDirectionLimit.VERTICAL;
                default:
                    throw new Error("Someone's been tampering with the universe!");
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        public abstract int a(int i, int j, int k);

        public abstract double a(double d0, double d1, double d2);
    }

    public static enum EnumAxisDirection {

        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private EnumAxisDirection(int i, String s) {
            this.step = i;
            this.name = s;
        }

        public int a() {
            return this.step;
        }

        public String b() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public EnumDirection.EnumAxisDirection c() {
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

        public EnumDirection a(Random random) {
            return (EnumDirection) SystemUtils.a((Object[]) this.faces, random);
        }

        public EnumDirection.EnumAxis b(Random random) {
            return (EnumDirection.EnumAxis) SystemUtils.a((Object[]) this.axis, random);
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.n().e() == this;
        }

        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.faces);
        }

        public Stream<EnumDirection> a() {
            return Arrays.stream(this.faces);
        }
    }
}

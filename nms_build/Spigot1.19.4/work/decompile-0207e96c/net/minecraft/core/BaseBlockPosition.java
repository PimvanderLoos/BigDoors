package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.SystemUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;

@Immutable
public class BaseBlockPosition implements Comparable<BaseBlockPosition> {

    public static final Codec<BaseBlockPosition> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.fixedSize(intstream, 3).map((aint) -> {
            return new BaseBlockPosition(aint[0], aint[1], aint[2]);
        });
    }, (baseblockposition) -> {
        return IntStream.of(new int[]{baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ()});
    });
    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public static Codec<BaseBlockPosition> offsetCodec(int i) {
        return ExtraCodecs.validate(BaseBlockPosition.CODEC, (baseblockposition) -> {
            return Math.abs(baseblockposition.getX()) < i && Math.abs(baseblockposition.getY()) < i && Math.abs(baseblockposition.getZ()) < i ? DataResult.success(baseblockposition) : DataResult.error(() -> {
                return "Position out of range, expected at most " + i + ": " + baseblockposition;
            });
        });
    }

    public BaseBlockPosition(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof BaseBlockPosition)) {
            return false;
        } else {
            BaseBlockPosition baseblockposition = (BaseBlockPosition) object;

            return this.getX() != baseblockposition.getX() ? false : (this.getY() != baseblockposition.getY() ? false : this.getZ() == baseblockposition.getZ());
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(BaseBlockPosition baseblockposition) {
        return this.getY() == baseblockposition.getY() ? (this.getZ() == baseblockposition.getZ() ? this.getX() - baseblockposition.getX() : this.getZ() - baseblockposition.getZ()) : this.getY() - baseblockposition.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    protected BaseBlockPosition setX(int i) {
        this.x = i;
        return this;
    }

    protected BaseBlockPosition setY(int i) {
        this.y = i;
        return this;
    }

    protected BaseBlockPosition setZ(int i) {
        this.z = i;
        return this;
    }

    public BaseBlockPosition offset(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BaseBlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BaseBlockPosition offset(BaseBlockPosition baseblockposition) {
        return this.offset(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BaseBlockPosition subtract(BaseBlockPosition baseblockposition) {
        return this.offset(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    public BaseBlockPosition multiply(int i) {
        return i == 1 ? this : (i == 0 ? BaseBlockPosition.ZERO : new BaseBlockPosition(this.getX() * i, this.getY() * i, this.getZ() * i));
    }

    public BaseBlockPosition above() {
        return this.above(1);
    }

    public BaseBlockPosition above(int i) {
        return this.relative(EnumDirection.UP, i);
    }

    public BaseBlockPosition below() {
        return this.below(1);
    }

    public BaseBlockPosition below(int i) {
        return this.relative(EnumDirection.DOWN, i);
    }

    public BaseBlockPosition north() {
        return this.north(1);
    }

    public BaseBlockPosition north(int i) {
        return this.relative(EnumDirection.NORTH, i);
    }

    public BaseBlockPosition south() {
        return this.south(1);
    }

    public BaseBlockPosition south(int i) {
        return this.relative(EnumDirection.SOUTH, i);
    }

    public BaseBlockPosition west() {
        return this.west(1);
    }

    public BaseBlockPosition west(int i) {
        return this.relative(EnumDirection.WEST, i);
    }

    public BaseBlockPosition east() {
        return this.east(1);
    }

    public BaseBlockPosition east(int i) {
        return this.relative(EnumDirection.EAST, i);
    }

    public BaseBlockPosition relative(EnumDirection enumdirection) {
        return this.relative(enumdirection, 1);
    }

    public BaseBlockPosition relative(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BaseBlockPosition(this.getX() + enumdirection.getStepX() * i, this.getY() + enumdirection.getStepY() * i, this.getZ() + enumdirection.getStepZ() * i);
    }

    public BaseBlockPosition relative(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        if (i == 0) {
            return this;
        } else {
            int j = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? i : 0;
            int k = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? i : 0;
            int l = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? i : 0;

            return new BaseBlockPosition(this.getX() + j, this.getY() + k, this.getZ() + l);
        }
    }

    public BaseBlockPosition cross(BaseBlockPosition baseblockposition) {
        return new BaseBlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public boolean closerThan(BaseBlockPosition baseblockposition, double d0) {
        return this.distSqr(baseblockposition) < MathHelper.square(d0);
    }

    public boolean closerToCenterThan(IPosition iposition, double d0) {
        return this.distToCenterSqr(iposition) < MathHelper.square(d0);
    }

    public double distSqr(BaseBlockPosition baseblockposition) {
        return this.distToLowCornerSqr((double) baseblockposition.getX(), (double) baseblockposition.getY(), (double) baseblockposition.getZ());
    }

    public double distToCenterSqr(IPosition iposition) {
        return this.distToCenterSqr(iposition.x(), iposition.y(), iposition.z());
    }

    public double distToCenterSqr(double d0, double d1, double d2) {
        double d3 = (double) this.getX() + 0.5D - d0;
        double d4 = (double) this.getY() + 0.5D - d1;
        double d5 = (double) this.getZ() + 0.5D - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double distToLowCornerSqr(double d0, double d1, double d2) {
        double d3 = (double) this.getX() - d0;
        double d4 = (double) this.getY() - d1;
        double d5 = (double) this.getZ() - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public int distManhattan(BaseBlockPosition baseblockposition) {
        float f = (float) Math.abs(baseblockposition.getX() - this.getX());
        float f1 = (float) Math.abs(baseblockposition.getY() - this.getY());
        float f2 = (float) Math.abs(baseblockposition.getZ() - this.getZ());

        return (int) (f + f1 + f2);
    }

    public int get(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.choose(this.x, this.y, this.z);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String toShortString() {
        int i = this.getX();

        return i + ", " + this.getY() + ", " + this.getZ();
    }
}

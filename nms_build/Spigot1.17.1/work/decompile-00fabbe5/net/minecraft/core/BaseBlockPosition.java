package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.SystemUtils;
import net.minecraft.util.MathHelper;

@Immutable
public class BaseBlockPosition implements Comparable<BaseBlockPosition> {

    public static final Codec<BaseBlockPosition> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.a(intstream, 3).map((aint) -> {
            return new BaseBlockPosition(aint[0], aint[1], aint[2]);
        });
    }, (baseblockposition) -> {
        return IntStream.of(new int[]{baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ()});
    });
    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public BaseBlockPosition(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public BaseBlockPosition(double d0, double d1, double d2) {
        this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
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

    protected BaseBlockPosition u(int i) {
        this.x = i;
        return this;
    }

    protected BaseBlockPosition t(int i) {
        this.y = i;
        return this;
    }

    protected BaseBlockPosition s(int i) {
        this.z = i;
        return this;
    }

    public BaseBlockPosition b(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BaseBlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    public BaseBlockPosition c(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BaseBlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BaseBlockPosition f(BaseBlockPosition baseblockposition) {
        return this.c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BaseBlockPosition e(BaseBlockPosition baseblockposition) {
        return this.c(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    public BaseBlockPosition o(int i) {
        return i == 1 ? this : (i == 0 ? BaseBlockPosition.ZERO : new BaseBlockPosition(this.getX() * i, this.getY() * i, this.getZ() * i));
    }

    public BaseBlockPosition up() {
        return this.up(1);
    }

    public BaseBlockPosition up(int i) {
        return this.shift(EnumDirection.UP, i);
    }

    public BaseBlockPosition down() {
        return this.down(1);
    }

    public BaseBlockPosition down(int i) {
        return this.shift(EnumDirection.DOWN, i);
    }

    public BaseBlockPosition north() {
        return this.north(1);
    }

    public BaseBlockPosition north(int i) {
        return this.shift(EnumDirection.NORTH, i);
    }

    public BaseBlockPosition south() {
        return this.south(1);
    }

    public BaseBlockPosition south(int i) {
        return this.shift(EnumDirection.SOUTH, i);
    }

    public BaseBlockPosition west() {
        return this.west(1);
    }

    public BaseBlockPosition west(int i) {
        return this.shift(EnumDirection.WEST, i);
    }

    public BaseBlockPosition east() {
        return this.east(1);
    }

    public BaseBlockPosition east(int i) {
        return this.shift(EnumDirection.EAST, i);
    }

    public BaseBlockPosition shift(EnumDirection enumdirection) {
        return this.shift(enumdirection, 1);
    }

    public BaseBlockPosition shift(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BaseBlockPosition(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
    }

    public BaseBlockPosition b(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        if (i == 0) {
            return this;
        } else {
            int j = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? i : 0;
            int k = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? i : 0;
            int l = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? i : 0;

            return new BaseBlockPosition(this.getX() + j, this.getY() + k, this.getZ() + l);
        }
    }

    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return new BaseBlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public boolean a(BaseBlockPosition baseblockposition, double d0) {
        return this.distanceSquared((double) baseblockposition.getX(), (double) baseblockposition.getY(), (double) baseblockposition.getZ(), false) < d0 * d0;
    }

    public boolean a(IPosition iposition, double d0) {
        return this.distanceSquared(iposition.getX(), iposition.getY(), iposition.getZ(), true) < d0 * d0;
    }

    public double j(BaseBlockPosition baseblockposition) {
        return this.distanceSquared((double) baseblockposition.getX(), (double) baseblockposition.getY(), (double) baseblockposition.getZ(), true);
    }

    public double a(IPosition iposition, boolean flag) {
        return this.distanceSquared(iposition.getX(), iposition.getY(), iposition.getZ(), flag);
    }

    public double a(BaseBlockPosition baseblockposition, boolean flag) {
        return this.distanceSquared((double) baseblockposition.x, (double) baseblockposition.y, (double) baseblockposition.z, flag);
    }

    public double distanceSquared(double d0, double d1, double d2, boolean flag) {
        double d3 = flag ? 0.5D : 0.0D;
        double d4 = (double) this.getX() + d3 - d0;
        double d5 = (double) this.getY() + d3 - d1;
        double d6 = (double) this.getZ() + d3 - d2;

        return d4 * d4 + d5 * d5 + d6 * d6;
    }

    public int k(BaseBlockPosition baseblockposition) {
        float f = (float) Math.abs(baseblockposition.getX() - this.getX());
        float f1 = (float) Math.abs(baseblockposition.getY() - this.getY());
        float f2 = (float) Math.abs(baseblockposition.getZ() - this.getZ());

        return (int) (f + f1 + f2);
    }

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.x, this.y, this.z);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String x() {
        int i = this.getX();

        return i + ", " + this.getY() + ", " + this.getZ();
    }
}

package net.minecraft.world.phys;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public class Vec3D implements IPosition {

    public static final Codec<Vec3D> CODEC = Codec.DOUBLE.listOf().comapFlatMap((list) -> {
        return SystemUtils.fixedSize(list, 3).map((list1) -> {
            return new Vec3D((Double) list1.get(0), (Double) list1.get(1), (Double) list1.get(2));
        });
    }, (vec3d) -> {
        return List.of(vec3d.x(), vec3d.y(), vec3d.z());
    });
    public static final Vec3D ZERO = new Vec3D(0.0D, 0.0D, 0.0D);
    public final double x;
    public final double y;
    public final double z;

    public static Vec3D fromRGB24(int i) {
        double d0 = (double) (i >> 16 & 255) / 255.0D;
        double d1 = (double) (i >> 8 & 255) / 255.0D;
        double d2 = (double) (i & 255) / 255.0D;

        return new Vec3D(d0, d1, d2);
    }

    public static Vec3D atLowerCornerOf(BaseBlockPosition baseblockposition) {
        return new Vec3D((double) baseblockposition.getX(), (double) baseblockposition.getY(), (double) baseblockposition.getZ());
    }

    public static Vec3D atLowerCornerWithOffset(BaseBlockPosition baseblockposition, double d0, double d1, double d2) {
        return new Vec3D((double) baseblockposition.getX() + d0, (double) baseblockposition.getY() + d1, (double) baseblockposition.getZ() + d2);
    }

    public static Vec3D atCenterOf(BaseBlockPosition baseblockposition) {
        return atLowerCornerWithOffset(baseblockposition, 0.5D, 0.5D, 0.5D);
    }

    public static Vec3D atBottomCenterOf(BaseBlockPosition baseblockposition) {
        return atLowerCornerWithOffset(baseblockposition, 0.5D, 0.0D, 0.5D);
    }

    public static Vec3D upFromBottomCenterOf(BaseBlockPosition baseblockposition, double d0) {
        return atLowerCornerWithOffset(baseblockposition, 0.5D, d0, 0.5D);
    }

    public Vec3D(double d0, double d1, double d2) {
        this.x = d0;
        this.y = d1;
        this.z = d2;
    }

    public Vec3D(Vector3f vector3f) {
        this((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z());
    }

    public Vec3D vectorTo(Vec3D vec3d) {
        return new Vec3D(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z);
    }

    public Vec3D normalize() {
        double d0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);

        return d0 < 1.0E-4D ? Vec3D.ZERO : new Vec3D(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dot(Vec3D vec3d) {
        return this.x * vec3d.x + this.y * vec3d.y + this.z * vec3d.z;
    }

    public Vec3D cross(Vec3D vec3d) {
        return new Vec3D(this.y * vec3d.z - this.z * vec3d.y, this.z * vec3d.x - this.x * vec3d.z, this.x * vec3d.y - this.y * vec3d.x);
    }

    public Vec3D subtract(Vec3D vec3d) {
        return this.subtract(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3D subtract(double d0, double d1, double d2) {
        return this.add(-d0, -d1, -d2);
    }

    public Vec3D add(Vec3D vec3d) {
        return this.add(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3D add(double d0, double d1, double d2) {
        return new Vec3D(this.x + d0, this.y + d1, this.z + d2);
    }

    public boolean closerThan(IPosition iposition, double d0) {
        return this.distanceToSqr(iposition.x(), iposition.y(), iposition.z()) < d0 * d0;
    }

    public double distanceTo(Vec3D vec3d) {
        double d0 = vec3d.x - this.x;
        double d1 = vec3d.y - this.y;
        double d2 = vec3d.z - this.z;

        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceToSqr(Vec3D vec3d) {
        double d0 = vec3d.x - this.x;
        double d1 = vec3d.y - this.y;
        double d2 = vec3d.z - this.z;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(double d0, double d1, double d2) {
        double d3 = d0 - this.x;
        double d4 = d1 - this.y;
        double d5 = d2 - this.z;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public Vec3D scale(double d0) {
        return this.multiply(d0, d0, d0);
    }

    public Vec3D reverse() {
        return this.scale(-1.0D);
    }

    public Vec3D multiply(Vec3D vec3d) {
        return this.multiply(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3D multiply(double d0, double d1, double d2) {
        return new Vec3D(this.x * d0, this.y * d1, this.z * d2);
    }

    public Vec3D offsetRandom(RandomSource randomsource, float f) {
        return this.add((double) ((randomsource.nextFloat() - 0.5F) * f), (double) ((randomsource.nextFloat() - 0.5F) * f), (double) ((randomsource.nextFloat() - 0.5F) * f));
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x + this.z * this.z;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Vec3D)) {
            return false;
        } else {
            Vec3D vec3d = (Vec3D) object;

            return Double.compare(vec3d.x, this.x) != 0 ? false : (Double.compare(vec3d.y, this.y) != 0 ? false : Double.compare(vec3d.z, this.z) == 0);
        }
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.x);
        int j = (int) (i ^ i >>> 32);

        i = Double.doubleToLongBits(this.y);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.z);
        j = 31 * j + (int) (i ^ i >>> 32);
        return j;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3D lerp(Vec3D vec3d, double d0) {
        return new Vec3D(MathHelper.lerp(d0, this.x, vec3d.x), MathHelper.lerp(d0, this.y, vec3d.y), MathHelper.lerp(d0, this.z, vec3d.z));
    }

    public Vec3D xRot(float f) {
        float f1 = MathHelper.cos(f);
        float f2 = MathHelper.sin(f);
        double d0 = this.x;
        double d1 = this.y * (double) f1 + this.z * (double) f2;
        double d2 = this.z * (double) f1 - this.y * (double) f2;

        return new Vec3D(d0, d1, d2);
    }

    public Vec3D yRot(float f) {
        float f1 = MathHelper.cos(f);
        float f2 = MathHelper.sin(f);
        double d0 = this.x * (double) f1 + this.z * (double) f2;
        double d1 = this.y;
        double d2 = this.z * (double) f1 - this.x * (double) f2;

        return new Vec3D(d0, d1, d2);
    }

    public Vec3D zRot(float f) {
        float f1 = MathHelper.cos(f);
        float f2 = MathHelper.sin(f);
        double d0 = this.x * (double) f1 + this.y * (double) f2;
        double d1 = this.y * (double) f1 - this.x * (double) f2;
        double d2 = this.z;

        return new Vec3D(d0, d1, d2);
    }

    public static Vec3D directionFromRotation(Vec2F vec2f) {
        return directionFromRotation(vec2f.x, vec2f.y);
    }

    public static Vec3D directionFromRotation(float f, float f1) {
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);

        return new Vec3D((double) (f3 * f4), (double) f5, (double) (f2 * f4));
    }

    public Vec3D align(EnumSet<EnumDirection.EnumAxis> enumset) {
        double d0 = enumset.contains(EnumDirection.EnumAxis.X) ? (double) MathHelper.floor(this.x) : this.x;
        double d1 = enumset.contains(EnumDirection.EnumAxis.Y) ? (double) MathHelper.floor(this.y) : this.y;
        double d2 = enumset.contains(EnumDirection.EnumAxis.Z) ? (double) MathHelper.floor(this.z) : this.z;

        return new Vec3D(d0, d1, d2);
    }

    public double get(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.choose(this.x, this.y, this.z);
    }

    public Vec3D with(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        double d1 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? d0 : this.x;
        double d2 = enumdirection_enumaxis == EnumDirection.EnumAxis.Y ? d0 : this.y;
        double d3 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? d0 : this.z;

        return new Vec3D(d1, d2, d3);
    }

    public Vec3D relative(EnumDirection enumdirection, double d0) {
        BaseBlockPosition baseblockposition = enumdirection.getNormal();

        return new Vec3D(this.x + d0 * (double) baseblockposition.getX(), this.y + d0 * (double) baseblockposition.getY(), this.z + d0 * (double) baseblockposition.getZ());
    }

    @Override
    public final double x() {
        return this.x;
    }

    @Override
    public final double y() {
        return this.y;
    }

    @Override
    public final double z() {
        return this.z;
    }

    public Vector3f toVector3f() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }
}

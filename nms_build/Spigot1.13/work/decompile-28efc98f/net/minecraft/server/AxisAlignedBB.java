package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class AxisAlignedBB {

    public final double a;
    public final double b;
    public final double c;
    public final double d;
    public final double e;
    public final double f;

    public AxisAlignedBB(double d0, double d1, double d2, double d3, double d4, double d5) {
        this.a = Math.min(d0, d3);
        this.b = Math.min(d1, d4);
        this.c = Math.min(d2, d5);
        this.d = Math.max(d0, d3);
        this.e = Math.max(d1, d4);
        this.f = Math.max(d2, d5);
    }

    public AxisAlignedBB(BlockPosition blockposition) {
        this((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1));
    }

    public AxisAlignedBB(BlockPosition blockposition, BlockPosition blockposition1) {
        this((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
    }

    public double a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.a, this.b, this.c);
    }

    public double b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.d, this.e, this.f);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof AxisAlignedBB)) {
            return false;
        } else {
            AxisAlignedBB axisalignedbb = (AxisAlignedBB) object;

            return Double.compare(axisalignedbb.a, this.a) != 0 ? false : (Double.compare(axisalignedbb.b, this.b) != 0 ? false : (Double.compare(axisalignedbb.c, this.c) != 0 ? false : (Double.compare(axisalignedbb.d, this.d) != 0 ? false : (Double.compare(axisalignedbb.e, this.e) != 0 ? false : Double.compare(axisalignedbb.f, this.f) == 0))));
        }
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.a);
        int j = (int) (i ^ i >>> 32);

        i = Double.doubleToLongBits(this.b);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.c);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.d);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.e);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.f);
        j = 31 * j + (int) (i ^ i >>> 32);
        return j;
    }

    public AxisAlignedBB a(double d0, double d1, double d2) {
        double d3 = this.a;
        double d4 = this.b;
        double d5 = this.c;
        double d6 = this.d;
        double d7 = this.e;
        double d8 = this.f;

        if (d0 < 0.0D) {
            d3 -= d0;
        } else if (d0 > 0.0D) {
            d6 -= d0;
        }

        if (d1 < 0.0D) {
            d4 -= d1;
        } else if (d1 > 0.0D) {
            d7 -= d1;
        }

        if (d2 < 0.0D) {
            d5 -= d2;
        } else if (d2 > 0.0D) {
            d8 -= d2;
        }

        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB b(double d0, double d1, double d2) {
        double d3 = this.a;
        double d4 = this.b;
        double d5 = this.c;
        double d6 = this.d;
        double d7 = this.e;
        double d8 = this.f;

        if (d0 < 0.0D) {
            d3 += d0;
        } else if (d0 > 0.0D) {
            d6 += d0;
        }

        if (d1 < 0.0D) {
            d4 += d1;
        } else if (d1 > 0.0D) {
            d7 += d1;
        }

        if (d2 < 0.0D) {
            d5 += d2;
        } else if (d2 > 0.0D) {
            d8 += d2;
        }

        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB grow(double d0, double d1, double d2) {
        double d3 = this.a - d0;
        double d4 = this.b - d1;
        double d5 = this.c - d2;
        double d6 = this.d + d0;
        double d7 = this.e + d1;
        double d8 = this.f + d2;

        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB g(double d0) {
        return this.grow(d0, d0, d0);
    }

    public AxisAlignedBB a(AxisAlignedBB axisalignedbb) {
        double d0 = Math.max(this.a, axisalignedbb.a);
        double d1 = Math.max(this.b, axisalignedbb.b);
        double d2 = Math.max(this.c, axisalignedbb.c);
        double d3 = Math.min(this.d, axisalignedbb.d);
        double d4 = Math.min(this.e, axisalignedbb.e);
        double d5 = Math.min(this.f, axisalignedbb.f);

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB b(AxisAlignedBB axisalignedbb) {
        double d0 = Math.min(this.a, axisalignedbb.a);
        double d1 = Math.min(this.b, axisalignedbb.b);
        double d2 = Math.min(this.c, axisalignedbb.c);
        double d3 = Math.max(this.d, axisalignedbb.d);
        double d4 = Math.max(this.e, axisalignedbb.e);
        double d5 = Math.max(this.f, axisalignedbb.f);

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB d(double d0, double d1, double d2) {
        return new AxisAlignedBB(this.a + d0, this.b + d1, this.c + d2, this.d + d0, this.e + d1, this.f + d2);
    }

    public AxisAlignedBB a(BlockPosition blockposition) {
        return new AxisAlignedBB(this.a + (double) blockposition.getX(), this.b + (double) blockposition.getY(), this.c + (double) blockposition.getZ(), this.d + (double) blockposition.getX(), this.e + (double) blockposition.getY(), this.f + (double) blockposition.getZ());
    }

    public AxisAlignedBB a(Vec3D vec3d) {
        return this.d(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean c(AxisAlignedBB axisalignedbb) {
        return this.a(axisalignedbb.a, axisalignedbb.b, axisalignedbb.c, axisalignedbb.d, axisalignedbb.e, axisalignedbb.f);
    }

    public boolean a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return this.a < d3 && this.d > d0 && this.b < d4 && this.e > d1 && this.c < d5 && this.f > d2;
    }

    public boolean b(Vec3D vec3d) {
        return this.e(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean e(double d0, double d1, double d2) {
        return d0 >= this.a && d0 < this.d && d1 >= this.b && d1 < this.e && d2 >= this.c && d2 < this.f;
    }

    public double a() {
        double d0 = this.d - this.a;
        double d1 = this.e - this.b;
        double d2 = this.f - this.c;

        return (d0 + d1 + d2) / 3.0D;
    }

    public AxisAlignedBB f(double d0, double d1, double d2) {
        return this.grow(-d0, -d1, -d2);
    }

    public AxisAlignedBB shrink(double d0) {
        return this.g(-d0);
    }

    @Nullable
    public MovingObjectPosition b(Vec3D vec3d, Vec3D vec3d1) {
        return this.a(vec3d, vec3d1, (BlockPosition) null);
    }

    @Nullable
    public MovingObjectPosition a(Vec3D vec3d, Vec3D vec3d1, @Nullable BlockPosition blockposition) {
        double[] adouble = new double[] { 1.0D};
        EnumDirection enumdirection = null;
        double d0 = vec3d1.x - vec3d.x;
        double d1 = vec3d1.y - vec3d.y;
        double d2 = vec3d1.z - vec3d.z;

        enumdirection = a(blockposition == null ? this : this.a(blockposition), vec3d, adouble, enumdirection, d0, d1, d2);
        if (enumdirection == null) {
            return null;
        } else {
            double d3 = adouble[0];

            return new MovingObjectPosition(vec3d.add(d3 * d0, d3 * d1, d3 * d2), enumdirection, blockposition == null ? BlockPosition.ZERO : blockposition);
        }
    }

    @Nullable
    public static MovingObjectPosition a(Iterable<AxisAlignedBB> iterable, Vec3D vec3d, Vec3D vec3d1, BlockPosition blockposition) {
        double[] adouble = new double[] { 1.0D};
        EnumDirection enumdirection = null;
        double d0 = vec3d1.x - vec3d.x;
        double d1 = vec3d1.y - vec3d.y;
        double d2 = vec3d1.z - vec3d.z;

        AxisAlignedBB axisalignedbb;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); enumdirection = a(axisalignedbb.a(blockposition), vec3d, adouble, enumdirection, d0, d1, d2)) {
            axisalignedbb = (AxisAlignedBB) iterator.next();
        }

        if (enumdirection == null) {
            return null;
        } else {
            double d3 = adouble[0];

            return new MovingObjectPosition(vec3d.add(d3 * d0, d3 * d1, d3 * d2), enumdirection, blockposition);
        }
    }

    @Nullable
    private static EnumDirection a(AxisAlignedBB axisalignedbb, Vec3D vec3d, double[] adouble, @Nullable EnumDirection enumdirection, double d0, double d1, double d2) {
        if (d0 > 1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d0, d1, d2, axisalignedbb.a, axisalignedbb.b, axisalignedbb.e, axisalignedbb.c, axisalignedbb.f, EnumDirection.WEST, vec3d.x, vec3d.y, vec3d.z);
        } else if (d0 < -1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d0, d1, d2, axisalignedbb.d, axisalignedbb.b, axisalignedbb.e, axisalignedbb.c, axisalignedbb.f, EnumDirection.EAST, vec3d.x, vec3d.y, vec3d.z);
        }

        if (d1 > 1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d1, d2, d0, axisalignedbb.b, axisalignedbb.c, axisalignedbb.f, axisalignedbb.a, axisalignedbb.d, EnumDirection.DOWN, vec3d.y, vec3d.z, vec3d.x);
        } else if (d1 < -1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d1, d2, d0, axisalignedbb.e, axisalignedbb.c, axisalignedbb.f, axisalignedbb.a, axisalignedbb.d, EnumDirection.UP, vec3d.y, vec3d.z, vec3d.x);
        }

        if (d2 > 1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d2, d0, d1, axisalignedbb.c, axisalignedbb.a, axisalignedbb.d, axisalignedbb.b, axisalignedbb.e, EnumDirection.NORTH, vec3d.z, vec3d.x, vec3d.y);
        } else if (d2 < -1.0E-7D) {
            enumdirection = a(adouble, enumdirection, d2, d0, d1, axisalignedbb.f, axisalignedbb.a, axisalignedbb.d, axisalignedbb.b, axisalignedbb.e, EnumDirection.SOUTH, vec3d.z, vec3d.x, vec3d.y);
        }

        return enumdirection;
    }

    @Nullable
    private static EnumDirection a(double[] adouble, @Nullable EnumDirection enumdirection, double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, EnumDirection enumdirection1, double d8, double d9, double d10) {
        double d11 = (d3 - d8) / d0;
        double d12 = d9 + d11 * d1;
        double d13 = d10 + d11 * d2;

        if (0.0D < d11 && d11 < adouble[0] && d4 - 1.0E-7D < d12 && d12 < d5 + 1.0E-7D && d6 - 1.0E-7D < d13 && d13 < d7 + 1.0E-7D) {
            adouble[0] = d11;
            return enumdirection1;
        } else {
            return enumdirection;
        }
    }

    public String toString() {
        return "box[" + this.a + ", " + this.b + ", " + this.c + " -> " + this.d + ", " + this.e + ", " + this.f + "]";
    }
}

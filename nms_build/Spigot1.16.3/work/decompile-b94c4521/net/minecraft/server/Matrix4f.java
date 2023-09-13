package net.minecraft.server;

public final class Matrix4f {

    protected float a;
    protected float b;
    protected float c;
    protected float d;
    protected float e;
    protected float f;
    protected float g;
    protected float h;
    protected float i;
    protected float j;
    protected float k;
    protected float l;
    protected float m;
    protected float n;
    protected float o;
    protected float p;

    public Matrix4f() {}

    public Matrix4f(Matrix4f matrix4f) {
        this.a = matrix4f.a;
        this.b = matrix4f.b;
        this.c = matrix4f.c;
        this.d = matrix4f.d;
        this.e = matrix4f.e;
        this.f = matrix4f.f;
        this.g = matrix4f.g;
        this.h = matrix4f.h;
        this.i = matrix4f.i;
        this.j = matrix4f.j;
        this.k = matrix4f.k;
        this.l = matrix4f.l;
        this.m = matrix4f.m;
        this.n = matrix4f.n;
        this.o = matrix4f.o;
        this.p = matrix4f.p;
    }

    public Matrix4f(Quaternion quaternion) {
        float f = quaternion.a();
        float f1 = quaternion.b();
        float f2 = quaternion.c();
        float f3 = quaternion.d();
        float f4 = 2.0F * f * f;
        float f5 = 2.0F * f1 * f1;
        float f6 = 2.0F * f2 * f2;

        this.a = 1.0F - f5 - f6;
        this.f = 1.0F - f6 - f4;
        this.k = 1.0F - f4 - f5;
        this.p = 1.0F;
        float f7 = f * f1;
        float f8 = f1 * f2;
        float f9 = f2 * f;
        float f10 = f * f3;
        float f11 = f1 * f3;
        float f12 = f2 * f3;

        this.e = 2.0F * (f7 + f12);
        this.b = 2.0F * (f7 - f12);
        this.i = 2.0F * (f9 - f11);
        this.c = 2.0F * (f9 + f11);
        this.j = 2.0F * (f8 + f10);
        this.g = 2.0F * (f8 - f10);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Matrix4f matrix4f = (Matrix4f) object;

            return Float.compare(matrix4f.a, this.a) == 0 && Float.compare(matrix4f.b, this.b) == 0 && Float.compare(matrix4f.c, this.c) == 0 && Float.compare(matrix4f.d, this.d) == 0 && Float.compare(matrix4f.e, this.e) == 0 && Float.compare(matrix4f.f, this.f) == 0 && Float.compare(matrix4f.g, this.g) == 0 && Float.compare(matrix4f.h, this.h) == 0 && Float.compare(matrix4f.i, this.i) == 0 && Float.compare(matrix4f.j, this.j) == 0 && Float.compare(matrix4f.k, this.k) == 0 && Float.compare(matrix4f.l, this.l) == 0 && Float.compare(matrix4f.m, this.m) == 0 && Float.compare(matrix4f.n, this.n) == 0 && Float.compare(matrix4f.o, this.o) == 0 && Float.compare(matrix4f.p, this.p) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.a != 0.0F ? Float.floatToIntBits(this.a) : 0;

        i = 31 * i + (this.b != 0.0F ? Float.floatToIntBits(this.b) : 0);
        i = 31 * i + (this.c != 0.0F ? Float.floatToIntBits(this.c) : 0);
        i = 31 * i + (this.d != 0.0F ? Float.floatToIntBits(this.d) : 0);
        i = 31 * i + (this.e != 0.0F ? Float.floatToIntBits(this.e) : 0);
        i = 31 * i + (this.f != 0.0F ? Float.floatToIntBits(this.f) : 0);
        i = 31 * i + (this.g != 0.0F ? Float.floatToIntBits(this.g) : 0);
        i = 31 * i + (this.h != 0.0F ? Float.floatToIntBits(this.h) : 0);
        i = 31 * i + (this.i != 0.0F ? Float.floatToIntBits(this.i) : 0);
        i = 31 * i + (this.j != 0.0F ? Float.floatToIntBits(this.j) : 0);
        i = 31 * i + (this.k != 0.0F ? Float.floatToIntBits(this.k) : 0);
        i = 31 * i + (this.l != 0.0F ? Float.floatToIntBits(this.l) : 0);
        i = 31 * i + (this.m != 0.0F ? Float.floatToIntBits(this.m) : 0);
        i = 31 * i + (this.n != 0.0F ? Float.floatToIntBits(this.n) : 0);
        i = 31 * i + (this.o != 0.0F ? Float.floatToIntBits(this.o) : 0);
        i = 31 * i + (this.p != 0.0F ? Float.floatToIntBits(this.p) : 0);
        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("Matrix4f:\n");
        stringbuilder.append(this.a);
        stringbuilder.append(" ");
        stringbuilder.append(this.b);
        stringbuilder.append(" ");
        stringbuilder.append(this.c);
        stringbuilder.append(" ");
        stringbuilder.append(this.d);
        stringbuilder.append("\n");
        stringbuilder.append(this.e);
        stringbuilder.append(" ");
        stringbuilder.append(this.f);
        stringbuilder.append(" ");
        stringbuilder.append(this.g);
        stringbuilder.append(" ");
        stringbuilder.append(this.h);
        stringbuilder.append("\n");
        stringbuilder.append(this.i);
        stringbuilder.append(" ");
        stringbuilder.append(this.j);
        stringbuilder.append(" ");
        stringbuilder.append(this.k);
        stringbuilder.append(" ");
        stringbuilder.append(this.l);
        stringbuilder.append("\n");
        stringbuilder.append(this.m);
        stringbuilder.append(" ");
        stringbuilder.append(this.n);
        stringbuilder.append(" ");
        stringbuilder.append(this.o);
        stringbuilder.append(" ");
        stringbuilder.append(this.p);
        stringbuilder.append("\n");
        return stringbuilder.toString();
    }
}

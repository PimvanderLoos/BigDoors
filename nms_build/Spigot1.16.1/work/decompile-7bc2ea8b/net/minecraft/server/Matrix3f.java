package net.minecraft.server;

public final class Matrix3f {

    private static final float j = 3.0F + 2.0F * (float) Math.sqrt(2.0D);
    private static final float k = (float) Math.cos(0.39269908169872414D);
    private static final float l = (float) Math.sin(0.39269908169872414D);
    private static final float m = 1.0F / (float) Math.sqrt(2.0D);
    protected float a;
    protected float b;
    protected float c;
    protected float d;
    protected float e;
    protected float f;
    protected float g;
    protected float h;
    protected float i;

    public Matrix3f() {}

    public Matrix3f(Quaternion quaternion) {
        float f = quaternion.a();
        float f1 = quaternion.b();
        float f2 = quaternion.c();
        float f3 = quaternion.d();
        float f4 = 2.0F * f * f;
        float f5 = 2.0F * f1 * f1;
        float f6 = 2.0F * f2 * f2;

        this.a = 1.0F - f5 - f6;
        this.e = 1.0F - f6 - f4;
        this.i = 1.0F - f4 - f5;
        float f7 = f * f1;
        float f8 = f1 * f2;
        float f9 = f2 * f;
        float f10 = f * f3;
        float f11 = f1 * f3;
        float f12 = f2 * f3;

        this.d = 2.0F * (f7 + f12);
        this.b = 2.0F * (f7 - f12);
        this.g = 2.0F * (f9 - f11);
        this.c = 2.0F * (f9 + f11);
        this.h = 2.0F * (f8 + f10);
        this.f = 2.0F * (f8 - f10);
    }

    public Matrix3f(Matrix4f matrix4f) {
        this.a = matrix4f.a;
        this.b = matrix4f.b;
        this.c = matrix4f.c;
        this.d = matrix4f.e;
        this.e = matrix4f.f;
        this.f = matrix4f.g;
        this.g = matrix4f.i;
        this.h = matrix4f.j;
        this.i = matrix4f.k;
    }

    public Matrix3f(Matrix3f matrix3f) {
        this.a = matrix3f.a;
        this.b = matrix3f.b;
        this.c = matrix3f.c;
        this.d = matrix3f.d;
        this.e = matrix3f.e;
        this.f = matrix3f.f;
        this.g = matrix3f.g;
        this.h = matrix3f.h;
        this.i = matrix3f.i;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Matrix3f matrix3f = (Matrix3f) object;

            return Float.compare(matrix3f.a, this.a) == 0 && Float.compare(matrix3f.b, this.b) == 0 && Float.compare(matrix3f.c, this.c) == 0 && Float.compare(matrix3f.d, this.d) == 0 && Float.compare(matrix3f.e, this.e) == 0 && Float.compare(matrix3f.f, this.f) == 0 && Float.compare(matrix3f.g, this.g) == 0 && Float.compare(matrix3f.h, this.h) == 0 && Float.compare(matrix3f.i, this.i) == 0;
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
        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("Matrix3f:\n");
        stringbuilder.append(this.a);
        stringbuilder.append(" ");
        stringbuilder.append(this.b);
        stringbuilder.append(" ");
        stringbuilder.append(this.c);
        stringbuilder.append("\n");
        stringbuilder.append(this.d);
        stringbuilder.append(" ");
        stringbuilder.append(this.e);
        stringbuilder.append(" ");
        stringbuilder.append(this.f);
        stringbuilder.append("\n");
        stringbuilder.append(this.g);
        stringbuilder.append(" ");
        stringbuilder.append(this.h);
        stringbuilder.append(" ");
        stringbuilder.append(this.i);
        stringbuilder.append("\n");
        return stringbuilder.toString();
    }

    public void a(int i, int j, float f) {
        if (i == 0) {
            if (j == 0) {
                this.a = f;
            } else if (j == 1) {
                this.b = f;
            } else {
                this.c = f;
            }
        } else if (i == 1) {
            if (j == 0) {
                this.d = f;
            } else if (j == 1) {
                this.e = f;
            } else {
                this.f = f;
            }
        } else if (j == 0) {
            this.g = f;
        } else if (j == 1) {
            this.h = f;
        } else {
            this.i = f;
        }

    }

    public void b(Matrix3f matrix3f) {
        float f = this.a * matrix3f.a + this.b * matrix3f.d + this.c * matrix3f.g;
        float f1 = this.a * matrix3f.b + this.b * matrix3f.e + this.c * matrix3f.h;
        float f2 = this.a * matrix3f.c + this.b * matrix3f.f + this.c * matrix3f.i;
        float f3 = this.d * matrix3f.a + this.e * matrix3f.d + this.f * matrix3f.g;
        float f4 = this.d * matrix3f.b + this.e * matrix3f.e + this.f * matrix3f.h;
        float f5 = this.d * matrix3f.c + this.e * matrix3f.f + this.f * matrix3f.i;
        float f6 = this.g * matrix3f.a + this.h * matrix3f.d + this.i * matrix3f.g;
        float f7 = this.g * matrix3f.b + this.h * matrix3f.e + this.i * matrix3f.h;
        float f8 = this.g * matrix3f.c + this.h * matrix3f.f + this.i * matrix3f.i;

        this.a = f;
        this.b = f1;
        this.c = f2;
        this.d = f3;
        this.e = f4;
        this.f = f5;
        this.g = f6;
        this.h = f7;
        this.i = f8;
    }
}

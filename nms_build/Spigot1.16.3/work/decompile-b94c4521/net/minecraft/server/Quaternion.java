package net.minecraft.server;

public final class Quaternion {

    public static final Quaternion a = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private float b;
    private float c;
    private float d;
    private float e;

    public Quaternion(float f, float f1, float f2, float f3) {
        this.b = f;
        this.c = f1;
        this.d = f2;
        this.e = f3;
    }

    public Quaternion(Vector3fa vector3fa, float f, boolean flag) {
        if (flag) {
            f *= 0.017453292F;
        }

        float f1 = c(f / 2.0F);

        this.b = vector3fa.a() * f1;
        this.c = vector3fa.b() * f1;
        this.d = vector3fa.c() * f1;
        this.e = b(f / 2.0F);
    }

    public Quaternion(Quaternion quaternion) {
        this.b = quaternion.b;
        this.c = quaternion.c;
        this.d = quaternion.d;
        this.e = quaternion.e;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Quaternion quaternion = (Quaternion) object;

            return Float.compare(quaternion.b, this.b) != 0 ? false : (Float.compare(quaternion.c, this.c) != 0 ? false : (Float.compare(quaternion.d, this.d) != 0 ? false : Float.compare(quaternion.e, this.e) == 0));
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.b);

        i = 31 * i + Float.floatToIntBits(this.c);
        i = 31 * i + Float.floatToIntBits(this.d);
        i = 31 * i + Float.floatToIntBits(this.e);
        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("Quaternion[").append(this.d()).append(" + ");
        stringbuilder.append(this.a()).append("i + ");
        stringbuilder.append(this.b()).append("j + ");
        stringbuilder.append(this.c()).append("k]");
        return stringbuilder.toString();
    }

    public float a() {
        return this.b;
    }

    public float b() {
        return this.c;
    }

    public float c() {
        return this.d;
    }

    public float d() {
        return this.e;
    }

    public void a(Quaternion quaternion) {
        float f = this.a();
        float f1 = this.b();
        float f2 = this.c();
        float f3 = this.d();
        float f4 = quaternion.a();
        float f5 = quaternion.b();
        float f6 = quaternion.c();
        float f7 = quaternion.d();

        this.b = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
        this.c = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
        this.d = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
        this.e = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
    }

    public void e() {
        this.b = -this.b;
        this.c = -this.c;
        this.d = -this.d;
    }

    private static float b(float f) {
        return (float) Math.cos((double) f);
    }

    private static float c(float f) {
        return (float) Math.sin((double) f);
    }
}

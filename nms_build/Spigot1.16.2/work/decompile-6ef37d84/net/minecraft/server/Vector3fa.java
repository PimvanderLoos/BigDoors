package net.minecraft.server;

public final class Vector3fa {

    public static Vector3fa a = new Vector3fa(-1.0F, 0.0F, 0.0F);
    public static Vector3fa b = new Vector3fa(1.0F, 0.0F, 0.0F);
    public static Vector3fa c = new Vector3fa(0.0F, -1.0F, 0.0F);
    public static Vector3fa d = new Vector3fa(0.0F, 1.0F, 0.0F);
    public static Vector3fa e = new Vector3fa(0.0F, 0.0F, -1.0F);
    public static Vector3fa f = new Vector3fa(0.0F, 0.0F, 1.0F);
    private float g;
    private float h;
    private float i;

    public Vector3fa() {}

    public Vector3fa(float f, float f1, float f2) {
        this.g = f;
        this.h = f1;
        this.i = f2;
    }

    public Vector3fa(Vec3D vec3d) {
        this((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Vector3fa vector3fa = (Vector3fa) object;

            return Float.compare(vector3fa.g, this.g) != 0 ? false : (Float.compare(vector3fa.h, this.h) != 0 ? false : Float.compare(vector3fa.i, this.i) == 0);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.g);

        i = 31 * i + Float.floatToIntBits(this.h);
        i = 31 * i + Float.floatToIntBits(this.i);
        return i;
    }

    public float a() {
        return this.g;
    }

    public float b() {
        return this.h;
    }

    public float c() {
        return this.i;
    }

    public void a(float f, float f1, float f2) {
        this.g = f;
        this.h = f1;
        this.i = f2;
    }

    public void a(Quaternion quaternion) {
        Quaternion quaternion1 = new Quaternion(quaternion);

        quaternion1.a(new Quaternion(this.a(), this.b(), this.c(), 0.0F));
        Quaternion quaternion2 = new Quaternion(quaternion);

        quaternion2.e();
        quaternion1.a(quaternion2);
        this.a(quaternion1.a(), quaternion1.b(), quaternion1.c());
    }

    public String toString() {
        return "[" + this.g + ", " + this.h + ", " + this.i + "]";
    }
}

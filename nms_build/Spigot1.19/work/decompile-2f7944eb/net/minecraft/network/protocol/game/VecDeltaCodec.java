package net.minecraft.network.protocol.game;

import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.Vec3D;

public class VecDeltaCodec {

    private static final double TRUNCATION_STEPS = 4096.0D;
    private Vec3D base;

    public VecDeltaCodec() {
        this.base = Vec3D.ZERO;
    }

    private static long encode(double d0) {
        return MathHelper.lfloor(d0 * 4096.0D);
    }

    private static double decode(long i) {
        return (double) i / 4096.0D;
    }

    public Vec3D decode(long i, long j, long k) {
        if (i == 0L && j == 0L && k == 0L) {
            return this.base;
        } else {
            double d0 = i == 0L ? this.base.x : decode(encode(this.base.x) + i);
            double d1 = j == 0L ? this.base.y : decode(encode(this.base.y) + j);
            double d2 = k == 0L ? this.base.z : decode(encode(this.base.z) + k);

            return new Vec3D(d0, d1, d2);
        }
    }

    public long encodeX(Vec3D vec3d) {
        return encode(vec3d.x - this.base.x);
    }

    public long encodeY(Vec3D vec3d) {
        return encode(vec3d.y - this.base.y);
    }

    public long encodeZ(Vec3D vec3d) {
        return encode(vec3d.z - this.base.z);
    }

    public Vec3D delta(Vec3D vec3d) {
        return vec3d.subtract(this.base);
    }

    public void setBase(Vec3D vec3d) {
        this.base = vec3d;
    }
}

package net.minecraft.world.entity;

import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntitySize {

    public final float width;
    public final float height;
    public final boolean c;

    public EntitySize(float f, float f1, boolean flag) {
        this.width = f;
        this.height = f1;
        this.c = flag;
    }

    public AxisAlignedBB a(Vec3D vec3d) {
        return this.a(vec3d.x, vec3d.y, vec3d.z);
    }

    public AxisAlignedBB a(double d0, double d1, double d2) {
        float f = this.width / 2.0F;
        float f1 = this.height;

        return new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f);
    }

    public EntitySize a(float f) {
        return this.a(f, f);
    }

    public EntitySize a(float f, float f1) {
        return !this.c && (f != 1.0F || f1 != 1.0F) ? b(this.width * f, this.height * f1) : this;
    }

    public static EntitySize b(float f, float f1) {
        return new EntitySize(f, f1, false);
    }

    public static EntitySize c(float f, float f1) {
        return new EntitySize(f, f1, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.c;
    }
}

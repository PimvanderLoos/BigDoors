package net.minecraft.world.entity;

import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntitySize {

    public final float width;
    public final float height;
    public final boolean fixed;

    public EntitySize(float f, float f1, boolean flag) {
        this.width = f;
        this.height = f1;
        this.fixed = flag;
    }

    public AxisAlignedBB makeBoundingBox(Vec3D vec3d) {
        return this.makeBoundingBox(vec3d.x, vec3d.y, vec3d.z);
    }

    public AxisAlignedBB makeBoundingBox(double d0, double d1, double d2) {
        float f = this.width / 2.0F;
        float f1 = this.height;

        return new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f);
    }

    public EntitySize scale(float f) {
        return this.scale(f, f);
    }

    public EntitySize scale(float f, float f1) {
        return !this.fixed && (f != 1.0F || f1 != 1.0F) ? scalable(this.width * f, this.height * f1) : this;
    }

    public static EntitySize scalable(float f, float f1) {
        return new EntitySize(f, f1, false);
    }

    public static EntitySize fixed(float f, float f1) {
        return new EntitySize(f, f1, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}

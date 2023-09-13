package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class MovingObjectPosition {

    protected final Vec3D pos;

    protected MovingObjectPosition(Vec3D vec3d) {
        this.pos = vec3d;
    }

    public double a(Entity entity) {
        double d0 = this.pos.x - entity.locX();
        double d1 = this.pos.y - entity.locY();
        double d2 = this.pos.z - entity.locZ();

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract MovingObjectPosition.EnumMovingObjectType getType();

    public Vec3D getPos() {
        return this.pos;
    }

    public static enum EnumMovingObjectType {

        MISS, BLOCK, ENTITY;

        private EnumMovingObjectType() {}
    }
}

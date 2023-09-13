package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class MovingObjectPositionEntity extends MovingObjectPosition {

    private final Entity b;

    public MovingObjectPositionEntity(Entity entity) {
        this(entity, entity.getPositionVector());
    }

    public MovingObjectPositionEntity(Entity entity, Vec3D vec3d) {
        super(vec3d);
        this.b = entity;
    }

    public Entity getEntity() {
        return this.b;
    }

    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return MovingObjectPosition.EnumMovingObjectType.ENTITY;
    }
}

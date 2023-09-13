package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3D;

public class FlyingRandomStroll extends BehaviorStrollRandomUnconstrained {

    public FlyingRandomStroll(float f) {
        this(f, true);
    }

    public FlyingRandomStroll(float f, boolean flag) {
        super(f, flag);
    }

    @Override
    protected Vec3D getTargetPos(EntityCreature entitycreature) {
        Vec3D vec3d = entitycreature.getViewVector(0.0F);

        return AirAndWaterRandomPos.getPos(entitycreature, this.maxHorizontalDistance, this.maxVerticalDistance, -2, vec3d.x, vec3d.z, 1.5707963705062866D);
    }
}

package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomStrollLand extends PathfinderGoalRandomStroll {

    public static final float PROBABILITY = 0.001F;
    protected final float probability;

    public PathfinderGoalRandomStrollLand(EntityCreature entitycreature, double d0) {
        this(entitycreature, d0, 0.001F);
    }

    public PathfinderGoalRandomStrollLand(EntityCreature entitycreature, double d0, float f) {
        super(entitycreature, d0);
        this.probability = f;
    }

    @Nullable
    @Override
    protected Vec3D getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3D vec3d = LandRandomPos.getPos(this.mob, 15, 7);

            return vec3d == null ? super.getPosition() : vec3d;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
        }
    }
}

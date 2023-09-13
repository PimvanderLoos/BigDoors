package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomFly extends PathfinderGoalRandomStrollLand {

    public PathfinderGoalRandomFly(EntityCreature entitycreature, double d0) {
        super(entitycreature, d0);
    }

    @Nullable
    @Override
    protected Vec3D getPosition() {
        Vec3D vec3d = this.mob.getViewVector(0.0F);
        boolean flag = true;
        Vec3D vec3d1 = HoverRandomPos.getPos(this.mob, 8, 7, vec3d.x, vec3d.z, 1.5707964F, 3, 1);

        return vec3d1 != null ? vec3d1 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3d.x, vec3d.z, 1.5707963705062866D);
    }
}

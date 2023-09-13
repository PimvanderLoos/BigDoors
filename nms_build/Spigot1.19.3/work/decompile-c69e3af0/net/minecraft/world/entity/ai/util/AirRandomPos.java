package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class AirRandomPos {

    public AirRandomPos() {}

    @Nullable
    public static Vec3D getPosTowards(EntityCreature entitycreature, int i, int j, int k, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.subtract(entitycreature.getX(), entitycreature.getY(), entitycreature.getZ());
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = AirAndWaterRandomPos.generateRandomPos(entitycreature, i, j, k, vec3d1.x, vec3d1.z, d0, flag);

            return blockposition != null && !PathfinderGoalUtil.isWater(entitycreature, blockposition) ? blockposition : null;
        });
    }
}

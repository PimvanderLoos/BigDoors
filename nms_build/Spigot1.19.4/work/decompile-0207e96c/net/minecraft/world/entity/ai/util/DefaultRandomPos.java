package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class DefaultRandomPos {

    public DefaultRandomPos() {}

    @Nullable
    public static Vec3D getPos(EntityCreature entitycreature, int i, int j) {
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirection(entitycreature.getRandom(), i, j);

            return generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    public static Vec3D getPosTowards(EntityCreature entitycreature, int i, int j, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.subtract(entitycreature.getX(), entitycreature.getY(), entitycreature.getZ());
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirectionWithinRadians(entitycreature.getRandom(), i, j, 0, vec3d1.x, vec3d1.z, d0);

            return blockposition == null ? null : generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    public static Vec3D getPosAway(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = entitycreature.position().subtract(vec3d);
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirectionWithinRadians(entitycreature.getRandom(), i, j, 0, vec3d1.x, vec3d1.z, 1.5707963705062866D);

            return blockposition == null ? null : generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);
        });
    }

    @Nullable
    private static BlockPosition generateRandomPosTowardDirection(EntityCreature entitycreature, int i, boolean flag, BlockPosition blockposition) {
        BlockPosition blockposition1 = RandomPositionGenerator.generateRandomPosTowardDirection(entitycreature, i, entitycreature.getRandom(), blockposition);

        return !PathfinderGoalUtil.isOutsideLimits(blockposition1, entitycreature) && !PathfinderGoalUtil.isRestricted(flag, entitycreature, blockposition1) && !PathfinderGoalUtil.isNotStable(entitycreature.getNavigation(), blockposition1) && !PathfinderGoalUtil.hasMalus(entitycreature, blockposition1) ? blockposition1 : null;
    }
}

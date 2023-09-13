package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class AirAndWaterRandomPos {

    public AirAndWaterRandomPos() {}

    @Nullable
    public static Vec3D getPos(EntityCreature entitycreature, int i, int j, int k, double d0, double d1, double d2) {
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            return generateRandomPos(entitycreature, i, j, k, d0, d1, d2, flag);
        });
    }

    @Nullable
    public static BlockPosition generateRandomPos(EntityCreature entitycreature, int i, int j, int k, double d0, double d1, double d2, boolean flag) {
        BlockPosition blockposition = RandomPositionGenerator.generateRandomDirectionWithinRadians(entitycreature.getRandom(), i, j, k, d0, d1, d2);

        if (blockposition == null) {
            return null;
        } else {
            BlockPosition blockposition1 = RandomPositionGenerator.generateRandomPosTowardDirection(entitycreature, i, entitycreature.getRandom(), blockposition);

            if (!PathfinderGoalUtil.isOutsideLimits(blockposition1, entitycreature) && !PathfinderGoalUtil.isRestricted(flag, entitycreature, blockposition1)) {
                blockposition1 = RandomPositionGenerator.moveUpOutOfSolid(blockposition1, entitycreature.level.getMaxBuildHeight(), (blockposition2) -> {
                    return PathfinderGoalUtil.isSolid(entitycreature, blockposition2);
                });
                return PathfinderGoalUtil.hasMalus(entitycreature, blockposition1) ? null : blockposition1;
            } else {
                return null;
            }
        }
    }
}

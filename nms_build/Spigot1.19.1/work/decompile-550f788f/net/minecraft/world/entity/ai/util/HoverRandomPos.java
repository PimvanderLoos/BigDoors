package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class HoverRandomPos {

    public HoverRandomPos() {}

    @Nullable
    public static Vec3D getPos(EntityCreature entitycreature, int i, int j, double d0, double d1, float f, int k, int l) {
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirectionWithinRadians(entitycreature.getRandom(), i, j, 0, d0, d1, (double) f);

            if (blockposition == null) {
                return null;
            } else {
                BlockPosition blockposition1 = LandRandomPos.generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);

                if (blockposition1 == null) {
                    return null;
                } else {
                    blockposition1 = RandomPositionGenerator.moveUpToAboveSolid(blockposition1, entitycreature.getRandom().nextInt(k - l + 1) + l, entitycreature.level.getMaxBuildHeight(), (blockposition2) -> {
                        return PathfinderGoalUtil.isSolid(entitycreature, blockposition2);
                    });
                    return !PathfinderGoalUtil.isWater(entitycreature, blockposition1) && !PathfinderGoalUtil.hasMalus(entitycreature, blockposition1) ? blockposition1 : null;
                }
            }
        });
    }
}

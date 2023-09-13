package net.minecraft.world.entity.ai.util;

import java.util.Objects;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class LandRandomPos {

    public LandRandomPos() {}

    @Nullable
    public static Vec3D getPos(EntityCreature entitycreature, int i, int j) {
        Objects.requireNonNull(entitycreature);
        return getPos(entitycreature, i, j, entitycreature::getWalkTargetValue);
    }

    @Nullable
    public static Vec3D getPos(EntityCreature entitycreature, int i, int j, ToDoubleFunction<BlockPosition> todoublefunction) {
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return RandomPositionGenerator.generateRandomPos(() -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirection(entitycreature.getRandom(), i, j);
            BlockPosition blockposition1 = generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);

            return blockposition1 == null ? null : movePosUpOutOfSolid(entitycreature, blockposition1);
        }, todoublefunction);
    }

    @Nullable
    public static Vec3D getPosTowards(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.subtract(entitycreature.getX(), entitycreature.getY(), entitycreature.getZ());
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return getPosInDirection(entitycreature, i, j, vec3d1, flag);
    }

    @Nullable
    public static Vec3D getPosAway(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = entitycreature.position().subtract(vec3d);
        boolean flag = PathfinderGoalUtil.mobRestricted(entitycreature, i);

        return getPosInDirection(entitycreature, i, j, vec3d1, flag);
    }

    @Nullable
    private static Vec3D getPosInDirection(EntityCreature entitycreature, int i, int j, Vec3D vec3d, boolean flag) {
        return RandomPositionGenerator.generateRandomPos(entitycreature, () -> {
            BlockPosition blockposition = RandomPositionGenerator.generateRandomDirectionWithinRadians(entitycreature.getRandom(), i, j, 0, vec3d.x, vec3d.z, 1.5707963705062866D);

            if (blockposition == null) {
                return null;
            } else {
                BlockPosition blockposition1 = generateRandomPosTowardDirection(entitycreature, i, flag, blockposition);

                return blockposition1 == null ? null : movePosUpOutOfSolid(entitycreature, blockposition1);
            }
        });
    }

    @Nullable
    public static BlockPosition movePosUpOutOfSolid(EntityCreature entitycreature, BlockPosition blockposition) {
        blockposition = RandomPositionGenerator.moveUpOutOfSolid(blockposition, entitycreature.level.getMaxBuildHeight(), (blockposition1) -> {
            return PathfinderGoalUtil.isSolid(entitycreature, blockposition1);
        });
        return !PathfinderGoalUtil.isWater(entitycreature, blockposition) && !PathfinderGoalUtil.hasMalus(entitycreature, blockposition) ? blockposition : null;
    }

    @Nullable
    public static BlockPosition generateRandomPosTowardDirection(EntityCreature entitycreature, int i, boolean flag, BlockPosition blockposition) {
        BlockPosition blockposition1 = RandomPositionGenerator.generateRandomPosTowardDirection(entitycreature, i, entitycreature.getRandom(), blockposition);

        return !PathfinderGoalUtil.isOutsideLimits(blockposition1, entitycreature) && !PathfinderGoalUtil.isRestricted(flag, entitycreature, blockposition1) && !PathfinderGoalUtil.isNotStable(entitycreature.getNavigation(), blockposition1) ? blockposition1 : null;
    }
}

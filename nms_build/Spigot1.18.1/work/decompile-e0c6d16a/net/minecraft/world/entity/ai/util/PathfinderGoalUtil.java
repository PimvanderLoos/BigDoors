package net.minecraft.world.entity.ai.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

public class PathfinderGoalUtil {

    public PathfinderGoalUtil() {}

    public static boolean hasGroundPathNavigation(EntityInsentient entityinsentient) {
        return entityinsentient.getNavigation() instanceof Navigation;
    }

    public static boolean mobRestricted(EntityCreature entitycreature, int i) {
        return entitycreature.hasRestriction() && entitycreature.getRestrictCenter().closerThan((IPosition) entitycreature.position(), (double) (entitycreature.getRestrictRadius() + (float) i) + 1.0D);
    }

    public static boolean isOutsideLimits(BlockPosition blockposition, EntityCreature entitycreature) {
        return blockposition.getY() < entitycreature.level.getMinBuildHeight() || blockposition.getY() > entitycreature.level.getMaxBuildHeight();
    }

    public static boolean isRestricted(boolean flag, EntityCreature entitycreature, BlockPosition blockposition) {
        return flag && !entitycreature.isWithinRestriction(blockposition);
    }

    public static boolean isNotStable(NavigationAbstract navigationabstract, BlockPosition blockposition) {
        return !navigationabstract.isStableDestination(blockposition);
    }

    public static boolean isWater(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.level.getFluidState(blockposition).is((Tag) TagsFluid.WATER);
    }

    public static boolean hasMalus(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.getPathfindingMalus(PathfinderNormal.getBlockPathTypeStatic(entitycreature.level, blockposition.mutable())) != 0.0F;
    }

    public static boolean isSolid(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.level.getBlockState(blockposition).getMaterial().isSolid();
    }
}

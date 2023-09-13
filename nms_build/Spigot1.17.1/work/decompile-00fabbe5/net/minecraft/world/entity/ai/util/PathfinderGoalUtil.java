package net.minecraft.world.entity.ai.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

public class PathfinderGoalUtil {

    public PathfinderGoalUtil() {}

    public static boolean a(EntityInsentient entityinsentient) {
        return entityinsentient.getNavigation() instanceof Navigation;
    }

    public static boolean a(EntityCreature entitycreature, int i) {
        return entitycreature.fl() && entitycreature.fi().a((IPosition) entitycreature.getPositionVector(), (double) (entitycreature.fj() + (float) i) + 1.0D);
    }

    public static boolean a(BlockPosition blockposition, EntityCreature entitycreature) {
        return blockposition.getY() < entitycreature.level.getMinBuildHeight() || blockposition.getY() > entitycreature.level.getMaxBuildHeight();
    }

    public static boolean a(boolean flag, EntityCreature entitycreature, BlockPosition blockposition) {
        return flag && !entitycreature.a(blockposition);
    }

    public static boolean a(NavigationAbstract navigationabstract, BlockPosition blockposition) {
        return !navigationabstract.a(blockposition);
    }

    public static boolean a(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.level.getFluid(blockposition).a((Tag) TagsFluid.WATER);
    }

    public static boolean b(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.a(PathfinderNormal.a((IBlockAccess) entitycreature.level, blockposition.i())) != 0.0F;
    }

    public static boolean c(EntityCreature entitycreature, BlockPosition blockposition) {
        return entitycreature.level.getType(blockposition).getMaterial().isBuildable();
    }
}

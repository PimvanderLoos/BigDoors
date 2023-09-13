package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderWater;
import net.minecraft.world.phys.Vec3D;

public class NavigationGuardian extends NavigationAbstract {

    private boolean allowBreaching;

    public NavigationGuardian(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder createPathFinder(int i) {
        this.allowBreaching = this.mob.getType() == EntityTypes.DOLPHIN;
        this.nodeEvaluator = new PathfinderWater(this.allowBreaching);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.allowBreaching || this.isInLiquid();
    }

    @Override
    protected Vec3D getTempMobPos() {
        return new Vec3D(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
    }

    @Override
    protected double getGroundY(Vec3D vec3d) {
        return vec3d.y;
    }

    @Override
    protected boolean canMoveDirectly(Vec3D vec3d, Vec3D vec3d1) {
        return isClearForMovementBetween(this.mob, vec3d, vec3d1, false);
    }

    @Override
    public boolean isStableDestination(BlockPosition blockposition) {
        return !this.level.getBlockState(blockposition).isSolidRender(this.level, blockposition);
    }

    @Override
    public void setCanFloat(boolean flag) {}
}

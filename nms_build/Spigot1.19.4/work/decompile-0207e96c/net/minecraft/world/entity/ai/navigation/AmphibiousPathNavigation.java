package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public class AmphibiousPathNavigation extends NavigationAbstract {

    public AmphibiousPathNavigation(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder createPathFinder(int i) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
        this.nodeEvaluator.setCanPassDoors(true);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
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
        return this.isInLiquid() ? isClearForMovementBetween(this.mob, vec3d, vec3d1, false) : false;
    }

    @Override
    public boolean isStableDestination(BlockPosition blockposition) {
        return !this.level.getBlockState(blockposition.below()).isAir();
    }

    @Override
    public void setCanFloat(boolean flag) {}
}

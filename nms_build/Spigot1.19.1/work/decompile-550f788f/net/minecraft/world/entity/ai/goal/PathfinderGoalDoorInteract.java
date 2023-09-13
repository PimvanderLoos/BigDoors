package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;

public abstract class PathfinderGoalDoorInteract extends PathfinderGoal {

    protected EntityInsentient mob;
    protected BlockPosition doorPos;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public PathfinderGoalDoorInteract(EntityInsentient entityinsentient) {
        this.doorPos = BlockPosition.ZERO;
        this.mob = entityinsentient;
        if (!PathfinderGoalUtil.hasGroundPathNavigation(entityinsentient)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        } else {
            IBlockData iblockdata = this.mob.level.getBlockState(this.doorPos);

            if (!(iblockdata.getBlock() instanceof BlockDoor)) {
                this.hasDoor = false;
                return false;
            } else {
                return (Boolean) iblockdata.getValue(BlockDoor.OPEN);
            }
        }
    }

    protected void setOpen(boolean flag) {
        if (this.hasDoor) {
            IBlockData iblockdata = this.mob.level.getBlockState(this.doorPos);

            if (iblockdata.getBlock() instanceof BlockDoor) {
                ((BlockDoor) iblockdata.getBlock()).setOpen(this.mob, this.mob.level, iblockdata, this.doorPos, flag);
            }
        }

    }

    @Override
    public boolean canUse() {
        if (!PathfinderGoalUtil.hasGroundPathNavigation(this.mob)) {
            return false;
        } else if (!this.mob.horizontalCollision) {
            return false;
        } else {
            Navigation navigation = (Navigation) this.mob.getNavigation();
            PathEntity pathentity = navigation.getPath();

            if (pathentity != null && !pathentity.isDone() && navigation.canOpenDoors()) {
                for (int i = 0; i < Math.min(pathentity.getNextNodeIndex() + 2, pathentity.getNodeCount()); ++i) {
                    PathPoint pathpoint = pathentity.getNode(i);

                    this.doorPos = new BlockPosition(pathpoint.x, pathpoint.y + 1, pathpoint.z);
                    if (this.mob.distanceToSqr((double) this.doorPos.getX(), this.mob.getY(), (double) this.doorPos.getZ()) <= 2.25D) {
                        this.hasDoor = BlockDoor.isWoodenDoor(this.mob.level, this.doorPos);
                        if (this.hasDoor) {
                            return true;
                        }
                    }
                }

                this.doorPos = this.mob.blockPosition().above();
                this.hasDoor = BlockDoor.isWoodenDoor(this.mob.level, this.doorPos);
                return this.hasDoor;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float) ((double) this.doorPos.getX() + 0.5D - this.mob.getX());
        this.doorOpenDirZ = (float) ((double) this.doorPos.getZ() + 0.5D - this.mob.getZ());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        float f = (float) ((double) this.doorPos.getX() + 0.5D - this.mob.getX());
        float f1 = (float) ((double) this.doorPos.getZ() + 0.5D - this.mob.getZ());
        float f2 = this.doorOpenDirX * f + this.doorOpenDirZ * f1;

        if (f2 < 0.0F) {
            this.passed = true;
        }

    }
}

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
        if (!PathfinderGoalUtil.a(entityinsentient)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean g() {
        if (!this.hasDoor) {
            return false;
        } else {
            IBlockData iblockdata = this.mob.level.getType(this.doorPos);

            if (!(iblockdata.getBlock() instanceof BlockDoor)) {
                this.hasDoor = false;
                return false;
            } else {
                return (Boolean) iblockdata.get(BlockDoor.OPEN);
            }
        }
    }

    protected void a(boolean flag) {
        if (this.hasDoor) {
            IBlockData iblockdata = this.mob.level.getType(this.doorPos);

            if (iblockdata.getBlock() instanceof BlockDoor) {
                ((BlockDoor) iblockdata.getBlock()).setDoor(this.mob, this.mob.level, iblockdata, this.doorPos, flag);
            }
        }

    }

    @Override
    public boolean a() {
        if (!PathfinderGoalUtil.a(this.mob)) {
            return false;
        } else if (!this.mob.horizontalCollision) {
            return false;
        } else {
            Navigation navigation = (Navigation) this.mob.getNavigation();
            PathEntity pathentity = navigation.k();

            if (pathentity != null && !pathentity.c() && navigation.f()) {
                for (int i = 0; i < Math.min(pathentity.f() + 2, pathentity.e()); ++i) {
                    PathPoint pathpoint = pathentity.a(i);

                    this.doorPos = new BlockPosition(pathpoint.x, pathpoint.y + 1, pathpoint.z);
                    if (this.mob.h((double) this.doorPos.getX(), this.mob.locY(), (double) this.doorPos.getZ()) <= 2.25D) {
                        this.hasDoor = BlockDoor.a(this.mob.level, this.doorPos);
                        if (this.hasDoor) {
                            return true;
                        }
                    }
                }

                this.doorPos = this.mob.getChunkCoordinates().up();
                this.hasDoor = BlockDoor.a(this.mob.level, this.doorPos);
                return this.hasDoor;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.passed;
    }

    @Override
    public void c() {
        this.passed = false;
        this.doorOpenDirX = (float) ((double) this.doorPos.getX() + 0.5D - this.mob.locX());
        this.doorOpenDirZ = (float) ((double) this.doorPos.getZ() + 0.5D - this.mob.locZ());
    }

    @Override
    public void e() {
        float f = (float) ((double) this.doorPos.getX() + 0.5D - this.mob.locX());
        float f1 = (float) ((double) this.doorPos.getZ() + 0.5D - this.mob.locZ());
        float f2 = this.doorOpenDirX * f + this.doorOpenDirZ * f1;

        if (f2 < 0.0F) {
            this.passed = true;
        }

    }
}

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

    protected EntityInsentient entity;
    protected BlockPosition door;
    protected boolean f;
    private boolean a;
    private float b;
    private float c;

    public PathfinderGoalDoorInteract(EntityInsentient entityinsentient) {
        this.door = BlockPosition.ZERO;
        this.entity = entityinsentient;
        if (!PathfinderGoalUtil.a(entityinsentient)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean g() {
        if (!this.f) {
            return false;
        } else {
            IBlockData iblockdata = this.entity.world.getType(this.door);

            if (!(iblockdata.getBlock() instanceof BlockDoor)) {
                this.f = false;
                return false;
            } else {
                return (Boolean) iblockdata.get(BlockDoor.OPEN);
            }
        }
    }

    protected void a(boolean flag) {
        if (this.f) {
            IBlockData iblockdata = this.entity.world.getType(this.door);

            if (iblockdata.getBlock() instanceof BlockDoor) {
                ((BlockDoor) iblockdata.getBlock()).setDoor(this.entity.world, iblockdata, this.door, flag);
            }
        }

    }

    @Override
    public boolean a() {
        if (!PathfinderGoalUtil.a(this.entity)) {
            return false;
        } else if (!this.entity.positionChanged) {
            return false;
        } else {
            Navigation navigation = (Navigation) this.entity.getNavigation();
            PathEntity pathentity = navigation.k();

            if (pathentity != null && !pathentity.c() && navigation.f()) {
                for (int i = 0; i < Math.min(pathentity.f() + 2, pathentity.e()); ++i) {
                    PathPoint pathpoint = pathentity.a(i);

                    this.door = new BlockPosition(pathpoint.a, pathpoint.b + 1, pathpoint.c);
                    if (this.entity.h((double) this.door.getX(), this.entity.locY(), (double) this.door.getZ()) <= 2.25D) {
                        this.f = BlockDoor.a(this.entity.world, this.door);
                        if (this.f) {
                            return true;
                        }
                    }
                }

                this.door = this.entity.getChunkCoordinates().up();
                this.f = BlockDoor.a(this.entity.world, this.door);
                return this.f;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.a;
    }

    @Override
    public void c() {
        this.a = false;
        this.b = (float) ((double) this.door.getX() + 0.5D - this.entity.locX());
        this.c = (float) ((double) this.door.getZ() + 0.5D - this.entity.locZ());
    }

    @Override
    public void e() {
        float f = (float) ((double) this.door.getX() + 0.5D - this.entity.locX());
        float f1 = (float) ((double) this.door.getZ() + 0.5D - this.entity.locZ());
        float f2 = this.b * f + this.c * f1;

        if (f2 < 0.0F) {
            this.a = true;
        }

    }
}

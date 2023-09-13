package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalFollowBoat extends PathfinderGoal {

    private int timeToRecalcPath;
    private final EntityCreature mob;
    @Nullable
    private EntityHuman following;
    private PathfinderGoalBoat currentGoal;

    public PathfinderGoalFollowBoat(EntityCreature entitycreature) {
        this.mob = entitycreature;
    }

    @Override
    public boolean canUse() {
        List<EntityBoat> list = this.mob.level.getEntitiesOfClass(EntityBoat.class, this.mob.getBoundingBox().inflate(5.0D));
        boolean flag = false;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();
            Entity entity = entityboat.getControllingPassenger();

            if (entity instanceof EntityHuman && (MathHelper.abs(((EntityHuman) entity).xxa) > 0.0F || MathHelper.abs(((EntityHuman) entity).zza) > 0.0F)) {
                flag = true;
                break;
            }
        }

        return this.following != null && (MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F) || flag;
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.following != null && this.following.isPassenger() && (MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F);
    }

    @Override
    public void start() {
        List<EntityBoat> list = this.mob.level.getEntitiesOfClass(EntityBoat.class, this.mob.getBoundingBox().inflate(5.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();

            if (entityboat.getControllingPassenger() != null && entityboat.getControllingPassenger() instanceof EntityHuman) {
                this.following = (EntityHuman) entityboat.getControllingPassenger();
                break;
            }
        }

        this.timeToRecalcPath = 0;
        this.currentGoal = PathfinderGoalBoat.GO_TO_BOAT;
    }

    @Override
    public void stop() {
        this.following = null;
    }

    @Override
    public void tick() {
        boolean flag = MathHelper.abs(this.following.xxa) > 0.0F || MathHelper.abs(this.following.zza) > 0.0F;
        float f = this.currentGoal == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION ? (flag ? 0.01F : 0.0F) : 0.015F;

        this.mob.moveRelative(f, new Vec3D((double) this.mob.xxa, (double) this.mob.yya, (double) this.mob.zza));
        this.mob.move(EnumMoveType.SELF, this.mob.getDeltaMovement());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.currentGoal == PathfinderGoalBoat.GO_TO_BOAT) {
                BlockPosition blockposition = this.following.blockPosition().relative(this.following.getDirection().getOpposite());

                blockposition = blockposition.offset(0, -1, 0);
                this.mob.getNavigation().moveTo((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
                if (this.mob.distanceTo(this.following) < 4.0F) {
                    this.timeToRecalcPath = 0;
                    this.currentGoal = PathfinderGoalBoat.GO_IN_BOAT_DIRECTION;
                }
            } else if (this.currentGoal == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION) {
                EnumDirection enumdirection = this.following.getMotionDirection();
                BlockPosition blockposition1 = this.following.blockPosition().relative(enumdirection, 10);

                this.mob.getNavigation().moveTo((double) blockposition1.getX(), (double) (blockposition1.getY() - 1), (double) blockposition1.getZ(), 1.0D);
                if (this.mob.distanceTo(this.following) > 12.0F) {
                    this.timeToRecalcPath = 0;
                    this.currentGoal = PathfinderGoalBoat.GO_TO_BOAT;
                }
            }

        }
    }
}

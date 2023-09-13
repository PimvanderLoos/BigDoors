package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
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
    private EntityHuman following;
    private PathfinderGoalBoat currentGoal;

    public PathfinderGoalFollowBoat(EntityCreature entitycreature) {
        this.mob = entitycreature;
    }

    @Override
    public boolean a() {
        List<EntityBoat> list = this.mob.level.a(EntityBoat.class, this.mob.getBoundingBox().g(5.0D));
        boolean flag = false;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();
            Entity entity = entityboat.getRidingPassenger();

            if (entity instanceof EntityHuman && (MathHelper.e(((EntityHuman) entity).xxa) > 0.0F || MathHelper.e(((EntityHuman) entity).zza) > 0.0F)) {
                flag = true;
                break;
            }
        }

        return this.following != null && (MathHelper.e(this.following.xxa) > 0.0F || MathHelper.e(this.following.zza) > 0.0F) || flag;
    }

    @Override
    public boolean C_() {
        return true;
    }

    @Override
    public boolean b() {
        return this.following != null && this.following.isPassenger() && (MathHelper.e(this.following.xxa) > 0.0F || MathHelper.e(this.following.zza) > 0.0F);
    }

    @Override
    public void c() {
        List<EntityBoat> list = this.mob.level.a(EntityBoat.class, this.mob.getBoundingBox().g(5.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();

            if (entityboat.getRidingPassenger() != null && entityboat.getRidingPassenger() instanceof EntityHuman) {
                this.following = (EntityHuman) entityboat.getRidingPassenger();
                break;
            }
        }

        this.timeToRecalcPath = 0;
        this.currentGoal = PathfinderGoalBoat.GO_TO_BOAT;
    }

    @Override
    public void d() {
        this.following = null;
    }

    @Override
    public void e() {
        boolean flag = MathHelper.e(this.following.xxa) > 0.0F || MathHelper.e(this.following.zza) > 0.0F;
        float f = this.currentGoal == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION ? (flag ? 0.01F : 0.0F) : 0.015F;

        this.mob.a(f, new Vec3D((double) this.mob.xxa, (double) this.mob.yya, (double) this.mob.zza));
        this.mob.move(EnumMoveType.SELF, this.mob.getMot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (this.currentGoal == PathfinderGoalBoat.GO_TO_BOAT) {
                BlockPosition blockposition = this.following.getChunkCoordinates().shift(this.following.getDirection().opposite());

                blockposition = blockposition.c(0, -1, 0);
                this.mob.getNavigation().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
                if (this.mob.e((Entity) this.following) < 4.0F) {
                    this.timeToRecalcPath = 0;
                    this.currentGoal = PathfinderGoalBoat.GO_IN_BOAT_DIRECTION;
                }
            } else if (this.currentGoal == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION) {
                EnumDirection enumdirection = this.following.getAdjustedDirection();
                BlockPosition blockposition1 = this.following.getChunkCoordinates().shift(enumdirection, 10);

                this.mob.getNavigation().a((double) blockposition1.getX(), (double) (blockposition1.getY() - 1), (double) blockposition1.getZ(), 1.0D);
                if (this.mob.e((Entity) this.following) > 12.0F) {
                    this.timeToRecalcPath = 0;
                    this.currentGoal = PathfinderGoalBoat.GO_TO_BOAT;
                }
            }

        }
    }
}

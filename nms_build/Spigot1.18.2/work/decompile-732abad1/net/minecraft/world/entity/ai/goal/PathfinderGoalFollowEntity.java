package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.level.pathfinder.PathType;

public class PathfinderGoalFollowEntity extends PathfinderGoal {

    private final EntityInsentient mob;
    private final Predicate<EntityInsentient> followPredicate;
    @Nullable
    private EntityInsentient followingMob;
    private final double speedModifier;
    private final NavigationAbstract navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;

    public PathfinderGoalFollowEntity(EntityInsentient entityinsentient, double d0, float f, float f1) {
        this.mob = entityinsentient;
        this.followPredicate = (entityinsentient1) -> {
            return entityinsentient1 != null && entityinsentient.getClass() != entityinsentient1.getClass();
        };
        this.speedModifier = d0;
        this.navigation = entityinsentient.getNavigation();
        this.stopDistance = f;
        this.areaSize = f1;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        if (!(entityinsentient.getNavigation() instanceof Navigation) && !(entityinsentient.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean canUse() {
        List<EntityInsentient> list = this.mob.level.getEntitiesOfClass(EntityInsentient.class, this.mob.getBoundingBox().inflate((double) this.areaSize), this.followPredicate);

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (!entityinsentient.isInvisible()) {
                    this.followingMob = entityinsentient;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr((Entity) this.followingMob) > (double) (this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(PathType.WATER);
        this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.followingMob = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.followingMob != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float) this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double d0 = this.mob.getX() - this.followingMob.getX();
                double d1 = this.mob.getY() - this.followingMob.getY();
                double d2 = this.mob.getZ() - this.followingMob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > (double) (this.stopDistance * this.stopDistance)) {
                    this.navigation.moveTo((Entity) this.followingMob, this.speedModifier);
                } else {
                    this.navigation.stop();
                    ControllerLook controllerlook = this.followingMob.getLookControl();

                    if (d3 <= (double) this.stopDistance || controllerlook.getWantedX() == this.mob.getX() && controllerlook.getWantedY() == this.mob.getY() && controllerlook.getWantedZ() == this.mob.getZ()) {
                        double d4 = this.followingMob.getX() - this.mob.getX();
                        double d5 = this.followingMob.getZ() - this.mob.getZ();

                        this.navigation.moveTo(this.mob.getX() - d4, this.mob.getY(), this.mob.getZ() - d5, this.speedModifier);
                    }

                }
            }
        }
    }
}

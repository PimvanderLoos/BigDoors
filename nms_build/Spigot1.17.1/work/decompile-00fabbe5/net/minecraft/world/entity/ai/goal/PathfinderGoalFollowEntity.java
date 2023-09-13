package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        if (!(entityinsentient.getNavigation() instanceof Navigation) && !(entityinsentient.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean a() {
        List<EntityInsentient> list = this.mob.level.a(EntityInsentient.class, this.mob.getBoundingBox().g((double) this.areaSize), this.followPredicate);

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
    public boolean b() {
        return this.followingMob != null && !this.navigation.m() && this.mob.f((Entity) this.followingMob) > (double) (this.stopDistance * this.stopDistance);
    }

    @Override
    public void c() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.a(PathType.WATER);
        this.mob.a(PathType.WATER, 0.0F);
    }

    @Override
    public void d() {
        this.followingMob = null;
        this.navigation.o();
        this.mob.a(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void e() {
        if (this.followingMob != null && !this.mob.isLeashed()) {
            this.mob.getControllerLook().a(this.followingMob, 10.0F, (float) this.mob.eZ());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                double d0 = this.mob.locX() - this.followingMob.locX();
                double d1 = this.mob.locY() - this.followingMob.locY();
                double d2 = this.mob.locZ() - this.followingMob.locZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > (double) (this.stopDistance * this.stopDistance)) {
                    this.navigation.a((Entity) this.followingMob, this.speedModifier);
                } else {
                    this.navigation.o();
                    ControllerLook controllerlook = this.followingMob.getControllerLook();

                    if (d3 <= (double) this.stopDistance || controllerlook.e() == this.mob.locX() && controllerlook.f() == this.mob.locY() && controllerlook.g() == this.mob.locZ()) {
                        double d4 = this.followingMob.locX() - this.mob.locX();
                        double d5 = this.followingMob.locZ() - this.mob.locZ();

                        this.navigation.a(this.mob.locX() - d4, this.mob.locY(), this.mob.locZ() - d5, this.speedModifier);
                    }

                }
            }
        }
    }
}

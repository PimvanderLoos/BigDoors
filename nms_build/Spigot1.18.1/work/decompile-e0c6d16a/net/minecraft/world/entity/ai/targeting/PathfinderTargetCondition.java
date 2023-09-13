package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;

public class PathfinderTargetCondition {

    public static final PathfinderTargetCondition DEFAULT = forCombat();
    private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0D;
    private final boolean isCombat;
    private double range = -1.0D;
    private boolean checkLineOfSight = true;
    private boolean testInvisible = true;
    @Nullable
    private Predicate<EntityLiving> selector;

    private PathfinderTargetCondition(boolean flag) {
        this.isCombat = flag;
    }

    public static PathfinderTargetCondition forCombat() {
        return new PathfinderTargetCondition(true);
    }

    public static PathfinderTargetCondition forNonCombat() {
        return new PathfinderTargetCondition(false);
    }

    public PathfinderTargetCondition copy() {
        PathfinderTargetCondition pathfindertargetcondition = this.isCombat ? forCombat() : forNonCombat();

        pathfindertargetcondition.range = this.range;
        pathfindertargetcondition.checkLineOfSight = this.checkLineOfSight;
        pathfindertargetcondition.testInvisible = this.testInvisible;
        pathfindertargetcondition.selector = this.selector;
        return pathfindertargetcondition;
    }

    public PathfinderTargetCondition range(double d0) {
        this.range = d0;
        return this;
    }

    public PathfinderTargetCondition ignoreLineOfSight() {
        this.checkLineOfSight = false;
        return this;
    }

    public PathfinderTargetCondition ignoreInvisibilityTesting() {
        this.testInvisible = false;
        return this;
    }

    public PathfinderTargetCondition selector(@Nullable Predicate<EntityLiving> predicate) {
        this.selector = predicate;
        return this;
    }

    public boolean test(@Nullable EntityLiving entityliving, EntityLiving entityliving1) {
        if (entityliving == entityliving1) {
            return false;
        } else if (!entityliving1.canBeSeenByAnyone()) {
            return false;
        } else if (this.selector != null && !this.selector.test(entityliving1)) {
            return false;
        } else {
            if (entityliving == null) {
                if (this.isCombat && (!entityliving1.canBeSeenAsEnemy() || entityliving1.level.getDifficulty() == EnumDifficulty.PEACEFUL)) {
                    return false;
                }
            } else {
                if (this.isCombat && (!entityliving.canAttack(entityliving1) || !entityliving.canAttackType(entityliving1.getType()) || entityliving.isAlliedTo((Entity) entityliving1))) {
                    return false;
                }

                if (this.range > 0.0D) {
                    double d0 = this.testInvisible ? entityliving1.getVisibilityPercent(entityliving) : 1.0D;
                    double d1 = Math.max(this.range * d0, 2.0D);
                    double d2 = entityliving.distanceToSqr(entityliving1.getX(), entityliving1.getY(), entityliving1.getZ());

                    if (d2 > d1 * d1) {
                        return false;
                    }
                }

                if (this.checkLineOfSight && entityliving instanceof EntityInsentient) {
                    EntityInsentient entityinsentient = (EntityInsentient) entityliving;

                    if (!entityinsentient.getSensing().hasLineOfSight(entityliving1)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}

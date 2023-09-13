package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;

public class PathfinderTargetCondition {

    public static final PathfinderTargetCondition DEFAULT = a();
    private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0D;
    private final boolean isCombat;
    private double range = -1.0D;
    private boolean checkLineOfSight = true;
    private boolean testInvisible = true;
    private Predicate<EntityLiving> selector;

    private PathfinderTargetCondition(boolean flag) {
        this.isCombat = flag;
    }

    public static PathfinderTargetCondition a() {
        return new PathfinderTargetCondition(true);
    }

    public static PathfinderTargetCondition b() {
        return new PathfinderTargetCondition(false);
    }

    public PathfinderTargetCondition c() {
        PathfinderTargetCondition pathfindertargetcondition = this.isCombat ? a() : b();

        pathfindertargetcondition.range = this.range;
        pathfindertargetcondition.checkLineOfSight = this.checkLineOfSight;
        pathfindertargetcondition.testInvisible = this.testInvisible;
        pathfindertargetcondition.selector = this.selector;
        return pathfindertargetcondition;
    }

    public PathfinderTargetCondition a(double d0) {
        this.range = d0;
        return this;
    }

    public PathfinderTargetCondition d() {
        this.checkLineOfSight = false;
        return this;
    }

    public PathfinderTargetCondition e() {
        this.testInvisible = false;
        return this;
    }

    public PathfinderTargetCondition a(@Nullable Predicate<EntityLiving> predicate) {
        this.selector = predicate;
        return this;
    }

    public boolean a(@Nullable EntityLiving entityliving, EntityLiving entityliving1) {
        if (entityliving == entityliving1) {
            return false;
        } else if (!entityliving1.dO()) {
            return false;
        } else if (this.selector != null && !this.selector.test(entityliving1)) {
            return false;
        } else {
            if (entityliving == null) {
                if (this.isCombat && (!entityliving1.dN() || entityliving1.level.getDifficulty() == EnumDifficulty.PEACEFUL)) {
                    return false;
                }
            } else {
                if (this.isCombat && (!entityliving.c(entityliving1) || !entityliving.a(entityliving1.getEntityType()) || entityliving.p(entityliving1))) {
                    return false;
                }

                if (this.range > 0.0D) {
                    double d0 = this.testInvisible ? entityliving1.y(entityliving) : 1.0D;
                    double d1 = Math.max(this.range * d0, 2.0D);
                    double d2 = entityliving.h(entityliving1.locX(), entityliving1.locY(), entityliving1.locZ());

                    if (d2 > d1 * d1) {
                        return false;
                    }
                }

                if (this.checkLineOfSight && entityliving instanceof EntityInsentient && !((EntityInsentient) entityliving).getEntitySenses().a(entityliving1)) {
                    return false;
                }
            }

            return true;
        }
    }
}

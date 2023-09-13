package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalAvoidTarget<T extends EntityLiving> extends PathfinderGoal {

    protected final EntityCreature mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected PathEntity path;
    protected final NavigationAbstract pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<EntityLiving> avoidPredicate;
    protected final Predicate<EntityLiving> predicateOnAvoidEntity;
    private final PathfinderTargetCondition avoidEntityTargeting;

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, float f, double d0, double d1) {
        Predicate predicate = (entityliving) -> {
            return true;
        };
        Predicate predicate1 = IEntitySelector.NO_CREATIVE_OR_SPECTATOR;

        Objects.requireNonNull(predicate1);
        this(entitycreature, oclass, predicate, f, d0, d1, predicate1::test);
    }

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, Predicate<EntityLiving> predicate, float f, double d0, double d1, Predicate<EntityLiving> predicate1) {
        this.mob = entitycreature;
        this.avoidClass = oclass;
        this.avoidPredicate = predicate;
        this.maxDist = f;
        this.walkSpeedModifier = d0;
        this.sprintSpeedModifier = d1;
        this.predicateOnAvoidEntity = predicate1;
        this.pathNav = entitycreature.getNavigation();
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        this.avoidEntityTargeting = PathfinderTargetCondition.a().a((double) f).a(predicate1.and(predicate));
    }

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, float f, double d0, double d1, Predicate<EntityLiving> predicate) {
        this(entitycreature, oclass, (entityliving) -> {
            return true;
        }, f, d0, d1, predicate);
    }

    @Override
    public boolean a() {
        this.toAvoid = this.mob.level.a(this.mob.level.a(this.avoidClass, this.mob.getBoundingBox().grow((double) this.maxDist, 3.0D, (double) this.maxDist), (entityliving) -> {
            return true;
        }), this.avoidEntityTargeting, (EntityLiving) this.mob, this.mob.locX(), this.mob.locY(), this.mob.locZ());
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3D vec3d = DefaultRandomPos.a(this.mob, 16, 7, this.toAvoid.getPositionVector());

            if (vec3d == null) {
                return false;
            } else if (this.toAvoid.h(vec3d.x, vec3d.y, vec3d.z) < this.toAvoid.f((Entity) this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.a(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.path != null;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.pathNav.m();
    }

    @Override
    public void c() {
        this.pathNav.a(this.path, this.walkSpeedModifier);
    }

    @Override
    public void d() {
        this.toAvoid = null;
    }

    @Override
    public void e() {
        if (this.mob.f((Entity) this.toAvoid) < 49.0D) {
            this.mob.getNavigation().a(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().a(this.walkSpeedModifier);
        }

    }
}

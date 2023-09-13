package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

    protected final EntityCreature mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private PathEntity path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

    public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.followingTargetEvenIfNotSeen = flag;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        long i = this.mob.level.getTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            EntityLiving entityliving = this.mob.getGoalTarget();

            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().a((Entity) entityliving, 0);
                return this.path != null ? true : this.a(entityliving) >= this.mob.h(entityliving.locX(), entityliving.locY(), entityliving.locZ());
            }
        }
    }

    @Override
    public boolean b() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        return entityliving == null ? false : (!entityliving.isAlive() ? false : (!this.followingTargetEvenIfNotSeen ? !this.mob.getNavigation().m() : (!this.mob.a(entityliving.getChunkCoordinates()) ? false : !(entityliving instanceof EntityHuman) || !entityliving.isSpectator() && !((EntityHuman) entityliving).isCreative())));
    }

    @Override
    public void c() {
        this.mob.getNavigation().a(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void d() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        if (!IEntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entityliving)) {
            this.mob.setGoalTarget((EntityLiving) null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().o();
    }

    @Override
    public void e() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        this.mob.getControllerLook().a(entityliving, 30.0F, 30.0F);
        double d0 = this.mob.h(entityliving.locX(), entityliving.locY(), entityliving.locZ());

        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if ((this.followingTargetEvenIfNotSeen || this.mob.getEntitySenses().a(entityliving)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || entityliving.h(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
            this.pathedTargetX = entityliving.locX();
            this.pathedTargetY = entityliving.locY();
            this.pathedTargetZ = entityliving.locZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            if (d0 > 1024.0D) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (d0 > 256.0D) {
                this.ticksUntilNextPathRecalculation += 5;
            }

            if (!this.mob.getNavigation().a((Entity) entityliving, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
        }

        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.a(entityliving, d0);
    }

    protected void a(EntityLiving entityliving, double d0) {
        double d1 = this.a(entityliving);

        if (d0 <= d1 && this.ticksUntilNextAttack <= 0) {
            this.g();
            this.mob.swingHand(EnumHand.MAIN_HAND);
            this.mob.attackEntity(entityliving);
        }

    }

    protected void g() {
        this.ticksUntilNextAttack = 20;
    }

    protected boolean h() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected int j() {
        return this.ticksUntilNextAttack;
    }

    protected int k() {
        return 20;
    }

    protected double a(EntityLiving entityliving) {
        return (double) (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entityliving.getWidth());
    }
}

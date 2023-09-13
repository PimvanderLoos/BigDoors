package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityCreeper;

public class PathfinderGoalSwell extends PathfinderGoal {

    private final EntityCreeper creeper;
    @Nullable
    private EntityLiving target;

    public PathfinderGoalSwell(EntityCreeper entitycreeper) {
        this.creeper = entitycreeper;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        EntityLiving entityliving = this.creeper.getTarget();

        return this.creeper.getSwellDir() > 0 || entityliving != null && this.creeper.distanceToSqr((Entity) entityliving) < 9.0D;
    }

    @Override
    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDir(-1);
        } else if (this.creeper.distanceToSqr((Entity) this.target) > 49.0D) {
            this.creeper.setSwellDir(-1);
        } else if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
            this.creeper.setSwellDir(-1);
        } else {
            this.creeper.setSwellDir(1);
        }
    }
}

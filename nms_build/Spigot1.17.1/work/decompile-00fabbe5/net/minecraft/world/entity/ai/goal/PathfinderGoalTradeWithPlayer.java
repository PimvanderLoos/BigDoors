package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalTradeWithPlayer extends PathfinderGoal {

    private final EntityVillagerAbstract mob;

    public PathfinderGoalTradeWithPlayer(EntityVillagerAbstract entityvillagerabstract) {
        this.mob = entityvillagerabstract;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (!this.mob.isAlive()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else if (!this.mob.isOnGround()) {
            return false;
        } else if (this.mob.hurtMarked) {
            return false;
        } else {
            EntityHuman entityhuman = this.mob.getTrader();

            return entityhuman == null ? false : (this.mob.f((Entity) entityhuman) > 16.0D ? false : entityhuman.containerMenu != null);
        }
    }

    @Override
    public void c() {
        this.mob.getNavigation().o();
    }

    @Override
    public void d() {
        this.mob.setTradingPlayer((EntityHuman) null);
    }
}

package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalTradeWithPlayer extends PathfinderGoal {

    private final EntityVillagerAbstract a;

    public PathfinderGoalTradeWithPlayer(EntityVillagerAbstract entityvillagerabstract) {
        this.a = entityvillagerabstract;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (!this.a.isAlive()) {
            return false;
        } else if (this.a.isInWater()) {
            return false;
        } else if (!this.a.isOnGround()) {
            return false;
        } else if (this.a.velocityChanged) {
            return false;
        } else {
            EntityHuman entityhuman = this.a.getTrader();

            return entityhuman == null ? false : (this.a.h((Entity) entityhuman) > 16.0D ? false : entityhuman.activeContainer != null);
        }
    }

    @Override
    public void c() {
        this.a.getNavigation().o();
    }

    @Override
    public void d() {
        this.a.setTradingPlayer((EntityHuman) null);
    }
}

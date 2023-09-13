package net.minecraft.server;

public class PathfinderGoalTradeWithPlayer extends PathfinderGoal {

    private final EntityVillager a;

    public PathfinderGoalTradeWithPlayer(EntityVillager entityvillager) {
        this.a = entityvillager;
        this.a(5);
    }

    public boolean a() {
        if (!this.a.isAlive()) {
            return false;
        } else if (this.a.isInWater()) {
            return false;
        } else if (!this.a.onGround) {
            return false;
        } else if (this.a.velocityChanged) {
            return false;
        } else {
            EntityHuman entityhuman = this.a.getTrader();

            return entityhuman == null ? false : (this.a.h(entityhuman) > 16.0D ? false : entityhuman.activeContainer != null);
        }
    }

    public void c() {
        this.a.getNavigation().p();
    }

    public void d() {
        this.a.setTradingPlayer((EntityHuman) null);
    }
}

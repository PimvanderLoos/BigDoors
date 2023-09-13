package net.minecraft.server;

public class PathfinderGoalPerch extends PathfinderGoal {

    private final EntityPerchable a;
    private EntityHuman b;
    private boolean c;

    public PathfinderGoalPerch(EntityPerchable entityperchable) {
        this.a = entityperchable;
    }

    public boolean a() {
        EntityLiving entityliving = this.a.getOwner();
        boolean flag = entityliving != null && !((EntityHuman) entityliving).isSpectator() && !((EntityHuman) entityliving).abilities.isFlying && !entityliving.isInWater();

        return !this.a.isSitting() && flag && this.a.dw();
    }

    public boolean g() {
        return !this.c;
    }

    public void c() {
        this.b = (EntityHuman) this.a.getOwner();
        this.c = false;
    }

    public void e() {
        if (!this.c && !this.a.isSitting() && !this.a.isLeashed()) {
            if (this.a.getBoundingBox().c(this.b.getBoundingBox())) {
                this.c = this.a.g(this.b);
            }

        }
    }
}

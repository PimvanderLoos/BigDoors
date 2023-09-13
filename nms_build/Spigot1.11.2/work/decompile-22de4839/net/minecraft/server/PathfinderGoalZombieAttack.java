package net.minecraft.server;

public class PathfinderGoalZombieAttack extends PathfinderGoalMeleeAttack {

    private final EntityZombie h;
    private int i;

    public PathfinderGoalZombieAttack(EntityZombie entityzombie, double d0, boolean flag) {
        super(entityzombie, d0, flag);
        this.h = entityzombie;
    }

    public void c() {
        super.c();
        this.i = 0;
    }

    public void d() {
        super.d();
        this.h.a(false);
    }

    public void e() {
        super.e();
        ++this.i;
        if (this.i >= 5 && this.c < 10) {
            this.h.a(true);
        } else {
            this.h.a(false);
        }

    }
}

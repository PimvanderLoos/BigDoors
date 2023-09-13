package net.minecraft.server;

public class PathfinderGoalZombieAttack extends PathfinderGoalMeleeAttack {

    private final EntityZombie d;
    private int e;

    public PathfinderGoalZombieAttack(EntityZombie entityzombie, double d0, boolean flag) {
        super(entityzombie, d0, flag);
        this.d = entityzombie;
    }

    public void c() {
        super.c();
        this.e = 0;
    }

    public void d() {
        super.d();
        this.d.s(false);
    }

    public void e() {
        super.e();
        ++this.e;
        if (this.e >= 5 && this.b < 10) {
            this.d.s(true);
        } else {
            this.d.s(false);
        }

    }
}

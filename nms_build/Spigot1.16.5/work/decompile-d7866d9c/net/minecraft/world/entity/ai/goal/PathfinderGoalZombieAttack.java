package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.monster.EntityZombie;

public class PathfinderGoalZombieAttack extends PathfinderGoalMeleeAttack {

    private final EntityZombie b;
    private int c;

    public PathfinderGoalZombieAttack(EntityZombie entityzombie, double d0, boolean flag) {
        super(entityzombie, d0, flag);
        this.b = entityzombie;
    }

    @Override
    public void c() {
        super.c();
        this.c = 0;
    }

    @Override
    public void d() {
        super.d();
        this.b.setAggressive(false);
    }

    @Override
    public void e() {
        super.e();
        ++this.c;
        if (this.c >= 5 && this.j() < this.k() / 2) {
            this.b.setAggressive(true);
        } else {
            this.b.setAggressive(false);
        }

    }
}

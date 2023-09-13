package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.monster.EntityZombie;

public class PathfinderGoalZombieAttack extends PathfinderGoalMeleeAttack {

    private final EntityZombie zombie;
    private int raiseArmTicks;

    public PathfinderGoalZombieAttack(EntityZombie entityzombie, double d0, boolean flag) {
        super(entityzombie, d0, flag);
        this.zombie = entityzombie;
    }

    @Override
    public void c() {
        super.c();
        this.raiseArmTicks = 0;
    }

    @Override
    public void d() {
        super.d();
        this.zombie.setAggressive(false);
    }

    @Override
    public void e() {
        super.e();
        ++this.raiseArmTicks;
        if (this.raiseArmTicks >= 5 && this.j() < this.k() / 2) {
            this.zombie.setAggressive(true);
        } else {
            this.zombie.setAggressive(false);
        }

    }
}

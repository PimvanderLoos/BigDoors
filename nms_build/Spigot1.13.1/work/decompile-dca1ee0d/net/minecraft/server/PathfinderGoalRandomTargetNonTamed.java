package net.minecraft.server;

import java.util.function.Predicate;

public class PathfinderGoalRandomTargetNonTamed<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {

    private final EntityTameableAnimal i;

    public PathfinderGoalRandomTargetNonTamed(EntityTameableAnimal entitytameableanimal, Class<T> oclass, boolean flag, Predicate<? super T> predicate) {
        super(entitytameableanimal, oclass, 10, flag, false, predicate);
        this.i = entitytameableanimal;
    }

    public boolean a() {
        return !this.i.isTamed() && super.a();
    }

    public boolean b() {
        return this.c != null ? this.c.test(this.d) : super.b();
    }
}

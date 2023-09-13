package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityGolem extends EntityCreature implements IAnimal {

    public EntityGolem(World world) {
        super(world);
    }

    public void e(float f, float f1) {}

    @Nullable
    protected SoundEffect F() {
        return null;
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return null;
    }

    @Nullable
    protected SoundEffect cf() {
        return null;
    }

    public int C() {
        return 120;
    }

    protected boolean isTypeNotPersistent() {
        return false;
    }
}

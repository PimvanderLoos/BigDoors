package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityGolem extends EntityCreature implements IAnimal {

    protected EntityGolem(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    public void c(float f, float f1) {}

    @Nullable
    protected SoundEffect D() {
        return null;
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return null;
    }

    @Nullable
    protected SoundEffect cs() {
        return null;
    }

    public int z() {
        return 120;
    }

    public boolean isTypeNotPersistent() {
        return false;
    }
}

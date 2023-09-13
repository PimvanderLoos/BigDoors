package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityGolem extends EntityCreature {

    protected EntityGolem(EntityTypes<? extends EntityGolem> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return null;
    }

    @Override
    public int D() {
        return 120;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }
}

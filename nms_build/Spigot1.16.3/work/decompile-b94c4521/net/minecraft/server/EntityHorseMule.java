package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseMule extends EntityHorseChestedAbstract {

    public EntityHorseMule(EntityTypes<? extends EntityHorseMule> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.ENTITY_MULE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.ENTITY_MULE_ANGRY;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.ENTITY_MULE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect fg() {
        return SoundEffects.ENTITY_MULE_EAT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.ENTITY_MULE_HURT;
    }

    @Override
    protected void eO() {
        this.playSound(SoundEffects.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.MULE.a((World) worldserver);
    }
}

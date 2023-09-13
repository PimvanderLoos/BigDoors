package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

public abstract class EntityGolem extends EntityCreature {

    protected EntityGolem(EntityTypes<? extends EntityGolem> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }
}

package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

public class EntityHorseMule extends EntityHorseChestedAbstract {

    public EntityHorseMule(EntityTypes<? extends EntityHorseMule> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.MULE_AMBIENT;
    }

    @Override
    protected SoundEffect getAngrySound() {
        return SoundEffects.MULE_ANGRY;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.MULE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getEatingSound() {
        return SoundEffects.MULE_EAT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.MULE_HURT;
    }

    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEffects.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.MULE.create(worldserver);
    }
}

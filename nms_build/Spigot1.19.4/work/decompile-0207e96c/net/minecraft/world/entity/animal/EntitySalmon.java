package net.minecraft.world.entity.animal;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntitySalmon extends EntityFishSchool {

    public EntitySalmon(EntityTypes<? extends EntitySalmon> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public int getMaxSchoolSize() {
        return 5;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.SALMON_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SALMON_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.SALMON_HURT;
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.SALMON_FLOP;
    }
}

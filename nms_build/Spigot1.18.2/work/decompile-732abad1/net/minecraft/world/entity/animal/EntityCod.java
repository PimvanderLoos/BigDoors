package net.minecraft.world.entity.animal;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntityCod extends EntityFishSchool {

    public EntityCod(EntityTypes<? extends EntityCod> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.COD_BUCKET);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.COD_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.COD_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.COD_HURT;
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.COD_FLOP;
    }
}

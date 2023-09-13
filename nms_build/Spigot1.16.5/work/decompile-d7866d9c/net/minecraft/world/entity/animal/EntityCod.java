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
    protected ItemStack eK() {
        return new ItemStack(Items.COD_BUCKET);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_COD_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_COD_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_COD_HURT;
    }

    @Override
    protected SoundEffect getSoundFlop() {
        return SoundEffects.ENTITY_COD_FLOP;
    }
}

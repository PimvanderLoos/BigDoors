package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntityHorseZombie extends EntityHorseAbstract {

    public EntityHorseZombie(EntityTypes<? extends EntityHorseZombie> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static AttributeProvider.Builder eL() {
        return fi().a(GenericAttributes.MAX_HEALTH, 15.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected void eK() {
        this.getAttributeInstance(GenericAttributes.JUMP_STRENGTH).setValue(this.fq());
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.ZOMBIE_HORSE.a((World) worldserver);
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isTamed()) {
            return EnumInteractionResult.PASS;
        } else if (this.isBaby()) {
            return super.b(entityhuman, enumhand);
        } else if (entityhuman.eq()) {
            this.f(entityhuman);
            return EnumInteractionResult.a(this.world.isClientSide);
        } else if (this.isVehicle()) {
            return super.b(entityhuman, enumhand);
        } else {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.SADDLE && !this.hasSaddle()) {
                    this.f(entityhuman);
                    return EnumInteractionResult.a(this.world.isClientSide);
                }

                EnumInteractionResult enuminteractionresult = itemstack.a(entityhuman, (EntityLiving) this, enumhand);

                if (enuminteractionresult.a()) {
                    return enuminteractionresult;
                }
            }

            this.h(entityhuman);
            return EnumInteractionResult.a(this.world.isClientSide);
        }
    }

    @Override
    protected void eV() {}
}

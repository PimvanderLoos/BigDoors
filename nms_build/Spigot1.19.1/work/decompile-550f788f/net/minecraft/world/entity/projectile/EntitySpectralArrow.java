package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntitySpectralArrow extends EntityArrow {

    public int duration = 200;

    public EntitySpectralArrow(EntityTypes<? extends EntitySpectralArrow> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntitySpectralArrow(World world, EntityLiving entityliving) {
        super(EntityTypes.SPECTRAL_ARROW, entityliving, world);
    }

    public EntitySpectralArrow(World world, double d0, double d1, double d2) {
        super(EntityTypes.SPECTRAL_ARROW, d0, d1, d2, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && !this.inGround) {
            this.level.addParticle(Particles.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }

    @Override
    protected void doPostHurtEffects(EntityLiving entityliving) {
        super.doPostHurtEffects(entityliving);
        MobEffect mobeffect = new MobEffect(MobEffects.GLOWING, this.duration, 0);

        entityliving.addEffect(mobeffect, this.getEffectSource());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("Duration")) {
            this.duration = nbttagcompound.getInt("Duration");
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Duration", this.duration);
    }
}

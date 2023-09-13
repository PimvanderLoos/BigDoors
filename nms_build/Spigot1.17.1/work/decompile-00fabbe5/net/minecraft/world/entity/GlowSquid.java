package net.minecraft.world.entity;

import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.level.World;

public class GlowSquid extends EntitySquid {

    private static final DataWatcherObject<Integer> DATA_DARK_TICKS_REMAINING = DataWatcher.a(GlowSquid.class, DataWatcherRegistry.INT);

    public GlowSquid(EntityTypes<? extends GlowSquid> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected ParticleParam n() {
        return Particles.GLOW_SQUID_INK;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(GlowSquid.DATA_DARK_TICKS_REMAINING, 0);
    }

    @Override
    protected SoundEffect p() {
        return SoundEffects.GLOW_SQUID_SQUIRT;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.GLOW_SQUID_DEATH;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DarkTicksRemaining", this.getDarkTicksRemaining());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setDarkTicksRemaining(nbttagcompound.getInt("DarkTicksRemaining"));
    }

    @Override
    public void movementTick() {
        super.movementTick();
        int i = this.getDarkTicksRemaining();

        if (i > 0) {
            this.setDarkTicksRemaining(i - 1);
        }

        this.level.addParticle(Particles.GLOW, this.d(0.6D), this.da(), this.g(0.6D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (flag) {
            this.setDarkTicksRemaining(100);
        }

        return flag;
    }

    public void setDarkTicksRemaining(int i) {
        this.entityData.set(GlowSquid.DATA_DARK_TICKS_REMAINING, i);
    }

    public int getDarkTicksRemaining() {
        return (Integer) this.entityData.get(GlowSquid.DATA_DARK_TICKS_REMAINING);
    }
}

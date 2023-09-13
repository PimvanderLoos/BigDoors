package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.core.BlockPosition;
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
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;

public class GlowSquid extends EntitySquid {

    private static final DataWatcherObject<Integer> DATA_DARK_TICKS_REMAINING = DataWatcher.defineId(GlowSquid.class, DataWatcherRegistry.INT);

    public GlowSquid(EntityTypes<? extends GlowSquid> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected ParticleParam getInkParticle() {
        return Particles.GLOW_SQUID_INK;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GlowSquid.DATA_DARK_TICKS_REMAINING, 0);
    }

    @Override
    protected SoundEffect getSquirtSound() {
        return SoundEffects.GLOW_SQUID_SQUIRT;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.GLOW_SQUID_DEATH;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setDarkTicks(nbttagcompound.getInt("DarkTicksRemaining"));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        int i = this.getDarkTicksRemaining();

        if (i > 0) {
            this.setDarkTicks(i - 1);
        }

        this.level.addParticle(Particles.GLOW, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        boolean flag = super.hurt(damagesource, f);

        if (flag) {
            this.setDarkTicks(100);
        }

        return flag;
    }

    public void setDarkTicks(int i) {
        this.entityData.set(GlowSquid.DATA_DARK_TICKS_REMAINING, i);
    }

    public int getDarkTicksRemaining() {
        return (Integer) this.entityData.get(GlowSquid.DATA_DARK_TICKS_REMAINING);
    }

    public static boolean checkGlowSquideSpawnRules(EntityTypes<? extends EntityLiving> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return worldaccess.getBlockState(blockposition).is(Blocks.WATER) && blockposition.getY() <= worldaccess.getSeaLevel() - 33;
    }
}

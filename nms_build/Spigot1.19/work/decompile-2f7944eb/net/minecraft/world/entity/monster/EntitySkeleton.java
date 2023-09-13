package net.minecraft.world.entity.monster;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;

public class EntitySkeleton extends EntitySkeletonAbstract {

    public static final DataWatcherObject<Boolean> DATA_STRAY_CONVERSION_ID = DataWatcher.defineId(EntitySkeleton.class, DataWatcherRegistry.BOOLEAN);
    public static final String CONVERSION_TAG = "StrayConversionTime";
    private int inPowderSnowTime;
    public int conversionTime;

    public EntitySkeleton(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(EntitySkeleton.DATA_STRAY_CONVERSION_ID, false);
    }

    public boolean isFreezeConverting() {
        return (Boolean) this.getEntityData().get(EntitySkeleton.DATA_STRAY_CONVERSION_ID);
    }

    public void setFreezeConverting(boolean flag) {
        this.entityData.set(EntitySkeleton.DATA_STRAY_CONVERSION_ID, flag);
    }

    @Override
    public boolean isShaking() {
        return this.isFreezeConverting();
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isFreezeConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.doFreezeConversion();
                }
            } else if (this.isInPowderSnow) {
                ++this.inPowderSnowTime;
                if (this.inPowderSnowTime >= 140) {
                    this.startFreezeConversion(300);
                }
            } else {
                this.inPowderSnowTime = -1;
            }
        }

        super.tick();
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("StrayConversionTime", this.isFreezeConverting() ? this.conversionTime : -1);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("StrayConversionTime", 99) && nbttagcompound.getInt("StrayConversionTime") > -1) {
            this.startFreezeConversion(nbttagcompound.getInt("StrayConversionTime"));
        }

    }

    public void startFreezeConversion(int i) {
        this.conversionTime = i;
        this.entityData.set(EntitySkeleton.DATA_STRAY_CONVERSION_ID, true);
    }

    protected void doFreezeConversion() {
        this.convertTo(EntityTypes.STRAY, true);
        if (!this.isSilent()) {
            this.level.levelEvent((EntityHuman) null, 1048, this.blockPosition(), 0);
        }

    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.SKELETON_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SKELETON_DEATH;
    }

    @Override
    SoundEffect getStepSound() {
        return SoundEffects.SKELETON_STEP;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canDropMobsSkull()) {
                entitycreeper.increaseDroppedSkulls();
                this.spawnAtLocation((IMaterial) Items.SKELETON_SKULL);
            }
        }

    }
}

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

    public static final DataWatcherObject<Boolean> DATA_STRAY_CONVERSION_ID = DataWatcher.a(EntitySkeleton.class, DataWatcherRegistry.BOOLEAN);
    public static final String CONVERSION_TAG = "StrayConversionTime";
    private int inPowderSnowTime;
    public int conversionTime;

    public EntitySkeleton(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntitySkeleton.DATA_STRAY_CONVERSION_ID, false);
    }

    public boolean isStrayConverting() {
        return (Boolean) this.getDataWatcher().get(EntitySkeleton.DATA_STRAY_CONVERSION_ID);
    }

    public void v(boolean flag) {
        this.entityData.set(EntitySkeleton.DATA_STRAY_CONVERSION_ID, flag);
    }

    @Override
    public boolean fw() {
        return this.isStrayConverting();
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAI()) {
            if (this.isStrayConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.fy();
                }
            } else if (this.isInPowderSnow) {
                ++this.inPowderSnowTime;
                if (this.inPowderSnowTime >= 140) {
                    this.startStrayConversion(300);
                }
            } else {
                this.inPowderSnowTime = -1;
            }
        }

        super.tick();
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("StrayConversionTime", this.isStrayConverting() ? this.conversionTime : -1);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("StrayConversionTime", 99) && nbttagcompound.getInt("StrayConversionTime") > -1) {
            this.startStrayConversion(nbttagcompound.getInt("StrayConversionTime"));
        }

    }

    public void startStrayConversion(int i) {
        this.conversionTime = i;
        this.entityData.set(EntitySkeleton.DATA_STRAY_CONVERSION_ID, true);
    }

    protected void fy() {
        this.a(EntityTypes.STRAY, true);
        if (!this.isSilent()) {
            this.level.a((EntityHuman) null, 1048, this.getChunkCoordinates(), 0);
        }

    }

    @Override
    public boolean dg() {
        return false;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.SKELETON_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SKELETON_DEATH;
    }

    @Override
    SoundEffect p() {
        return SoundEffects.SKELETON_STEP;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a((IMaterial) Items.SKELETON_SKULL);
            }
        }

    }
}

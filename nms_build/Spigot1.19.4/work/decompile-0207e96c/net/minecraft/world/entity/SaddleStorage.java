package net.minecraft.world.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class SaddleStorage {

    private static final int MIN_BOOST_TIME = 140;
    private static final int MAX_BOOST_TIME = 700;
    private final DataWatcher entityData;
    private final DataWatcherObject<Integer> boostTimeAccessor;
    private final DataWatcherObject<Boolean> hasSaddleAccessor;
    public boolean boosting;
    public int boostTime;

    public SaddleStorage(DataWatcher datawatcher, DataWatcherObject<Integer> datawatcherobject, DataWatcherObject<Boolean> datawatcherobject1) {
        this.entityData = datawatcher;
        this.boostTimeAccessor = datawatcherobject;
        this.hasSaddleAccessor = datawatcherobject1;
    }

    public void onSynced() {
        this.boosting = true;
        this.boostTime = 0;
    }

    public boolean boost(RandomSource randomsource) {
        if (this.boosting) {
            return false;
        } else {
            this.boosting = true;
            this.boostTime = 0;
            this.entityData.set(this.boostTimeAccessor, randomsource.nextInt(841) + 140);
            return true;
        }
    }

    public void tickBoost() {
        if (this.boosting && this.boostTime++ > this.boostTimeTotal()) {
            this.boosting = false;
        }

    }

    public float boostFactor() {
        return this.boosting ? 1.0F + 1.15F * MathHelper.sin((float) this.boostTime / (float) this.boostTimeTotal() * 3.1415927F) : 1.0F;
    }

    public int boostTimeTotal() {
        return (Integer) this.entityData.get(this.boostTimeAccessor);
    }

    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putBoolean("Saddle", this.hasSaddle());
    }

    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    public void setSaddle(boolean flag) {
        this.entityData.set(this.hasSaddleAccessor, flag);
    }

    public boolean hasSaddle() {
        return (Boolean) this.entityData.get(this.hasSaddleAccessor);
    }
}

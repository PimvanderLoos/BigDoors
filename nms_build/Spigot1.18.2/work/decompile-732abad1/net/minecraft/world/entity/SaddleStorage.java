package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;

public class SaddleStorage {

    private static final int MIN_BOOST_TIME = 140;
    private static final int MAX_BOOST_TIME = 700;
    private final DataWatcher entityData;
    private final DataWatcherObject<Integer> boostTimeAccessor;
    private final DataWatcherObject<Boolean> hasSaddleAccessor;
    public boolean boosting;
    public int boostTime;
    public int boostTimeTotal;

    public SaddleStorage(DataWatcher datawatcher, DataWatcherObject<Integer> datawatcherobject, DataWatcherObject<Boolean> datawatcherobject1) {
        this.entityData = datawatcher;
        this.boostTimeAccessor = datawatcherobject;
        this.hasSaddleAccessor = datawatcherobject1;
    }

    public void onSynced() {
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = (Integer) this.entityData.get(this.boostTimeAccessor);
    }

    public boolean boost(Random random) {
        if (this.boosting) {
            return false;
        } else {
            this.boosting = true;
            this.boostTime = 0;
            this.boostTimeTotal = random.nextInt(841) + 140;
            this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
            return true;
        }
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

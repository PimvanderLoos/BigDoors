package net.minecraft.world.level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.WeightedRandom;

public class MobSpawnerData extends WeightedRandom.WeightedRandomChoice {

    private final NBTTagCompound entity;

    public MobSpawnerData() {
        super(1);
        this.entity = new NBTTagCompound();
        this.entity.setString("id", "minecraft:pig");
    }

    public MobSpawnerData(NBTTagCompound nbttagcompound) {
        this(nbttagcompound.hasKeyOfType("Weight", 99) ? nbttagcompound.getInt("Weight") : 1, nbttagcompound.getCompound("Entity"));
    }

    public MobSpawnerData(int i, NBTTagCompound nbttagcompound) {
        super(i);
        this.entity = nbttagcompound;
        MinecraftKey minecraftkey = MinecraftKey.a(nbttagcompound.getString("id"));

        if (minecraftkey != null) {
            nbttagcompound.setString("id", minecraftkey.toString());
        } else {
            nbttagcompound.setString("id", "minecraft:pig");
        }

    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.set("Entity", this.entity);
        nbttagcompound.setInt("Weight", this.a);
        return nbttagcompound;
    }

    public NBTTagCompound getEntity() {
        return this.entity;
    }
}

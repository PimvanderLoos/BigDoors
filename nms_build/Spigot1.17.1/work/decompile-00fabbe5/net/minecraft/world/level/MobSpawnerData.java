package net.minecraft.world.level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.random.WeightedEntry;

public class MobSpawnerData extends WeightedEntry.a {

    public static final int DEFAULT_WEIGHT = 1;
    public static final String DEFAULT_TYPE = "minecraft:pig";
    private final NBTTagCompound tag;

    public MobSpawnerData() {
        super(1);
        this.tag = new NBTTagCompound();
        this.tag.setString("id", "minecraft:pig");
    }

    public MobSpawnerData(NBTTagCompound nbttagcompound) {
        this(nbttagcompound.hasKeyOfType("Weight", 99) ? nbttagcompound.getInt("Weight") : 1, nbttagcompound.getCompound("Entity"));
    }

    public MobSpawnerData(int i, NBTTagCompound nbttagcompound) {
        super(i);
        this.tag = nbttagcompound;
        MinecraftKey minecraftkey = MinecraftKey.a(nbttagcompound.getString("id"));

        if (minecraftkey != null) {
            nbttagcompound.setString("id", minecraftkey.toString());
        } else {
            nbttagcompound.setString("id", "minecraft:pig");
        }

    }

    public NBTTagCompound b() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.set("Entity", this.tag);
        nbttagcompound.setInt("Weight", this.a().a());
        return nbttagcompound;
    }

    public NBTTagCompound getEntity() {
        return this.tag;
    }
}

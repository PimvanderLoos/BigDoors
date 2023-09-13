package net.minecraft.server;

public class DataConverterPotionWater implements IDataConverter {

    public DataConverterPotionWater() {}

    public int a() {
        return 806;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.getString("id");

        if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");

            if (!nbttagcompound1.hasKeyOfType("Potion", 8)) {
                nbttagcompound1.setString("Potion", "minecraft:water");
            }

            if (!nbttagcompound.hasKeyOfType("tag", 10)) {
                nbttagcompound.set("tag", nbttagcompound1);
            }
        }

        return nbttagcompound;
    }
}

package net.minecraft.server;

public class DataConverterCookedFish implements IDataConverter {

    private static final MinecraftKey a = new MinecraftKey("cooked_fished");

    public DataConverterCookedFish() {}

    public int a() {
        return 502;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("id", 8) && DataConverterCookedFish.a.equals(new MinecraftKey(nbttagcompound.getString("id")))) {
            nbttagcompound.setString("id", "minecraft:cooked_fish");
        }

        return nbttagcompound;
    }
}

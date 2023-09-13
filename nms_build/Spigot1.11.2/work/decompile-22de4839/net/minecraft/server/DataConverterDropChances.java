package net.minecraft.server;

public class DataConverterDropChances implements IDataConverter {

    public DataConverterDropChances() {}

    public int a() {
        return 113;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist;

        if (nbttagcompound.hasKeyOfType("HandDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("HandDropChances", 5);
            if (nbttaglist.size() == 2 && nbttaglist.f(0) == 0.0F && nbttaglist.f(1) == 0.0F) {
                nbttagcompound.remove("HandDropChances");
            }
        }

        if (nbttagcompound.hasKeyOfType("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);
            if (nbttaglist.size() == 4 && nbttaglist.f(0) == 0.0F && nbttaglist.f(1) == 0.0F && nbttaglist.f(2) == 0.0F && nbttaglist.f(3) == 0.0F) {
                nbttagcompound.remove("ArmorDropChances");
            }
        }

        return nbttagcompound;
    }
}

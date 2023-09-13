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
            if (nbttaglist.size() == 2 && nbttaglist.g(0) == 0.0F && nbttaglist.g(1) == 0.0F) {
                nbttagcompound.remove("HandDropChances");
            }
        }

        if (nbttagcompound.hasKeyOfType("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);
            if (nbttaglist.size() == 4 && nbttaglist.g(0) == 0.0F && nbttaglist.g(1) == 0.0F && nbttaglist.g(2) == 0.0F && nbttaglist.g(3) == 0.0F) {
                nbttagcompound.remove("ArmorDropChances");
            }
        }

        return nbttagcompound;
    }
}

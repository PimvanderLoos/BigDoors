package net.minecraft.server;

public class DataConverterShulkerBoxBlock implements IDataConverter {

    public DataConverterShulkerBoxBlock() {}

    public int a() {
        return 813;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:shulker".equals(nbttagcompound.getString("id"))) {
            nbttagcompound.remove("Color");
        }

        return nbttagcompound;
    }
}

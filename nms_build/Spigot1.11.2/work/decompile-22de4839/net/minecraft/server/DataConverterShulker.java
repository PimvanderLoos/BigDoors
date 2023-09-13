package net.minecraft.server;

public class DataConverterShulker implements IDataConverter {

    public DataConverterShulker() {}

    public int a() {
        return 808;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:shulker".equals(nbttagcompound.getString("id")) && !nbttagcompound.hasKeyOfType("Color", 99)) {
            nbttagcompound.setByte("Color", (byte) 10);
        }

        return nbttagcompound;
    }
}

package net.minecraft.server;

public class DataConverterBedItem implements IDataConverter {

    public DataConverterBedItem() {}

    public int a() {
        return 1125;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:bed".equals(nbttagcompound.getString("id")) && nbttagcompound.getShort("Damage") == 0) {
            nbttagcompound.setShort("Damage", (short) EnumColor.RED.getColorIndex());
        }

        return nbttagcompound;
    }
}

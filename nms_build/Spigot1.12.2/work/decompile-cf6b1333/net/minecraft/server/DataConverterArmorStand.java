package net.minecraft.server;

public class DataConverterArmorStand implements IDataConverter {

    public DataConverterArmorStand() {}

    public int a() {
        return 147;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("ArmorStand".equals(nbttagcompound.getString("id")) && nbttagcompound.getBoolean("Silent") && !nbttagcompound.getBoolean("Marker")) {
            nbttagcompound.remove("Silent");
        }

        return nbttagcompound;
    }
}

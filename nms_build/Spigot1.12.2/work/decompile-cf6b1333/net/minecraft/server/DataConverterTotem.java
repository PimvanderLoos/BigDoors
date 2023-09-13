package net.minecraft.server;

public class DataConverterTotem implements IDataConverter {

    public DataConverterTotem() {}

    public int a() {
        return 820;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:totem".equals(nbttagcompound.getString("id"))) {
            nbttagcompound.setString("id", "minecraft:totem_of_undying");
        }

        return nbttagcompound;
    }
}

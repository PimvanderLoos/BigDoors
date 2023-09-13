package net.minecraft.server;

public class DataConverterVBO implements IDataConverter {

    public DataConverterVBO() {}

    public int a() {
        return 505;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("useVbo", "true");
        return nbttagcompound;
    }
}

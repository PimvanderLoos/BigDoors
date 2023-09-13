package net.minecraft.server;

public class DataConverterGuardian implements IDataConverter {

    public DataConverterGuardian() {}

    public int a() {
        return 700;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("Guardian".equals(nbttagcompound.getString("id"))) {
            if (nbttagcompound.getBoolean("Elder")) {
                nbttagcompound.setString("id", "ElderGuardian");
            }

            nbttagcompound.remove("Elder");
        }

        return nbttagcompound;
    }
}

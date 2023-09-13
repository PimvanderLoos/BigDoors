package net.minecraft.server;

import java.util.UUID;

public class DataConverterUUID implements IDataConverter {

    public DataConverterUUID() {}

    public int a() {
        return 108;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("UUID", 8)) {
            nbttagcompound.a("UUID", UUID.fromString(nbttagcompound.getString("UUID")));
        }

        return nbttagcompound;
    }
}

package net.minecraft.server;

import java.util.Locale;

public class DataConverterLang implements IDataConverter {

    public DataConverterLang() {}

    public int a() {
        return 816;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("lang", 8)) {
            nbttagcompound.setString("lang", nbttagcompound.getString("lang").toLowerCase(Locale.ROOT));
        }

        return nbttagcompound;
    }
}

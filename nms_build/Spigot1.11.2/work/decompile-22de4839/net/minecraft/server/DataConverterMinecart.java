package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class DataConverterMinecart implements IDataConverter {

    private static final List<String> a = Lists.newArrayList(new String[] { "MinecartRideable", "MinecartChest", "MinecartFurnace", "MinecartTNT", "MinecartSpawner", "MinecartHopper", "MinecartCommandBlock"});

    public DataConverterMinecart() {}

    public int a() {
        return 106;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("Minecart".equals(nbttagcompound.getString("id"))) {
            String s = "MinecartRideable";
            int i = nbttagcompound.getInt("Type");

            if (i > 0 && i < DataConverterMinecart.a.size()) {
                s = (String) DataConverterMinecart.a.get(i);
            }

            nbttagcompound.setString("id", s);
            nbttagcompound.remove("Type");
        }

        return nbttagcompound;
    }
}

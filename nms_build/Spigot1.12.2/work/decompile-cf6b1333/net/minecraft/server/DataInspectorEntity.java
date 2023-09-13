package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataInspectorEntity implements DataInspector {

    private static final Logger a = LogManager.getLogger();

    public DataInspectorEntity() {}

    public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");

        if (nbttagcompound1.hasKeyOfType("EntityTag", 10)) {
            NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("EntityTag");
            String s = nbttagcompound.getString("id");
            String s1;

            if ("minecraft:armor_stand".equals(s)) {
                s1 = i < 515 ? "ArmorStand" : "minecraft:armor_stand";
            } else {
                if (!"minecraft:spawn_egg".equals(s)) {
                    return nbttagcompound;
                }

                s1 = nbttagcompound2.getString("id");
            }

            boolean flag;

            if (s1 == null) {
                DataInspectorEntity.a.warn("Unable to resolve Entity for ItemInstance: {}", s);
                flag = false;
            } else {
                flag = !nbttagcompound2.hasKeyOfType("id", 8);
                nbttagcompound2.setString("id", s1);
            }

            dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound2, i);
            if (flag) {
                nbttagcompound2.remove("id");
            }
        }

        return nbttagcompound;
    }
}

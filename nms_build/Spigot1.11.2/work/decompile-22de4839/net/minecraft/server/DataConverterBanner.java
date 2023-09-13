package net.minecraft.server;

public class DataConverterBanner implements IDataConverter {

    public DataConverterBanner() {}

    public int a() {
        return 804;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:banner".equals(nbttagcompound.getString("id")) && nbttagcompound.hasKeyOfType("tag", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");

            if (nbttagcompound1.hasKeyOfType("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("BlockEntityTag");

                if (nbttagcompound2.hasKeyOfType("Base", 99)) {
                    nbttagcompound.setShort("Damage", (short) (nbttagcompound2.getShort("Base") & 15));
                    if (nbttagcompound1.hasKeyOfType("display", 10)) {
                        NBTTagCompound nbttagcompound3 = nbttagcompound1.getCompound("display");

                        if (nbttagcompound3.hasKeyOfType("Lore", 9)) {
                            NBTTagList nbttaglist = nbttagcompound3.getList("Lore", 8);

                            if (nbttaglist.size() == 1 && "(+NBT)".equals(nbttaglist.getString(0))) {
                                return nbttagcompound;
                            }
                        }
                    }

                    nbttagcompound2.remove("Base");
                    if (nbttagcompound2.isEmpty()) {
                        nbttagcompound1.remove("BlockEntityTag");
                    }

                    if (nbttagcompound1.isEmpty()) {
                        nbttagcompound.remove("tag");
                    }
                }
            }
        }

        return nbttagcompound;
    }
}

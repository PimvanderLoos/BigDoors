package net.minecraft.server;

public class DataConverterEquipment implements IDataConverter {

    public DataConverterEquipment() {}

    public int a() {
        return 100;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("Equipment", 10);
        NBTTagList nbttaglist1;

        if (!nbttaglist.isEmpty() && !nbttagcompound.hasKeyOfType("HandItems", 10)) {
            nbttaglist1 = new NBTTagList();
            nbttaglist1.add(nbttaglist.i(0));
            nbttaglist1.add(new NBTTagCompound());
            nbttagcompound.set("HandItems", nbttaglist1);
        }

        if (nbttaglist.size() > 1 && !nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            nbttaglist1 = new NBTTagList();
            nbttaglist1.add(nbttaglist.get(1));
            nbttaglist1.add(nbttaglist.get(2));
            nbttaglist1.add(nbttaglist.get(3));
            nbttaglist1.add(nbttaglist.get(4));
            nbttagcompound.set("ArmorItems", nbttaglist1);
        }

        nbttagcompound.remove("Equipment");
        if (nbttagcompound.hasKeyOfType("DropChances", 9)) {
            nbttaglist1 = nbttagcompound.getList("DropChances", 5);
            NBTTagList nbttaglist2;

            if (!nbttagcompound.hasKeyOfType("HandDropChances", 10)) {
                nbttaglist2 = new NBTTagList();
                nbttaglist2.add(new NBTTagFloat(nbttaglist1.g(0)));
                nbttaglist2.add(new NBTTagFloat(0.0F));
                nbttagcompound.set("HandDropChances", nbttaglist2);
            }

            if (!nbttagcompound.hasKeyOfType("ArmorDropChances", 10)) {
                nbttaglist2 = new NBTTagList();
                nbttaglist2.add(new NBTTagFloat(nbttaglist1.g(1)));
                nbttaglist2.add(new NBTTagFloat(nbttaglist1.g(2)));
                nbttaglist2.add(new NBTTagFloat(nbttaglist1.g(3)));
                nbttaglist2.add(new NBTTagFloat(nbttaglist1.g(4)));
                nbttagcompound.set("ArmorDropChances", nbttaglist2);
            }

            nbttagcompound.remove("DropChances");
        }

        return nbttagcompound;
    }
}

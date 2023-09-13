package net.minecraft.server;

public class DataConverterMobSpawner implements IDataConverter {

    public DataConverterMobSpawner() {}

    public int a() {
        return 107;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (!"MobSpawner".equals(nbttagcompound.getString("id"))) {
            return nbttagcompound;
        } else {
            if (nbttagcompound.hasKeyOfType("EntityId", 8)) {
                String s = nbttagcompound.getString("EntityId");
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("SpawnData");

                nbttagcompound1.setString("id", s.isEmpty() ? "Pig" : s);
                nbttagcompound.set("SpawnData", nbttagcompound1);
                nbttagcompound.remove("EntityId");
            }

            if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound2 = nbttaglist.get(i);

                    if (nbttagcompound2.hasKeyOfType("Type", 8)) {
                        NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompound("Properties");

                        nbttagcompound3.setString("id", nbttagcompound2.getString("Type"));
                        nbttagcompound2.set("Entity", nbttagcompound3);
                        nbttagcompound2.remove("Type");
                        nbttagcompound2.remove("Properties");
                    }
                }
            }

            return nbttagcompound;
        }
    }
}

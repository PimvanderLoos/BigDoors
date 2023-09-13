package net.minecraft.server;

import java.util.Random;

public class DataConverterZombie implements IDataConverter {

    private static final Random a = new Random();

    public DataConverterZombie() {}

    public int a() {
        return 502;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("Zombie".equals(nbttagcompound.getString("id")) && nbttagcompound.getBoolean("IsVillager")) {
            if (!nbttagcompound.hasKeyOfType("ZombieType", 99)) {
                int i = -1;

                if (nbttagcompound.hasKeyOfType("VillagerProfession", 99)) {
                    try {
                        i = this.a(nbttagcompound.getInt("VillagerProfession"));
                    } catch (RuntimeException runtimeexception) {
                        ;
                    }
                }

                if (i == -1) {
                    i = this.a(DataConverterZombie.a.nextInt(6));
                }

                nbttagcompound.setInt("ZombieType", i);
            }

            nbttagcompound.remove("IsVillager");
        }

        return nbttagcompound;
    }

    private int a(int i) {
        return i >= 0 && i < 6 ? i : -1;
    }
}

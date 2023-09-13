package net.minecraft.server;

public class DataConverterZombieType implements IDataConverter {

    public DataConverterZombieType() {}

    public int a() {
        return 702;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("Zombie".equals(nbttagcompound.getString("id"))) {
            int i = nbttagcompound.getInt("ZombieType");

            switch (i) {
            case 0:
            default:
                break;

            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                nbttagcompound.setString("id", "ZombieVillager");
                nbttagcompound.setInt("Profession", i - 1);
                break;

            case 6:
                nbttagcompound.setString("id", "Husk");
            }

            nbttagcompound.remove("ZombieType");
        }

        return nbttagcompound;
    }
}

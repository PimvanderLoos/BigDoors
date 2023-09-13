package net.minecraft.server;

public class DataConverterHorse implements IDataConverter {

    public DataConverterHorse() {}

    public int a() {
        return 703;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("EntityHorse".equals(nbttagcompound.getString("id"))) {
            int i = nbttagcompound.getInt("Type");

            switch (i) {
            case 0:
            default:
                nbttagcompound.setString("id", "Horse");
                break;

            case 1:
                nbttagcompound.setString("id", "Donkey");
                break;

            case 2:
                nbttagcompound.setString("id", "Mule");
                break;

            case 3:
                nbttagcompound.setString("id", "ZombieHorse");
                break;

            case 4:
                nbttagcompound.setString("id", "SkeletonHorse");
            }

            nbttagcompound.remove("Type");
        }

        return nbttagcompound;
    }
}

package net.minecraft.server;

public class DataConverterSaddle implements IDataConverter {

    public DataConverterSaddle() {}

    public int a() {
        return 110;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("EntityHorse".equals(nbttagcompound.getString("id")) && !nbttagcompound.hasKeyOfType("SaddleItem", 10) && nbttagcompound.getBoolean("Saddle")) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setString("id", "minecraft:saddle");
            nbttagcompound1.setByte("Count", (byte) 1);
            nbttagcompound1.setShort("Damage", (short) 0);
            nbttagcompound.set("SaddleItem", nbttagcompound1);
            nbttagcompound.remove("Saddle");
        }

        return nbttagcompound;
    }
}

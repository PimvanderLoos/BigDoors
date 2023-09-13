package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataConverterBedBlock implements IDataConverter {

    private static final Logger a = LogManager.getLogger();

    public DataConverterBedBlock() {}

    public int a() {
        return 1125;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        boolean flag = true;

        try {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
            int i = nbttagcompound1.getInt("xPos");
            int j = nbttagcompound1.getInt("zPos");
            NBTTagList nbttaglist = nbttagcompound1.getList("TileEntities", 10);
            NBTTagList nbttaglist1 = nbttagcompound1.getList("Sections", 10);

            for (int k = 0; k < nbttaglist1.size(); ++k) {
                NBTTagCompound nbttagcompound2 = nbttaglist1.get(k);
                byte b0 = nbttagcompound2.getByte("Y");
                byte[] abyte = nbttagcompound2.getByteArray("Blocks");

                for (int l = 0; l < abyte.length; ++l) {
                    if (416 == (abyte[l] & 255) << 4) {
                        int i1 = l & 15;
                        int j1 = l >> 8 & 15;
                        int k1 = l >> 4 & 15;
                        NBTTagCompound nbttagcompound3 = new NBTTagCompound();

                        nbttagcompound3.setString("id", "bed");
                        nbttagcompound3.setInt("x", i1 + (i << 4));
                        nbttagcompound3.setInt("y", j1 + (b0 << 4));
                        nbttagcompound3.setInt("z", k1 + (j << 4));
                        nbttaglist.add(nbttagcompound3);
                    }
                }
            }
        } catch (Exception exception) {
            DataConverterBedBlock.a.warn("Unable to datafix Bed blocks, level format may be missing tags.");
        }

        return nbttagcompound;
    }
}

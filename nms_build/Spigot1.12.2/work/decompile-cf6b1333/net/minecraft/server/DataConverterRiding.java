package net.minecraft.server;

public class DataConverterRiding implements IDataConverter {

    public DataConverterRiding() {}

    public int a() {
        return 135;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        while (nbttagcompound.hasKeyOfType("Riding", 10)) {
            NBTTagCompound nbttagcompound1 = this.b(nbttagcompound);

            this.a(nbttagcompound, nbttagcompound1);
            nbttagcompound = nbttagcompound1;
        }

        return nbttagcompound;
    }

    protected void a(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        NBTTagList nbttaglist = new NBTTagList();

        nbttaglist.add(nbttagcompound);
        nbttagcompound1.set("Passengers", nbttaglist);
    }

    protected NBTTagCompound b(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Riding");

        nbttagcompound.remove("Riding");
        return nbttagcompound1;
    }
}

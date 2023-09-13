package net.minecraft.server;

public class TileEntityComparator extends TileEntity {

    private int a;

    public TileEntityComparator() {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("OutputSignal", this.a);
        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a = nbttagcompound.getInt("OutputSignal");
    }

    public int a() {
        return this.a;
    }

    public void a(int i) {
        this.a = i;
    }
}

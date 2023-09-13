package net.minecraft.world.level.block.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityComparator extends TileEntity {

    private int a;

    public TileEntityComparator() {
        super(TileEntityTypes.COMPARATOR);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("OutputSignal", this.a);
        return nbttagcompound;
    }

    @Override
    public void load(IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        super.load(iblockdata, nbttagcompound);
        this.a = nbttagcompound.getInt("OutputSignal");
    }

    public int d() {
        return this.a;
    }

    public void a(int i) {
        this.a = i;
    }
}

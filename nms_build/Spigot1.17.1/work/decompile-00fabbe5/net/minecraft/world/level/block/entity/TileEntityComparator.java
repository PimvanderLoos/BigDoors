package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityComparator extends TileEntity {

    private int output;

    public TileEntityComparator(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.COMPARATOR, blockposition, iblockdata);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("OutputSignal", this.output);
        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.output = nbttagcompound.getInt("OutputSignal");
    }

    public int d() {
        return this.output;
    }

    public void a(int i) {
        this.output = i;
    }
}

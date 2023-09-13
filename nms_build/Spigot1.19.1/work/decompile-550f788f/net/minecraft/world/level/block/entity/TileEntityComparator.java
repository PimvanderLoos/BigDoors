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
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putInt("OutputSignal", this.output);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.output = nbttagcompound.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.output;
    }

    public void setOutputSignal(int i) {
        this.output = i;
    }
}

package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityJukeBox extends TileEntity implements Clearable {

    private ItemStack record;

    public TileEntityJukeBox(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.JUKEBOX, blockposition, iblockdata);
        this.record = ItemStack.EMPTY;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.of(nbttagcompound.getCompound("RecordItem")));
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.getRecord().isEmpty()) {
            nbttagcompound.put("RecordItem", this.getRecord().save(new NBTTagCompound()));
        }

    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack itemstack) {
        this.record = itemstack;
        this.setChanged();
    }

    @Override
    public void clearContent() {
        this.setRecord(ItemStack.EMPTY);
    }
}

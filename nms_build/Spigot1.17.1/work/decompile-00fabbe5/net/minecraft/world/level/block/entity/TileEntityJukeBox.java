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
        if (nbttagcompound.hasKeyOfType("RecordItem", 10)) {
            this.setRecord(ItemStack.a(nbttagcompound.getCompound("RecordItem")));
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.getRecord().isEmpty()) {
            nbttagcompound.set("RecordItem", this.getRecord().save(new NBTTagCompound()));
        }

        return nbttagcompound;
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack itemstack) {
        this.record = itemstack;
        this.update();
    }

    @Override
    public void clear() {
        this.setRecord(ItemStack.EMPTY);
    }
}

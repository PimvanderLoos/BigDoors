package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityEnderChest;

public class InventoryEnderChest extends InventorySubcontainer {

    @Nullable
    private TileEntityEnderChest activeChest;

    public InventoryEnderChest() {
        super(27);
    }

    public void setActiveChest(TileEntityEnderChest tileentityenderchest) {
        this.activeChest = tileentityenderchest;
    }

    public boolean isActiveChest(TileEntityEnderChest tileentityenderchest) {
        return this.activeChest == tileentityenderchest;
    }

    @Override
    public void fromTag(NBTTagList nbttaglist) {
        int i;

        for (i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.getContainerSize()) {
                this.setItem(j, ItemStack.of(nbttagcompound));
            }
        }

    }

    @Override
    public NBTTagList createTag() {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.putByte("Slot", (byte) i);
                itemstack.save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.activeChest != null && !this.activeChest.stillValid(entityhuman) ? false : super.stillValid(entityhuman);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (this.activeChest != null) {
            this.activeChest.startOpen(entityhuman);
        }

        super.startOpen(entityhuman);
    }

    @Override
    public void stopOpen(EntityHuman entityhuman) {
        if (this.activeChest != null) {
            this.activeChest.stopOpen(entityhuman);
        }

        super.stopOpen(entityhuman);
        this.activeChest = null;
    }
}

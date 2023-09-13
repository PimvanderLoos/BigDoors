package net.minecraft.world.inventory;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityFurnace;

public class SlotFurnaceResult extends Slot {

    private final EntityHuman player;
    private int removeCount;

    public SlotFurnaceResult(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.player = entityhuman;
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.removeCount += Math.min(i, this.getItem().getCount());
        }

        return super.a(i);
    }

    @Override
    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.b_(itemstack);
        super.a(entityhuman, itemstack);
    }

    @Override
    protected void a(ItemStack itemstack, int i) {
        this.removeCount += i;
        this.b_(itemstack);
    }

    @Override
    protected void b_(ItemStack itemstack) {
        itemstack.a(this.player.level, this.player, this.removeCount);
        if (this.player instanceof EntityPlayer && this.container instanceof TileEntityFurnace) {
            ((TileEntityFurnace) this.container).a((EntityPlayer) this.player);
        }

        this.removeCount = 0;
    }
}

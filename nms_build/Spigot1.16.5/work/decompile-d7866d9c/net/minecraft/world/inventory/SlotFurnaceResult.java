package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityFurnace;

public class SlotFurnaceResult extends Slot {

    private final EntityHuman a;
    private int b;

    public SlotFurnaceResult(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.a = entityhuman;
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.b += Math.min(i, this.getItem().getCount());
        }

        return super.a(i);
    }

    @Override
    public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
        this.c(itemstack);
        super.a(entityhuman, itemstack);
        return itemstack;
    }

    @Override
    protected void a(ItemStack itemstack, int i) {
        this.b += i;
        this.c(itemstack);
    }

    @Override
    protected void c(ItemStack itemstack) {
        itemstack.a(this.a.world, this.a, this.b);
        if (!this.a.world.isClientSide && this.inventory instanceof TileEntityFurnace) {
            ((TileEntityFurnace) this.inventory).d(this.a);
        }

        this.b = 0;
    }
}

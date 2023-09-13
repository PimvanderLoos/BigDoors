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
    public boolean mayPlace(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack remove(int i) {
        if (this.hasItem()) {
            this.removeCount += Math.min(i, this.getItem().getCount());
        }

        return super.remove(i);
    }

    @Override
    public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
        this.checkTakeAchievements(itemstack);
        super.onTake(entityhuman, itemstack);
    }

    @Override
    protected void onQuickCraft(ItemStack itemstack, int i) {
        this.removeCount += i;
        this.checkTakeAchievements(itemstack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemstack) {
        itemstack.onCraftedBy(this.player.level, this.player, this.removeCount);
        if (this.player instanceof EntityPlayer && this.container instanceof TileEntityFurnace) {
            ((TileEntityFurnace) this.container).awardUsedRecipesAndPopExperience((EntityPlayer) this.player);
        }

        this.removeCount = 0;
    }
}

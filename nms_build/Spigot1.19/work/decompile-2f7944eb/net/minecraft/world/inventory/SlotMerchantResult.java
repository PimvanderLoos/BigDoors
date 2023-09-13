package net.minecraft.world.inventory;

import net.minecraft.stats.StatisticList;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;

public class SlotMerchantResult extends Slot {

    private final InventoryMerchant slots;
    private final EntityHuman player;
    private int removeCount;
    private final IMerchant merchant;

    public SlotMerchantResult(EntityHuman entityhuman, IMerchant imerchant, InventoryMerchant inventorymerchant, int i, int j, int k) {
        super(inventorymerchant, i, j, k);
        this.player = entityhuman;
        this.merchant = imerchant;
        this.slots = inventorymerchant;
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
    protected void onQuickCraft(ItemStack itemstack, int i) {
        this.removeCount += i;
        this.checkTakeAchievements(itemstack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemstack) {
        itemstack.onCraftedBy(this.player.level, this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
        this.checkTakeAchievements(itemstack);
        MerchantRecipe merchantrecipe = this.slots.getActiveOffer();

        if (merchantrecipe != null) {
            ItemStack itemstack1 = this.slots.getItem(0);
            ItemStack itemstack2 = this.slots.getItem(1);

            if (merchantrecipe.take(itemstack1, itemstack2) || merchantrecipe.take(itemstack2, itemstack1)) {
                this.merchant.notifyTrade(merchantrecipe);
                entityhuman.awardStat(StatisticList.TRADED_WITH_VILLAGER);
                this.slots.setItem(0, itemstack1);
                this.slots.setItem(1, itemstack2);
            }

            this.merchant.overrideXp(this.merchant.getVillagerXp() + merchantrecipe.getXp());
        }

    }
}

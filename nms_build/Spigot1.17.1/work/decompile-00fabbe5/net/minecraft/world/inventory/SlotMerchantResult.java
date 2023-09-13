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
    protected void a(ItemStack itemstack, int i) {
        this.removeCount += i;
        this.b_(itemstack);
    }

    @Override
    protected void b_(ItemStack itemstack) {
        itemstack.a(this.player.level, this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.b_(itemstack);
        MerchantRecipe merchantrecipe = this.slots.getRecipe();

        if (merchantrecipe != null) {
            ItemStack itemstack1 = this.slots.getItem(0);
            ItemStack itemstack2 = this.slots.getItem(1);

            if (merchantrecipe.b(itemstack1, itemstack2) || merchantrecipe.b(itemstack2, itemstack1)) {
                this.merchant.a(merchantrecipe);
                entityhuman.a(StatisticList.TRADED_WITH_VILLAGER);
                this.slots.setItem(0, itemstack1);
                this.slots.setItem(1, itemstack2);
            }

            this.merchant.setForcedExperience(this.merchant.getExperience() + merchantrecipe.getXp());
        }

    }
}

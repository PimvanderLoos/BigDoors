package net.minecraft.world.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class InventoryMerchant implements IInventory {

    private final IMerchant merchant;
    private final NonNullList<ItemStack> itemStacks;
    @Nullable
    private MerchantRecipe activeOffer;
    public int selectionHint;
    private int futureXp;

    public InventoryMerchant(IMerchant imerchant) {
        this.itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
        this.merchant = imerchant;
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.itemStacks.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return (ItemStack) this.itemStacks.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemstack = (ItemStack) this.itemStacks.get(i);

        if (i == 2 && !itemstack.isEmpty()) {
            return ContainerUtil.removeItem(this.itemStacks, i, itemstack.getCount());
        } else {
            ItemStack itemstack1 = ContainerUtil.removeItem(this.itemStacks, i, j);

            if (!itemstack1.isEmpty() && this.isPaymentSlot(i)) {
                this.updateSellItem();
            }

            return itemstack1;
        }
    }

    private boolean isPaymentSlot(int i) {
        return i == 0 || i == 1;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerUtil.takeItem(this.itemStacks, i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.itemStacks.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        if (this.isPaymentSlot(i)) {
            this.updateSellItem();
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.merchant.getTradingPlayer() == entityhuman;
    }

    @Override
    public void setChanged() {
        this.updateSellItem();
    }

    public void updateSellItem() {
        this.activeOffer = null;
        ItemStack itemstack;
        ItemStack itemstack1;

        if (((ItemStack) this.itemStacks.get(0)).isEmpty()) {
            itemstack = (ItemStack) this.itemStacks.get(1);
            itemstack1 = ItemStack.EMPTY;
        } else {
            itemstack = (ItemStack) this.itemStacks.get(0);
            itemstack1 = (ItemStack) this.itemStacks.get(1);
        }

        if (itemstack.isEmpty()) {
            this.setItem(2, ItemStack.EMPTY);
            this.futureXp = 0;
        } else {
            MerchantRecipeList merchantrecipelist = this.merchant.getOffers();

            if (!merchantrecipelist.isEmpty()) {
                MerchantRecipe merchantrecipe = merchantrecipelist.getRecipeFor(itemstack, itemstack1, this.selectionHint);

                if (merchantrecipe == null || merchantrecipe.isOutOfStock()) {
                    this.activeOffer = merchantrecipe;
                    merchantrecipe = merchantrecipelist.getRecipeFor(itemstack1, itemstack, this.selectionHint);
                }

                if (merchantrecipe != null && !merchantrecipe.isOutOfStock()) {
                    this.activeOffer = merchantrecipe;
                    this.setItem(2, merchantrecipe.assemble());
                    this.futureXp = merchantrecipe.getXp();
                } else {
                    this.setItem(2, ItemStack.EMPTY);
                    this.futureXp = 0;
                }
            }

            this.merchant.notifyTradeUpdated(this.getItem(2));
        }
    }

    @Nullable
    public MerchantRecipe getActiveOffer() {
        return this.activeOffer;
    }

    public void setSelectionHint(int i) {
        this.selectionHint = i;
        this.updateSellItem();
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    public int getFutureXp() {
        return this.futureXp;
    }
}

package net.minecraft.server;

public class SlotMerchantResult extends Slot {

    private final InventoryMerchant a;
    private final EntityHuman b;
    private int c;
    private final IMerchant h;

    public SlotMerchantResult(EntityHuman entityhuman, IMerchant imerchant, InventoryMerchant inventorymerchant, int i, int j, int k) {
        super(inventorymerchant, i, j, k);
        this.b = entityhuman;
        this.h = imerchant;
        this.a = inventorymerchant;
    }

    public boolean isAllowed(ItemStack itemstack) {
        return false;
    }

    public ItemStack a(int i) {
        if (this.hasItem()) {
            this.c += Math.min(i, this.getItem().getCount());
        }

        return super.a(i);
    }

    protected void a(ItemStack itemstack, int i) {
        this.c += i;
        this.c(itemstack);
    }

    protected void c(ItemStack itemstack) {
        itemstack.a(this.b.world, this.b, this.c);
        this.c = 0;
    }

    public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
        this.c(itemstack);
        MerchantRecipe merchantrecipe = this.a.getRecipe();

        if (merchantrecipe != null) {
            ItemStack itemstack1 = this.a.getItem(0);
            ItemStack itemstack2 = this.a.getItem(1);

            if (this.a(merchantrecipe, itemstack1, itemstack2) || this.a(merchantrecipe, itemstack2, itemstack1)) {
                this.h.a(merchantrecipe);
                entityhuman.a(StatisticList.TRADED_WITH_VILLAGER);
                this.a.setItem(0, itemstack1);
                this.a.setItem(1, itemstack2);
            }
        }

        return itemstack;
    }

    private boolean a(MerchantRecipe merchantrecipe, ItemStack itemstack, ItemStack itemstack1) {
        ItemStack itemstack2 = merchantrecipe.getBuyItem1();
        ItemStack itemstack3 = merchantrecipe.getBuyItem2();

        if (itemstack.getItem() == itemstack2.getItem() && itemstack.getCount() >= itemstack2.getCount()) {
            if (!itemstack3.isEmpty() && !itemstack1.isEmpty() && itemstack3.getItem() == itemstack1.getItem() && itemstack1.getCount() >= itemstack3.getCount()) {
                itemstack.subtract(itemstack2.getCount());
                itemstack1.subtract(itemstack3.getCount());
                return true;
            }

            if (itemstack3.isEmpty() && itemstack1.isEmpty()) {
                itemstack.subtract(itemstack2.getCount());
                return true;
            }
        }

        return false;
    }
}

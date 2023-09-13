package net.minecraft.world.inventory;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.MerchantWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class ContainerMerchant extends Container {

    protected static final int PAYMENT1_SLOT = 0;
    protected static final int PAYMENT2_SLOT = 1;
    protected static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private static final int SELLSLOT1_X = 136;
    private static final int SELLSLOT2_X = 162;
    private static final int BUYSLOT_X = 220;
    private static final int ROW_Y = 37;
    private final IMerchant trader;
    private final InventoryMerchant tradeContainer;
    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;

    public ContainerMerchant(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, new MerchantWrapper(playerinventory.player));
    }

    public ContainerMerchant(int i, PlayerInventory playerinventory, IMerchant imerchant) {
        super(Containers.MERCHANT, i);
        this.trader = imerchant;
        this.tradeContainer = new InventoryMerchant(imerchant);
        this.a(new Slot(this.tradeContainer, 0, 136, 37));
        this.a(new Slot(this.tradeContainer, 1, 162, 37));
        this.a((Slot) (new SlotMerchantResult(playerinventory.player, imerchant, this.tradeContainer, 2, 220, 37)));

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 108 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 108 + j * 18, 142));
        }

    }

    public void a(boolean flag) {
        this.showProgressBar = flag;
    }

    @Override
    public void a(IInventory iinventory) {
        this.tradeContainer.f();
        super.a(iinventory);
    }

    public void d(int i) {
        this.tradeContainer.c(i);
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return this.trader.getTrader() == entityhuman;
    }

    public int l() {
        return this.trader.getExperience();
    }

    public int m() {
        return this.tradeContainer.h();
    }

    public void e(int i) {
        this.trader.setForcedExperience(i);
    }

    public int n() {
        return this.merchantLevel;
    }

    public void f(int i) {
        this.merchantLevel = i;
    }

    public void b(boolean flag) {
        this.canRestock = flag;
    }

    public boolean o() {
        return this.canRestock;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
                this.r();
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 30) {
                    if (!this.a(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    private void r() {
        if (!this.trader.getWorld().isClientSide) {
            Entity entity = (Entity) this.trader;

            this.trader.getWorld().a(entity.locX(), entity.locY(), entity.locZ(), this.trader.getTradeSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
        }

    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.trader.setTradingPlayer((EntityHuman) null);
        if (!this.trader.getWorld().isClientSide) {
            if (entityhuman.isAlive() && (!(entityhuman instanceof EntityPlayer) || !((EntityPlayer) entityhuman).q())) {
                if (entityhuman instanceof EntityPlayer) {
                    entityhuman.getInventory().f(this.tradeContainer.splitWithoutUpdate(0));
                    entityhuman.getInventory().f(this.tradeContainer.splitWithoutUpdate(1));
                }
            } else {
                ItemStack itemstack = this.tradeContainer.splitWithoutUpdate(0);

                if (!itemstack.isEmpty()) {
                    entityhuman.drop(itemstack, false);
                }

                itemstack = this.tradeContainer.splitWithoutUpdate(1);
                if (!itemstack.isEmpty()) {
                    entityhuman.drop(itemstack, false);
                }
            }

        }
    }

    public void g(int i) {
        if (this.p().size() > i) {
            ItemStack itemstack = this.tradeContainer.getItem(0);

            if (!itemstack.isEmpty()) {
                if (!this.a(itemstack, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(0, itemstack);
            }

            ItemStack itemstack1 = this.tradeContainer.getItem(1);

            if (!itemstack1.isEmpty()) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, itemstack1);
            }

            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                ItemStack itemstack2 = ((MerchantRecipe) this.p().get(i)).getBuyItem1();

                this.c(0, itemstack2);
                ItemStack itemstack3 = ((MerchantRecipe) this.p().get(i)).getBuyItem2();

                this.c(1, itemstack3);
            }

        }
    }

    private void c(int i, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            for (int j = 3; j < 39; ++j) {
                ItemStack itemstack1 = ((Slot) this.slots.get(j)).getItem();

                if (!itemstack1.isEmpty() && ItemStack.e(itemstack, itemstack1)) {
                    ItemStack itemstack2 = this.tradeContainer.getItem(i);
                    int k = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    int l = Math.min(itemstack.getMaxStackSize() - k, itemstack1.getCount());
                    ItemStack itemstack3 = itemstack1.cloneItemStack();
                    int i1 = k + l;

                    itemstack1.subtract(l);
                    itemstack3.setCount(i1);
                    this.tradeContainer.setItem(i, itemstack3);
                    if (i1 >= itemstack.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }

    }

    public void a(MerchantRecipeList merchantrecipelist) {
        this.trader.a(merchantrecipelist);
    }

    public MerchantRecipeList p() {
        return this.trader.getOffers();
    }

    public boolean q() {
        return this.showProgressBar;
    }
}

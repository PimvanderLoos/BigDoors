package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.InventoryMerchant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;

public class MerchantWrapper implements IMerchant {

    private final InventoryMerchant a;
    private final EntityHuman b;
    private MerchantRecipeList c = new MerchantRecipeList();
    private int d;

    public MerchantWrapper(EntityHuman entityhuman) {
        this.b = entityhuman;
        this.a = new InventoryMerchant(this);
    }

    @Nullable
    @Override
    public EntityHuman getTrader() {
        return this.b;
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {}

    @Override
    public MerchantRecipeList getOffers() {
        return this.c;
    }

    @Override
    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
    }

    @Override
    public void k(ItemStack itemstack) {}

    @Override
    public World getWorld() {
        return this.b.world;
    }

    @Override
    public int getExperience() {
        return this.d;
    }

    @Override
    public void setForcedExperience(int i) {
        this.d = i;
    }

    @Override
    public boolean isRegularVillager() {
        return true;
    }

    @Override
    public SoundEffect getTradeSound() {
        return SoundEffects.ENTITY_VILLAGER_YES;
    }
}

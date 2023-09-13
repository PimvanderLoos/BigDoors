package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class MerchantWrapper implements IMerchant {

    private final EntityHuman source;
    private MerchantRecipeList offers = new MerchantRecipeList();
    private int xp;

    public MerchantWrapper(EntityHuman entityhuman) {
        this.source = entityhuman;
    }

    @Override
    public EntityHuman getTradingPlayer() {
        return this.source;
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {}

    @Override
    public MerchantRecipeList getOffers() {
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantRecipeList merchantrecipelist) {
        this.offers = merchantrecipelist;
    }

    @Override
    public void notifyTrade(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
    }

    @Override
    public void notifyTradeUpdated(ItemStack itemstack) {}

    @Override
    public boolean isClientSide() {
        return this.source.getLevel().isClientSide;
    }

    @Override
    public int getVillagerXp() {
        return this.xp;
    }

    @Override
    public void overrideXp(int i) {
        this.xp = i;
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public SoundEffect getNotifyTradeSound() {
        return SoundEffects.VILLAGER_YES;
    }
}

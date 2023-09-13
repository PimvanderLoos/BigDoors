package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;

public class MerchantWrapper implements IMerchant {

    private final EntityHuman source;
    private MerchantRecipeList offers = new MerchantRecipeList();
    private int xp;

    public MerchantWrapper(EntityHuman entityhuman) {
        this.source = entityhuman;
    }

    @Override
    public EntityHuman getTrader() {
        return this.source;
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {}

    @Override
    public MerchantRecipeList getOffers() {
        return this.offers;
    }

    @Override
    public void a(MerchantRecipeList merchantrecipelist) {
        this.offers = merchantrecipelist;
    }

    @Override
    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
    }

    @Override
    public void m(ItemStack itemstack) {}

    @Override
    public World getWorld() {
        return this.source.level;
    }

    @Override
    public int getExperience() {
        return this.xp;
    }

    @Override
    public void setForcedExperience(int i) {
        this.xp = i;
    }

    @Override
    public boolean isRegularVillager() {
        return true;
    }

    @Override
    public SoundEffect getTradeSound() {
        return SoundEffects.VILLAGER_YES;
    }
}

package net.minecraft.server;

import javax.annotation.Nullable;

public interface IMerchant {

    void setTradingPlayer(EntityHuman entityhuman);

    EntityHuman getTrader();

    @Nullable
    MerchantRecipeList getOffers(EntityHuman entityhuman);

    void a(MerchantRecipe merchantrecipe);

    void a(ItemStack itemstack);

    IChatBaseComponent getScoreboardDisplayName();

    World t_();

    BlockPosition u_();
}

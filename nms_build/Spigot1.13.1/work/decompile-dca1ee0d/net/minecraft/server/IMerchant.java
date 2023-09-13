package net.minecraft.server;

import javax.annotation.Nullable;

public interface IMerchant {

    void setTradingPlayer(@Nullable EntityHuman entityhuman);

    @Nullable
    EntityHuman getTrader();

    @Nullable
    MerchantRecipeList getOffers(EntityHuman entityhuman);

    void a(MerchantRecipe merchantrecipe);

    void a(ItemStack itemstack);

    IChatBaseComponent getScoreboardDisplayName();

    World getWorld();

    BlockPosition getPosition();
}

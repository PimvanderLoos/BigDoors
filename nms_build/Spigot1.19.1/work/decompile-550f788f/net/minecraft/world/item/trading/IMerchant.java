package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerMerchant;
import net.minecraft.world.item.ItemStack;

public interface IMerchant {

    void setTradingPlayer(@Nullable EntityHuman entityhuman);

    @Nullable
    EntityHuman getTradingPlayer();

    MerchantRecipeList getOffers();

    void overrideOffers(MerchantRecipeList merchantrecipelist);

    void notifyTrade(MerchantRecipe merchantrecipe);

    void notifyTradeUpdated(ItemStack itemstack);

    int getVillagerXp();

    void overrideXp(int i);

    boolean showProgressBar();

    SoundEffect getNotifyTradeSound();

    default boolean canRestock() {
        return false;
    }

    default void openTradingScreen(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent, int i) {
        OptionalInt optionalint = entityhuman.openMenu(new TileInventory((j, playerinventory, entityhuman1) -> {
            return new ContainerMerchant(j, playerinventory, this);
        }, ichatbasecomponent));

        if (optionalint.isPresent()) {
            MerchantRecipeList merchantrecipelist = this.getOffers();

            if (!merchantrecipelist.isEmpty()) {
                entityhuman.sendMerchantOffers(optionalint.getAsInt(), merchantrecipelist, i, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
            }
        }

    }

    boolean isClientSide();
}

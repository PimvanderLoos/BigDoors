package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerMerchant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public interface IMerchant {

    void setTradingPlayer(@Nullable EntityHuman entityhuman);

    @Nullable
    EntityHuman getTrader();

    MerchantRecipeList getOffers();

    void a(MerchantRecipe merchantrecipe);

    void k(ItemStack itemstack);

    World getWorld();

    int getExperience();

    void setForcedExperience(int i);

    boolean isRegularVillager();

    SoundEffect getTradeSound();

    default boolean fa() {
        return false;
    }

    default void openTrade(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent, int i) {
        OptionalInt optionalint = entityhuman.openContainer(new TileInventory((j, playerinventory, entityhuman1) -> {
            return new ContainerMerchant(j, playerinventory, this);
        }, ichatbasecomponent));

        if (optionalint.isPresent()) {
            MerchantRecipeList merchantrecipelist = this.getOffers();

            if (!merchantrecipelist.isEmpty()) {
                entityhuman.openTrade(optionalint.getAsInt(), merchantrecipelist, i, this.getExperience(), this.isRegularVillager(), this.fa());
            }
        }

    }
}

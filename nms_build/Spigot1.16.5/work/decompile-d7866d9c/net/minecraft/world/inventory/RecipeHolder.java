package net.minecraft.world.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;

public interface RecipeHolder {

    void a(@Nullable IRecipe<?> irecipe);

    @Nullable
    IRecipe<?> ak_();

    default void b(EntityHuman entityhuman) {
        IRecipe<?> irecipe = this.ak_();

        if (irecipe != null && !irecipe.isComplex()) {
            entityhuman.discoverRecipes(Collections.singleton(irecipe));
            this.a((IRecipe) null);
        }

    }

    default boolean a(World world, EntityPlayer entityplayer, IRecipe<?> irecipe) {
        if (!irecipe.isComplex() && world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !entityplayer.getRecipeBook().b(irecipe)) {
            return false;
        } else {
            this.a(irecipe);
            return true;
        }
    }
}

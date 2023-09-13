package net.minecraft.world.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;

public interface RecipeHolder {

    void setRecipeUsed(@Nullable IRecipe<?> irecipe);

    @Nullable
    IRecipe<?> getRecipeUsed();

    default void awardUsedRecipes(EntityHuman entityhuman) {
        IRecipe<?> irecipe = this.getRecipeUsed();

        if (irecipe != null && !irecipe.isComplex()) {
            entityhuman.discoverRecipes(Collections.singleton(irecipe));
            this.setRecipeUsed((IRecipe) null);
        }

    }

    default boolean setRecipeUsed(World world, EntityPlayer entityplayer, IRecipe<?> irecipe) {
        if (!irecipe.isComplex() && world.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !entityplayer.getRecipeBook().b(irecipe)) {
            return false;
        } else {
            this.setRecipeUsed(irecipe);
            return true;
        }
    }
}

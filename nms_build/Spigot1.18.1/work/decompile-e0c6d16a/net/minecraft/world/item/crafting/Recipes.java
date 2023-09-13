package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.level.World;

public interface Recipes<T extends IRecipe<?>> {

    Recipes<RecipeCrafting> CRAFTING = register("crafting");
    Recipes<FurnaceRecipe> SMELTING = register("smelting");
    Recipes<RecipeBlasting> BLASTING = register("blasting");
    Recipes<RecipeSmoking> SMOKING = register("smoking");
    Recipes<RecipeCampfire> CAMPFIRE_COOKING = register("campfire_cooking");
    Recipes<RecipeStonecutting> STONECUTTING = register("stonecutting");
    Recipes<RecipeSmithing> SMITHING = register("smithing");

    static <T extends IRecipe<?>> Recipes<T> register(final String s) {
        return (Recipes) IRegistry.register(IRegistry.RECIPE_TYPE, new MinecraftKey(s), new Recipes<T>() {
            public String toString() {
                return s;
            }
        });
    }

    default <C extends IInventory> Optional<T> tryMatch(IRecipe<C> irecipe, World world, C c0) {
        return irecipe.matches(c0, world) ? Optional.of(irecipe) : Optional.empty();
    }
}

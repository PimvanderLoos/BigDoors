package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;

public interface Recipes<T extends IRecipe<?>> {

    Recipes<RecipeCrafting> CRAFTING = register("crafting");
    Recipes<FurnaceRecipe> SMELTING = register("smelting");
    Recipes<RecipeBlasting> BLASTING = register("blasting");
    Recipes<RecipeSmoking> SMOKING = register("smoking");
    Recipes<RecipeCampfire> CAMPFIRE_COOKING = register("campfire_cooking");
    Recipes<RecipeStonecutting> STONECUTTING = register("stonecutting");
    Recipes<SmithingRecipe> SMITHING = register("smithing");

    static <T extends IRecipe<?>> Recipes<T> register(final String s) {
        return (Recipes) IRegistry.register(BuiltInRegistries.RECIPE_TYPE, new MinecraftKey(s), new Recipes<T>() {
            public String toString() {
                return s;
            }
        });
    }
}

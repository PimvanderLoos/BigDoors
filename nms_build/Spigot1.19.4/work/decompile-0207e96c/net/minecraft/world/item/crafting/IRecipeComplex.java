package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;

public abstract class IRecipeComplex implements RecipeCrafting {

    private final MinecraftKey id;
    private final CraftingBookCategory category;

    public IRecipeComplex(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        this.id = minecraftkey;
        this.category = craftingbookcategory;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return ItemStack.EMPTY;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }
}

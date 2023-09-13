package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;

public abstract class IRecipeComplex implements RecipeCrafting {

    private final MinecraftKey a;

    public IRecipeComplex(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    @Override
    public MinecraftKey getKey() {
        return this.a;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ItemStack getResult() {
        return ItemStack.b;
    }
}

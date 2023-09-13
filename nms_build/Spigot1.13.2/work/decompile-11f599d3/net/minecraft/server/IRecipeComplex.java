package net.minecraft.server;

public abstract class IRecipeComplex implements IRecipe {

    private final MinecraftKey a;

    public IRecipeComplex(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public MinecraftKey getKey() {
        return this.a;
    }

    public boolean c() {
        return true;
    }

    public ItemStack d() {
        return ItemStack.a;
    }
}

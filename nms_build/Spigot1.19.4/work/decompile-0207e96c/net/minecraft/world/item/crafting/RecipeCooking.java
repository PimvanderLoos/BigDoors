package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public abstract class RecipeCooking implements IRecipe<IInventory> {

    protected final Recipes<?> type;
    protected final MinecraftKey id;
    private final CookingBookCategory category;
    protected final String group;
    protected final RecipeItemStack ingredient;
    protected final ItemStack result;
    protected final float experience;
    protected final int cookingTime;

    public RecipeCooking(Recipes<?> recipes, MinecraftKey minecraftkey, String s, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i) {
        this.type = recipes;
        this.category = cookingbookcategory;
        this.id = minecraftkey;
        this.group = s;
        this.ingredient = recipeitemstack;
        this.result = itemstack;
        this.experience = f;
        this.cookingTime = i;
    }

    @Override
    public boolean matches(IInventory iinventory, World world) {
        return this.ingredient.test(iinventory.getItem(0));
    }

    @Override
    public ItemStack assemble(IInventory iinventory, IRegistryCustom iregistrycustom) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public NonNullList<RecipeItemStack> getIngredients() {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.create();

        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return this.result;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public Recipes<?> getType() {
        return this.type;
    }

    public CookingBookCategory category() {
        return this.category;
    }
}

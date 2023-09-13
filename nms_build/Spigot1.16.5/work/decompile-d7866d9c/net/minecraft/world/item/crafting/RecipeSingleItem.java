package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public abstract class RecipeSingleItem implements IRecipe<IInventory> {

    protected final RecipeItemStack ingredient;
    protected final ItemStack result;
    private final Recipes<?> e;
    private final RecipeSerializer<?> f;
    protected final MinecraftKey key;
    protected final String group;

    public RecipeSingleItem(Recipes<?> recipes, RecipeSerializer<?> recipeserializer, MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack) {
        this.e = recipes;
        this.f = recipeserializer;
        this.key = minecraftkey;
        this.group = s;
        this.ingredient = recipeitemstack;
        this.result = itemstack;
    }

    @Override
    public Recipes<?> g() {
        return this.e;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return this.f;
    }

    @Override
    public MinecraftKey getKey() {
        return this.key;
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public NonNullList<RecipeItemStack> a() {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.a();

        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public ItemStack a(IInventory iinventory) {
        return this.result.cloneItemStack();
    }

    public static class a<T extends RecipeSingleItem> implements RecipeSerializer<T> {

        final RecipeSingleItem.a.a<T> v;

        protected a(RecipeSingleItem.a.a<T> recipesingleitem_a_a) {
            this.v = recipesingleitem_a_a;
        }

        @Override
        public T a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.a(jsonobject, "group", "");
            RecipeItemStack recipeitemstack;

            if (ChatDeserializer.d(jsonobject, "ingredient")) {
                recipeitemstack = RecipeItemStack.a((JsonElement) ChatDeserializer.u(jsonobject, "ingredient"));
            } else {
                recipeitemstack = RecipeItemStack.a((JsonElement) ChatDeserializer.t(jsonobject, "ingredient"));
            }

            String s1 = ChatDeserializer.h(jsonobject, "result");
            int i = ChatDeserializer.n(jsonobject, "count");
            ItemStack itemstack = new ItemStack((IMaterial) IRegistry.ITEM.get(new MinecraftKey(s1)), i);

            return this.v.create(minecraftkey, s, recipeitemstack, itemstack);
        }

        @Override
        public T a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            String s = packetdataserializer.e(32767);
            RecipeItemStack recipeitemstack = RecipeItemStack.b(packetdataserializer);
            ItemStack itemstack = packetdataserializer.n();

            return this.v.create(minecraftkey, s, recipeitemstack, itemstack);
        }

        public void a(PacketDataSerializer packetdataserializer, T t0) {
            packetdataserializer.a(t0.group);
            t0.ingredient.a(packetdataserializer);
            packetdataserializer.a(t0.result);
        }

        interface a<T extends RecipeSingleItem> {

            T create(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack);
        }
    }
}

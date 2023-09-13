package net.minecraft.world.item.crafting;

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
    private final Recipes<?> type;
    private final RecipeSerializer<?> serializer;
    protected final MinecraftKey id;
    protected final String group;

    public RecipeSingleItem(Recipes<?> recipes, RecipeSerializer<?> recipeserializer, MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack) {
        this.type = recipes;
        this.serializer = recipeserializer;
        this.id = minecraftkey;
        this.group = s;
        this.ingredient = recipeitemstack;
        this.result = itemstack;
    }

    @Override
    public Recipes<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<RecipeItemStack> getIngredients() {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.create();

        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack assemble(IInventory iinventory) {
        return this.result.copy();
    }

    public static class a<T extends RecipeSingleItem> implements RecipeSerializer<T> {

        final RecipeSingleItem.a.a<T> factory;

        protected a(RecipeSingleItem.a.a<T> recipesingleitem_a_a) {
            this.factory = recipesingleitem_a_a;
        }

        @Override
        public T fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.getAsString(jsonobject, "group", "");
            RecipeItemStack recipeitemstack;

            if (ChatDeserializer.isArrayNode(jsonobject, "ingredient")) {
                recipeitemstack = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonArray(jsonobject, "ingredient"));
            } else {
                recipeitemstack = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "ingredient"));
            }

            String s1 = ChatDeserializer.getAsString(jsonobject, "result");
            int i = ChatDeserializer.getAsInt(jsonobject, "count");
            ItemStack itemstack = new ItemStack((IMaterial) IRegistry.ITEM.get(new MinecraftKey(s1)), i);

            return this.factory.create(minecraftkey, s, recipeitemstack, itemstack);
        }

        @Override
        public T fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            String s = packetdataserializer.readUtf();
            RecipeItemStack recipeitemstack = RecipeItemStack.fromNetwork(packetdataserializer);
            ItemStack itemstack = packetdataserializer.readItem();

            return this.factory.create(minecraftkey, s, recipeitemstack, itemstack);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, T t0) {
            packetdataserializer.writeUtf(t0.group);
            t0.ingredient.toNetwork(packetdataserializer);
            packetdataserializer.writeItem(t0.result);
        }

        interface a<T extends RecipeSingleItem> {

            T create(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack);
        }
    }
}

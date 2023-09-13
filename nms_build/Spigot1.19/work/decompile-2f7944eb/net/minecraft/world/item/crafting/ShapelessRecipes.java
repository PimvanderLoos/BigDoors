package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class ShapelessRecipes implements RecipeCrafting {

    private final MinecraftKey id;
    final String group;
    final ItemStack result;
    final NonNullList<RecipeItemStack> ingredients;

    public ShapelessRecipes(MinecraftKey minecraftkey, String s, ItemStack itemstack, NonNullList<RecipeItemStack> nonnulllist) {
        this.id = minecraftkey;
        this.group = s;
        this.result = itemstack;
        this.ingredients = nonnulllist;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
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
        return this.ingredients;
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        AutoRecipeStackManager autorecipestackmanager = new AutoRecipeStackManager();
        int i = 0;

        for (int j = 0; j < inventorycrafting.getContainerSize(); ++j) {
            ItemStack itemstack = inventorycrafting.getItem(j);

            if (!itemstack.isEmpty()) {
                ++i;
                autorecipestackmanager.accountStack(itemstack, 1);
            }
        }

        return i == this.ingredients.size() && autorecipestackmanager.canCraft(this, (IntList) null);
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= this.ingredients.size();
    }

    public static class a implements RecipeSerializer<ShapelessRecipes> {

        public a() {}

        @Override
        public ShapelessRecipes fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.getAsString(jsonobject, "group", "");
            NonNullList<RecipeItemStack> nonnulllist = itemsFromJson(ChatDeserializer.getAsJsonArray(jsonobject, "ingredients"));

            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemstack = ShapedRecipes.itemStackFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "result"));

                return new ShapelessRecipes(minecraftkey, s, itemstack, nonnulllist);
            }
        }

        private static NonNullList<RecipeItemStack> itemsFromJson(JsonArray jsonarray) {
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.create();

            for (int i = 0; i < jsonarray.size(); ++i) {
                RecipeItemStack recipeitemstack = RecipeItemStack.fromJson(jsonarray.get(i));

                if (!recipeitemstack.isEmpty()) {
                    nonnulllist.add(recipeitemstack);
                }
            }

            return nonnulllist;
        }

        @Override
        public ShapelessRecipes fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            String s = packetdataserializer.readUtf();
            int i = packetdataserializer.readVarInt();
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.withSize(i, RecipeItemStack.EMPTY);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, RecipeItemStack.fromNetwork(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.readItem();

            return new ShapelessRecipes(minecraftkey, s, itemstack, nonnulllist);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, ShapelessRecipes shapelessrecipes) {
            packetdataserializer.writeUtf(shapelessrecipes.group);
            packetdataserializer.writeVarInt(shapelessrecipes.ingredients.size());
            Iterator iterator = shapelessrecipes.ingredients.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                recipeitemstack.toNetwork(packetdataserializer);
            }

            packetdataserializer.writeItem(shapelessrecipes.result);
        }
    }
}

package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class RecipeSmithing implements IRecipe<IInventory> {

    final RecipeItemStack base;
    final RecipeItemStack addition;
    final ItemStack result;
    private final MinecraftKey id;

    public RecipeSmithing(MinecraftKey minecraftkey, RecipeItemStack recipeitemstack, RecipeItemStack recipeitemstack1, ItemStack itemstack) {
        this.id = minecraftkey;
        this.base = recipeitemstack;
        this.addition = recipeitemstack1;
        this.result = itemstack;
    }

    @Override
    public boolean matches(IInventory iinventory, World world) {
        return this.base.test(iinventory.getItem(0)) && this.addition.test(iinventory.getItem(1));
    }

    @Override
    public ItemStack assemble(IInventory iinventory) {
        ItemStack itemstack = this.result.copy();
        NBTTagCompound nbttagcompound = iinventory.getItem(0).getTag();

        if (nbttagcompound != null) {
            itemstack.setTag(nbttagcompound.copy());
        }

        return itemstack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    public boolean isAdditionIngredient(ItemStack itemstack) {
        return this.addition.test(itemstack);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    @Override
    public Recipes<?> getType() {
        return Recipes.SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.base, this.addition).anyMatch((recipeitemstack) -> {
            return recipeitemstack.getItems().length == 0;
        });
    }

    public static class a implements RecipeSerializer<RecipeSmithing> {

        public a() {}

        @Override
        public RecipeSmithing fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "base"));
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "addition"));
            ItemStack itemstack = ShapedRecipes.itemStackFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "result"));

            return new RecipeSmithing(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        @Override
        public RecipeSmithing fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromNetwork(packetdataserializer);
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromNetwork(packetdataserializer);
            ItemStack itemstack = packetdataserializer.readItem();

            return new RecipeSmithing(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, RecipeSmithing recipesmithing) {
            recipesmithing.base.toNetwork(packetdataserializer);
            recipesmithing.addition.toNetwork(packetdataserializer);
            packetdataserializer.writeItem(recipesmithing.result);
        }
    }
}

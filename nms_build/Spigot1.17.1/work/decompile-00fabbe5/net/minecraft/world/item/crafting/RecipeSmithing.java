package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
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
    public boolean a(IInventory iinventory, World world) {
        return this.base.test(iinventory.getItem(0)) && this.addition.test(iinventory.getItem(1));
    }

    @Override
    public ItemStack a(IInventory iinventory) {
        ItemStack itemstack = this.result.cloneItemStack();
        NBTTagCompound nbttagcompound = iinventory.getItem(0).getTag();

        if (nbttagcompound != null) {
            itemstack.setTag(nbttagcompound.clone());
        }

        return itemstack;
    }

    @Override
    public boolean a(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    public boolean a(ItemStack itemstack) {
        return this.addition.test(itemstack);
    }

    @Override
    public ItemStack h() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public MinecraftKey getKey() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.SMITHING;
    }

    @Override
    public Recipes<?> g() {
        return Recipes.SMITHING;
    }

    @Override
    public boolean i() {
        return Stream.of(this.base, this.addition).anyMatch((recipeitemstack) -> {
            return recipeitemstack.a().length == 0;
        });
    }

    public static class a implements RecipeSerializer<RecipeSmithing> {

        public a() {}

        @Override
        public RecipeSmithing a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            RecipeItemStack recipeitemstack = RecipeItemStack.a((JsonElement) ChatDeserializer.t(jsonobject, "base"));
            RecipeItemStack recipeitemstack1 = RecipeItemStack.a((JsonElement) ChatDeserializer.t(jsonobject, "addition"));
            ItemStack itemstack = ShapedRecipes.a(ChatDeserializer.t(jsonobject, "result"));

            return new RecipeSmithing(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        @Override
        public RecipeSmithing a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            RecipeItemStack recipeitemstack = RecipeItemStack.b(packetdataserializer);
            RecipeItemStack recipeitemstack1 = RecipeItemStack.b(packetdataserializer);
            ItemStack itemstack = packetdataserializer.o();

            return new RecipeSmithing(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        public void a(PacketDataSerializer packetdataserializer, RecipeSmithing recipesmithing) {
            recipesmithing.base.a(packetdataserializer);
            recipesmithing.addition.a(packetdataserializer);
            packetdataserializer.a(recipesmithing.result);
        }
    }
}

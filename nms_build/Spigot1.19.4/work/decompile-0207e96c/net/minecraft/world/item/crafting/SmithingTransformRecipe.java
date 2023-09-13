package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class SmithingTransformRecipe implements SmithingRecipe {

    private final MinecraftKey id;
    final RecipeItemStack template;
    final RecipeItemStack base;
    final RecipeItemStack addition;
    final ItemStack result;

    public SmithingTransformRecipe(MinecraftKey minecraftkey, RecipeItemStack recipeitemstack, RecipeItemStack recipeitemstack1, RecipeItemStack recipeitemstack2, ItemStack itemstack) {
        this.id = minecraftkey;
        this.template = recipeitemstack;
        this.base = recipeitemstack1;
        this.addition = recipeitemstack2;
        this.result = itemstack;
    }

    @Override
    public boolean matches(IInventory iinventory, World world) {
        return this.template.test(iinventory.getItem(0)) && this.base.test(iinventory.getItem(1)) && this.addition.test(iinventory.getItem(2));
    }

    @Override
    public ItemStack assemble(IInventory iinventory, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = this.result.copy();
        NBTTagCompound nbttagcompound = iinventory.getItem(1).getTag();

        if (nbttagcompound != null) {
            itemstack.setTag(nbttagcompound.copy());
        }

        return itemstack;
    }

    @Override
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemstack) {
        return this.template.test(itemstack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemstack) {
        return this.base.test(itemstack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack itemstack) {
        return this.addition.test(itemstack);
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(RecipeItemStack::isEmpty);
    }

    public static class a implements RecipeSerializer<SmithingTransformRecipe> {

        public a() {}

        @Override
        public SmithingTransformRecipe fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "template"));
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "base"));
            RecipeItemStack recipeitemstack2 = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "addition"));
            ItemStack itemstack = ShapedRecipes.itemStackFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "result"));

            return new SmithingTransformRecipe(minecraftkey, recipeitemstack, recipeitemstack1, recipeitemstack2, itemstack);
        }

        @Override
        public SmithingTransformRecipe fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromNetwork(packetdataserializer);
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromNetwork(packetdataserializer);
            RecipeItemStack recipeitemstack2 = RecipeItemStack.fromNetwork(packetdataserializer);
            ItemStack itemstack = packetdataserializer.readItem();

            return new SmithingTransformRecipe(minecraftkey, recipeitemstack, recipeitemstack1, recipeitemstack2, itemstack);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, SmithingTransformRecipe smithingtransformrecipe) {
            smithingtransformrecipe.template.toNetwork(packetdataserializer);
            smithingtransformrecipe.base.toNetwork(packetdataserializer);
            smithingtransformrecipe.addition.toNetwork(packetdataserializer);
            packetdataserializer.writeItem(smithingtransformrecipe.result);
        }
    }
}

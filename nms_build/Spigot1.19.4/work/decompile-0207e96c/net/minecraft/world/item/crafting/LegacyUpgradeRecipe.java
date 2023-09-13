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

/** @deprecated */
@Deprecated(forRemoval = true)
public class LegacyUpgradeRecipe implements SmithingRecipe {

    final RecipeItemStack base;
    final RecipeItemStack addition;
    final ItemStack result;
    private final MinecraftKey id;

    public LegacyUpgradeRecipe(MinecraftKey minecraftkey, RecipeItemStack recipeitemstack, RecipeItemStack recipeitemstack1, ItemStack itemstack) {
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
    public ItemStack assemble(IInventory iinventory, IRegistryCustom iregistrycustom) {
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
    public ItemStack getResultItem(IRegistryCustom iregistrycustom) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemstack) {
        return false;
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
        return RecipeSerializer.SMITHING;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.base, this.addition).anyMatch((recipeitemstack) -> {
            return recipeitemstack.getItems().length == 0;
        });
    }

    public static class a implements RecipeSerializer<LegacyUpgradeRecipe> {

        public a() {}

        @Override
        public LegacyUpgradeRecipe fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "base"));
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, "addition"));
            ItemStack itemstack = ShapedRecipes.itemStackFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "result"));

            return new LegacyUpgradeRecipe(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        @Override
        public LegacyUpgradeRecipe fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            RecipeItemStack recipeitemstack = RecipeItemStack.fromNetwork(packetdataserializer);
            RecipeItemStack recipeitemstack1 = RecipeItemStack.fromNetwork(packetdataserializer);
            ItemStack itemstack = packetdataserializer.readItem();

            return new LegacyUpgradeRecipe(minecraftkey, recipeitemstack, recipeitemstack1, itemstack);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, LegacyUpgradeRecipe legacyupgraderecipe) {
            legacyupgraderecipe.base.toNetwork(packetdataserializer);
            legacyupgraderecipe.addition.toNetwork(packetdataserializer);
            packetdataserializer.writeItem(legacyupgraderecipe.result);
        }
    }
}

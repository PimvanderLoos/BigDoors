package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public class SimpleCraftingRecipeSerializer<T extends RecipeCrafting> implements RecipeSerializer<T> {

    private final SimpleCraftingRecipeSerializer.a<T> constructor;

    public SimpleCraftingRecipeSerializer(SimpleCraftingRecipeSerializer.a<T> simplecraftingrecipeserializer_a) {
        this.constructor = simplecraftingrecipeserializer_a;
    }

    @Override
    public T fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
        CraftingBookCategory craftingbookcategory = (CraftingBookCategory) CraftingBookCategory.CODEC.byName(ChatDeserializer.getAsString(jsonobject, "category", (String) null), CraftingBookCategory.MISC);

        return this.constructor.create(minecraftkey, craftingbookcategory);
    }

    @Override
    public T fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        CraftingBookCategory craftingbookcategory = (CraftingBookCategory) packetdataserializer.readEnum(CraftingBookCategory.class);

        return this.constructor.create(minecraftkey, craftingbookcategory);
    }

    public void toNetwork(PacketDataSerializer packetdataserializer, T t0) {
        packetdataserializer.writeEnum(t0.category());
    }

    @FunctionalInterface
    public interface a<T extends RecipeCrafting> {

        T create(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory);
    }
}

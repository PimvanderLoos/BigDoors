package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public class RecipeSerializerCooking<T extends RecipeCooking> implements RecipeSerializer<T> {

    private final int defaultCookingTime;
    private final RecipeSerializerCooking.a<T> factory;

    public RecipeSerializerCooking(RecipeSerializerCooking.a<T> recipeserializercooking_a, int i) {
        this.defaultCookingTime = i;
        this.factory = recipeserializercooking_a;
    }

    @Override
    public T fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
        String s = ChatDeserializer.getAsString(jsonobject, "group", "");
        CookingBookCategory cookingbookcategory = (CookingBookCategory) CookingBookCategory.CODEC.byName(ChatDeserializer.getAsString(jsonobject, "category", (String) null), CookingBookCategory.MISC);
        Object object = ChatDeserializer.isArrayNode(jsonobject, "ingredient") ? ChatDeserializer.getAsJsonArray(jsonobject, "ingredient") : ChatDeserializer.getAsJsonObject(jsonobject, "ingredient");
        RecipeItemStack recipeitemstack = RecipeItemStack.fromJson((JsonElement) object);
        String s1 = ChatDeserializer.getAsString(jsonobject, "result");
        MinecraftKey minecraftkey1 = new MinecraftKey(s1);
        ItemStack itemstack = new ItemStack((IMaterial) BuiltInRegistries.ITEM.getOptional(minecraftkey1).orElseThrow(() -> {
            return new IllegalStateException("Item: " + s1 + " does not exist");
        }));
        float f = ChatDeserializer.getAsFloat(jsonobject, "experience", 0.0F);
        int i = ChatDeserializer.getAsInt(jsonobject, "cookingtime", this.defaultCookingTime);

        return this.factory.create(minecraftkey, s, cookingbookcategory, recipeitemstack, itemstack, f, i);
    }

    @Override
    public T fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        String s = packetdataserializer.readUtf();
        CookingBookCategory cookingbookcategory = (CookingBookCategory) packetdataserializer.readEnum(CookingBookCategory.class);
        RecipeItemStack recipeitemstack = RecipeItemStack.fromNetwork(packetdataserializer);
        ItemStack itemstack = packetdataserializer.readItem();
        float f = packetdataserializer.readFloat();
        int i = packetdataserializer.readVarInt();

        return this.factory.create(minecraftkey, s, cookingbookcategory, recipeitemstack, itemstack, f, i);
    }

    public void toNetwork(PacketDataSerializer packetdataserializer, T t0) {
        packetdataserializer.writeUtf(t0.group);
        packetdataserializer.writeEnum(t0.category());
        t0.ingredient.toNetwork(packetdataserializer);
        packetdataserializer.writeItem(t0.result);
        packetdataserializer.writeFloat(t0.experience);
        packetdataserializer.writeVarInt(t0.cookingTime);
    }

    interface a<T extends RecipeCooking> {

        T create(MinecraftKey minecraftkey, String s, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i);
    }
}

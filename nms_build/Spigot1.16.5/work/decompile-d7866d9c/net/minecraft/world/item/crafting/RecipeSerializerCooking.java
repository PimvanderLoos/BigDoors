package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public class RecipeSerializerCooking<T extends RecipeCooking> implements RecipeSerializer<T> {

    private final int v;
    private final RecipeSerializerCooking.a<T> w;

    public RecipeSerializerCooking(RecipeSerializerCooking.a<T> recipeserializercooking_a, int i) {
        this.v = i;
        this.w = recipeserializercooking_a;
    }

    @Override
    public T a(MinecraftKey minecraftkey, JsonObject jsonobject) {
        String s = ChatDeserializer.a(jsonobject, "group", "");
        Object object = ChatDeserializer.d(jsonobject, "ingredient") ? ChatDeserializer.u(jsonobject, "ingredient") : ChatDeserializer.t(jsonobject, "ingredient");
        RecipeItemStack recipeitemstack = RecipeItemStack.a((JsonElement) object);
        String s1 = ChatDeserializer.h(jsonobject, "result");
        MinecraftKey minecraftkey1 = new MinecraftKey(s1);
        ItemStack itemstack = new ItemStack((IMaterial) IRegistry.ITEM.getOptional(minecraftkey1).orElseThrow(() -> {
            return new IllegalStateException("Item: " + s1 + " does not exist");
        }));
        float f = ChatDeserializer.a(jsonobject, "experience", 0.0F);
        int i = ChatDeserializer.a(jsonobject, "cookingtime", this.v);

        return this.w.create(minecraftkey, s, recipeitemstack, itemstack, f, i);
    }

    @Override
    public T a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        String s = packetdataserializer.e(32767);
        RecipeItemStack recipeitemstack = RecipeItemStack.b(packetdataserializer);
        ItemStack itemstack = packetdataserializer.n();
        float f = packetdataserializer.readFloat();
        int i = packetdataserializer.i();

        return this.w.create(minecraftkey, s, recipeitemstack, itemstack, f, i);
    }

    public void a(PacketDataSerializer packetdataserializer, T t0) {
        packetdataserializer.a(t0.group);
        t0.ingredient.a(packetdataserializer);
        packetdataserializer.a(t0.result);
        packetdataserializer.writeFloat(t0.experience);
        packetdataserializer.d(t0.cookingTime);
    }

    interface a<T extends RecipeCooking> {

        T create(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i);
    }
}

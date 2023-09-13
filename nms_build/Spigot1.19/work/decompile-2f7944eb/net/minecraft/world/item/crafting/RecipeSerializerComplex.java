package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public class RecipeSerializerComplex<T extends IRecipe<?>> implements RecipeSerializer<T> {

    private final Function<MinecraftKey, T> constructor;

    public RecipeSerializerComplex(Function<MinecraftKey, T> function) {
        this.constructor = function;
    }

    @Override
    public T fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
        return (IRecipe) this.constructor.apply(minecraftkey);
    }

    @Override
    public T fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        return (IRecipe) this.constructor.apply(minecraftkey);
    }

    @Override
    public void toNetwork(PacketDataSerializer packetdataserializer, T t0) {}
}

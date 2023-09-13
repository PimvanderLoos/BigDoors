package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.function.Function;

public class RecipeSerializerComplex<T extends IRecipe<?>> implements RecipeSerializer<T> {

    private final Function<MinecraftKey, T> v;

    public RecipeSerializerComplex(Function<MinecraftKey, T> function) {
        this.v = function;
    }

    @Override
    public T a(MinecraftKey minecraftkey, JsonObject jsonobject) {
        return (IRecipe) this.v.apply(minecraftkey);
    }

    @Override
    public T a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        return (IRecipe) this.v.apply(minecraftkey);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer, T t0) {}
}

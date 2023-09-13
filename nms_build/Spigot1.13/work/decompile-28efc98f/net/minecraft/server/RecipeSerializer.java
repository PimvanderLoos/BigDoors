package net.minecraft.server;

import com.google.gson.JsonObject;

public interface RecipeSerializer<T extends IRecipe> {

    T a(MinecraftKey minecraftkey, JsonObject jsonobject);

    T a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer);

    void a(PacketDataSerializer packetdataserializer, T t0);

    String a();
}

package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketDataSerializer;

public interface ArgumentSerializer<T extends ArgumentType<?>> {

    void serializeToNetwork(T t0, PacketDataSerializer packetdataserializer);

    T deserializeFromNetwork(PacketDataSerializer packetdataserializer);

    void serializeToJson(T t0, JsonObject jsonobject);
}

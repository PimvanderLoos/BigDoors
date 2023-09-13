package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketDataSerializer;

public interface ArgumentSerializer<T extends ArgumentType<?>> {

    void a(T t0, PacketDataSerializer packetdataserializer);

    T b(PacketDataSerializer packetdataserializer);

    void a(T t0, JsonObject jsonobject);
}

package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.PacketDataSerializer;

public interface ArgumentTypeInfo<A extends ArgumentType<?>, T extends ArgumentTypeInfo.a<A>> {

    void serializeToNetwork(T t0, PacketDataSerializer packetdataserializer);

    T deserializeFromNetwork(PacketDataSerializer packetdataserializer);

    void serializeToJson(T t0, JsonObject jsonobject);

    T unpack(A a0);

    public interface a<A extends ArgumentType<?>> {

        A instantiate(CommandBuildContext commandbuildcontext);

        ArgumentTypeInfo<A, ?> type();
    }
}

package net.minecraft.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.ChatTypeAdapterFactory;

public class PacketStatusOutServerInfo implements Packet<PacketStatusOutListener> {

    private static final Gson a = (new GsonBuilder()).registerTypeAdapter(ServerPing.ServerData.class, new ServerPing.ServerData.Serializer()).registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing.ServerPingPlayerSample.Serializer()).registerTypeAdapter(ServerPing.class, new ServerPing.Serializer()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    private ServerPing b;

    public PacketStatusOutServerInfo() {}

    public PacketStatusOutServerInfo(ServerPing serverping) {
        this.b = serverping;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.b = (ServerPing) ChatDeserializer.a(PacketStatusOutServerInfo.a, packetdataserializer.e(32767), ServerPing.class);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(PacketStatusOutServerInfo.a.toJson(this.b));
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }
}

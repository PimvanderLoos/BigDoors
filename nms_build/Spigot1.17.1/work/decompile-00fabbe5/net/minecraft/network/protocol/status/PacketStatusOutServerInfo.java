package net.minecraft.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.ChatTypeAdapterFactory;

public class PacketStatusOutServerInfo implements Packet<PacketStatusOutListener> {

    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerPing.ServerData.class, new ServerPing.ServerData.Serializer()).registerTypeAdapter(ServerPing.ServerPingPlayerSample.class, new ServerPing.ServerPingPlayerSample.Serializer()).registerTypeAdapter(ServerPing.class, new ServerPing.Serializer()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    private final ServerPing status;

    public PacketStatusOutServerInfo(ServerPing serverping) {
        this.status = serverping;
    }

    public PacketStatusOutServerInfo(PacketDataSerializer packetdataserializer) {
        this.status = (ServerPing) ChatDeserializer.a(PacketStatusOutServerInfo.GSON, packetdataserializer.e(32767), ServerPing.class);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(PacketStatusOutServerInfo.GSON.toJson(this.status));
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }

    public ServerPing b() {
        return this.status;
    }
}

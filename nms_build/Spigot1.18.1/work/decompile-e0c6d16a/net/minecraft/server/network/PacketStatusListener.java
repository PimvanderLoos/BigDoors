package net.minecraft.server.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.status.PacketStatusInListener;
import net.minecraft.network.protocol.status.PacketStatusInPing;
import net.minecraft.network.protocol.status.PacketStatusInStart;
import net.minecraft.network.protocol.status.PacketStatusOutPong;
import net.minecraft.network.protocol.status.PacketStatusOutServerInfo;
import net.minecraft.server.MinecraftServer;

public class PacketStatusListener implements PacketStatusInListener {

    private static final IChatBaseComponent DISCONNECT_REASON = new ChatMessage("multiplayer.status.request_handled");
    private final MinecraftServer server;
    private final NetworkManager connection;
    private boolean hasRequestedStatus;

    public PacketStatusListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.server = minecraftserver;
        this.connection = networkmanager;
    }

    @Override
    public void onDisconnect(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public NetworkManager getConnection() {
        return this.connection;
    }

    @Override
    public void handleStatusRequest(PacketStatusInStart packetstatusinstart) {
        if (this.hasRequestedStatus) {
            this.connection.disconnect(PacketStatusListener.DISCONNECT_REASON);
        } else {
            this.hasRequestedStatus = true;
            this.connection.send(new PacketStatusOutServerInfo(this.server.getStatus()));
        }
    }

    @Override
    public void handlePingRequest(PacketStatusInPing packetstatusinping) {
        this.connection.send(new PacketStatusOutPong(packetstatusinping.getTime()));
        this.connection.disconnect(PacketStatusListener.DISCONNECT_REASON);
    }
}

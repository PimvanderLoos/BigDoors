package net.minecraft.server.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.handshake.PacketHandshakingInListener;
import net.minecraft.network.protocol.handshake.PacketHandshakingInSetProtocol;
import net.minecraft.server.MinecraftServer;

public class MemoryServerHandshakePacketListenerImpl implements PacketHandshakingInListener {

    private final MinecraftServer server;
    private final NetworkManager connection;

    public MemoryServerHandshakePacketListenerImpl(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.server = minecraftserver;
        this.connection = networkmanager;
    }

    @Override
    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        this.connection.setProtocol(packethandshakinginsetprotocol.b());
        this.connection.setPacketListener(new LoginListener(this.server, this.connection));
    }

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public NetworkManager a() {
        return this.connection;
    }
}

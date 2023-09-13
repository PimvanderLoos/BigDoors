package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.EnumProtocol;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.handshake.PacketHandshakingInListener;
import net.minecraft.network.protocol.handshake.PacketHandshakingInSetProtocol;
import net.minecraft.network.protocol.login.PacketLoginOutDisconnect;
import net.minecraft.server.MinecraftServer;

public class HandshakeListener implements PacketHandshakingInListener {

    private static final IChatBaseComponent IGNORE_STATUS_REASON = new ChatComponentText("Ignoring status request");
    private final MinecraftServer server;
    private final NetworkManager connection;

    public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.server = minecraftserver;
        this.connection = networkmanager;
    }

    @Override
    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        switch (packethandshakinginsetprotocol.b()) {
            case LOGIN:
                this.connection.setProtocol(EnumProtocol.LOGIN);
                if (packethandshakinginsetprotocol.c() != SharedConstants.getGameVersion().getProtocolVersion()) {
                    ChatMessage chatmessage;

                    if (packethandshakinginsetprotocol.c() < 754) {
                        chatmessage = new ChatMessage("multiplayer.disconnect.outdated_client", new Object[]{SharedConstants.getGameVersion().getName()});
                    } else {
                        chatmessage = new ChatMessage("multiplayer.disconnect.incompatible", new Object[]{SharedConstants.getGameVersion().getName()});
                    }

                    this.connection.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                    this.connection.close(chatmessage);
                } else {
                    this.connection.setPacketListener(new LoginListener(this.server, this.connection));
                }
                break;
            case STATUS:
                if (this.server.ak()) {
                    this.connection.setProtocol(EnumProtocol.STATUS);
                    this.connection.setPacketListener(new PacketStatusListener(this.server, this.connection));
                } else {
                    this.connection.close(HandshakeListener.IGNORE_STATUS_REASON);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.b());
        }

    }

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public NetworkManager a() {
        return this.connection;
    }
}

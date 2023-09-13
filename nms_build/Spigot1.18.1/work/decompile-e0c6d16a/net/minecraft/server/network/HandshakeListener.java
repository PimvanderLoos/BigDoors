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
    public void handleIntention(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        switch (packethandshakinginsetprotocol.getIntention()) {
            case LOGIN:
                this.connection.setProtocol(EnumProtocol.LOGIN);
                if (packethandshakinginsetprotocol.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    ChatMessage chatmessage;

                    if (packethandshakinginsetprotocol.getProtocolVersion() < 754) {
                        chatmessage = new ChatMessage("multiplayer.disconnect.outdated_client", new Object[]{SharedConstants.getCurrentVersion().getName()});
                    } else {
                        chatmessage = new ChatMessage("multiplayer.disconnect.incompatible", new Object[]{SharedConstants.getCurrentVersion().getName()});
                    }

                    this.connection.send(new PacketLoginOutDisconnect(chatmessage));
                    this.connection.disconnect(chatmessage);
                } else {
                    this.connection.setListener(new LoginListener(this.server, this.connection));
                }
                break;
            case STATUS:
                if (this.server.repliesToStatus()) {
                    this.connection.setProtocol(EnumProtocol.STATUS);
                    this.connection.setListener(new PacketStatusListener(this.server, this.connection));
                } else {
                    this.connection.disconnect(HandshakeListener.IGNORE_STATUS_REASON);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.getIntention());
        }

    }

    @Override
    public void onDisconnect(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public NetworkManager getConnection() {
        return this.connection;
    }
}

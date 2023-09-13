package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<PacketListenerPlayOut> {

    private final IChatBaseComponent motd;
    private final Optional<byte[]> iconBytes;
    private final boolean enforcesSecureChat;

    public ClientboundServerDataPacket(IChatBaseComponent ichatbasecomponent, Optional<byte[]> optional, boolean flag) {
        this.motd = ichatbasecomponent;
        this.iconBytes = optional;
        this.enforcesSecureChat = flag;
    }

    public ClientboundServerDataPacket(PacketDataSerializer packetdataserializer) {
        this.motd = packetdataserializer.readComponent();
        this.iconBytes = packetdataserializer.readOptional(PacketDataSerializer::readByteArray);
        this.enforcesSecureChat = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.motd);
        packetdataserializer.writeOptional(this.iconBytes, PacketDataSerializer::writeByteArray);
        packetdataserializer.writeBoolean(this.enforcesSecureChat);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleServerData(this);
    }

    public IChatBaseComponent getMotd() {
        return this.motd;
    }

    public Optional<byte[]> getIconBytes() {
        return this.iconBytes;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }
}

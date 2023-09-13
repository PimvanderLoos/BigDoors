package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<PacketListenerPlayOut> {

    private final Optional<IChatBaseComponent> motd;
    private final Optional<String> iconBase64;
    private final boolean previewsChat;
    private final boolean enforcesSecureChat;

    public ClientboundServerDataPacket(@Nullable IChatBaseComponent ichatbasecomponent, @Nullable String s, boolean flag, boolean flag1) {
        this.motd = Optional.ofNullable(ichatbasecomponent);
        this.iconBase64 = Optional.ofNullable(s);
        this.previewsChat = flag;
        this.enforcesSecureChat = flag1;
    }

    public ClientboundServerDataPacket(PacketDataSerializer packetdataserializer) {
        this.motd = packetdataserializer.readOptional(PacketDataSerializer::readComponent);
        this.iconBase64 = packetdataserializer.readOptional(PacketDataSerializer::readUtf);
        this.previewsChat = packetdataserializer.readBoolean();
        this.enforcesSecureChat = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeOptional(this.motd, PacketDataSerializer::writeComponent);
        packetdataserializer.writeOptional(this.iconBase64, PacketDataSerializer::writeUtf);
        packetdataserializer.writeBoolean(this.previewsChat);
        packetdataserializer.writeBoolean(this.enforcesSecureChat);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleServerData(this);
    }

    public Optional<IChatBaseComponent> getMotd() {
        return this.motd;
    }

    public Optional<String> getIconBase64() {
        return this.iconBase64;
    }

    public boolean previewsChat() {
        return this.previewsChat;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }
}

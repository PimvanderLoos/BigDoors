package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;

public record PacketPlayInSettings(String language, int viewDistance, EnumChatVisibility chatVisibility, boolean chatColors, int modelCustomisation, EnumMainHand mainHand, boolean textFilteringEnabled, boolean allowsListing) implements Packet<PacketListenerPlayIn> {

    public static final int MAX_LANGUAGE_LENGTH = 16;

    public PacketPlayInSettings(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(16), packetdataserializer.readByte(), (EnumChatVisibility) packetdataserializer.readEnum(EnumChatVisibility.class), packetdataserializer.readBoolean(), packetdataserializer.readUnsignedByte(), (EnumMainHand) packetdataserializer.readEnum(EnumMainHand.class), packetdataserializer.readBoolean(), packetdataserializer.readBoolean());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.language);
        packetdataserializer.writeByte(this.viewDistance);
        packetdataserializer.writeEnum(this.chatVisibility);
        packetdataserializer.writeBoolean(this.chatColors);
        packetdataserializer.writeByte(this.modelCustomisation);
        packetdataserializer.writeEnum(this.mainHand);
        packetdataserializer.writeBoolean(this.textFilteringEnabled);
        packetdataserializer.writeBoolean(this.allowsListing);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleClientInformation(this);
    }
}

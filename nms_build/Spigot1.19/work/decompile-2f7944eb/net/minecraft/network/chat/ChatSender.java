package net.minecraft.network.chat;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.network.PacketDataSerializer;

public record ChatSender(UUID uuid, IChatBaseComponent name, @Nullable IChatBaseComponent teamName) {

    public ChatSender(UUID uuid, IChatBaseComponent ichatbasecomponent) {
        this(uuid, ichatbasecomponent, (IChatBaseComponent) null);
    }

    public ChatSender(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUUID(), packetdataserializer.readComponent(), (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent));
    }

    public static ChatSender system(IChatBaseComponent ichatbasecomponent) {
        return new ChatSender(SystemUtils.NIL_UUID, ichatbasecomponent);
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUUID(this.uuid);
        packetdataserializer.writeComponent(this.name);
        packetdataserializer.writeNullable(this.teamName, PacketDataSerializer::writeComponent);
    }

    public ChatSender withTeamName(IChatBaseComponent ichatbasecomponent) {
        return new ChatSender(this.uuid, this.name, ichatbasecomponent);
    }
}

package net.minecraft.network.chat;

import java.util.Objects;
import net.minecraft.network.PacketDataSerializer;

public record ChatMessageContent(String plain, IChatBaseComponent decorated) {

    public ChatMessageContent(String s) {
        this(s, IChatBaseComponent.literal(s));
    }

    public boolean isDecorated() {
        return !this.decorated.equals(IChatBaseComponent.literal(this.plain));
    }

    public static ChatMessageContent read(PacketDataSerializer packetdataserializer) {
        String s = packetdataserializer.readUtf(256);
        IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent);

        return new ChatMessageContent(s, (IChatBaseComponent) Objects.requireNonNullElse(ichatbasecomponent, IChatBaseComponent.literal(s)));
    }

    public static void write(PacketDataSerializer packetdataserializer, ChatMessageContent chatmessagecontent) {
        packetdataserializer.writeUtf(chatmessagecontent.plain(), 256);
        IChatBaseComponent ichatbasecomponent = chatmessagecontent.isDecorated() ? chatmessagecontent.decorated() : null;

        packetdataserializer.writeNullable(ichatbasecomponent, PacketDataSerializer::writeComponent);
    }
}

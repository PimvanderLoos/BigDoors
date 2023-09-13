package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutTabComplete implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final Suggestions suggestions;

    public PacketPlayOutTabComplete(int i, Suggestions suggestions) {
        this.id = i;
        this.suggestions = suggestions;
    }

    public PacketPlayOutTabComplete(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        int i = packetdataserializer.readVarInt();
        int j = packetdataserializer.readVarInt();
        StringRange stringrange = StringRange.between(i, i + j);
        List<Suggestion> list = packetdataserializer.readList((packetdataserializer1) -> {
            String s = packetdataserializer1.readUtf();
            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) packetdataserializer1.readNullable(PacketDataSerializer::readComponent);

            return new Suggestion(stringrange, s, ichatbasecomponent);
        });

        this.suggestions = new Suggestions(stringrange, list);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeVarInt(this.suggestions.getRange().getStart());
        packetdataserializer.writeVarInt(this.suggestions.getRange().getLength());
        packetdataserializer.writeCollection(this.suggestions.getList(), (packetdataserializer1, suggestion) -> {
            packetdataserializer1.writeUtf(suggestion.getText());
            packetdataserializer1.writeNullable(suggestion.getTooltip(), (packetdataserializer2, message) -> {
                packetdataserializer2.writeComponent(ChatComponentUtils.fromMessage(message));
            });
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}

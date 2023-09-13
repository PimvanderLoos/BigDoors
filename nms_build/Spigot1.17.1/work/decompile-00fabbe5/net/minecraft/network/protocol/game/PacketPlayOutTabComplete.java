package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Collection;
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
        this.id = packetdataserializer.j();
        int i = packetdataserializer.j();
        int j = packetdataserializer.j();
        StringRange stringrange = StringRange.between(i, i + j);
        List<Suggestion> list = packetdataserializer.a((packetdataserializer1) -> {
            String s = packetdataserializer1.p();
            IChatBaseComponent ichatbasecomponent = packetdataserializer1.readBoolean() ? packetdataserializer1.i() : null;

            return new Suggestion(stringrange, s, ichatbasecomponent);
        });

        this.suggestions = new Suggestions(stringrange, list);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.d(this.suggestions.getRange().getStart());
        packetdataserializer.d(this.suggestions.getRange().getLength());
        packetdataserializer.a((Collection) this.suggestions.getList(), (packetdataserializer1, suggestion) -> {
            packetdataserializer1.a(suggestion.getText());
            packetdataserializer1.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() != null) {
                packetdataserializer1.a(ChatComponentUtils.a(suggestion.getTooltip()));
            }

        });
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public Suggestions c() {
        return this.suggestions;
    }
}

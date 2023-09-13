package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBEdit implements Packet<PacketListenerPlayIn> {

    public static final int MAX_BYTES_PER_CHAR = 4;
    private static final int TITLE_MAX_CHARS = 128;
    private static final int PAGE_MAX_CHARS = 8192;
    private static final int MAX_PAGES_COUNT = 200;
    private final int slot;
    private final List<String> pages;
    private final Optional<String> title;

    public PacketPlayInBEdit(int i, List<String> list, Optional<String> optional) {
        this.slot = i;
        this.pages = ImmutableList.copyOf(list);
        this.title = optional;
    }

    public PacketPlayInBEdit(PacketDataSerializer packetdataserializer) {
        this.slot = packetdataserializer.readVarInt();
        this.pages = (List) packetdataserializer.readCollection(PacketDataSerializer.limitValue(Lists::newArrayListWithCapacity, 200), (packetdataserializer1) -> {
            return packetdataserializer1.readUtf(8192);
        });
        this.title = packetdataserializer.readOptional((packetdataserializer1) -> {
            return packetdataserializer1.readUtf(128);
        });
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.slot);
        packetdataserializer.writeCollection(this.pages, (packetdataserializer1, s) -> {
            packetdataserializer1.writeUtf(s, 8192);
        });
        packetdataserializer.writeOptional(this.title, (packetdataserializer1, s) -> {
            packetdataserializer1.writeUtf(s, 128);
        });
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleEditBook(this);
    }

    public List<String> getPages() {
        return this.pages;
    }

    public Optional<String> getTitle() {
        return this.title;
    }

    public int getSlot() {
        return this.slot;
    }
}

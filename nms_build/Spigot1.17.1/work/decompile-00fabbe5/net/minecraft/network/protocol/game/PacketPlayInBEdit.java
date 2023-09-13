package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
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
        this.slot = packetdataserializer.j();
        this.pages = (List) packetdataserializer.a(PacketDataSerializer.a(Lists::newArrayListWithCapacity, 200), (packetdataserializer1) -> {
            return packetdataserializer1.e(8192);
        });
        this.title = packetdataserializer.b((packetdataserializer1) -> {
            return packetdataserializer1.e(128);
        });
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.slot);
        packetdataserializer.a((Collection) this.pages, (packetdataserializer1, s) -> {
            packetdataserializer1.a(s, 8192);
        });
        packetdataserializer.a(this.title, (packetdataserializer1, s) -> {
            packetdataserializer1.a(s, 128);
        });
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public List<String> b() {
        return this.pages;
    }

    public Optional<String> c() {
        return this.title;
    }

    public int d() {
        return this.slot;
    }
}

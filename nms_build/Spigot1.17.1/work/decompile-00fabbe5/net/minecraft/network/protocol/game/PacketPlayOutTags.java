package net.minecraft.network.protocol.game;

import java.util.Map;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.Tags;

public class PacketPlayOutTags implements Packet<PacketListenerPlayOut> {

    private final Map<ResourceKey<? extends IRegistry<?>>, Tags.a> tags;

    public PacketPlayOutTags(Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map) {
        this.tags = map;
    }

    public PacketPlayOutTags(PacketDataSerializer packetdataserializer) {
        this.tags = packetdataserializer.a((packetdataserializer1) -> {
            return ResourceKey.a(packetdataserializer1.q());
        }, Tags.a::b);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.tags, (packetdataserializer1, resourcekey) -> {
            packetdataserializer1.a(resourcekey.a());
        }, (packetdataserializer1, tags_a) -> {
            tags_a.a(packetdataserializer1);
        });
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public Map<ResourceKey<? extends IRegistry<?>>, Tags.a> b() {
        return this.tags;
    }
}

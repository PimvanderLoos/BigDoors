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
        this.tags = packetdataserializer.readMap((packetdataserializer1) -> {
            return ResourceKey.createRegistryKey(packetdataserializer1.readResourceLocation());
        }, Tags.a::read);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeMap(this.tags, (packetdataserializer1, resourcekey) -> {
            packetdataserializer1.writeResourceLocation(resourcekey.location());
        }, (packetdataserializer1, tags_a) -> {
            tags_a.write(packetdataserializer1);
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateTags(this);
    }

    public Map<ResourceKey<? extends IRegistry<?>>, Tags.a> getTags() {
        return this.tags;
    }
}

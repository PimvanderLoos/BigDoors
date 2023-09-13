package net.minecraft.network.protocol.game;

import java.util.Map;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

public class PacketPlayOutTags implements Packet<PacketListenerPlayOut> {

    private final Map<ResourceKey<? extends IRegistry<?>>, TagNetworkSerialization.a> tags;

    public PacketPlayOutTags(Map<ResourceKey<? extends IRegistry<?>>, TagNetworkSerialization.a> map) {
        this.tags = map;
    }

    public PacketPlayOutTags(PacketDataSerializer packetdataserializer) {
        this.tags = packetdataserializer.readMap((packetdataserializer1) -> {
            return ResourceKey.createRegistryKey(packetdataserializer1.readResourceLocation());
        }, TagNetworkSerialization.a::read);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeMap(this.tags, (packetdataserializer1, resourcekey) -> {
            packetdataserializer1.writeResourceLocation(resourcekey.location());
        }, (packetdataserializer1, tagnetworkserialization_a) -> {
            tagnetworkserialization_a.write(packetdataserializer1);
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateTags(this);
    }

    public Map<ResourceKey<? extends IRegistry<?>>, TagNetworkSerialization.a> getTags() {
        return this.tags;
    }
}

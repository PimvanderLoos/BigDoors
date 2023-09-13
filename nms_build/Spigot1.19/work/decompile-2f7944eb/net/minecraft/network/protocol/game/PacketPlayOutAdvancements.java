package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutAdvancements implements Packet<PacketListenerPlayOut> {

    private final boolean reset;
    private final Map<MinecraftKey, Advancement.SerializedAdvancement> added;
    private final Set<MinecraftKey> removed;
    private final Map<MinecraftKey, AdvancementProgress> progress;

    public PacketPlayOutAdvancements(boolean flag, Collection<Advancement> collection, Set<MinecraftKey> set, Map<MinecraftKey, AdvancementProgress> map) {
        this.reset = flag;
        Builder<MinecraftKey, Advancement.SerializedAdvancement> builder = ImmutableMap.builder();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            builder.put(advancement.getId(), advancement.deconstruct());
        }

        this.added = builder.build();
        this.removed = ImmutableSet.copyOf(set);
        this.progress = ImmutableMap.copyOf(map);
    }

    public PacketPlayOutAdvancements(PacketDataSerializer packetdataserializer) {
        this.reset = packetdataserializer.readBoolean();
        this.added = packetdataserializer.readMap(PacketDataSerializer::readResourceLocation, Advancement.SerializedAdvancement::fromNetwork);
        this.removed = (Set) packetdataserializer.readCollection(Sets::newLinkedHashSetWithExpectedSize, PacketDataSerializer::readResourceLocation);
        this.progress = packetdataserializer.readMap(PacketDataSerializer::readResourceLocation, AdvancementProgress::fromNetwork);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.reset);
        packetdataserializer.writeMap(this.added, PacketDataSerializer::writeResourceLocation, (packetdataserializer1, advancement_serializedadvancement) -> {
            advancement_serializedadvancement.serializeToNetwork(packetdataserializer1);
        });
        packetdataserializer.writeCollection(this.removed, PacketDataSerializer::writeResourceLocation);
        packetdataserializer.writeMap(this.progress, PacketDataSerializer::writeResourceLocation, (packetdataserializer1, advancementprogress) -> {
            advancementprogress.serializeToNetwork(packetdataserializer1);
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateAdvancementsPacket(this);
    }

    public Map<MinecraftKey, Advancement.SerializedAdvancement> getAdded() {
        return this.added;
    }

    public Set<MinecraftKey> getRemoved() {
        return this.removed;
    }

    public Map<MinecraftKey, AdvancementProgress> getProgress() {
        return this.progress;
    }

    public boolean shouldReset() {
        return this.reset;
    }
}

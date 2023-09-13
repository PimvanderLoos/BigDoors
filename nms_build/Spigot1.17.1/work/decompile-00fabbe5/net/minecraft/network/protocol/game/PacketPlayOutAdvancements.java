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

            builder.put(advancement.getName(), advancement.a());
        }

        this.added = builder.build();
        this.removed = ImmutableSet.copyOf(set);
        this.progress = ImmutableMap.copyOf(map);
    }

    public PacketPlayOutAdvancements(PacketDataSerializer packetdataserializer) {
        this.reset = packetdataserializer.readBoolean();
        this.added = packetdataserializer.a(PacketDataSerializer::q, Advancement.SerializedAdvancement::b);
        this.removed = (Set) packetdataserializer.a(Sets::newLinkedHashSetWithExpectedSize, PacketDataSerializer::q);
        this.progress = packetdataserializer.a(PacketDataSerializer::q, AdvancementProgress::b);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.reset);
        packetdataserializer.a(this.added, PacketDataSerializer::a, (packetdataserializer1, advancement_serializedadvancement) -> {
            advancement_serializedadvancement.a(packetdataserializer1);
        });
        packetdataserializer.a((Collection) this.removed, PacketDataSerializer::a);
        packetdataserializer.a(this.progress, PacketDataSerializer::a, (packetdataserializer1, advancementprogress) -> {
            advancementprogress.a(packetdataserializer1);
        });
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public Map<MinecraftKey, Advancement.SerializedAdvancement> b() {
        return this.added;
    }

    public Set<MinecraftKey> c() {
        return this.removed;
    }

    public Map<MinecraftKey, AdvancementProgress> d() {
        return this.progress;
    }

    public boolean e() {
        return this.reset;
    }
}

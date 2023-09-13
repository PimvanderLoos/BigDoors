package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {

    public TagNetworkSerialization() {}

    public static Map<ResourceKey<? extends IRegistry<?>>, TagNetworkSerialization.a> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> layeredregistryaccess) {
        return (Map) RegistrySynchronization.networkSafeRegistries(layeredregistryaccess).map((iregistrycustom_d) -> {
            return Pair.of(iregistrycustom_d.key(), serializeToNetwork(iregistrycustom_d.value()));
        }).filter((pair) -> {
            return !((TagNetworkSerialization.a) pair.getSecond()).isEmpty();
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> TagNetworkSerialization.a serializeToNetwork(IRegistry<T> iregistry) {
        Map<MinecraftKey, IntList> map = new HashMap();

        iregistry.getTags().forEach((pair) -> {
            HolderSet<T> holderset = (HolderSet) pair.getSecond();
            IntArrayList intarraylist = new IntArrayList(holderset.size());
            Iterator iterator = holderset.iterator();

            while (iterator.hasNext()) {
                Holder<T> holder = (Holder) iterator.next();

                if (holder.kind() != Holder.b.REFERENCE) {
                    throw new IllegalStateException("Can't serialize unregistered value " + holder);
                }

                intarraylist.add(iregistry.getId(holder.value()));
            }

            map.put(((TagKey) pair.getFirst()).location(), intarraylist);
        });
        return new TagNetworkSerialization.a(map);
    }

    public static <T> void deserializeTagsFromNetwork(ResourceKey<? extends IRegistry<T>> resourcekey, IRegistry<T> iregistry, TagNetworkSerialization.a tagnetworkserialization_a, TagNetworkSerialization.b<T> tagnetworkserialization_b) {
        tagnetworkserialization_a.tags.forEach((minecraftkey, intlist) -> {
            TagKey<T> tagkey = TagKey.create(resourcekey, minecraftkey);
            IntStream intstream = intlist.intStream();

            Objects.requireNonNull(iregistry);
            List<Holder<T>> list = (List) intstream.mapToObj(iregistry::getHolder).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());

            tagnetworkserialization_b.accept(tagkey, list);
        });
    }

    public static final class a {

        final Map<MinecraftKey, IntList> tags;

        a(Map<MinecraftKey, IntList> map) {
            this.tags = map;
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeMap(this.tags, PacketDataSerializer::writeResourceLocation, PacketDataSerializer::writeIntIdList);
        }

        public static TagNetworkSerialization.a read(PacketDataSerializer packetdataserializer) {
            return new TagNetworkSerialization.a(packetdataserializer.readMap(PacketDataSerializer::readResourceLocation, PacketDataSerializer::readIntIdList));
        }

        public boolean isEmpty() {
            return this.tags.isEmpty();
        }
    }

    @FunctionalInterface
    public interface b<T> {

        void accept(TagKey<T> tagkey, List<Holder<T>> list);
    }
}

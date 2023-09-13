package net.minecraft.tags;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public interface Tags<T> {

    Map<MinecraftKey, Tag<T>> getAllTags();

    @Nullable
    default Tag<T> getTag(MinecraftKey minecraftkey) {
        return (Tag) this.getAllTags().get(minecraftkey);
    }

    Tag<T> getTagOrEmpty(MinecraftKey minecraftkey);

    @Nullable
    default MinecraftKey getId(Tag.e<T> tag_e) {
        return tag_e.getName();
    }

    @Nullable
    MinecraftKey getId(Tag<T> tag);

    default boolean hasTag(MinecraftKey minecraftkey) {
        return this.getAllTags().containsKey(minecraftkey);
    }

    default Collection<MinecraftKey> getAvailableTags() {
        return this.getAllTags().keySet();
    }

    default Collection<MinecraftKey> getMatchingTags(T t0) {
        List<MinecraftKey> list = Lists.newArrayList();
        Iterator iterator = this.getAllTags().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag<T>> entry = (Entry) iterator.next();

            if (((Tag) entry.getValue()).contains(t0)) {
                list.add((MinecraftKey) entry.getKey());
            }
        }

        return list;
    }

    default Tags.a serializeToNetwork(IRegistry<T> iregistry) {
        Map<MinecraftKey, Tag<T>> map = this.getAllTags();
        Map<MinecraftKey, IntList> map1 = Maps.newHashMapWithExpectedSize(map.size());

        map.forEach((minecraftkey, tag) -> {
            List<T> list = tag.getValues();
            IntArrayList intarraylist = new IntArrayList(list.size());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                intarraylist.add(iregistry.getId(t0));
            }

            map1.put(minecraftkey, intarraylist);
        });
        return new Tags.a(map1);
    }

    static <T> Tags<T> createFromNetwork(Tags.a tags_a, IRegistry<? extends T> iregistry) {
        Map<MinecraftKey, Tag<T>> map = Maps.newHashMapWithExpectedSize(tags_a.tags.size());

        tags_a.tags.forEach((minecraftkey, intlist) -> {
            Builder<T> builder = ImmutableSet.builder();
            IntListIterator intlistiterator = intlist.iterator();

            while (intlistiterator.hasNext()) {
                int i = (Integer) intlistiterator.next();

                builder.add(iregistry.byId(i));
            }

            map.put(minecraftkey, Tag.fromSet(builder.build()));
        });
        return of(map);
    }

    static <T> Tags<T> empty() {
        return of(ImmutableBiMap.of());
    }

    static <T> Tags<T> of(Map<MinecraftKey, Tag<T>> map) {
        final BiMap<MinecraftKey, Tag<T>> bimap = ImmutableBiMap.copyOf(map);

        return new Tags<T>() {
            private final Tag<T> empty = TagSet.empty();

            @Override
            public Tag<T> getTagOrEmpty(MinecraftKey minecraftkey) {
                return (Tag) bimap.getOrDefault(minecraftkey, this.empty);
            }

            @Nullable
            @Override
            public MinecraftKey getId(Tag<T> tag) {
                return tag instanceof Tag.e ? ((Tag.e) tag).getName() : (MinecraftKey) bimap.inverse().get(tag);
            }

            @Override
            public Map<MinecraftKey, Tag<T>> getAllTags() {
                return bimap;
            }
        };
    }

    public static class a {

        final Map<MinecraftKey, IntList> tags;

        a(Map<MinecraftKey, IntList> map) {
            this.tags = map;
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeMap(this.tags, PacketDataSerializer::writeResourceLocation, PacketDataSerializer::writeIntIdList);
        }

        public static Tags.a read(PacketDataSerializer packetdataserializer) {
            return new Tags.a(packetdataserializer.readMap(PacketDataSerializer::readResourceLocation, PacketDataSerializer::readIntIdList));
        }
    }
}

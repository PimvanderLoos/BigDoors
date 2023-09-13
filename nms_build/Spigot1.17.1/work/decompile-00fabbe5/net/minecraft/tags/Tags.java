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

    Map<MinecraftKey, Tag<T>> a();

    @Nullable
    default Tag<T> a(MinecraftKey minecraftkey) {
        return (Tag) this.a().get(minecraftkey);
    }

    Tag<T> b(MinecraftKey minecraftkey);

    @Nullable
    default MinecraftKey a(Tag.e<T> tag_e) {
        return tag_e.a();
    }

    @Nullable
    MinecraftKey a(Tag<T> tag);

    default boolean c(MinecraftKey minecraftkey) {
        return this.a().containsKey(minecraftkey);
    }

    default Collection<MinecraftKey> b() {
        return this.a().keySet();
    }

    default Collection<MinecraftKey> a(T t0) {
        List<MinecraftKey> list = Lists.newArrayList();
        Iterator iterator = this.a().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag<T>> entry = (Entry) iterator.next();

            if (((Tag) entry.getValue()).isTagged(t0)) {
                list.add((MinecraftKey) entry.getKey());
            }
        }

        return list;
    }

    default Tags.a a(IRegistry<T> iregistry) {
        Map<MinecraftKey, Tag<T>> map = this.a();
        Map<MinecraftKey, IntList> map1 = Maps.newHashMapWithExpectedSize(map.size());

        map.forEach((minecraftkey, tag) -> {
            List<T> list = tag.getTagged();
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

    static <T> Tags<T> a(Tags.a tags_a, IRegistry<? extends T> iregistry) {
        Map<MinecraftKey, Tag<T>> map = Maps.newHashMapWithExpectedSize(tags_a.tags.size());

        tags_a.tags.forEach((minecraftkey, intlist) -> {
            Builder<T> builder = ImmutableSet.builder();
            IntListIterator intlistiterator = intlist.iterator();

            while (intlistiterator.hasNext()) {
                int i = (Integer) intlistiterator.next();

                builder.add(iregistry.fromId(i));
            }

            map.put(minecraftkey, Tag.b(builder.build()));
        });
        return a((Map) map);
    }

    static <T> Tags<T> c() {
        return a((Map) ImmutableBiMap.of());
    }

    static <T> Tags<T> a(Map<MinecraftKey, Tag<T>> map) {
        final BiMap<MinecraftKey, Tag<T>> bimap = ImmutableBiMap.copyOf(map);

        return new Tags<T>() {
            private final Tag<T> empty = TagSet.a();

            @Override
            public Tag<T> b(MinecraftKey minecraftkey) {
                return (Tag) bimap.getOrDefault(minecraftkey, this.empty);
            }

            @Nullable
            @Override
            public MinecraftKey a(Tag<T> tag) {
                return tag instanceof Tag.e ? ((Tag.e) tag).a() : (MinecraftKey) bimap.inverse().get(tag);
            }

            @Override
            public Map<MinecraftKey, Tag<T>> a() {
                return bimap;
            }
        };
    }

    public static class a {

        final Map<MinecraftKey, IntList> tags;

        a(Map<MinecraftKey, IntList> map) {
            this.tags = map;
        }

        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.a(this.tags, PacketDataSerializer::a, PacketDataSerializer::a);
        }

        public static Tags.a b(PacketDataSerializer packetdataserializer) {
            return new Tags.a(packetdataserializer.a(PacketDataSerializer::q, PacketDataSerializer::a));
        }
    }
}

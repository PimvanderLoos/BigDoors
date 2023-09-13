package net.minecraft.tags;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
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
    MinecraftKey a(Tag<T> tag);

    default MinecraftKey b(Tag<T> tag) {
        MinecraftKey minecraftkey = this.a(tag);

        if (minecraftkey == null) {
            throw new IllegalStateException("Unrecognized tag");
        } else {
            return minecraftkey;
        }
    }

    default Collection<MinecraftKey> b() {
        return this.a().keySet();
    }

    default void a(PacketDataSerializer packetdataserializer, RegistryBlocks<T> registryblocks) {
        Map<MinecraftKey, Tag<T>> map = this.a();

        packetdataserializer.d(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag<T>> entry = (Entry) iterator.next();

            packetdataserializer.a((MinecraftKey) entry.getKey());
            packetdataserializer.d(((Tag) entry.getValue()).getTagged().size());
            Iterator iterator1 = ((Tag) entry.getValue()).getTagged().iterator();

            while (iterator1.hasNext()) {
                T t0 = iterator1.next();

                packetdataserializer.d(registryblocks.a(t0));
            }
        }

    }

    static <T> Tags<T> a(PacketDataSerializer packetdataserializer, IRegistry<T> iregistry) {
        Map<MinecraftKey, Tag<T>> map = Maps.newHashMap();
        int i = packetdataserializer.i();

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = packetdataserializer.p();
            int k = packetdataserializer.i();
            Builder<T> builder = ImmutableSet.builder();

            for (int l = 0; l < k; ++l) {
                builder.add(iregistry.fromId(packetdataserializer.i()));
            }

            map.put(minecraftkey, Tag.b(builder.build()));
        }

        return a((Map) map);
    }

    static <T> Tags<T> c() {
        return a((Map) ImmutableBiMap.of());
    }

    static <T> Tags<T> a(Map<MinecraftKey, Tag<T>> map) {
        final BiMap<MinecraftKey, Tag<T>> bimap = ImmutableBiMap.copyOf(map);

        return new Tags<T>() {
            private final Tag<T> b = TagSet.a();

            @Override
            public Tag<T> b(MinecraftKey minecraftkey) {
                return (Tag) bimap.getOrDefault(minecraftkey, this.b);
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
}

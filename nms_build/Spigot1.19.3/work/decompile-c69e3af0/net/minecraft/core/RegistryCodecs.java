package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {

    public RegistryCodecs() {}

    private static <T> MapCodec<RegistryCodecs.a<T>> withNameAndId(ResourceKey<? extends IRegistry<T>> resourcekey, MapCodec<T> mapcodec) {
        return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ResourceKey.codec(resourcekey).fieldOf("name").forGetter(RegistryCodecs.a::key), Codec.INT.fieldOf("id").forGetter(RegistryCodecs.a::id), mapcodec.forGetter(RegistryCodecs.a::value)).apply(instance, RegistryCodecs.a::new);
        });
    }

    public static <T> Codec<IRegistry<T>> networkCodec(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return withNameAndId(resourcekey, codec.fieldOf("element")).codec().listOf().xmap((list) -> {
            IRegistryWritable<T> iregistrywritable = new RegistryMaterials<>(resourcekey, lifecycle);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                RegistryCodecs.a<T> registrycodecs_a = (RegistryCodecs.a) iterator.next();

                iregistrywritable.registerMapping(registrycodecs_a.id(), registrycodecs_a.key(), registrycodecs_a.value(), lifecycle);
            }

            return iregistrywritable;
        }, (iregistry) -> {
            Builder<RegistryCodecs.a<T>> builder = ImmutableList.builder();
            Iterator iterator = iregistry.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                builder.add(new RegistryCodecs.a<>((ResourceKey) iregistry.getResourceKey(t0).get(), iregistry.getId(t0), t0));
            }

            return builder.build();
        });
    }

    public static <E> Codec<IRegistry<E>> fullCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Lifecycle lifecycle, Codec<E> codec) {
        Codec<Map<ResourceKey<E>, E>> codec1 = Codec.unboundedMap(ResourceKey.codec(resourcekey), codec);

        return codec1.xmap((map) -> {
            IRegistryWritable<E> iregistrywritable = new RegistryMaterials<>(resourcekey, lifecycle);

            map.forEach((resourcekey1, object) -> {
                iregistrywritable.register(resourcekey1, object, lifecycle);
            });
            return iregistrywritable.freeze();
        }, (iregistry) -> {
            return ImmutableMap.copyOf(iregistry.entrySet());
        });
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        return homogeneousList(resourcekey, codec, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        return HolderSetCodec.create(resourcekey, RegistryFileCodec.create(resourcekey, codec), flag);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return homogeneousList(resourcekey, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends IRegistry<E>> resourcekey, boolean flag) {
        return HolderSetCodec.create(resourcekey, RegistryFixedCodec.create(resourcekey), flag);
    }

    private static record a<T> (ResourceKey<T> key, int id, T value) {

    }
}

package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;

public class RegistrySynchronization {

    private static final Map<ResourceKey<? extends IRegistry<?>>, RegistrySynchronization.a<?>> NETWORKABLE_REGISTRIES = (Map) SystemUtils.make(() -> {
        Builder<ResourceKey<? extends IRegistry<?>>, RegistrySynchronization.a<?>> builder = ImmutableMap.builder();

        put(builder, Registries.BIOME, BiomeBase.NETWORK_CODEC);
        put(builder, Registries.CHAT_TYPE, ChatMessageType.CODEC);
        put(builder, Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC);
        put(builder, Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC);
        put(builder, Registries.DIMENSION_TYPE, DimensionManager.DIRECT_CODEC);
        put(builder, Registries.DAMAGE_TYPE, DamageType.CODEC);
        return builder.build();
    });
    public static final Codec<IRegistryCustom> NETWORK_CODEC = makeNetworkCodec();

    public RegistrySynchronization() {}

    private static <E> void put(Builder<ResourceKey<? extends IRegistry<?>>, RegistrySynchronization.a<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        builder.put(resourcekey, new RegistrySynchronization.a<>(resourcekey, codec));
    }

    private static Stream<IRegistryCustom.d<?>> ownedNetworkableRegistries(IRegistryCustom iregistrycustom) {
        return iregistrycustom.registries().filter((iregistrycustom_d) -> {
            return RegistrySynchronization.NETWORKABLE_REGISTRIES.containsKey(iregistrycustom_d.key());
        });
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (DataResult) Optional.ofNullable((RegistrySynchronization.a) RegistrySynchronization.NETWORKABLE_REGISTRIES.get(resourcekey)).map((registrysynchronization_a) -> {
            return registrysynchronization_a.networkCodec();
        }).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
                return "Unknown or not serializable registry: " + resourcekey;
            });
        });
    }

    private static <E> Codec<IRegistryCustom> makeNetworkCodec() {
        Codec<ResourceKey<? extends IRegistry<E>>> codec = MinecraftKey.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
        Codec<IRegistry<E>> codec1 = codec.partialDispatch("type", (iregistry) -> {
            return DataResult.success(iregistry.key());
        }, (resourcekey) -> {
            return getNetworkCodec(resourcekey).map((codec2) -> {
                return RegistryCodecs.networkCodec(resourcekey, Lifecycle.experimental(), codec2);
            });
        });
        UnboundedMapCodec<? extends ResourceKey<? extends IRegistry<?>>, ? extends IRegistry<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);

        return captureMap(unboundedmapcodec);
    }

    private static <K extends ResourceKey<? extends IRegistry<?>>, V extends IRegistry<?>> Codec<IRegistryCustom> captureMap(UnboundedMapCodec<K, V> unboundedmapcodec) {
        return unboundedmapcodec.xmap(IRegistryCustom.c::new, (iregistrycustom) -> {
            return (Map) ownedNetworkableRegistries(iregistrycustom).collect(ImmutableMap.toImmutableMap((iregistrycustom_d) -> {
                return iregistrycustom_d.key();
            }, (iregistrycustom_d) -> {
                return iregistrycustom_d.value();
            }));
        });
    }

    public static Stream<IRegistryCustom.d<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> layeredregistryaccess) {
        return ownedNetworkableRegistries(layeredregistryaccess.getAccessFrom(RegistryLayer.WORLDGEN));
    }

    public static Stream<IRegistryCustom.d<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> layeredregistryaccess) {
        Stream<IRegistryCustom.d<?>> stream = layeredregistryaccess.getLayer(RegistryLayer.STATIC).registries();
        Stream<IRegistryCustom.d<?>> stream1 = networkedRegistries(layeredregistryaccess);

        return Stream.concat(stream1, stream);
    }

    private static record a<E> (ResourceKey<? extends IRegistry<E>> key, Codec<E> networkCodec) {

    }
}

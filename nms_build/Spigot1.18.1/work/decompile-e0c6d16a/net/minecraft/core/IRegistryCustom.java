package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IRegistryCustom {

    static final Logger LOGGER = LogManager.getLogger();
    static final Map<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> REGISTRIES = (Map) SystemUtils.make(() -> {
        Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> builder = ImmutableMap.builder();

        put(builder, IRegistry.DIMENSION_TYPE_REGISTRY, DimensionManager.DIRECT_CODEC, DimensionManager.DIRECT_CODEC);
        put(builder, IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC, BiomeBase.NETWORK_CODEC);
        put(builder, IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
        put(builder, IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
        put(builder, IRegistry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
        put(builder, IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
        put(builder, IRegistry.PROCESSOR_LIST_REGISTRY, DefinedStructureStructureProcessorType.DIRECT_CODEC);
        put(builder, IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC);
        put(builder, IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase.DIRECT_CODEC);
        put(builder, IRegistry.NOISE_REGISTRY, NoiseGeneratorNormal.a.DIRECT_CODEC);
        return builder.build();
    });
    private static final IRegistryCustom.Dimension BUILTIN = (IRegistryCustom.Dimension) SystemUtils.make(() -> {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();

        DimensionManager.registerBuiltin(iregistrycustom_dimension);
        IRegistryCustom.REGISTRIES.keySet().stream().filter((resourcekey) -> {
            return !resourcekey.equals(IRegistry.DIMENSION_TYPE_REGISTRY);
        }).forEach((resourcekey) -> {
            copyBuiltin(iregistrycustom_dimension, resourcekey);
        });
        return iregistrycustom_dimension;
    });

    public IRegistryCustom() {}

    public abstract <E> Optional<IRegistryWritable<E>> ownedRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey);

    public <E> IRegistryWritable<E> ownedRegistryOrThrow(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return (IRegistryWritable) this.ownedRegistry(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    public <E> Optional<? extends IRegistry<E>> registry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        Optional<? extends IRegistry<E>> optional = this.ownedRegistry(resourcekey);

        return optional.isPresent() ? optional : IRegistry.REGISTRY.getOptional(resourcekey.location());
    }

    public <E> IRegistry<E> registryOrThrow(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return (IRegistry) this.registry(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    private static <E> void put(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        builder.put(resourcekey, new IRegistryCustom.RegistryData<>(resourcekey, codec, (Codec) null));
    }

    private static <E> void put(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, Codec<E> codec1) {
        builder.put(resourcekey, new IRegistryCustom.RegistryData<>(resourcekey, codec, codec1));
    }

    public static Iterable<IRegistryCustom.RegistryData<?>> knownRegistries() {
        return IRegistryCustom.REGISTRIES.values();
    }

    public static IRegistryCustom.Dimension builtin() {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();
        RegistryResourceAccess.InMemoryStorage registryresourceaccess_inmemorystorage = new RegistryResourceAccess.InMemoryStorage();
        Iterator iterator = IRegistryCustom.REGISTRIES.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.RegistryData<?> iregistrycustom_registrydata = (IRegistryCustom.RegistryData) iterator.next();

            addBuiltinElements(iregistrycustom_dimension, registryresourceaccess_inmemorystorage, iregistrycustom_registrydata);
        }

        RegistryReadOps.createAndLoad(JsonOps.INSTANCE, (RegistryResourceAccess) registryresourceaccess_inmemorystorage, iregistrycustom_dimension);
        return iregistrycustom_dimension;
    }

    private static <E> void addBuiltinElements(IRegistryCustom.Dimension iregistrycustom_dimension, RegistryResourceAccess.InMemoryStorage registryresourceaccess_inmemorystorage, IRegistryCustom.RegistryData<E> iregistrycustom_registrydata) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_registrydata.key();
        boolean flag = !resourcekey.equals(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY) && !resourcekey.equals(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<E> iregistry = IRegistryCustom.BUILTIN.registryOrThrow(resourcekey);
        IRegistryWritable<E> iregistrywritable = iregistrycustom_dimension.ownedRegistryOrThrow(resourcekey);
        Iterator iterator = iregistry.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            ResourceKey<E> resourcekey1 = (ResourceKey) entry.getKey();
            E e0 = entry.getValue();

            if (flag) {
                registryresourceaccess_inmemorystorage.add(IRegistryCustom.BUILTIN, resourcekey1, iregistrycustom_registrydata.codec(), iregistry.getId(e0), e0, iregistry.lifecycle(e0));
            } else {
                iregistrywritable.registerMapping(iregistry.getId(e0), resourcekey1, e0, iregistry.lifecycle(e0));
            }
        }

    }

    private static <R extends IRegistry<?>> void copyBuiltin(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<R> resourcekey) {
        IRegistry<R> iregistry = RegistryGeneration.REGISTRY;
        IRegistry<?> iregistry1 = (IRegistry) iregistry.getOrThrow(resourcekey);

        copy(iregistrycustom_dimension, iregistry1);
    }

    private static <E> void copy(IRegistryCustom.Dimension iregistrycustom_dimension, IRegistry<E> iregistry) {
        IRegistryWritable<E> iregistrywritable = iregistrycustom_dimension.ownedRegistryOrThrow(iregistry.key());
        Iterator iterator = iregistry.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            E e0 = entry.getValue();

            iregistrywritable.registerMapping(iregistry.getId(e0), (ResourceKey) entry.getKey(), e0, iregistry.lifecycle(e0));
        }

    }

    public static void load(IRegistryCustom iregistrycustom, RegistryReadOps<?> registryreadops) {
        Iterator iterator = IRegistryCustom.REGISTRIES.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.RegistryData<?> iregistrycustom_registrydata = (IRegistryCustom.RegistryData) iterator.next();

            readRegistry(registryreadops, iregistrycustom, iregistrycustom_registrydata);
        }

    }

    private static <E> void readRegistry(RegistryReadOps<?> registryreadops, IRegistryCustom iregistrycustom, IRegistryCustom.RegistryData<E> iregistrycustom_registrydata) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_registrydata.key();
        RegistryMaterials<E> registrymaterials = (RegistryMaterials) iregistrycustom.ownedRegistryOrThrow(resourcekey);
        DataResult<RegistryMaterials<E>> dataresult = registryreadops.decodeElements(registrymaterials, iregistrycustom_registrydata.key(), iregistrycustom_registrydata.codec());

        dataresult.error().ifPresent((partialresult) -> {
            throw new JsonParseException("Error loading registry data: " + partialresult.message());
        });
    }

    public static record RegistryData<E> (ResourceKey<? extends IRegistry<E>> a, Codec<E> b, @Nullable Codec<E> c) {

        private final ResourceKey<? extends IRegistry<E>> key;
        private final Codec<E> codec;
        @Nullable
        private final Codec<E> networkCodec;

        public RegistryData(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, @Nullable Codec<E> codec1) {
            this.key = resourcekey;
            this.codec = codec;
            this.networkCodec = codec1;
        }

        public boolean sendToClient() {
            return this.networkCodec != null;
        }

        public ResourceKey<? extends IRegistry<E>> key() {
            return this.key;
        }

        public Codec<E> codec() {
            return this.codec;
        }

        @Nullable
        public Codec<E> networkCodec() {
            return this.networkCodec;
        }
    }

    public static final class Dimension extends IRegistryCustom {

        public static final Codec<IRegistryCustom.Dimension> NETWORK_CODEC = makeNetworkCodec();
        private final Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> registries;

        private static <E> Codec<IRegistryCustom.Dimension> makeNetworkCodec() {
            Codec<ResourceKey<? extends IRegistry<E>>> codec = MinecraftKey.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
            Codec<RegistryMaterials<E>> codec1 = codec.partialDispatch("type", (registrymaterials) -> {
                return DataResult.success(registrymaterials.key());
            }, (resourcekey) -> {
                return getNetworkCodec(resourcekey).map((codec2) -> {
                    return RegistryMaterials.networkCodec(resourcekey, Lifecycle.experimental(), codec2);
                });
            });
            UnboundedMapCodec<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);

            return captureMap(unboundedmapcodec);
        }

        private static <K extends ResourceKey<? extends IRegistry<?>>, V extends RegistryMaterials<?>> Codec<IRegistryCustom.Dimension> captureMap(UnboundedMapCodec<K, V> unboundedmapcodec) {
            return unboundedmapcodec.xmap(IRegistryCustom.Dimension::new, (iregistrycustom_dimension) -> {
                return (Map) iregistrycustom_dimension.registries.entrySet().stream().filter((entry) -> {
                    return ((IRegistryCustom.RegistryData) IRegistryCustom.REGISTRIES.get(entry.getKey())).sendToClient();
                }).collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
            });
        }

        private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return (DataResult) Optional.ofNullable((IRegistryCustom.RegistryData) IRegistryCustom.REGISTRIES.get(resourcekey)).map((iregistrycustom_registrydata) -> {
                return iregistrycustom_registrydata.networkCodec();
            }).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown or not serializable registry: " + resourcekey);
            });
        }

        public Dimension() {
            this((Map) IRegistryCustom.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), IRegistryCustom.Dimension::createRegistry)));
        }

        public static IRegistryCustom readFromDisk(Dynamic<?> dynamic) {
            return new IRegistryCustom.Dimension((Map) IRegistryCustom.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), (resourcekey) -> {
                return parseRegistry(resourcekey, dynamic);
            })));
        }

        private static <E> RegistryMaterials<?> parseRegistry(ResourceKey<? extends IRegistry<?>> resourcekey, Dynamic<?> dynamic) {
            DataResult dataresult = RegistryLookupCodec.create(resourcekey).codec().parse(dynamic);
            String s = resourcekey + " registry: ";
            Logger logger = IRegistryCustom.LOGGER;

            Objects.requireNonNull(logger);
            return (RegistryMaterials) dataresult.resultOrPartial(SystemUtils.prefix(s, logger::error)).orElseThrow(() -> {
                return new IllegalStateException("Failed to get " + resourcekey + " registry");
            });
        }

        private Dimension(Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> map) {
            this.registries = map;
        }

        private static <E> RegistryMaterials<?> createRegistry(ResourceKey<? extends IRegistry<?>> resourcekey) {
            return new RegistryMaterials<>(resourcekey, Lifecycle.stable());
        }

        @Override
        public <E> Optional<IRegistryWritable<E>> ownedRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return Optional.ofNullable((RegistryMaterials) this.registries.get(resourcekey)).map((registrymaterials) -> {
                return registrymaterials;
            });
        }
    }
}

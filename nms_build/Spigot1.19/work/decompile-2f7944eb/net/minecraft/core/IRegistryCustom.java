package net.minecraft.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.slf4j.Logger;

public interface IRegistryCustom {

    Logger LOGGER = LogUtils.getLogger();
    Map<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> REGISTRIES = (Map) SystemUtils.make(() -> {
        Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> builder = ImmutableMap.builder();

        put(builder, IRegistry.DIMENSION_TYPE_REGISTRY, DimensionManager.DIRECT_CODEC, DimensionManager.DIRECT_CODEC);
        put(builder, IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC, BiomeBase.NETWORK_CODEC);
        put(builder, IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
        put(builder, IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
        put(builder, IRegistry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
        put(builder, IRegistry.STRUCTURE_REGISTRY, Structure.DIRECT_CODEC);
        put(builder, IRegistry.STRUCTURE_SET_REGISTRY, StructureSet.DIRECT_CODEC);
        put(builder, IRegistry.PROCESSOR_LIST_REGISTRY, DefinedStructureStructureProcessorType.DIRECT_CODEC);
        put(builder, IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC);
        put(builder, IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase.DIRECT_CODEC);
        put(builder, IRegistry.NOISE_REGISTRY, NoiseGeneratorNormal.a.DIRECT_CODEC);
        put(builder, IRegistry.DENSITY_FUNCTION_REGISTRY, DensityFunction.DIRECT_CODEC);
        put(builder, IRegistry.CHAT_TYPE_REGISTRY, ChatMessageType.CODEC, ChatMessageType.CODEC);
        put(builder, IRegistry.WORLD_PRESET_REGISTRY, WorldPreset.DIRECT_CODEC);
        put(builder, IRegistry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY, FlatLevelGeneratorPreset.DIRECT_CODEC);
        return builder.build();
    });
    Codec<IRegistryCustom> NETWORK_CODEC = makeNetworkCodec();
    Supplier<IRegistryCustom.Dimension> BUILTIN = Suppliers.memoize(() -> {
        return builtinCopy().freeze();
    });

    <E> Optional<IRegistry<E>> ownedRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey);

    default <E> IRegistry<E> ownedRegistryOrThrow(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return (IRegistry) this.ownedRegistry(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    default <E> Optional<? extends IRegistry<E>> registry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        Optional<? extends IRegistry<E>> optional = this.ownedRegistry(resourcekey);

        return optional.isPresent() ? optional : IRegistry.REGISTRY.getOptional(resourcekey.location());
    }

    default <E> IRegistry<E> registryOrThrow(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
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

    static Iterable<IRegistryCustom.RegistryData<?>> knownRegistries() {
        return IRegistryCustom.REGISTRIES.values();
    }

    Stream<IRegistryCustom.d<?>> ownedRegistries();

    private static Stream<IRegistryCustom.d<Object>> globalRegistries() {
        return IRegistry.REGISTRY.holders().map(IRegistryCustom.d::fromHolder);
    }

    default Stream<IRegistryCustom.d<?>> registries() {
        return Stream.concat(this.ownedRegistries(), globalRegistries());
    }

    default Stream<IRegistryCustom.d<?>> networkSafeRegistries() {
        return Stream.concat(this.ownedNetworkableRegistries(), globalRegistries());
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
        return unboundedmapcodec.xmap(IRegistryCustom.b::new, (iregistrycustom) -> {
            return (Map) iregistrycustom.ownedNetworkableRegistries().collect(ImmutableMap.toImmutableMap((iregistrycustom_d) -> {
                return iregistrycustom_d.key();
            }, (iregistrycustom_d) -> {
                return iregistrycustom_d.value();
            }));
        });
    }

    private default Stream<IRegistryCustom.d<?>> ownedNetworkableRegistries() {
        return this.ownedRegistries().filter((iregistrycustom_d) -> {
            return ((IRegistryCustom.RegistryData) IRegistryCustom.REGISTRIES.get(iregistrycustom_d.key)).sendToClient();
        });
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (DataResult) Optional.ofNullable((IRegistryCustom.RegistryData) IRegistryCustom.REGISTRIES.get(resourcekey)).map((iregistrycustom_registrydata) -> {
            return iregistrycustom_registrydata.networkCodec();
        }).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown or not serializable registry: " + resourcekey);
        });
    }

    private static Map<ResourceKey<? extends IRegistry<?>>, ? extends IRegistryWritable<?>> createFreshRegistries() {
        return (Map) IRegistryCustom.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), IRegistryCustom::createRegistry));
    }

    private static IRegistryCustom.e blankWriteable() {
        return new IRegistryCustom.f(createFreshRegistries());
    }

    static IRegistryCustom.Dimension fromRegistryOfRegistries(final IRegistry<? extends IRegistry<?>> iregistry) {
        return new IRegistryCustom.Dimension() {
            @Override
            public <T> Optional<IRegistry<T>> ownedRegistry(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
                IRegistry<IRegistry<T>> iregistry1 = iregistry;

                return iregistry1.getOptional(resourcekey);
            }

            @Override
            public Stream<IRegistryCustom.d<?>> ownedRegistries() {
                return iregistry.entrySet().stream().map(IRegistryCustom.d::fromMapEntry);
            }
        };
    }

    static IRegistryCustom.e builtinCopy() {
        IRegistryCustom.e iregistrycustom_e = blankWriteable();
        RegistryResourceAccess.InMemoryStorage registryresourceaccess_inmemorystorage = new RegistryResourceAccess.InMemoryStorage();
        Iterator iterator = IRegistryCustom.REGISTRIES.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.RegistryData<?>> entry = (Entry) iterator.next();

            addBuiltinElements(registryresourceaccess_inmemorystorage, (IRegistryCustom.RegistryData) entry.getValue());
        }

        RegistryOps.createAndLoad(JsonOps.INSTANCE, iregistrycustom_e, (RegistryResourceAccess) registryresourceaccess_inmemorystorage);
        return iregistrycustom_e;
    }

    private static <E> void addBuiltinElements(RegistryResourceAccess.InMemoryStorage registryresourceaccess_inmemorystorage, IRegistryCustom.RegistryData<E> iregistrycustom_registrydata) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_registrydata.key();
        IRegistry<E> iregistry = RegistryGeneration.ACCESS.registryOrThrow(resourcekey);
        Iterator iterator = iregistry.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            ResourceKey<E> resourcekey1 = (ResourceKey) entry.getKey();
            E e0 = entry.getValue();

            registryresourceaccess_inmemorystorage.add(RegistryGeneration.ACCESS, resourcekey1, iregistrycustom_registrydata.codec(), iregistry.getId(e0), e0, iregistry.lifecycle(e0));
        }

    }

    static void load(IRegistryCustom.e iregistrycustom_e, DynamicOps<JsonElement> dynamicops, RegistryLoader registryloader) {
        RegistryLoader.a registryloader_a = registryloader.bind(iregistrycustom_e);
        Iterator iterator = IRegistryCustom.REGISTRIES.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.RegistryData<?> iregistrycustom_registrydata = (IRegistryCustom.RegistryData) iterator.next();

            readRegistry(dynamicops, registryloader_a, iregistrycustom_registrydata);
        }

    }

    private static <E> void readRegistry(DynamicOps<JsonElement> dynamicops, RegistryLoader.a registryloader_a, IRegistryCustom.RegistryData<E> iregistrycustom_registrydata) {
        DataResult<? extends IRegistry<E>> dataresult = registryloader_a.overrideRegistryFromResources(iregistrycustom_registrydata.key(), iregistrycustom_registrydata.codec(), dynamicops);

        dataresult.error().ifPresent((partialresult) -> {
            throw new JsonParseException("Error loading registry data: " + partialresult.message());
        });
    }

    static IRegistryCustom readFromDisk(Dynamic<?> dynamic) {
        return new IRegistryCustom.b((Map) IRegistryCustom.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), (resourcekey) -> {
            return retrieveRegistry(resourcekey, dynamic);
        })));
    }

    static <E> IRegistry<E> retrieveRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey, Dynamic<?> dynamic) {
        DataResult dataresult = RegistryOps.retrieveRegistry(resourcekey).codec().parse(dynamic);
        String s = resourcekey + " registry: ";
        Logger logger = IRegistryCustom.LOGGER;

        Objects.requireNonNull(logger);
        return (IRegistry) dataresult.resultOrPartial(SystemUtils.prefix(s, logger::error)).orElseThrow(() -> {
            return new IllegalStateException("Failed to get " + resourcekey + " registry");
        });
    }

    static <E> IRegistryWritable<?> createRegistry(ResourceKey<? extends IRegistry<?>> resourcekey) {
        return new RegistryMaterials<>(resourcekey, Lifecycle.stable(), (Function) null);
    }

    default IRegistryCustom.Dimension freeze() {
        return new IRegistryCustom.b(this.ownedRegistries().map(IRegistryCustom.d::freeze));
    }

    default Lifecycle allElementsLifecycle() {
        return (Lifecycle) this.ownedRegistries().map((iregistrycustom_d) -> {
            return iregistrycustom_d.value.elementsLifecycle();
        }).reduce(Lifecycle.stable(), Lifecycle::add);
    }

    public static record RegistryData<E> (ResourceKey<? extends IRegistry<E>> key, Codec<E> codec, @Nullable Codec<E> networkCodec) {

        public boolean sendToClient() {
            return this.networkCodec != null;
        }
    }

    public static final class f implements IRegistryCustom.e {

        private final Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends IRegistryWritable<?>> registries;

        f(Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends IRegistryWritable<?>> map) {
            this.registries = map;
        }

        @Override
        public <E> Optional<IRegistry<E>> ownedRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return Optional.ofNullable((IRegistryWritable) this.registries.get(resourcekey)).map((iregistrywritable) -> {
                return iregistrywritable;
            });
        }

        @Override
        public <E> Optional<IRegistryWritable<E>> ownedWritableRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return Optional.ofNullable((IRegistryWritable) this.registries.get(resourcekey)).map((iregistrywritable) -> {
                return iregistrywritable;
            });
        }

        @Override
        public Stream<IRegistryCustom.d<?>> ownedRegistries() {
            return this.registries.entrySet().stream().map(IRegistryCustom.d::fromMapEntry);
        }
    }

    public interface e extends IRegistryCustom {

        <E> Optional<IRegistryWritable<E>> ownedWritableRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey);

        default <E> IRegistryWritable<E> ownedWritableRegistryOrThrow(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return (IRegistryWritable) this.ownedWritableRegistry(resourcekey).orElseThrow(() -> {
                return new IllegalStateException("Missing registry: " + resourcekey);
            });
        }
    }

    public static final class b implements IRegistryCustom.Dimension {

        private final Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends IRegistry<?>> registries;

        public b(Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends IRegistry<?>> map) {
            this.registries = Map.copyOf(map);
        }

        b(Stream<IRegistryCustom.d<?>> stream) {
            this.registries = (Map) stream.collect(ImmutableMap.toImmutableMap(IRegistryCustom.d::key, IRegistryCustom.d::value));
        }

        @Override
        public <E> Optional<IRegistry<E>> ownedRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return Optional.ofNullable((IRegistry) this.registries.get(resourcekey)).map((iregistry) -> {
                return iregistry;
            });
        }

        @Override
        public Stream<IRegistryCustom.d<?>> ownedRegistries() {
            return this.registries.entrySet().stream().map(IRegistryCustom.d::fromMapEntry);
        }
    }

    public static record d<T> (ResourceKey<? extends IRegistry<T>> key, IRegistry<T> value) {

        private static <T, R extends IRegistry<? extends T>> IRegistryCustom.d<T> fromMapEntry(Entry<? extends ResourceKey<? extends IRegistry<?>>, R> entry) {
            return fromUntyped((ResourceKey) entry.getKey(), (IRegistry) entry.getValue());
        }

        private static <T> IRegistryCustom.d<T> fromHolder(Holder.c<? extends IRegistry<? extends T>> holder_c) {
            return fromUntyped(holder_c.key(), (IRegistry) holder_c.value());
        }

        private static <T> IRegistryCustom.d<T> fromUntyped(ResourceKey<? extends IRegistry<?>> resourcekey, IRegistry<?> iregistry) {
            return new IRegistryCustom.d<>(resourcekey, iregistry);
        }

        private IRegistryCustom.d<T> freeze() {
            return new IRegistryCustom.d<>(this.key, this.value.freeze());
        }
    }

    public interface Dimension extends IRegistryCustom {

        @Override
        default IRegistryCustom.Dimension freeze() {
            return this;
        }
    }
}

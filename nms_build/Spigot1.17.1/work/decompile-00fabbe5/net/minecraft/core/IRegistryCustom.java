package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IRegistryCustom {

    private static final Logger LOGGER = LogManager.getLogger();
    static final Map<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> REGISTRIES = (Map) SystemUtils.a(() -> {
        Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder = ImmutableMap.builder();

        a(builder, IRegistry.DIMENSION_TYPE_REGISTRY, DimensionManager.DIRECT_CODEC, DimensionManager.DIRECT_CODEC);
        a(builder, IRegistry.BIOME_REGISTRY, BiomeBase.DIRECT_CODEC, BiomeBase.NETWORK_CODEC);
        a(builder, IRegistry.CONFIGURED_SURFACE_BUILDER_REGISTRY, WorldGenSurfaceComposite.DIRECT_CODEC);
        a(builder, IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
        a(builder, IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
        a(builder, IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
        a(builder, IRegistry.PROCESSOR_LIST_REGISTRY, DefinedStructureStructureProcessorType.DIRECT_CODEC);
        a(builder, IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeatureDefinedStructurePoolTemplate.DIRECT_CODEC);
        a(builder, IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase.DIRECT_CODEC);
        return builder.build();
    });
    private static final IRegistryCustom.Dimension BUILTIN = (IRegistryCustom.Dimension) SystemUtils.a(() -> {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();

        DimensionManager.a(iregistrycustom_dimension);
        IRegistryCustom.REGISTRIES.keySet().stream().filter((resourcekey) -> {
            return !resourcekey.equals(IRegistry.DIMENSION_TYPE_REGISTRY);
        }).forEach((resourcekey) -> {
            a(iregistrycustom_dimension, resourcekey);
        });
        return iregistrycustom_dimension;
    });

    public IRegistryCustom() {}

    public abstract <E> Optional<IRegistryWritable<E>> a(ResourceKey<? extends IRegistry<? extends E>> resourcekey);

    public <E> IRegistryWritable<E> b(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return (IRegistryWritable) this.a(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    public <E> Optional<? extends IRegistry<E>> c(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        Optional<? extends IRegistry<E>> optional = this.a(resourcekey);

        return optional.isPresent() ? optional : IRegistry.REGISTRY.getOptional(resourcekey.a());
    }

    public <E> IRegistry<E> d(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return (IRegistry) this.c(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    private static <E> void a(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        builder.put(resourcekey, new IRegistryCustom.a<>(resourcekey, codec, (Codec) null));
    }

    private static <E> void a(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, Codec<E> codec1) {
        builder.put(resourcekey, new IRegistryCustom.a<>(resourcekey, codec, codec1));
    }

    public static IRegistryCustom.Dimension a() {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();
        RegistryReadOps.b.a registryreadops_b_a = new RegistryReadOps.b.a();
        Iterator iterator = IRegistryCustom.REGISTRIES.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.a<?> iregistrycustom_a = (IRegistryCustom.a) iterator.next();

            a(iregistrycustom_dimension, registryreadops_b_a, iregistrycustom_a);
        }

        RegistryReadOps.a((DynamicOps) JsonOps.INSTANCE, (RegistryReadOps.b) registryreadops_b_a, (IRegistryCustom) iregistrycustom_dimension);
        return iregistrycustom_dimension;
    }

    private static <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, RegistryReadOps.b.a registryreadops_b_a, IRegistryCustom.a<E> iregistrycustom_a) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_a.a();
        boolean flag = !resourcekey.equals(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY) && !resourcekey.equals(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<E> iregistry = IRegistryCustom.BUILTIN.d(resourcekey);
        IRegistryWritable<E> iregistrywritable = iregistrycustom_dimension.b(resourcekey);
        Iterator iterator = iregistry.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            ResourceKey<E> resourcekey1 = (ResourceKey) entry.getKey();
            E e0 = entry.getValue();

            if (flag) {
                registryreadops_b_a.a(IRegistryCustom.BUILTIN, resourcekey1, iregistrycustom_a.b(), iregistry.getId(e0), e0, iregistry.d(e0));
            } else {
                iregistrywritable.a(iregistry.getId(e0), resourcekey1, e0, iregistry.d(e0));
            }
        }

    }

    private static <R extends IRegistry<?>> void a(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<R> resourcekey) {
        IRegistry<R> iregistry = RegistryGeneration.REGISTRY;
        IRegistry<?> iregistry1 = (IRegistry) iregistry.d(resourcekey);

        a(iregistrycustom_dimension, iregistry1);
    }

    private static <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, IRegistry<E> iregistry) {
        IRegistryWritable<E> iregistrywritable = iregistrycustom_dimension.b(iregistry.f());
        Iterator iterator = iregistry.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            E e0 = entry.getValue();

            iregistrywritable.a(iregistry.getId(e0), (ResourceKey) entry.getKey(), e0, iregistry.d(e0));
        }

    }

    public static void a(IRegistryCustom iregistrycustom, RegistryReadOps<?> registryreadops) {
        Iterator iterator = IRegistryCustom.REGISTRIES.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.a<?> iregistrycustom_a = (IRegistryCustom.a) iterator.next();

            a(registryreadops, iregistrycustom, iregistrycustom_a);
        }

    }

    private static <E> void a(RegistryReadOps<?> registryreadops, IRegistryCustom iregistrycustom, IRegistryCustom.a<E> iregistrycustom_a) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_a.a();
        RegistryMaterials<E> registrymaterials = (RegistryMaterials) iregistrycustom.b(resourcekey);
        DataResult<RegistryMaterials<E>> dataresult = registryreadops.a(registrymaterials, iregistrycustom_a.a(), iregistrycustom_a.b());

        dataresult.error().ifPresent((partialresult) -> {
            throw new JsonParseException("Error loading registry data: " + partialresult.message());
        });
    }

    private static final class a<E> {

        private final ResourceKey<? extends IRegistry<E>> key;
        private final Codec<E> codec;
        @Nullable
        private final Codec<E> networkCodec;

        public a(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, @Nullable Codec<E> codec1) {
            this.key = resourcekey;
            this.codec = codec;
            this.networkCodec = codec1;
        }

        public ResourceKey<? extends IRegistry<E>> a() {
            return this.key;
        }

        public Codec<E> b() {
            return this.codec;
        }

        @Nullable
        public Codec<E> c() {
            return this.networkCodec;
        }

        public boolean d() {
            return this.networkCodec != null;
        }
    }

    public static final class Dimension extends IRegistryCustom {

        public static final Codec<IRegistryCustom.Dimension> NETWORK_CODEC = b();
        private final Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> registries;

        private static <E> Codec<IRegistryCustom.Dimension> b() {
            Codec<ResourceKey<? extends IRegistry<E>>> codec = MinecraftKey.CODEC.xmap(ResourceKey::a, ResourceKey::a);
            Codec<RegistryMaterials<E>> codec1 = codec.partialDispatch("type", (registrymaterials) -> {
                return DataResult.success(registrymaterials.f());
            }, (resourcekey) -> {
                return e(resourcekey).map((codec2) -> {
                    return RegistryMaterials.a(resourcekey, Lifecycle.experimental(), codec2);
                });
            });
            UnboundedMapCodec<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);

            return a(unboundedmapcodec);
        }

        private static <K extends ResourceKey<? extends IRegistry<?>>, V extends RegistryMaterials<?>> Codec<IRegistryCustom.Dimension> a(UnboundedMapCodec<K, V> unboundedmapcodec) {
            return unboundedmapcodec.xmap(IRegistryCustom.Dimension::new, (iregistrycustom_dimension) -> {
                return (Map) iregistrycustom_dimension.registries.entrySet().stream().filter((entry) -> {
                    return ((IRegistryCustom.a) IRegistryCustom.REGISTRIES.get(entry.getKey())).d();
                }).collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
            });
        }

        private static <E> DataResult<? extends Codec<E>> e(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return (DataResult) Optional.ofNullable((IRegistryCustom.a) IRegistryCustom.REGISTRIES.get(resourcekey)).map((iregistrycustom_a) -> {
                return iregistrycustom_a.c();
            }).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown or not serializable registry: " + resourcekey);
            });
        }

        public Dimension() {
            this((Map) IRegistryCustom.REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), IRegistryCustom.Dimension::f)));
        }

        private Dimension(Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> map) {
            this.registries = map;
        }

        private static <E> RegistryMaterials<?> f(ResourceKey<? extends IRegistry<?>> resourcekey) {
            return new RegistryMaterials<>(resourcekey, Lifecycle.stable());
        }

        @Override
        public <E> Optional<IRegistryWritable<E>> a(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
            return Optional.ofNullable((RegistryMaterials) this.registries.get(resourcekey)).map((registrymaterials) -> {
                return registrymaterials;
            });
        }
    }
}

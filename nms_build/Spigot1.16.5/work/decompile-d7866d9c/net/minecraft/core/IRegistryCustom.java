package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
    private static final Map<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> b = (Map) SystemUtils.a(() -> {
        Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder = ImmutableMap.builder();

        a(builder, IRegistry.K, DimensionManager.d, DimensionManager.d);
        a(builder, IRegistry.ay, BiomeBase.b, BiomeBase.c);
        a(builder, IRegistry.as, WorldGenSurfaceComposite.a);
        a(builder, IRegistry.at, WorldGenCarverWrapper.a);
        a(builder, IRegistry.au, WorldGenFeatureConfigured.a);
        a(builder, IRegistry.av, StructureFeature.a);
        a(builder, IRegistry.aw, DefinedStructureStructureProcessorType.l);
        a(builder, IRegistry.ax, WorldGenFeatureDefinedStructurePoolTemplate.a);
        a(builder, IRegistry.ar, GeneratorSettingBase.a);
        return builder.build();
    });
    private static final IRegistryCustom.Dimension c = (IRegistryCustom.Dimension) SystemUtils.a(() -> {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();

        DimensionManager.a(iregistrycustom_dimension);
        IRegistryCustom.b.keySet().stream().filter((resourcekey) -> {
            return !resourcekey.equals(IRegistry.K);
        }).forEach((resourcekey) -> {
            a(iregistrycustom_dimension, resourcekey);
        });
        return iregistrycustom_dimension;
    });

    public IRegistryCustom() {}

    public abstract <E> Optional<IRegistryWritable<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey);

    public <E> IRegistryWritable<E> b(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (IRegistryWritable) this.a(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
    }

    public IRegistry<DimensionManager> a() {
        return this.b(IRegistry.K);
    }

    private static <E> void a(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        builder.put(resourcekey, new IRegistryCustom.a<>(resourcekey, codec, (Codec) null));
    }

    private static <E> void a(Builder<ResourceKey<? extends IRegistry<?>>, IRegistryCustom.a<?>> builder, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, Codec<E> codec1) {
        builder.put(resourcekey, new IRegistryCustom.a<>(resourcekey, codec, codec1));
    }

    public static IRegistryCustom.Dimension b() {
        IRegistryCustom.Dimension iregistrycustom_dimension = new IRegistryCustom.Dimension();
        RegistryReadOps.b.a registryreadops_b_a = new RegistryReadOps.b.a();
        Iterator iterator = IRegistryCustom.b.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.a<?> iregistrycustom_a = (IRegistryCustom.a) iterator.next();

            a(iregistrycustom_dimension, registryreadops_b_a, iregistrycustom_a);
        }

        RegistryReadOps.a((DynamicOps) JsonOps.INSTANCE, (RegistryReadOps.b) registryreadops_b_a, iregistrycustom_dimension);
        return iregistrycustom_dimension;
    }

    private static <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, RegistryReadOps.b.a registryreadops_b_a, IRegistryCustom.a<E> iregistrycustom_a) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_a.a();
        boolean flag = !resourcekey.equals(IRegistry.ar) && !resourcekey.equals(IRegistry.K);
        IRegistry<E> iregistry = IRegistryCustom.c.b(resourcekey);
        IRegistryWritable<E> iregistrywritable = iregistrycustom_dimension.b(resourcekey);
        Iterator iterator = iregistry.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            E e0 = entry.getValue();

            if (flag) {
                registryreadops_b_a.a(IRegistryCustom.c, (ResourceKey) entry.getKey(), iregistrycustom_a.b(), iregistry.a(e0), e0, iregistry.d(e0));
            } else {
                iregistrywritable.a(iregistry.a(e0), (ResourceKey) entry.getKey(), e0, iregistry.d(e0));
            }
        }

    }

    private static <R extends IRegistry<?>> void a(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<R> resourcekey) {
        IRegistry<R> iregistry = RegistryGeneration.b;
        IRegistry<?> iregistry1 = (IRegistry) iregistry.a(resourcekey);

        if (iregistry1 == null) {
            throw new IllegalStateException("Missing builtin registry: " + resourcekey);
        } else {
            a(iregistrycustom_dimension, iregistry1);
        }
    }

    private static <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, IRegistry<E> iregistry) {
        IRegistryWritable<E> iregistrywritable = (IRegistryWritable) iregistrycustom_dimension.a(iregistry.f()).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + iregistry.f());
        });
        Iterator iterator = iregistry.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<E>, E> entry = (Entry) iterator.next();
            E e0 = entry.getValue();

            iregistrywritable.a(iregistry.a(e0), (ResourceKey) entry.getKey(), e0, iregistry.d(e0));
        }

    }

    public static void a(IRegistryCustom.Dimension iregistrycustom_dimension, RegistryReadOps<?> registryreadops) {
        Iterator iterator = IRegistryCustom.b.values().iterator();

        while (iterator.hasNext()) {
            IRegistryCustom.a<?> iregistrycustom_a = (IRegistryCustom.a) iterator.next();

            a(registryreadops, iregistrycustom_dimension, iregistrycustom_a);
        }

    }

    private static <E> void a(RegistryReadOps<?> registryreadops, IRegistryCustom.Dimension iregistrycustom_dimension, IRegistryCustom.a<E> iregistrycustom_a) {
        ResourceKey<? extends IRegistry<E>> resourcekey = iregistrycustom_a.a();
        RegistryMaterials<E> registrymaterials = (RegistryMaterials) Optional.ofNullable(iregistrycustom_dimension.b.get(resourcekey)).map((registrymaterials1) -> {
            return registrymaterials1;
        }).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + resourcekey);
        });
        DataResult<RegistryMaterials<E>> dataresult = registryreadops.a(registrymaterials, iregistrycustom_a.a(), iregistrycustom_a.b());

        dataresult.error().ifPresent((partialresult) -> {
            IRegistryCustom.LOGGER.error("Error loading registry data: {}", partialresult.message());
        });
    }

    public static final class Dimension extends IRegistryCustom {

        public static final Codec<IRegistryCustom.Dimension> a = d();
        private final Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> b;

        private static <E> Codec<IRegistryCustom.Dimension> d() {
            Codec<ResourceKey<? extends IRegistry<E>>> codec = MinecraftKey.a.xmap(ResourceKey::a, ResourceKey::a);
            Codec<RegistryMaterials<E>> codec1 = codec.partialDispatch("type", (registrymaterials) -> {
                return DataResult.success(registrymaterials.f());
            }, (resourcekey) -> {
                return c(resourcekey).map((codec2) -> {
                    return RegistryMaterials.a(resourcekey, Lifecycle.experimental(), codec2);
                });
            });
            UnboundedMapCodec<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);

            return a(unboundedmapcodec);
        }

        private static <K extends ResourceKey<? extends IRegistry<?>>, V extends RegistryMaterials<?>> Codec<IRegistryCustom.Dimension> a(UnboundedMapCodec<K, V> unboundedmapcodec) {
            return unboundedmapcodec.xmap(IRegistryCustom.Dimension::new, (iregistrycustom_dimension) -> {
                return (ImmutableMap) iregistrycustom_dimension.b.entrySet().stream().filter((entry) -> {
                    return ((IRegistryCustom.a) IRegistryCustom.b.get(entry.getKey())).d();
                }).collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
            });
        }

        private static <E> DataResult<? extends Codec<E>> c(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return (DataResult) Optional.ofNullable(IRegistryCustom.b.get(resourcekey)).map((iregistrycustom_a) -> {
                return iregistrycustom_a.c();
            }).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown or not serializable registry: " + resourcekey);
            });
        }

        public Dimension() {
            this((Map) IRegistryCustom.b.keySet().stream().collect(Collectors.toMap(Function.identity(), IRegistryCustom.Dimension::d)));
        }

        private Dimension(Map<? extends ResourceKey<? extends IRegistry<?>>, ? extends RegistryMaterials<?>> map) {
            this.b = map;
        }

        private static <E> RegistryMaterials<?> d(ResourceKey<? extends IRegistry<?>> resourcekey) {
            return new RegistryMaterials<>(resourcekey, Lifecycle.stable());
        }

        @Override
        public <E> Optional<IRegistryWritable<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return Optional.ofNullable(this.b.get(resourcekey)).map((registrymaterials) -> {
                return registrymaterials;
            });
        }
    }

    static final class a<E> {

        private final ResourceKey<? extends IRegistry<E>> a;
        private final Codec<E> b;
        @Nullable
        private final Codec<E> c;

        public a(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, @Nullable Codec<E> codec1) {
            this.a = resourcekey;
            this.b = codec;
            this.c = codec1;
        }

        public ResourceKey<? extends IRegistry<E>> a() {
            return this.a;
        }

        public Codec<E> b() {
            return this.b;
        }

        @Nullable
        public Codec<E> c() {
            return this.c;
        }

        public boolean d() {
            return this.c != null;
        }
    }
}

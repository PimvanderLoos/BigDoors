package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryGeneration {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    private static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(ResourceKey.a(new MinecraftKey("root")), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = RegistryGeneration.WRITABLE_REGISTRY;
    public static final IRegistry<WorldGenSurfaceComposite<?>> CONFIGURED_SURFACE_BUILDER = a(IRegistry.CONFIGURED_SURFACE_BUILDER_REGISTRY, () -> {
        return WorldGenSurfaceComposites.NOPE;
    });
    public static final IRegistry<WorldGenCarverWrapper<?>> CONFIGURED_CARVER = a(IRegistry.CONFIGURED_CARVER_REGISTRY, () -> {
        return WorldGenCarvers.CAVE;
    });
    public static final IRegistry<WorldGenFeatureConfigured<?, ?>> CONFIGURED_FEATURE = a(IRegistry.CONFIGURED_FEATURE_REGISTRY, () -> {
        return BiomeDecoratorGroups.OAK;
    });
    public static final IRegistry<StructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = a(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, () -> {
        return StructureFeatures.MINESHAFT;
    });
    public static final IRegistry<ProcessorList> PROCESSOR_LIST = a(IRegistry.PROCESSOR_LIST_REGISTRY, () -> {
        return ProcessorLists.ZOMBIE_PLAINS;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> TEMPLATE_POOL = a(IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeaturePieces::a);
    public static final IRegistry<BiomeBase> BIOME = a(IRegistry.BIOME_REGISTRY, () -> {
        return BiomeRegistry.PLAINS;
    });
    public static final IRegistry<GeneratorSettingBase> NOISE_GENERATOR_SETTINGS = a(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase::o);

    public RegistryGeneration() {}

    private static <T> IRegistry<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, Supplier<T> supplier) {
        return a(resourcekey, Lifecycle.stable(), supplier);
    }

    private static <T> IRegistry<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<T> supplier) {
        return a(resourcekey, new RegistryMaterials<>(resourcekey, lifecycle), supplier, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R a(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, Supplier<T> supplier, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.a();

        RegistryGeneration.LOADERS.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = RegistryGeneration.WRITABLE_REGISTRY;

        return (IRegistryWritable) iregistrywritable.a(resourcekey, (Object) r0, lifecycle);
    }

    public static <T> T a(IRegistry<? super T> iregistry, String s, T t0) {
        return a(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return ((IRegistryWritable) iregistry).a(ResourceKey.a(iregistry.f(), minecraftkey), t0, Lifecycle.stable());
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, int i, ResourceKey<V> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).a(i, resourcekey, t0, Lifecycle.stable());
    }

    public static void a() {}

    static {
        RegistryGeneration.LOADERS.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                RegistryGeneration.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        IRegistry.a(RegistryGeneration.WRITABLE_REGISTRY);
    }
}

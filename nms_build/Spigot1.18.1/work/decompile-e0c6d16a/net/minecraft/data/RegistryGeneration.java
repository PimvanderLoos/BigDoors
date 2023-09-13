package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryGeneration {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    private static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(ResourceKey.createRegistryKey(new MinecraftKey("root")), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = RegistryGeneration.WRITABLE_REGISTRY;
    public static final IRegistry<WorldGenCarverWrapper<?>> CONFIGURED_CARVER = registerSimple(IRegistry.CONFIGURED_CARVER_REGISTRY, () -> {
        return WorldGenCarvers.CAVE;
    });
    public static final IRegistry<WorldGenFeatureConfigured<?, ?>> CONFIGURED_FEATURE = registerSimple(IRegistry.CONFIGURED_FEATURE_REGISTRY, FeatureUtils::bootstrap);
    public static final IRegistry<StructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = registerSimple(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeatures::bootstrap);
    public static final IRegistry<PlacedFeature> PLACED_FEATURE = registerSimple(IRegistry.PLACED_FEATURE_REGISTRY, PlacementUtils::bootstrap);
    public static final IRegistry<ProcessorList> PROCESSOR_LIST = registerSimple(IRegistry.PROCESSOR_LIST_REGISTRY, () -> {
        return ProcessorLists.ZOMBIE_PLAINS;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> TEMPLATE_POOL = registerSimple(IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeaturePieces::bootstrap);
    public static final IRegistry<BiomeBase> BIOME = registerSimple(IRegistry.BIOME_REGISTRY, () -> {
        return BiomeRegistry.PLAINS;
    });
    public static final IRegistry<GeneratorSettingBase> NOISE_GENERATOR_SETTINGS = registerSimple(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase::bootstrap);
    public static final IRegistry<NoiseGeneratorNormal.a> NOISE = registerSimple(IRegistry.NOISE_REGISTRY, NoiseData::bootstrap);

    public RegistryGeneration() {}

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Supplier<T> supplier) {
        return registerSimple(resourcekey, Lifecycle.stable(), supplier);
    }

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<T> supplier) {
        return internalRegister(resourcekey, new RegistryMaterials<>(resourcekey, lifecycle), supplier, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R internalRegister(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, Supplier<T> supplier, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.location();

        RegistryGeneration.LOADERS.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = RegistryGeneration.WRITABLE_REGISTRY;

        return (IRegistryWritable) iregistrywritable.register(resourcekey, (Object) r0, lifecycle);
    }

    public static <T> T register(IRegistry<? super T> iregistry, String s, T t0) {
        return register(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T register(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return register(iregistry, ResourceKey.create(iregistry.key(), minecraftkey), t0);
    }

    public static <V, T extends V> T register(IRegistry<V> iregistry, ResourceKey<V> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
    }

    public static <V, T extends V> T registerMapping(IRegistry<V> iregistry, ResourceKey<V> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
    }

    public static void bootstrap() {}

    static {
        RegistryGeneration.LOADERS.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                RegistryGeneration.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        IRegistry.checkRegistry(RegistryGeneration.WRITABLE_REGISTRY);
    }
}

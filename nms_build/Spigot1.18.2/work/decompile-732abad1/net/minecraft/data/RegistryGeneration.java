package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.data.worldgen.biome.BiomeRegistry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.slf4j.Logger;

public class RegistryGeneration {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MinecraftKey, Supplier<? extends Holder<?>>> LOADERS = Maps.newLinkedHashMap();
    private static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(ResourceKey.createRegistryKey(new MinecraftKey("root")), Lifecycle.experimental(), (Function) null);
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = RegistryGeneration.WRITABLE_REGISTRY;
    public static final IRegistry<WorldGenCarverWrapper<?>> CONFIGURED_CARVER = registerSimple(IRegistry.CONFIGURED_CARVER_REGISTRY, () -> {
        return WorldGenCarvers.CAVE;
    });
    public static final IRegistry<WorldGenFeatureConfigured<?, ?>> CONFIGURED_FEATURE = registerSimple(IRegistry.CONFIGURED_FEATURE_REGISTRY, FeatureUtils::bootstrap);
    public static final IRegistry<PlacedFeature> PLACED_FEATURE = registerSimple(IRegistry.PLACED_FEATURE_REGISTRY, PlacementUtils::bootstrap);
    public static final IRegistry<StructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = registerSimple(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeatures::bootstrap);
    public static final IRegistry<StructureSet> STRUCTURE_SETS = registerSimple(IRegistry.STRUCTURE_SET_REGISTRY, StructureSets::bootstrap);
    public static final IRegistry<ProcessorList> PROCESSOR_LIST = registerSimple(IRegistry.PROCESSOR_LIST_REGISTRY, () -> {
        return ProcessorLists.ZOMBIE_PLAINS;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> TEMPLATE_POOL = registerSimple(IRegistry.TEMPLATE_POOL_REGISTRY, WorldGenFeaturePieces::bootstrap);
    public static final IRegistry<BiomeBase> BIOME = registerSimple(IRegistry.BIOME_REGISTRY, BiomeRegistry::bootstrap);
    public static final IRegistry<NoiseGeneratorNormal.a> NOISE = registerSimple(IRegistry.NOISE_REGISTRY, NoiseData::bootstrap);
    public static final IRegistry<DensityFunction> DENSITY_FUNCTION = registerSimple(IRegistry.DENSITY_FUNCTION_REGISTRY, NoiseRouterData::bootstrap);
    public static final IRegistry<GeneratorSettingBase> NOISE_GENERATOR_SETTINGS = registerSimple(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, GeneratorSettingBase::bootstrap);
    public static final IRegistryCustom ACCESS;

    public RegistryGeneration() {}

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Supplier<? extends Holder<? extends T>> supplier) {
        return registerSimple(resourcekey, Lifecycle.stable(), supplier);
    }

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<? extends Holder<? extends T>> supplier) {
        return internalRegister(resourcekey, new RegistryMaterials<>(resourcekey, lifecycle, (Function) null), supplier, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R internalRegister(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, Supplier<? extends Holder<? extends T>> supplier, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.location();

        RegistryGeneration.LOADERS.put(minecraftkey, supplier);
        RegistryGeneration.WRITABLE_REGISTRY.register(resourcekey, (Object) r0, lifecycle);
        return r0;
    }

    public static <V extends T, T> Holder<V> registerExact(IRegistry<T> iregistry, String s, V v0) {
        Holder<T> holder = register(iregistry, new MinecraftKey(s), v0);

        return holder;
    }

    public static <T> Holder<T> register(IRegistry<T> iregistry, String s, T t0) {
        return register(iregistry, new MinecraftKey(s), t0);
    }

    public static <T> Holder<T> register(IRegistry<T> iregistry, MinecraftKey minecraftkey, T t0) {
        return register(iregistry, ResourceKey.create(iregistry.key(), minecraftkey), t0);
    }

    public static <T> Holder<T> register(IRegistry<T> iregistry, ResourceKey<T> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
    }

    public static void bootstrap() {}

    static {
        RegistryGeneration.LOADERS.forEach((minecraftkey, supplier) -> {
            if (!((Holder) supplier.get()).isBound()) {
                RegistryGeneration.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        IRegistry.checkRegistry(RegistryGeneration.WRITABLE_REGISTRY);
        ACCESS = IRegistryCustom.fromRegistryOfRegistries(RegistryGeneration.REGISTRY);
    }
}

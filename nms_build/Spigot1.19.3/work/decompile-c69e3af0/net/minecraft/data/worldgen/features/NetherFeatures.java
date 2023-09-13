package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDeltaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRadiusConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.material.FluidTypes;

public class NetherFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DELTA = FeatureUtils.createKey("delta");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SMALL_BASALT_COLUMNS = FeatureUtils.createKey("small_basalt_columns");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> LARGE_BASALT_COLUMNS = FeatureUtils.createKey("large_basalt_columns");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BASALT_BLOBS = FeatureUtils.createKey("basalt_blobs");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BLACKSTONE_BLOBS = FeatureUtils.createKey("blackstone_blobs");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> GLOWSTONE_EXTRA = FeatureUtils.createKey("glowstone_extra");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> CRIMSON_FOREST_VEGETATION = FeatureUtils.createKey("crimson_forest_vegetation");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> CRIMSON_FOREST_VEGETATION_BONEMEAL = FeatureUtils.createKey("crimson_forest_vegetation_bonemeal");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WARPED_FOREST_VEGETION = FeatureUtils.createKey("warped_forest_vegetation");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WARPED_FOREST_VEGETATION_BONEMEAL = FeatureUtils.createKey("warped_forest_vegetation_bonemeal");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> NETHER_SPROUTS = FeatureUtils.createKey("nether_sprouts");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> NETHER_SPROUTS_BONEMEAL = FeatureUtils.createKey("nether_sprouts_bonemeal");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TWISTING_VINES = FeatureUtils.createKey("twisting_vines");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TWISTING_VINES_BONEMEAL = FeatureUtils.createKey("twisting_vines_bonemeal");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WEEPING_VINES = FeatureUtils.createKey("weeping_vines");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_CRIMSON_ROOTS = FeatureUtils.createKey("patch_crimson_roots");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BASALT_PILLAR = FeatureUtils.createKey("basalt_pillar");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_LAVA_NETHER = FeatureUtils.createKey("spring_lava_nether");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_NETHER_CLOSED = FeatureUtils.createKey("spring_nether_closed");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_NETHER_OPEN = FeatureUtils.createKey("spring_nether_open");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_FIRE = FeatureUtils.createKey("patch_fire");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_SOUL_FIRE = FeatureUtils.createKey("patch_soul_fire");

    public NetherFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        FeatureUtils.register(bootstapcontext, NetherFeatures.DELTA, WorldGenerator.DELTA_FEATURE, new WorldGenFeatureDeltaConfiguration(Blocks.LAVA.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.SMALL_BASALT_COLUMNS, WorldGenerator.BASALT_COLUMNS, new WorldGenFeatureBasaltColumnsConfiguration(ConstantInt.of(1), UniformInt.of(1, 4)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.LARGE_BASALT_COLUMNS, WorldGenerator.BASALT_COLUMNS, new WorldGenFeatureBasaltColumnsConfiguration(UniformInt.of(2, 3), UniformInt.of(5, 10)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.BASALT_BLOBS, WorldGenerator.REPLACE_BLOBS, new WorldGenFeatureRadiusConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BASALT.defaultBlockState(), UniformInt.of(3, 7)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.BLACKSTONE_BLOBS, WorldGenerator.REPLACE_BLOBS, new WorldGenFeatureRadiusConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.of(3, 7)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.GLOWSTONE_EXTRA, WorldGenerator.GLOWSTONE_BLOB);
        WorldGenFeatureStateProviderWeighted worldgenfeaturestateproviderweighted = new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 87).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 11).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1));

        FeatureUtils.register(bootstapcontext, NetherFeatures.CRIMSON_FOREST_VEGETATION, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(worldgenfeaturestateproviderweighted, 8, 4));
        FeatureUtils.register(bootstapcontext, NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(worldgenfeaturestateproviderweighted, 3, 1));
        WorldGenFeatureStateProviderWeighted worldgenfeaturestateproviderweighted1 = new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.WARPED_ROOTS.defaultBlockState(), 85).add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 1).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 13).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1));

        FeatureUtils.register(bootstapcontext, NetherFeatures.WARPED_FOREST_VEGETION, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(worldgenfeaturestateproviderweighted1, 8, 4));
        FeatureUtils.register(bootstapcontext, NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(worldgenfeaturestateproviderweighted1, 3, 1));
        FeatureUtils.register(bootstapcontext, NetherFeatures.NETHER_SPROUTS, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(WorldGenFeatureStateProvider.simple(Blocks.NETHER_SPROUTS), 8, 4));
        FeatureUtils.register(bootstapcontext, NetherFeatures.NETHER_SPROUTS_BONEMEAL, WorldGenerator.NETHER_FOREST_VEGETATION, new NetherForestVegetationConfig(WorldGenFeatureStateProvider.simple(Blocks.NETHER_SPROUTS), 3, 1));
        FeatureUtils.register(bootstapcontext, NetherFeatures.TWISTING_VINES, WorldGenerator.TWISTING_VINES, new TwistingVinesConfig(8, 4, 8));
        FeatureUtils.register(bootstapcontext, NetherFeatures.TWISTING_VINES_BONEMEAL, WorldGenerator.TWISTING_VINES, new TwistingVinesConfig(3, 1, 2));
        FeatureUtils.register(bootstapcontext, NetherFeatures.WEEPING_VINES, WorldGenerator.WEEPING_VINES);
        FeatureUtils.register(bootstapcontext, NetherFeatures.PATCH_CRIMSON_ROOTS, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.CRIMSON_ROOTS))));
        FeatureUtils.register(bootstapcontext, NetherFeatures.BASALT_PILLAR, WorldGenerator.BASALT_PILLAR);
        FeatureUtils.register(bootstapcontext, NetherFeatures.SPRING_LAVA_NETHER, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE))));
        FeatureUtils.register(bootstapcontext, NetherFeatures.SPRING_NETHER_CLOSED, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), false, 5, 0, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.NETHERRACK))));
        FeatureUtils.register(bootstapcontext, NetherFeatures.SPRING_NETHER_OPEN, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), false, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.NETHERRACK))));
        FeatureUtils.register(bootstapcontext, NetherFeatures.PATCH_FIRE, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.FIRE)), List.of(Blocks.NETHERRACK)));
        FeatureUtils.register(bootstapcontext, NetherFeatures.PATCH_SOUL_FIRE, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SOUL_FIRE)), List.of(Blocks.SOUL_SOIL)));
    }
}

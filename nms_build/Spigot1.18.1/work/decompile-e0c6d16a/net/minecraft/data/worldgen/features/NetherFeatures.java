package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDeltaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRadiusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.material.FluidTypes;

public class NetherFeatures {

    public static final WorldGenFeatureConfigured<WorldGenFeatureDeltaConfiguration, ?> DELTA = FeatureUtils.register("delta", WorldGenerator.DELTA_FEATURE.configured(new WorldGenFeatureDeltaConfiguration(Blocks.LAVA.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureBasaltColumnsConfiguration, ?> SMALL_BASALT_COLUMNS = FeatureUtils.register("small_basalt_columns", WorldGenerator.BASALT_COLUMNS.configured(new WorldGenFeatureBasaltColumnsConfiguration(ConstantInt.of(1), UniformInt.of(1, 4))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureBasaltColumnsConfiguration, ?> LARGE_BASALT_COLUMNS = FeatureUtils.register("large_basalt_columns", WorldGenerator.BASALT_COLUMNS.configured(new WorldGenFeatureBasaltColumnsConfiguration(UniformInt.of(2, 3), UniformInt.of(5, 10))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRadiusConfiguration, ?> BASALT_BLOBS = FeatureUtils.register("basalt_blobs", WorldGenerator.REPLACE_BLOBS.configured(new WorldGenFeatureRadiusConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BASALT.defaultBlockState(), UniformInt.of(3, 7))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRadiusConfiguration, ?> BLACKSTONE_BLOBS = FeatureUtils.register("blackstone_blobs", WorldGenerator.REPLACE_BLOBS.configured(new WorldGenFeatureRadiusConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.of(3, 7))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> GLOWSTONE_EXTRA = FeatureUtils.register("glowstone_extra", WorldGenerator.GLOWSTONE_BLOB.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureStateProviderWeighted CRIMSON_VEGETATION_PROVIDER = new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 87).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 11).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1));
    public static final WorldGenFeatureConfigured<?, ?> CRIMSON_FOREST_VEGETATION = FeatureUtils.register("crimson_forest_vegetation", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(NetherFeatures.CRIMSON_VEGETATION_PROVIDER, 8, 4)));
    public static final WorldGenFeatureConfigured<?, ?> CRIMSON_FOREST_VEGETATION_BONEMEAL = FeatureUtils.register("crimson_forest_vegetation_bonemeal", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(NetherFeatures.CRIMSON_VEGETATION_PROVIDER, 3, 1)));
    public static final WorldGenFeatureStateProviderWeighted WARPED_VEGETATION_PROVIDER = new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.WARPED_ROOTS.defaultBlockState(), 85).add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 1).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 13).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1));
    public static final WorldGenFeatureConfigured<?, ?> WARPED_FOREST_VEGETION = FeatureUtils.register("warped_forest_vegetation", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(NetherFeatures.WARPED_VEGETATION_PROVIDER, 8, 4)));
    public static final WorldGenFeatureConfigured<?, ?> WARPED_FOREST_VEGETATION_BONEMEAL = FeatureUtils.register("warped_forest_vegetation_bonemeal", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(NetherFeatures.WARPED_VEGETATION_PROVIDER, 3, 1)));
    public static final WorldGenFeatureConfigured<?, ?> NETHER_SPROUTS = FeatureUtils.register("nether_sprouts", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(WorldGenFeatureStateProvider.simple(Blocks.NETHER_SPROUTS), 8, 4)));
    public static final WorldGenFeatureConfigured<?, ?> NETHER_SPROUTS_BONEMEAL = FeatureUtils.register("nether_sprouts_bonemeal", WorldGenerator.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(WorldGenFeatureStateProvider.simple(Blocks.NETHER_SPROUTS), 3, 1)));
    public static final WorldGenFeatureConfigured<?, ?> TWISTING_VINES = FeatureUtils.register("twisting_vines", WorldGenerator.TWISTING_VINES.configured(new TwistingVinesConfig(8, 4, 8)));
    public static final WorldGenFeatureConfigured<?, ?> TWISTING_VINES_BONEMEAL = FeatureUtils.register("twisting_vines_bonemeal", WorldGenerator.TWISTING_VINES.configured(new TwistingVinesConfig(3, 1, 2)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> WEEPING_VINES = FeatureUtils.register("weeping_vines", WorldGenerator.WEEPING_VINES.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_CRIMSON_ROOTS = FeatureUtils.register("patch_crimson_roots", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.CRIMSON_ROOTS))))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> BASALT_PILLAR = FeatureUtils.register("basalt_pillar", WorldGenerator.BASALT_PILLAR.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_LAVA_NETHER = FeatureUtils.register("spring_lava_nether", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_NETHER_CLOSED = FeatureUtils.register("spring_nether_closed", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_NETHER_OPEN = FeatureUtils.register("spring_nether_open", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_FIRE = FeatureUtils.register("patch_fire", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.FIRE))), List.of(Blocks.NETHERRACK))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_SOUL_FIRE = FeatureUtils.register("patch_soul_fire", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SOUL_FIRE))), List.of(Blocks.SOUL_SOIL))));

    public NetherFeatures() {}
}

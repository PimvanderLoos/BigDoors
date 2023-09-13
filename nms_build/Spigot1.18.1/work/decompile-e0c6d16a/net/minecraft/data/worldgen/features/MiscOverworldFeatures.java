package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenLakes;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.material.FluidTypes;

public class MiscOverworldFeatures {

    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> ICE_SPIKE = FeatureUtils.register("ice_spike", WorldGenerator.ICE_SPIKE.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?> ICE_PATCH = FeatureUtils.register("ice_patch", WorldGenerator.ICE_PATCH.configured(new WorldGenFeatureCircleConfiguration(Blocks.PACKED_ICE.defaultBlockState(), UniformInt.of(2, 3), 1, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.PODZOL.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState(), Blocks.MYCELIUM.defaultBlockState(), Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.ICE.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?> FOREST_ROCK = FeatureUtils.register("forest_rock", WorldGenerator.FOREST_ROCK.configured(new WorldGenFeatureLakeConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState())));
    public static final WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?> ICEBERG_PACKED = FeatureUtils.register("iceberg_packed", WorldGenerator.ICEBERG.configured(new WorldGenFeatureLakeConfiguration(Blocks.PACKED_ICE.defaultBlockState())));
    public static final WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?> ICEBERG_BLUE = FeatureUtils.register("iceberg_blue", WorldGenerator.ICEBERG.configured(new WorldGenFeatureLakeConfiguration(Blocks.BLUE_ICE.defaultBlockState())));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> BLUE_ICE = FeatureUtils.register("blue_ice", WorldGenerator.BLUE_ICE.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenLakes.a, ?> LAKE_LAVA = FeatureUtils.register("lake_lava", WorldGenerator.LAKE.configured(new WorldGenLakes.a(WorldGenFeatureStateProvider.simple(Blocks.LAVA.defaultBlockState()), WorldGenFeatureStateProvider.simple(Blocks.STONE.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?> DISK_CLAY = FeatureUtils.register("disk_clay", WorldGenerator.DISK.configured(new WorldGenFeatureCircleConfiguration(Blocks.CLAY.defaultBlockState(), UniformInt.of(2, 3), 1, List.of(Blocks.DIRT.defaultBlockState(), Blocks.CLAY.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?> DISK_GRAVEL = FeatureUtils.register("disk_gravel", WorldGenerator.DISK.configured(new WorldGenFeatureCircleConfiguration(Blocks.GRAVEL.defaultBlockState(), UniformInt.of(2, 5), 2, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?> DISK_SAND = FeatureUtils.register("disk_sand", WorldGenerator.DISK.configured(new WorldGenFeatureCircleConfiguration(Blocks.SAND.defaultBlockState(), UniformInt.of(2, 6), 2, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<?, ?> FREEZE_TOP_LAYER = FeatureUtils.register("freeze_top_layer", WorldGenerator.FREEZE_TOP_LAYER.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<?, ?> BONUS_CHEST = FeatureUtils.register("bonus_chest", WorldGenerator.BONUS_CHEST.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<?, ?> VOID_START_PLATFORM = FeatureUtils.register("void_start_platform", WorldGenerator.VOID_START_PLATFORM.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> DESERT_WELL = FeatureUtils.register("desert_well", WorldGenerator.DESERT_WELL.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_LAVA_OVERWORLD = FeatureUtils.register("spring_lava_overworld", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, new Block[]{Blocks.CALCITE, Blocks.DIRT}))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_LAVA_FROZEN = FeatureUtils.register("spring_lava_frozen", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?> SPRING_WATER = FeatureUtils.register("spring_water", WorldGenerator.SPRING.configured(new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.WATER.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, new Block[]{Blocks.CALCITE, Blocks.DIRT, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE}))));

    public MiscOverworldFeatures() {}
}

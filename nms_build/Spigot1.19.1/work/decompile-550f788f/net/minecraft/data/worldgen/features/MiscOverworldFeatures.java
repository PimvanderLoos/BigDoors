package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenLakes;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.material.FluidTypes;

public class MiscOverworldFeatures {

    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> ICE_SPIKE = FeatureUtils.register("ice_spike", WorldGenerator.ICE_SPIKE);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?>> ICE_PATCH = FeatureUtils.register("ice_patch", WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.PACKED_ICE), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.ICE)), UniformInt.of(2, 3), 1));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?>> FOREST_ROCK = FeatureUtils.register("forest_rock", WorldGenerator.FOREST_ROCK, new WorldGenFeatureLakeConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState()));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?>> ICEBERG_PACKED = FeatureUtils.register("iceberg_packed", WorldGenerator.ICEBERG, new WorldGenFeatureLakeConfiguration(Blocks.PACKED_ICE.defaultBlockState()));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureLakeConfiguration, ?>> ICEBERG_BLUE = FeatureUtils.register("iceberg_blue", WorldGenerator.ICEBERG, new WorldGenFeatureLakeConfiguration(Blocks.BLUE_ICE.defaultBlockState()));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> BLUE_ICE = FeatureUtils.register("blue_ice", WorldGenerator.BLUE_ICE);
    public static final Holder<WorldGenFeatureConfigured<WorldGenLakes.a, ?>> LAKE_LAVA = FeatureUtils.register("lake_lava", WorldGenerator.LAKE, new WorldGenLakes.a(WorldGenFeatureStateProvider.simple(Blocks.LAVA.defaultBlockState()), WorldGenFeatureStateProvider.simple(Blocks.STONE.defaultBlockState())));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?>> DISK_CLAY = FeatureUtils.register("disk_clay", WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?>> DISK_GRAVEL = FeatureUtils.register("disk_gravel", WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?>> DISK_SAND = FeatureUtils.register("disk_sand", WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(new RuleBasedBlockStateProvider(WorldGenFeatureStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.a(BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.AIR), WorldGenFeatureStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> FREEZE_TOP_LAYER = FeatureUtils.register("freeze_top_layer", WorldGenerator.FREEZE_TOP_LAYER);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureCircleConfiguration, ?>> DISK_GRASS = FeatureUtils.register("disk_grass", WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(new RuleBasedBlockStateProvider(WorldGenFeatureStateProvider.simple(Blocks.DIRT), List.of(new RuleBasedBlockStateProvider.a(BlockPredicate.not(BlockPredicate.anyOf(BlockPredicate.solid(EnumDirection.UP.getNormal()), BlockPredicate.matchesFluids(EnumDirection.UP.getNormal(), FluidTypes.WATER))), WorldGenFeatureStateProvider.simple(Blocks.GRASS_BLOCK)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.MUD)), UniformInt.of(2, 6), 2));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> BONUS_CHEST = FeatureUtils.register("bonus_chest", WorldGenerator.BONUS_CHEST);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> VOID_START_PLATFORM = FeatureUtils.register("void_start_platform", WorldGenerator.VOID_START_PLATFORM);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> DESERT_WELL = FeatureUtils.register("desert_well", WorldGenerator.DESERT_WELL);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?>> SPRING_LAVA_OVERWORLD = FeatureUtils.register("spring_lava_overworld", WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT))));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?>> SPRING_LAVA_FROZEN = FeatureUtils.register("spring_lava_frozen", WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHellFlowingLavaConfiguration, ?>> SPRING_WATER = FeatureUtils.register("spring_water", WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.WATER.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));

    public MiscOverworldFeatures() {}
}

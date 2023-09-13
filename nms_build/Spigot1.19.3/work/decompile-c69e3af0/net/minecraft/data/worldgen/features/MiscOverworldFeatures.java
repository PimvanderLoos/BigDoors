package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenLakes;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.material.FluidTypes;

public class MiscOverworldFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ICE_SPIKE = FeatureUtils.createKey("ice_spike");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ICE_PATCH = FeatureUtils.createKey("ice_patch");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FOREST_ROCK = FeatureUtils.createKey("forest_rock");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ICEBERG_PACKED = FeatureUtils.createKey("iceberg_packed");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ICEBERG_BLUE = FeatureUtils.createKey("iceberg_blue");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BLUE_ICE = FeatureUtils.createKey("blue_ice");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> LAKE_LAVA = FeatureUtils.createKey("lake_lava");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DISK_CLAY = FeatureUtils.createKey("disk_clay");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DISK_GRAVEL = FeatureUtils.createKey("disk_gravel");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DISK_SAND = FeatureUtils.createKey("disk_sand");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FREEZE_TOP_LAYER = FeatureUtils.createKey("freeze_top_layer");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DISK_GRASS = FeatureUtils.createKey("disk_grass");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BONUS_CHEST = FeatureUtils.createKey("bonus_chest");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> VOID_START_PLATFORM = FeatureUtils.createKey("void_start_platform");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DESERT_WELL = FeatureUtils.createKey("desert_well");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_LAVA_OVERWORLD = FeatureUtils.createKey("spring_lava_overworld");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_LAVA_FROZEN = FeatureUtils.createKey("spring_lava_frozen");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRING_WATER = FeatureUtils.createKey("spring_water");

    public MiscOverworldFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.ICE_SPIKE, WorldGenerator.ICE_SPIKE);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.ICE_PATCH, WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.PACKED_ICE), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.ICE)), UniformInt.of(2, 3), 1));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.FOREST_ROCK, WorldGenerator.FOREST_ROCK, new WorldGenFeatureLakeConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState()));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.ICEBERG_PACKED, WorldGenerator.ICEBERG, new WorldGenFeatureLakeConfiguration(Blocks.PACKED_ICE.defaultBlockState()));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.ICEBERG_BLUE, WorldGenerator.ICEBERG, new WorldGenFeatureLakeConfiguration(Blocks.BLUE_ICE.defaultBlockState()));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.BLUE_ICE, WorldGenerator.BLUE_ICE);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.LAKE_LAVA, WorldGenerator.LAKE, new WorldGenLakes.a(WorldGenFeatureStateProvider.simple(Blocks.LAVA.defaultBlockState()), WorldGenFeatureStateProvider.simple(Blocks.STONE.defaultBlockState())));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.DISK_CLAY, WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.DISK_GRAVEL, WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.DISK_SAND, WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(new RuleBasedBlockStateProvider(WorldGenFeatureStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.a(BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.AIR), WorldGenFeatureStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.FREEZE_TOP_LAYER, WorldGenerator.FREEZE_TOP_LAYER);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.DISK_GRASS, WorldGenerator.DISK, new WorldGenFeatureCircleConfiguration(new RuleBasedBlockStateProvider(WorldGenFeatureStateProvider.simple(Blocks.DIRT), List.of(new RuleBasedBlockStateProvider.a(BlockPredicate.not(BlockPredicate.anyOf(BlockPredicate.solid(EnumDirection.UP.getNormal()), BlockPredicate.matchesFluids(EnumDirection.UP.getNormal(), FluidTypes.WATER))), WorldGenFeatureStateProvider.simple(Blocks.GRASS_BLOCK)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.MUD)), UniformInt.of(2, 6), 2));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.BONUS_CHEST, WorldGenerator.BONUS_CHEST);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.VOID_START_PLATFORM, WorldGenerator.VOID_START_PLATFORM);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.DESERT_WELL, WorldGenerator.DESERT_WELL);
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT))));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.SPRING_LAVA_FROZEN, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));
        FeatureUtils.register(bootstapcontext, MiscOverworldFeatures.SPRING_WATER, WorldGenerator.SPRING, new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.WATER.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));
    }
}

package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.EnumDirection;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;

public class CaveFeatures {

    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> MONSTER_ROOM = FeatureUtils.register("monster_room", WorldGenerator.MONSTER_ROOM.configured(WorldGenFeatureConfiguration.NONE));
    private static final List<MinecraftKey> FOSSIL_STRUCTURES = List.of(new MinecraftKey("fossil/spine_1"), new MinecraftKey("fossil/spine_2"), new MinecraftKey("fossil/spine_3"), new MinecraftKey("fossil/spine_4"), new MinecraftKey("fossil/skull_1"), new MinecraftKey("fossil/skull_2"), new MinecraftKey("fossil/skull_3"), new MinecraftKey("fossil/skull_4"));
    private static final List<MinecraftKey> FOSSIL_COAL_STRUCTURES = List.of(new MinecraftKey("fossil/spine_1_coal"), new MinecraftKey("fossil/spine_2_coal"), new MinecraftKey("fossil/spine_3_coal"), new MinecraftKey("fossil/spine_4_coal"), new MinecraftKey("fossil/skull_1_coal"), new MinecraftKey("fossil/skull_2_coal"), new MinecraftKey("fossil/skull_3_coal"), new MinecraftKey("fossil/skull_4_coal"));
    public static final WorldGenFeatureConfigured<FossilFeatureConfiguration, ?> FOSSIL_COAL = FeatureUtils.register("fossil_coal", WorldGenerator.FOSSIL.configured(new FossilFeatureConfiguration(CaveFeatures.FOSSIL_STRUCTURES, CaveFeatures.FOSSIL_COAL_STRUCTURES, ProcessorLists.FOSSIL_ROT, ProcessorLists.FOSSIL_COAL, 4)));
    public static final WorldGenFeatureConfigured<FossilFeatureConfiguration, ?> FOSSIL_DIAMONDS = FeatureUtils.register("fossil_diamonds", WorldGenerator.FOSSIL.configured(new FossilFeatureConfiguration(CaveFeatures.FOSSIL_STRUCTURES, CaveFeatures.FOSSIL_COAL_STRUCTURES, ProcessorLists.FOSSIL_ROT, ProcessorLists.FOSSIL_DIAMONDS, 4)));
    public static final WorldGenFeatureConfigured<DripstoneClusterConfiguration, ?> DRIPSTONE_CLUSTER = FeatureUtils.register("dripstone_cluster", WorldGenerator.DRIPSTONE_CLUSTER.configured(new DripstoneClusterConfiguration(12, UniformInt.of(3, 6), UniformInt.of(2, 8), 1, 3, UniformInt.of(2, 4), UniformFloat.of(0.3F, 0.7F), ClampedNormalFloat.of(0.1F, 0.3F, 0.1F, 0.9F), 0.1F, 3, 8)));
    public static final WorldGenFeatureConfigured<LargeDripstoneConfiguration, ?> LARGE_DRIPSTONE = FeatureUtils.register("large_dripstone", WorldGenerator.LARGE_DRIPSTONE.configured(new LargeDripstoneConfiguration(30, UniformInt.of(3, 19), UniformFloat.of(0.4F, 2.0F), 0.33F, UniformFloat.of(0.3F, 0.9F), UniformFloat.of(0.4F, 1.0F), UniformFloat.of(0.0F, 0.3F), 4, 0.6F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandom2, ?> POINTED_DRIPSTONE = FeatureUtils.register("pointed_dripstone", WorldGenerator.SIMPLE_RANDOM_SELECTOR.configured(new WorldGenFeatureRandom2(ImmutableList.of(() -> {
        return WorldGenerator.POINTED_DRIPSTONE.configured(new PointedDripstoneConfiguration(0.2F, 0.7F, 0.5F, 0.5F)).placed(EnvironmentScanPlacement.scanningFor(EnumDirection.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)));
    }, () -> {
        return WorldGenerator.POINTED_DRIPSTONE.configured(new PointedDripstoneConfiguration(0.2F, 0.7F, 0.5F, 0.5F)).placed(EnvironmentScanPlacement.scanningFor(EnumDirection.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)));
    }))));
    public static final WorldGenFeatureConfigured<UnderwaterMagmaConfiguration, ?> UNDERWATER_MAGMA = FeatureUtils.register("underwater_magma", WorldGenerator.UNDERWATER_MAGMA.configured(new UnderwaterMagmaConfiguration(5, 1, 0.5F)));
    public static final WorldGenFeatureConfigured<GlowLichenConfiguration, ?> GLOW_LICHEN = FeatureUtils.register("glow_lichen", WorldGenerator.GLOW_LICHEN.configured(new GlowLichenConfiguration(20, false, true, true, 0.5F, List.of(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE))));
    public static final WorldGenFeatureConfigured<RootSystemConfiguration, ?> ROOTED_AZALEA_TREE = FeatureUtils.register("rooted_azalea_tree", WorldGenerator.ROOT_SYSTEM.configured(new RootSystemConfiguration(() -> {
        return TreeFeatures.AZALEA_TREE.placed();
    }, 3, 3, TagsBlock.AZALEA_ROOT_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.ROOTED_DIRT), 20, 100, 3, 2, WorldGenFeatureStateProvider.simple(Blocks.HANGING_ROOTS), 20, 2, BlockPredicate.allOf(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.WATER)), BlockPredicate.matchesTag(TagsBlock.LEAVES), BlockPredicate.matchesTag(TagsBlock.REPLACEABLE_PLANTS)), BlockPredicate.matchesTag(TagsBlock.AZALEA_GROWS_ON, EnumDirection.DOWN.getNormal())))));
    private static final WorldGenFeatureStateProviderWeighted CAVE_VINES_BODY_PROVIDER = new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.CAVE_VINES_PLANT.defaultBlockState(), 4).add((IBlockData) Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, true), 1));
    private static final RandomizedIntStateProvider CAVE_VINES_HEAD_PROVIDER = new RandomizedIntStateProvider(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.CAVE_VINES.defaultBlockState(), 4).add((IBlockData) Blocks.CAVE_VINES.defaultBlockState().setValue(CaveVines.BERRIES, true), 1)), CaveVinesBlock.AGE, UniformInt.of(23, 25));
    public static final WorldGenFeatureConfigured<BlockColumnConfiguration, ?> CAVE_VINE = FeatureUtils.register("cave_vine", WorldGenerator.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.of(0, 19), 2).add(UniformInt.of(0, 2), 3).add(UniformInt.of(0, 6), 10).build()), CaveFeatures.CAVE_VINES_BODY_PROVIDER), BlockColumnConfiguration.layer(ConstantInt.of(1), CaveFeatures.CAVE_VINES_HEAD_PROVIDER)), EnumDirection.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true)));
    public static final WorldGenFeatureConfigured<BlockColumnConfiguration, ?> CAVE_VINE_IN_MOSS = FeatureUtils.register("cave_vine_in_moss", WorldGenerator.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.of(0, 3), 5).add(UniformInt.of(1, 7), 1).build()), CaveFeatures.CAVE_VINES_BODY_PROVIDER), BlockColumnConfiguration.layer(ConstantInt.of(1), CaveFeatures.CAVE_VINES_HEAD_PROVIDER)), EnumDirection.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureBlockConfiguration, ?> MOSS_VEGETATION = FeatureUtils.register("moss_vegetation", WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.FLOWERING_AZALEA.defaultBlockState(), 4).add(Blocks.AZALEA.defaultBlockState(), 7).add(Blocks.MOSS_CARPET.defaultBlockState(), 25).add(Blocks.GRASS.defaultBlockState(), 50).add(Blocks.TALL_GRASS.defaultBlockState(), 10)))));
    public static final WorldGenFeatureConfigured<VegetationPatchConfiguration, ?> MOSS_PATCH = FeatureUtils.register("moss_patch", WorldGenerator.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(TagsBlock.MOSS_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
        return CaveFeatures.MOSS_VEGETATION.placed();
    }, CaveSurface.FLOOR, ConstantInt.of(1), 0.0F, 5, 0.8F, UniformInt.of(4, 7), 0.3F)));
    public static final WorldGenFeatureConfigured<VegetationPatchConfiguration, ?> MOSS_PATCH_BONEMEAL = FeatureUtils.register("moss_patch_bonemeal", WorldGenerator.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(TagsBlock.MOSS_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
        return CaveFeatures.MOSS_VEGETATION.placed();
    }, CaveSurface.FLOOR, ConstantInt.of(1), 0.0F, 5, 0.6F, UniformInt.of(1, 2), 0.75F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandom2, ?> DRIPLEAF = FeatureUtils.register("dripleaf", WorldGenerator.SIMPLE_RANDOM_SELECTOR.configured(new WorldGenFeatureRandom2(List.of(CaveFeatures::makeSmallDripleaf, () -> {
        return makeDripleaf(EnumDirection.EAST);
    }, () -> {
        return makeDripleaf(EnumDirection.WEST);
    }, () -> {
        return makeDripleaf(EnumDirection.SOUTH);
    }, () -> {
        return makeDripleaf(EnumDirection.NORTH);
    }))));
    public static final WorldGenFeatureConfigured<?, ?> CLAY_WITH_DRIPLEAVES = FeatureUtils.register("clay_with_dripleaves", WorldGenerator.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(TagsBlock.LUSH_GROUND_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.CLAY), () -> {
        return CaveFeatures.DRIPLEAF.placed();
    }, CaveSurface.FLOOR, ConstantInt.of(3), 0.8F, 2, 0.05F, UniformInt.of(4, 7), 0.7F)));
    public static final WorldGenFeatureConfigured<?, ?> CLAY_POOL_WITH_DRIPLEAVES = FeatureUtils.register("clay_pool_with_dripleaves", WorldGenerator.WATERLOGGED_VEGETATION_PATCH.configured(new VegetationPatchConfiguration(TagsBlock.LUSH_GROUND_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.CLAY), () -> {
        return CaveFeatures.DRIPLEAF.placed();
    }, CaveSurface.FLOOR, ConstantInt.of(3), 0.8F, 5, 0.1F, UniformInt.of(4, 7), 0.7F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureChoiceConfiguration, ?> LUSH_CAVES_CLAY = FeatureUtils.register("lush_caves_clay", WorldGenerator.RANDOM_BOOLEAN_SELECTOR.configured(new WorldGenFeatureChoiceConfiguration(() -> {
        return CaveFeatures.CLAY_WITH_DRIPLEAVES.placed();
    }, () -> {
        return CaveFeatures.CLAY_POOL_WITH_DRIPLEAVES.placed();
    })));
    public static final WorldGenFeatureConfigured<VegetationPatchConfiguration, ?> MOSS_PATCH_CEILING = FeatureUtils.register("moss_patch_ceiling", WorldGenerator.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(TagsBlock.MOSS_REPLACEABLE.getName(), WorldGenFeatureStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
        return CaveFeatures.CAVE_VINE_IN_MOSS.placed();
    }, CaveSurface.CEILING, UniformInt.of(1, 2), 0.0F, 5, 0.08F, UniformInt.of(4, 7), 0.3F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureBlockConfiguration, ?> SPORE_BLOSSOM = FeatureUtils.register("spore_blossom", WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SPORE_BLOSSOM))));
    public static final WorldGenFeatureConfigured<GeodeConfiguration, ?> AMETHYST_GEODE = FeatureUtils.register("amethyst_geode", WorldGenerator.GEODE.configured(new GeodeConfiguration(new GeodeBlockSettings(WorldGenFeatureStateProvider.simple(Blocks.AIR), WorldGenFeatureStateProvider.simple(Blocks.AMETHYST_BLOCK), WorldGenFeatureStateProvider.simple(Blocks.BUDDING_AMETHYST), WorldGenFeatureStateProvider.simple(Blocks.CALCITE), WorldGenFeatureStateProvider.simple(Blocks.SMOOTH_BASALT), List.of(Blocks.SMALL_AMETHYST_BUD.defaultBlockState(), Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState(), Blocks.LARGE_AMETHYST_BUD.defaultBlockState(), Blocks.AMETHYST_CLUSTER.defaultBlockState()), TagsBlock.FEATURES_CANNOT_REPLACE.getName(), TagsBlock.GEODE_INVALID_BLOCKS.getName()), new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true, UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), -16, 16, 0.05D, 1)));

    public CaveFeatures() {}

    private static PlacedFeature makeDripleaf(EnumDirection enumdirection) {
        return WorldGenerator.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.of(0, 4), 2).add(ConstantInt.of(0), 1).build()), WorldGenFeatureStateProvider.simple((IBlockData) Blocks.BIG_DRIPLEAF_STEM.defaultBlockState().setValue(BlockProperties.HORIZONTAL_FACING, enumdirection))), BlockColumnConfiguration.layer(ConstantInt.of(1), WorldGenFeatureStateProvider.simple((IBlockData) Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(BlockProperties.HORIZONTAL_FACING, enumdirection)))), EnumDirection.UP, BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, true)).placed();
    }

    private static PlacedFeature makeSmallDripleaf() {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add((IBlockData) Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, EnumDirection.EAST), 1).add((IBlockData) Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, EnumDirection.WEST), 1).add((IBlockData) Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, EnumDirection.NORTH), 1).add((IBlockData) Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, EnumDirection.SOUTH), 1)))).placed();
    }
}

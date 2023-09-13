package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestTag;

public class OreFeatures {

    public static final DefinedStructureRuleTest NATURAL_STONE = new DefinedStructureTestTag(TagsBlock.BASE_STONE_OVERWORLD);
    public static final DefinedStructureRuleTest STONE_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.STONE_ORE_REPLACEABLES);
    public static final DefinedStructureRuleTest DEEPSLATE_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.DEEPSLATE_ORE_REPLACEABLES);
    public static final DefinedStructureRuleTest NETHERRACK = new DefinedStructureTestBlock(Blocks.NETHERRACK);
    public static final DefinedStructureRuleTest NETHER_ORE_REPLACEABLES = new DefinedStructureTestTag(TagsBlock.BASE_STONE_NETHER);
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_IRON_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.IRON_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_IRON_ORE.defaultBlockState()));
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_GOLD_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.GOLD_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState()));
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_DIAMOND_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.DIAMOND_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()));
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_LAPIS_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.LAPIS_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState()));
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_COPPER_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.COPPER_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState()));
    public static final List<WorldGenFeatureOreConfiguration.a> ORE_COAL_TARGET_LIST = List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.COAL_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState()));
    public static final WorldGenFeatureConfigured<?, ?> ORE_MAGMA = FeatureUtils.register("ore_magma", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.MAGMA_BLOCK.defaultBlockState(), 33)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_SOUL_SAND = FeatureUtils.register("ore_soul_sand", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.SOUL_SAND.defaultBlockState(), 12)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_NETHER_GOLD = FeatureUtils.register("ore_nether_gold", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 10)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_QUARTZ = FeatureUtils.register("ore_quartz", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_GRAVEL_NETHER = FeatureUtils.register("ore_gravel_nether", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.GRAVEL.defaultBlockState(), 33)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_BLACKSTONE = FeatureUtils.register("ore_blackstone", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHERRACK, Blocks.BLACKSTONE.defaultBlockState(), 33)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_DIRT = FeatureUtils.register("ore_dirt", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.DIRT.defaultBlockState(), 33)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_GRAVEL = FeatureUtils.register("ore_gravel", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.GRAVEL.defaultBlockState(), 33)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_GRANITE = FeatureUtils.register("ore_granite", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.GRANITE.defaultBlockState(), 64)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_DIORITE = FeatureUtils.register("ore_diorite", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.DIORITE.defaultBlockState(), 64)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_ANDESITE = FeatureUtils.register("ore_andesite", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.ANDESITE.defaultBlockState(), 64)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_TUFF = FeatureUtils.register("ore_tuff", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.TUFF.defaultBlockState(), 64)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_COAL = FeatureUtils.register("ore_coal", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_COAL_TARGET_LIST, 17)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_COAL_BURIED = FeatureUtils.register("ore_coal_buried", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_COAL_TARGET_LIST, 17, 0.5F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_IRON = FeatureUtils.register("ore_iron", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_IRON_TARGET_LIST, 9)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_IRON_SMALL = FeatureUtils.register("ore_iron_small", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_IRON_TARGET_LIST, 4)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_GOLD = FeatureUtils.register("ore_gold", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_GOLD_TARGET_LIST, 9)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_GOLD_BURIED = FeatureUtils.register("ore_gold_buried", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_GOLD_TARGET_LIST, 9, 0.5F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_REDSTONE = FeatureUtils.register("ore_redstone", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.REDSTONE_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState())), 8)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_DIAMOND_SMALL = FeatureUtils.register("ore_diamond_small", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_DIAMOND_TARGET_LIST, 4, 0.5F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_DIAMOND_LARGE = FeatureUtils.register("ore_diamond_large", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_DIAMOND_TARGET_LIST, 12, 0.7F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_DIAMOND_BURIED = FeatureUtils.register("ore_diamond_buried", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_DIAMOND_TARGET_LIST, 8, 1.0F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_LAPIS = FeatureUtils.register("ore_lapis", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_LAPIS_TARGET_LIST, 7)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_LAPIS_BURIED = FeatureUtils.register("ore_lapis_buried", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_LAPIS_TARGET_LIST, 7, 1.0F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_INFESTED = FeatureUtils.register("ore_infested", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.INFESTED_STONE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.INFESTED_DEEPSLATE.defaultBlockState())), 9)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_EMERALD = FeatureUtils.register("ore_emerald", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Blocks.EMERALD_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState())), 3)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_ANCIENT_DEBRIS_LARGE = FeatureUtils.register("ore_ancient_debris_large", WorldGenerator.SCATTERED_ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 3, 1.0F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_ANCIENT_DEBRIS_SMALL = FeatureUtils.register("ore_ancient_debris_small", WorldGenerator.SCATTERED_ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2, 1.0F)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_COPPPER_SMALL = FeatureUtils.register("ore_copper_small", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_COPPER_TARGET_LIST, 10)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_COPPER_LARGE = FeatureUtils.register("ore_copper_large", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.ORE_COPPER_TARGET_LIST, 20)));
    public static final WorldGenFeatureConfigured<?, ?> ORE_CLAY = FeatureUtils.register("ore_clay", WorldGenerator.ORE.configured(new WorldGenFeatureOreConfiguration(OreFeatures.NATURAL_STONE, Blocks.CLAY.defaultBlockState(), 33)));

    public OreFeatures() {}
}

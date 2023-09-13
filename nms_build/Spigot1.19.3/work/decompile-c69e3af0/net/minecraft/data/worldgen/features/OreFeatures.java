package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestTag;

public class OreFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_MAGMA = FeatureUtils.createKey("ore_magma");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_SOUL_SAND = FeatureUtils.createKey("ore_soul_sand");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_NETHER_GOLD = FeatureUtils.createKey("ore_nether_gold");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_QUARTZ = FeatureUtils.createKey("ore_quartz");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_GRAVEL_NETHER = FeatureUtils.createKey("ore_gravel_nether");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_BLACKSTONE = FeatureUtils.createKey("ore_blackstone");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_DIRT = FeatureUtils.createKey("ore_dirt");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_GRAVEL = FeatureUtils.createKey("ore_gravel");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_GRANITE = FeatureUtils.createKey("ore_granite");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_DIORITE = FeatureUtils.createKey("ore_diorite");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_ANDESITE = FeatureUtils.createKey("ore_andesite");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_TUFF = FeatureUtils.createKey("ore_tuff");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_COAL = FeatureUtils.createKey("ore_coal");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_COAL_BURIED = FeatureUtils.createKey("ore_coal_buried");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_IRON = FeatureUtils.createKey("ore_iron");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_IRON_SMALL = FeatureUtils.createKey("ore_iron_small");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_GOLD = FeatureUtils.createKey("ore_gold");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_GOLD_BURIED = FeatureUtils.createKey("ore_gold_buried");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_REDSTONE = FeatureUtils.createKey("ore_redstone");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_DIAMOND_SMALL = FeatureUtils.createKey("ore_diamond_small");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_DIAMOND_LARGE = FeatureUtils.createKey("ore_diamond_large");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_DIAMOND_BURIED = FeatureUtils.createKey("ore_diamond_buried");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_LAPIS = FeatureUtils.createKey("ore_lapis");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_LAPIS_BURIED = FeatureUtils.createKey("ore_lapis_buried");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_INFESTED = FeatureUtils.createKey("ore_infested");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_EMERALD = FeatureUtils.createKey("ore_emerald");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_ANCIENT_DEBRIS_LARGE = FeatureUtils.createKey("ore_ancient_debris_large");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_ANCIENT_DEBRIS_SMALL = FeatureUtils.createKey("ore_ancient_debris_small");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_COPPPER_SMALL = FeatureUtils.createKey("ore_copper_small");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_COPPER_LARGE = FeatureUtils.createKey("ore_copper_large");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ORE_CLAY = FeatureUtils.createKey("ore_clay");

    public OreFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        DefinedStructureTestTag definedstructuretesttag = new DefinedStructureTestTag(TagsBlock.BASE_STONE_OVERWORLD);
        DefinedStructureTestTag definedstructuretesttag1 = new DefinedStructureTestTag(TagsBlock.STONE_ORE_REPLACEABLES);
        DefinedStructureTestTag definedstructuretesttag2 = new DefinedStructureTestTag(TagsBlock.DEEPSLATE_ORE_REPLACEABLES);
        DefinedStructureTestBlock definedstructuretestblock = new DefinedStructureTestBlock(Blocks.NETHERRACK);
        DefinedStructureTestTag definedstructuretesttag3 = new DefinedStructureTestTag(TagsBlock.BASE_STONE_NETHER);
        List<WorldGenFeatureOreConfiguration.a> list = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.IRON_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_IRON_ORE.defaultBlockState()));
        List<WorldGenFeatureOreConfiguration.a> list1 = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.GOLD_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState()));
        List<WorldGenFeatureOreConfiguration.a> list2 = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.DIAMOND_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()));
        List<WorldGenFeatureOreConfiguration.a> list3 = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.LAPIS_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState()));
        List<WorldGenFeatureOreConfiguration.a> list4 = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.COPPER_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState()));
        List<WorldGenFeatureOreConfiguration.a> list5 = List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.COAL_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState()));

        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_MAGMA, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.MAGMA_BLOCK.defaultBlockState(), 33));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_SOUL_SAND, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.SOUL_SAND.defaultBlockState(), 12));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_NETHER_GOLD, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 10));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_QUARTZ, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_GRAVEL_NETHER, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.GRAVEL.defaultBlockState(), 33));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_BLACKSTONE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretestblock, Blocks.BLACKSTONE.defaultBlockState(), 33));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_DIRT, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.DIRT.defaultBlockState(), 33));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_GRAVEL, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.GRAVEL.defaultBlockState(), 33));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_GRANITE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.GRANITE.defaultBlockState(), 64));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_DIORITE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.DIORITE.defaultBlockState(), 64));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_ANDESITE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.ANDESITE.defaultBlockState(), 64));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_TUFF, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.TUFF.defaultBlockState(), 64));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_COAL, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list5, 17));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_COAL_BURIED, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list5, 17, 0.5F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_IRON, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list, 9));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_IRON_SMALL, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list, 4));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_GOLD, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list1, 9));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_GOLD_BURIED, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list1, 9, 0.5F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_REDSTONE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.REDSTONE_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState())), 8));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_DIAMOND_SMALL, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list2, 4, 0.5F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_DIAMOND_LARGE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list2, 12, 0.7F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_DIAMOND_BURIED, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list2, 8, 1.0F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_LAPIS, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list3, 7));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_LAPIS_BURIED, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list3, 7, 1.0F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_INFESTED, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.INFESTED_STONE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.INFESTED_DEEPSLATE.defaultBlockState())), 9));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_EMERALD, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(List.of(WorldGenFeatureOreConfiguration.target(definedstructuretesttag1, Blocks.EMERALD_ORE.defaultBlockState()), WorldGenFeatureOreConfiguration.target(definedstructuretesttag2, Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState())), 3));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, WorldGenerator.SCATTERED_ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag3, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 3, 1.0F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_ANCIENT_DEBRIS_SMALL, WorldGenerator.SCATTERED_ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag3, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2, 1.0F));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_COPPPER_SMALL, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list4, 10));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_COPPER_LARGE, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(list4, 20));
        FeatureUtils.register(bootstapcontext, OreFeatures.ORE_CLAY, WorldGenerator.ORE, new WorldGenFeatureOreConfiguration(definedstructuretesttag, Blocks.CLAY.defaultBlockState(), 33));
    }
}

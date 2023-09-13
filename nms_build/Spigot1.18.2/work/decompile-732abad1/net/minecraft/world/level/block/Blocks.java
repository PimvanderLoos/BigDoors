package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityShulkerBox;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.grower.WorldGenMegaTreeProviderDarkOak;
import net.minecraft.world.level.block.grower.WorldGenMegaTreeProviderJungle;
import net.minecraft.world.level.block.grower.WorldGenTreeProviderAcacia;
import net.minecraft.world.level.block.grower.WorldGenTreeProviderBirch;
import net.minecraft.world.level.block.grower.WorldGenTreeProviderOak;
import net.minecraft.world.level.block.grower.WorldGenTreeProviderSpruce;
import net.minecraft.world.level.block.piston.BlockPiston;
import net.minecraft.world.level.block.piston.BlockPistonExtension;
import net.minecraft.world.level.block.piston.BlockPistonMoving;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;
import net.minecraft.world.level.block.state.properties.BlockPropertyWood;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialMapColor;

public class Blocks {

    public static final Block AIR = register("air", new BlockAir(BlockBase.Info.of(Material.AIR).noCollission().noDrops().air()));
    public static final Block STONE = register("stone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block GRANITE = register("granite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIRT).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block POLISHED_GRANITE = register("polished_granite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIRT).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DIORITE = register("diorite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block POLISHED_DIORITE = register("polished_diorite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block ANDESITE = register("andesite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block POLISHED_ANDESITE = register("polished_andesite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block GRASS_BLOCK = register("grass_block", new BlockGrass(BlockBase.Info.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundEffectType.GRASS)));
    public static final Block DIRT = register("dirt", new Block(BlockBase.Info.of(Material.DIRT, MaterialMapColor.DIRT).strength(0.5F).sound(SoundEffectType.GRAVEL)));
    public static final Block COARSE_DIRT = register("coarse_dirt", new Block(BlockBase.Info.of(Material.DIRT, MaterialMapColor.DIRT).strength(0.5F).sound(SoundEffectType.GRAVEL)));
    public static final Block PODZOL = register("podzol", new BlockDirtSnow(BlockBase.Info.of(Material.DIRT, MaterialMapColor.PODZOL).strength(0.5F).sound(SoundEffectType.GRAVEL)));
    public static final Block COBBLESTONE = register("cobblestone", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block OAK_PLANKS = register("oak_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOD).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_PLANKS = register("spruce_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_PLANKS = register("birch_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_PLANKS = register("jungle_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_PLANKS = register("acacia_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_PLANKS = register("dark_oak_planks", new Block(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_BROWN).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block OAK_SAPLING = register("oak_sapling", new BlockSapling(new WorldGenTreeProviderOak(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block SPRUCE_SAPLING = register("spruce_sapling", new BlockSapling(new WorldGenTreeProviderSpruce(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block BIRCH_SAPLING = register("birch_sapling", new BlockSapling(new WorldGenTreeProviderBirch(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block JUNGLE_SAPLING = register("jungle_sapling", new BlockSapling(new WorldGenMegaTreeProviderJungle(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block ACACIA_SAPLING = register("acacia_sapling", new BlockSapling(new WorldGenTreeProviderAcacia(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block DARK_OAK_SAPLING = register("dark_oak_sapling", new BlockSapling(new WorldGenMegaTreeProviderDarkOak(), BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block BEDROCK = register("bedrock", new Block(BlockBase.Info.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().isValidSpawn(Blocks::never)));
    public static final Block WATER = register("water", new BlockFluids(FluidTypes.WATER, BlockBase.Info.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
    public static final Block LAVA = register("lava", new BlockFluids(FluidTypes.LAVA, BlockBase.Info.of(Material.LAVA).noCollission().randomTicks().strength(100.0F).lightLevel((iblockdata) -> {
        return 15;
    }).noDrops()));
    public static final Block SAND = register("sand", new BlockSand(14406560, BlockBase.Info.of(Material.SAND, MaterialMapColor.SAND).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block RED_SAND = register("red_sand", new BlockSand(11098145, BlockBase.Info.of(Material.SAND, MaterialMapColor.COLOR_ORANGE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block GRAVEL = register("gravel", new BlockGravel(BlockBase.Info.of(Material.SAND, MaterialMapColor.STONE).strength(0.6F).sound(SoundEffectType.GRAVEL)));
    public static final Block GOLD_ORE = register("gold_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final Block DEEPSLATE_GOLD_ORE = register("deepslate_gold_ore", new BlockOre(BlockBase.Info.copy(Blocks.GOLD_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE)));
    public static final Block IRON_ORE = register("iron_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final Block DEEPSLATE_IRON_ORE = register("deepslate_iron_ore", new BlockOre(BlockBase.Info.copy(Blocks.IRON_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE)));
    public static final Block COAL_ORE = register("coal_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(0, 2)));
    public static final Block DEEPSLATE_COAL_ORE = register("deepslate_coal_ore", new BlockOre(BlockBase.Info.copy(Blocks.COAL_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE), UniformInt.of(0, 2)));
    public static final Block NETHER_GOLD_ORE = register("nether_gold_ore", new BlockOre(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundEffectType.NETHER_GOLD_ORE), UniformInt.of(0, 1)));
    public static final Block OAK_LOG = register("oak_log", log(MaterialMapColor.WOOD, MaterialMapColor.PODZOL));
    public static final Block SPRUCE_LOG = register("spruce_log", log(MaterialMapColor.PODZOL, MaterialMapColor.COLOR_BROWN));
    public static final Block BIRCH_LOG = register("birch_log", log(MaterialMapColor.SAND, MaterialMapColor.QUARTZ));
    public static final Block JUNGLE_LOG = register("jungle_log", log(MaterialMapColor.DIRT, MaterialMapColor.PODZOL));
    public static final Block ACACIA_LOG = register("acacia_log", log(MaterialMapColor.COLOR_ORANGE, MaterialMapColor.STONE));
    public static final Block DARK_OAK_LOG = register("dark_oak_log", log(MaterialMapColor.COLOR_BROWN, MaterialMapColor.COLOR_BROWN));
    public static final Block STRIPPED_SPRUCE_LOG = register("stripped_spruce_log", log(MaterialMapColor.PODZOL, MaterialMapColor.PODZOL));
    public static final Block STRIPPED_BIRCH_LOG = register("stripped_birch_log", log(MaterialMapColor.SAND, MaterialMapColor.SAND));
    public static final Block STRIPPED_JUNGLE_LOG = register("stripped_jungle_log", log(MaterialMapColor.DIRT, MaterialMapColor.DIRT));
    public static final Block STRIPPED_ACACIA_LOG = register("stripped_acacia_log", log(MaterialMapColor.COLOR_ORANGE, MaterialMapColor.COLOR_ORANGE));
    public static final Block STRIPPED_DARK_OAK_LOG = register("stripped_dark_oak_log", log(MaterialMapColor.COLOR_BROWN, MaterialMapColor.COLOR_BROWN));
    public static final Block STRIPPED_OAK_LOG = register("stripped_oak_log", log(MaterialMapColor.WOOD, MaterialMapColor.WOOD));
    public static final Block OAK_WOOD = register("oak_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOD).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_WOOD = register("spruce_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_WOOD = register("birch_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_WOOD = register("jungle_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_WOOD = register("acacia_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_GRAY).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_WOOD = register("dark_oak_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_BROWN).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_OAK_WOOD = register("stripped_oak_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOD).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_SPRUCE_WOOD = register("stripped_spruce_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_BIRCH_WOOD = register("stripped_birch_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_JUNGLE_WOOD = register("stripped_jungle_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_ACACIA_WOOD = register("stripped_acacia_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block STRIPPED_DARK_OAK_WOOD = register("stripped_dark_oak_wood", new BlockRotatable(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_BROWN).strength(2.0F).sound(SoundEffectType.WOOD)));
    public static final Block OAK_LEAVES = register("oak_leaves", leaves(SoundEffectType.GRASS));
    public static final Block SPRUCE_LEAVES = register("spruce_leaves", leaves(SoundEffectType.GRASS));
    public static final Block BIRCH_LEAVES = register("birch_leaves", leaves(SoundEffectType.GRASS));
    public static final Block JUNGLE_LEAVES = register("jungle_leaves", leaves(SoundEffectType.GRASS));
    public static final Block ACACIA_LEAVES = register("acacia_leaves", leaves(SoundEffectType.GRASS));
    public static final Block DARK_OAK_LEAVES = register("dark_oak_leaves", leaves(SoundEffectType.GRASS));
    public static final Block AZALEA_LEAVES = register("azalea_leaves", leaves(SoundEffectType.AZALEA_LEAVES));
    public static final Block FLOWERING_AZALEA_LEAVES = register("flowering_azalea_leaves", leaves(SoundEffectType.AZALEA_LEAVES));
    public static final Block SPONGE = register("sponge", new BlockSponge(BlockBase.Info.of(Material.SPONGE).strength(0.6F).sound(SoundEffectType.GRASS)));
    public static final Block WET_SPONGE = register("wet_sponge", new BlockWetSponge(BlockBase.Info.of(Material.SPONGE).strength(0.6F).sound(SoundEffectType.GRASS)));
    public static final Block GLASS = register("glass", new BlockGlass(BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)));
    public static final Block LAPIS_ORE = register("lapis_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(2, 5)));
    public static final Block DEEPSLATE_LAPIS_ORE = register("deepslate_lapis_ore", new BlockOre(BlockBase.Info.copy(Blocks.LAPIS_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE), UniformInt.of(2, 5)));
    public static final Block LAPIS_BLOCK = register("lapis_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.LAPIS).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final Block DISPENSER = register("dispenser", new BlockDispenser(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F)));
    public static final Block SANDSTONE = register("sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block CHISELED_SANDSTONE = register("chiseled_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block CUT_SANDSTONE = register("cut_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block NOTE_BLOCK = register("note_block", new BlockNote(BlockBase.Info.of(Material.WOOD).sound(SoundEffectType.WOOD).strength(0.8F)));
    public static final Block WHITE_BED = register("white_bed", bed(EnumColor.WHITE));
    public static final Block ORANGE_BED = register("orange_bed", bed(EnumColor.ORANGE));
    public static final Block MAGENTA_BED = register("magenta_bed", bed(EnumColor.MAGENTA));
    public static final Block LIGHT_BLUE_BED = register("light_blue_bed", bed(EnumColor.LIGHT_BLUE));
    public static final Block YELLOW_BED = register("yellow_bed", bed(EnumColor.YELLOW));
    public static final Block LIME_BED = register("lime_bed", bed(EnumColor.LIME));
    public static final Block PINK_BED = register("pink_bed", bed(EnumColor.PINK));
    public static final Block GRAY_BED = register("gray_bed", bed(EnumColor.GRAY));
    public static final Block LIGHT_GRAY_BED = register("light_gray_bed", bed(EnumColor.LIGHT_GRAY));
    public static final Block CYAN_BED = register("cyan_bed", bed(EnumColor.CYAN));
    public static final Block PURPLE_BED = register("purple_bed", bed(EnumColor.PURPLE));
    public static final Block BLUE_BED = register("blue_bed", bed(EnumColor.BLUE));
    public static final Block BROWN_BED = register("brown_bed", bed(EnumColor.BROWN));
    public static final Block GREEN_BED = register("green_bed", bed(EnumColor.GREEN));
    public static final Block RED_BED = register("red_bed", bed(EnumColor.RED));
    public static final Block BLACK_BED = register("black_bed", bed(EnumColor.BLACK));
    public static final Block POWERED_RAIL = register("powered_rail", new BlockPoweredRail(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundEffectType.METAL)));
    public static final Block DETECTOR_RAIL = register("detector_rail", new BlockMinecartDetector(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundEffectType.METAL)));
    public static final Block STICKY_PISTON = register("sticky_piston", pistonBase(true));
    public static final Block COBWEB = register("cobweb", new BlockWeb(BlockBase.Info.of(Material.WEB).noCollission().requiresCorrectToolForDrops().strength(4.0F)));
    public static final Block GRASS = register("grass", new BlockLongGrass(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block FERN = register("fern", new BlockLongGrass(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block DEAD_BUSH = register("dead_bush", new BlockDeadBush(BlockBase.Info.of(Material.REPLACEABLE_PLANT, MaterialMapColor.WOOD).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block SEAGRASS = register("seagrass", new SeagrassBlock(BlockBase.Info.of(Material.REPLACEABLE_WATER_PLANT).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block TALL_SEAGRASS = register("tall_seagrass", new TallSeagrassBlock(BlockBase.Info.of(Material.REPLACEABLE_WATER_PLANT).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block PISTON = register("piston", pistonBase(false));
    public static final Block PISTON_HEAD = register("piston_head", new BlockPistonExtension(BlockBase.Info.of(Material.PISTON).strength(1.5F).noDrops()));
    public static final Block WHITE_WOOL = register("white_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.SNOW).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block ORANGE_WOOL = register("orange_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_ORANGE).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block MAGENTA_WOOL = register("magenta_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_MAGENTA).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block LIGHT_BLUE_WOOL = register("light_blue_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_LIGHT_BLUE).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block YELLOW_WOOL = register("yellow_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_YELLOW).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block LIME_WOOL = register("lime_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_LIGHT_GREEN).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block PINK_WOOL = register("pink_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_PINK).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block GRAY_WOOL = register("gray_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_GRAY).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block LIGHT_GRAY_WOOL = register("light_gray_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_LIGHT_GRAY).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block CYAN_WOOL = register("cyan_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_CYAN).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block PURPLE_WOOL = register("purple_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_PURPLE).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block BLUE_WOOL = register("blue_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_BLUE).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block BROWN_WOOL = register("brown_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_BROWN).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block GREEN_WOOL = register("green_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_GREEN).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block RED_WOOL = register("red_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_RED).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block BLACK_WOOL = register("black_wool", new Block(BlockBase.Info.of(Material.WOOL, MaterialMapColor.COLOR_BLACK).strength(0.8F).sound(SoundEffectType.WOOL)));
    public static final Block MOVING_PISTON = register("moving_piston", new BlockPistonMoving(BlockBase.Info.of(Material.PISTON).strength(-1.0F).dynamicShape().noDrops().noOcclusion().isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)));
    public static final Block DANDELION = register("dandelion", new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block POPPY = register("poppy", new BlockFlowers(MobEffects.NIGHT_VISION, 5, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block BLUE_ORCHID = register("blue_orchid", new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block ALLIUM = register("allium", new BlockFlowers(MobEffects.FIRE_RESISTANCE, 4, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block AZURE_BLUET = register("azure_bluet", new BlockFlowers(MobEffects.BLINDNESS, 8, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block RED_TULIP = register("red_tulip", new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block ORANGE_TULIP = register("orange_tulip", new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block WHITE_TULIP = register("white_tulip", new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block PINK_TULIP = register("pink_tulip", new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block OXEYE_DAISY = register("oxeye_daisy", new BlockFlowers(MobEffects.REGENERATION, 8, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block CORNFLOWER = register("cornflower", new BlockFlowers(MobEffects.JUMP, 6, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block WITHER_ROSE = register("wither_rose", new BlockWitherRose(MobEffects.WITHER, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block LILY_OF_THE_VALLEY = register("lily_of_the_valley", new BlockFlowers(MobEffects.POISON, 12, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block BROWN_MUSHROOM = register("brown_mushroom", new BlockMushroom(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_BROWN).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS).lightLevel((iblockdata) -> {
        return 1;
    }).hasPostProcess(Blocks::always), () -> {
        return TreeFeatures.HUGE_BROWN_MUSHROOM;
    }));
    public static final Block RED_MUSHROOM = register("red_mushroom", new BlockMushroom(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_RED).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS).hasPostProcess(Blocks::always), () -> {
        return TreeFeatures.HUGE_RED_MUSHROOM;
    }));
    public static final Block GOLD_BLOCK = register("gold_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.GOLD).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.METAL)));
    public static final Block IRON_BLOCK = register("iron_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.METAL)));
    public static final Block BRICKS = register("bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block TNT = register("tnt", new BlockTNT(BlockBase.Info.of(Material.EXPLOSIVE).instabreak().sound(SoundEffectType.GRASS)));
    public static final Block BOOKSHELF = register("bookshelf", new Block(BlockBase.Info.of(Material.WOOD).strength(1.5F).sound(SoundEffectType.WOOD)));
    public static final Block MOSSY_COBBLESTONE = register("mossy_cobblestone", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block OBSIDIAN = register("obsidian", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));
    public static final Block TORCH = register("torch", new BlockTorch(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 14;
    }).sound(SoundEffectType.WOOD), Particles.FLAME));
    public static final Block WALL_TORCH = register("wall_torch", new BlockTorchWall(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 14;
    }).sound(SoundEffectType.WOOD).dropsLike(Blocks.TORCH), Particles.FLAME));
    public static final Block FIRE = register("fire", new BlockFire(BlockBase.Info.of(Material.FIRE, MaterialMapColor.FIRE).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 15;
    }).sound(SoundEffectType.WOOL)));
    public static final Block SOUL_FIRE = register("soul_fire", new BlockSoulFire(BlockBase.Info.of(Material.FIRE, MaterialMapColor.COLOR_LIGHT_BLUE).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 10;
    }).sound(SoundEffectType.WOOL)));
    public static final Block SPAWNER = register("spawner", new BlockMobSpawner(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(5.0F).sound(SoundEffectType.METAL).noOcclusion()));
    public static final Block OAK_STAIRS = register("oak_stairs", new BlockStairs(Blocks.OAK_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.OAK_PLANKS)));
    public static final Block CHEST = register("chest", new BlockChest(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD), () -> {
        return TileEntityTypes.CHEST;
    }));
    public static final Block REDSTONE_WIRE = register("redstone_wire", new BlockRedstoneWire(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak()));
    public static final Block DIAMOND_ORE = register("diamond_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final Block DEEPSLATE_DIAMOND_ORE = register("deepslate_diamond_ore", new BlockOre(BlockBase.Info.copy(Blocks.DIAMOND_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final Block DIAMOND_BLOCK = register("diamond_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.METAL)));
    public static final Block CRAFTING_TABLE = register("crafting_table", new BlockWorkbench(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block WHEAT = register("wheat", new BlockCrops(BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.CROP)));
    public static final Block FARMLAND = register("farmland", new BlockSoil(BlockBase.Info.of(Material.DIRT).randomTicks().strength(0.6F).sound(SoundEffectType.GRAVEL).isViewBlocking(Blocks::always).isSuffocating(Blocks::always)));
    public static final Block FURNACE = register("furnace", new BlockFurnaceFurace(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))));
    public static final Block OAK_SIGN = register("oak_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.OAK));
    public static final Block SPRUCE_SIGN = register("spruce_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.SPRUCE));
    public static final Block BIRCH_SIGN = register("birch_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.BIRCH));
    public static final Block ACACIA_SIGN = register("acacia_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.ACACIA));
    public static final Block JUNGLE_SIGN = register("jungle_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.JUNGLE));
    public static final Block DARK_OAK_SIGN = register("dark_oak_sign", new BlockFloorSign(BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.DARK_OAK));
    public static final Block OAK_DOOR = register("oak_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block LADDER = register("ladder", new BlockLadder(BlockBase.Info.of(Material.DECORATION).strength(0.4F).sound(SoundEffectType.LADDER).noOcclusion()));
    public static final Block RAIL = register("rail", new BlockMinecartTrack(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundEffectType.METAL)));
    public static final Block COBBLESTONE_STAIRS = register("cobblestone_stairs", new BlockStairs(Blocks.COBBLESTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.COBBLESTONE)));
    public static final Block OAK_WALL_SIGN = register("oak_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.OAK_SIGN), BlockPropertyWood.OAK));
    public static final Block SPRUCE_WALL_SIGN = register("spruce_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.SPRUCE_SIGN), BlockPropertyWood.SPRUCE));
    public static final Block BIRCH_WALL_SIGN = register("birch_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.BIRCH_SIGN), BlockPropertyWood.BIRCH));
    public static final Block ACACIA_WALL_SIGN = register("acacia_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.ACACIA_SIGN), BlockPropertyWood.ACACIA));
    public static final Block JUNGLE_WALL_SIGN = register("jungle_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.JUNGLE_SIGN), BlockPropertyWood.JUNGLE));
    public static final Block DARK_OAK_WALL_SIGN = register("dark_oak_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_LOG.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.DARK_OAK_SIGN), BlockPropertyWood.DARK_OAK));
    public static final Block LEVER = register("lever", new BlockLever(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block STONE_PRESSURE_PLATE = register("stone_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().noCollission().strength(0.5F)));
    public static final Block IRON_DOOR = register("iron_door", new BlockDoor(BlockBase.Info.of(Material.METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundEffectType.METAL).noOcclusion()));
    public static final Block OAK_PRESSURE_PLATE = register("oak_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_PRESSURE_PLATE = register("spruce_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_PRESSURE_PLATE = register("birch_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_PRESSURE_PLATE = register("jungle_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_PRESSURE_PLATE = register("acacia_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_PRESSURE_PLATE = register("dark_oak_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block REDSTONE_ORE = register("redstone_ore", new BlockRedstoneOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().randomTicks().lightLevel(litBlockEmission(9)).strength(3.0F, 3.0F)));
    public static final Block DEEPSLATE_REDSTONE_ORE = register("deepslate_redstone_ore", new BlockRedstoneOre(BlockBase.Info.copy(Blocks.REDSTONE_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE)));
    public static final Block REDSTONE_TORCH = register("redstone_torch", new BlockRedstoneTorch(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel(litBlockEmission(7)).sound(SoundEffectType.WOOD)));
    public static final Block REDSTONE_WALL_TORCH = register("redstone_wall_torch", new BlockRedstoneTorchWall(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel(litBlockEmission(7)).sound(SoundEffectType.WOOD).dropsLike(Blocks.REDSTONE_TORCH)));
    public static final Block STONE_BUTTON = register("stone_button", new BlockStoneButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F)));
    public static final Block SNOW = register("snow", new BlockSnow(BlockBase.Info.of(Material.TOP_SNOW).randomTicks().strength(0.1F).requiresCorrectToolForDrops().sound(SoundEffectType.SNOW).isViewBlocking((iblockdata, iblockaccess, blockposition) -> {
        return (Integer) iblockdata.getValue(BlockSnow.LAYERS) >= 8;
    })));
    public static final Block ICE = register("ice", new BlockIce(BlockBase.Info.of(Material.ICE).friction(0.98F).randomTicks().strength(0.5F).sound(SoundEffectType.GLASS).noOcclusion().isValidSpawn((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    })));
    public static final Block SNOW_BLOCK = register("snow_block", new Block(BlockBase.Info.of(Material.SNOW).requiresCorrectToolForDrops().strength(0.2F).sound(SoundEffectType.SNOW)));
    public static final Block CACTUS = register("cactus", new BlockCactus(BlockBase.Info.of(Material.CACTUS).randomTicks().strength(0.4F).sound(SoundEffectType.WOOL)));
    public static final Block CLAY = register("clay", new Block(BlockBase.Info.of(Material.CLAY).strength(0.6F).sound(SoundEffectType.GRAVEL)));
    public static final Block SUGAR_CANE = register("sugar_cane", new BlockReed(BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block JUKEBOX = register("jukebox", new BlockJukeBox(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(2.0F, 6.0F)));
    public static final Block OAK_FENCE = register("oak_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block PUMPKIN = register("pumpkin", new BlockPumpkin(BlockBase.Info.of(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block NETHERRACK = register("netherrack", new BlockNetherrack(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(0.4F).sound(SoundEffectType.NETHERRACK)));
    public static final Block SOUL_SAND = register("soul_sand", new BlockSlowSand(BlockBase.Info.of(Material.SAND, MaterialMapColor.COLOR_BROWN).strength(0.5F).speedFactor(0.4F).sound(SoundEffectType.SOUL_SAND).isValidSpawn(Blocks::always).isRedstoneConductor(Blocks::always).isViewBlocking(Blocks::always).isSuffocating(Blocks::always)));
    public static final Block SOUL_SOIL = register("soul_soil", new Block(BlockBase.Info.of(Material.DIRT, MaterialMapColor.COLOR_BROWN).strength(0.5F).sound(SoundEffectType.SOUL_SOIL)));
    public static final Block BASALT = register("basalt", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.25F, 4.2F).sound(SoundEffectType.BASALT)));
    public static final Block POLISHED_BASALT = register("polished_basalt", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.25F, 4.2F).sound(SoundEffectType.BASALT)));
    public static final Block SOUL_TORCH = register("soul_torch", new BlockTorch(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 10;
    }).sound(SoundEffectType.WOOD), Particles.SOUL_FIRE_FLAME));
    public static final Block SOUL_WALL_TORCH = register("soul_wall_torch", new BlockTorchWall(BlockBase.Info.of(Material.DECORATION).noCollission().instabreak().lightLevel((iblockdata) -> {
        return 10;
    }).sound(SoundEffectType.WOOD).dropsLike(Blocks.SOUL_TORCH), Particles.SOUL_FIRE_FLAME));
    public static final Block GLOWSTONE = register("glowstone", new Block(BlockBase.Info.of(Material.GLASS, MaterialMapColor.SAND).strength(0.3F).sound(SoundEffectType.GLASS).lightLevel((iblockdata) -> {
        return 15;
    })));
    public static final Block NETHER_PORTAL = register("nether_portal", new BlockPortal(BlockBase.Info.of(Material.PORTAL).noCollission().randomTicks().strength(-1.0F).sound(SoundEffectType.GLASS).lightLevel((iblockdata) -> {
        return 11;
    })));
    public static final Block CARVED_PUMPKIN = register("carved_pumpkin", new BlockPumpkinCarved(BlockBase.Info.of(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).strength(1.0F).sound(SoundEffectType.WOOD).isValidSpawn(Blocks::always)));
    public static final Block JACK_O_LANTERN = register("jack_o_lantern", new BlockPumpkinCarved(BlockBase.Info.of(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).strength(1.0F).sound(SoundEffectType.WOOD).lightLevel((iblockdata) -> {
        return 15;
    }).isValidSpawn(Blocks::always)));
    public static final Block CAKE = register("cake", new BlockCake(BlockBase.Info.of(Material.CAKE).strength(0.5F).sound(SoundEffectType.WOOL)));
    public static final Block REPEATER = register("repeater", new BlockRepeater(BlockBase.Info.of(Material.DECORATION).instabreak().sound(SoundEffectType.WOOD)));
    public static final Block WHITE_STAINED_GLASS = register("white_stained_glass", stainedGlass(EnumColor.WHITE));
    public static final Block ORANGE_STAINED_GLASS = register("orange_stained_glass", stainedGlass(EnumColor.ORANGE));
    public static final Block MAGENTA_STAINED_GLASS = register("magenta_stained_glass", stainedGlass(EnumColor.MAGENTA));
    public static final Block LIGHT_BLUE_STAINED_GLASS = register("light_blue_stained_glass", stainedGlass(EnumColor.LIGHT_BLUE));
    public static final Block YELLOW_STAINED_GLASS = register("yellow_stained_glass", stainedGlass(EnumColor.YELLOW));
    public static final Block LIME_STAINED_GLASS = register("lime_stained_glass", stainedGlass(EnumColor.LIME));
    public static final Block PINK_STAINED_GLASS = register("pink_stained_glass", stainedGlass(EnumColor.PINK));
    public static final Block GRAY_STAINED_GLASS = register("gray_stained_glass", stainedGlass(EnumColor.GRAY));
    public static final Block LIGHT_GRAY_STAINED_GLASS = register("light_gray_stained_glass", stainedGlass(EnumColor.LIGHT_GRAY));
    public static final Block CYAN_STAINED_GLASS = register("cyan_stained_glass", stainedGlass(EnumColor.CYAN));
    public static final Block PURPLE_STAINED_GLASS = register("purple_stained_glass", stainedGlass(EnumColor.PURPLE));
    public static final Block BLUE_STAINED_GLASS = register("blue_stained_glass", stainedGlass(EnumColor.BLUE));
    public static final Block BROWN_STAINED_GLASS = register("brown_stained_glass", stainedGlass(EnumColor.BROWN));
    public static final Block GREEN_STAINED_GLASS = register("green_stained_glass", stainedGlass(EnumColor.GREEN));
    public static final Block RED_STAINED_GLASS = register("red_stained_glass", stainedGlass(EnumColor.RED));
    public static final Block BLACK_STAINED_GLASS = register("black_stained_glass", stainedGlass(EnumColor.BLACK));
    public static final Block OAK_TRAPDOOR = register("oak_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOD).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block SPRUCE_TRAPDOOR = register("spruce_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block BIRCH_TRAPDOOR = register("birch_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block JUNGLE_TRAPDOOR = register("jungle_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block ACACIA_TRAPDOOR = register("acacia_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block DARK_OAK_TRAPDOOR = register("dark_oak_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_BROWN).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block STONE_BRICKS = register("stone_bricks", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block MOSSY_STONE_BRICKS = register("mossy_stone_bricks", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block CRACKED_STONE_BRICKS = register("cracked_stone_bricks", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block CHISELED_STONE_BRICKS = register("chiseled_stone_bricks", new Block(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block INFESTED_STONE = register("infested_stone", new BlockMonsterEggs(Blocks.STONE, BlockBase.Info.of(Material.CLAY)));
    public static final Block INFESTED_COBBLESTONE = register("infested_cobblestone", new BlockMonsterEggs(Blocks.COBBLESTONE, BlockBase.Info.of(Material.CLAY)));
    public static final Block INFESTED_STONE_BRICKS = register("infested_stone_bricks", new BlockMonsterEggs(Blocks.STONE_BRICKS, BlockBase.Info.of(Material.CLAY)));
    public static final Block INFESTED_MOSSY_STONE_BRICKS = register("infested_mossy_stone_bricks", new BlockMonsterEggs(Blocks.MOSSY_STONE_BRICKS, BlockBase.Info.of(Material.CLAY)));
    public static final Block INFESTED_CRACKED_STONE_BRICKS = register("infested_cracked_stone_bricks", new BlockMonsterEggs(Blocks.CRACKED_STONE_BRICKS, BlockBase.Info.of(Material.CLAY)));
    public static final Block INFESTED_CHISELED_STONE_BRICKS = register("infested_chiseled_stone_bricks", new BlockMonsterEggs(Blocks.CHISELED_STONE_BRICKS, BlockBase.Info.of(Material.CLAY)));
    public static final Block BROWN_MUSHROOM_BLOCK = register("brown_mushroom_block", new BlockHugeMushroom(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(0.2F).sound(SoundEffectType.WOOD)));
    public static final Block RED_MUSHROOM_BLOCK = register("red_mushroom_block", new BlockHugeMushroom(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_RED).strength(0.2F).sound(SoundEffectType.WOOD)));
    public static final Block MUSHROOM_STEM = register("mushroom_stem", new BlockHugeMushroom(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOL).strength(0.2F).sound(SoundEffectType.WOOD)));
    public static final Block IRON_BARS = register("iron_bars", new BlockIronBars(BlockBase.Info.of(Material.METAL, MaterialMapColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.METAL).noOcclusion()));
    public static final Block CHAIN = register("chain", new BlockChain(BlockBase.Info.of(Material.METAL, MaterialMapColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.CHAIN).noOcclusion()));
    public static final Block GLASS_PANE = register("glass_pane", new BlockIronBars(BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block MELON = register("melon", new BlockMelon(BlockBase.Info.of(Material.VEGETABLE, MaterialMapColor.COLOR_LIGHT_GREEN).strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block ATTACHED_PUMPKIN_STEM = register("attached_pumpkin_stem", new BlockStemAttached((BlockStemmed) Blocks.PUMPKIN, () -> {
        return Items.PUMPKIN_SEEDS;
    }, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.WOOD)));
    public static final Block ATTACHED_MELON_STEM = register("attached_melon_stem", new BlockStemAttached((BlockStemmed) Blocks.MELON, () -> {
        return Items.MELON_SEEDS;
    }, BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.WOOD)));
    public static final Block PUMPKIN_STEM = register("pumpkin_stem", new BlockStem((BlockStemmed) Blocks.PUMPKIN, () -> {
        return Items.PUMPKIN_SEEDS;
    }, BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.HARD_CROP)));
    public static final Block MELON_STEM = register("melon_stem", new BlockStem((BlockStemmed) Blocks.MELON, () -> {
        return Items.MELON_SEEDS;
    }, BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.HARD_CROP)));
    public static final Block VINE = register("vine", new BlockVine(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundEffectType.VINE)));
    public static final Block GLOW_LICHEN = register("glow_lichen", new GlowLichenBlock(BlockBase.Info.of(Material.REPLACEABLE_PLANT, MaterialMapColor.GLOW_LICHEN).noCollission().strength(0.2F).sound(SoundEffectType.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(7))));
    public static final Block OAK_FENCE_GATE = register("oak_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block BRICK_STAIRS = register("brick_stairs", new BlockStairs(Blocks.BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.BRICKS)));
    public static final Block STONE_BRICK_STAIRS = register("stone_brick_stairs", new BlockStairs(Blocks.STONE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.STONE_BRICKS)));
    public static final Block MYCELIUM = register("mycelium", new BlockMycel(BlockBase.Info.of(Material.GRASS, MaterialMapColor.COLOR_PURPLE).randomTicks().strength(0.6F).sound(SoundEffectType.GRASS)));
    public static final Block LILY_PAD = register("lily_pad", new BlockWaterLily(BlockBase.Info.of(Material.PLANT).instabreak().sound(SoundEffectType.LILY_PAD).noOcclusion()));
    public static final Block NETHER_BRICKS = register("nether_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block NETHER_BRICK_FENCE = register("nether_brick_fence", new BlockFence(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block NETHER_BRICK_STAIRS = register("nether_brick_stairs", new BlockStairs(Blocks.NETHER_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.NETHER_BRICKS)));
    public static final Block NETHER_WART = register("nether_wart", new BlockNetherWart(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_RED).noCollission().randomTicks().sound(SoundEffectType.NETHER_WART)));
    public static final Block ENCHANTING_TABLE = register("enchanting_table", new BlockEnchantmentTable(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_RED).requiresCorrectToolForDrops().lightLevel((iblockdata) -> {
        return 7;
    }).strength(5.0F, 1200.0F)));
    public static final Block BREWING_STAND = register("brewing_stand", new BlockBrewingStand(BlockBase.Info.of(Material.METAL).requiresCorrectToolForDrops().strength(0.5F).lightLevel((iblockdata) -> {
        return 1;
    }).noOcclusion()));
    public static final Block CAULDRON = register("cauldron", new BlockCauldron(BlockBase.Info.of(Material.METAL, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F).noOcclusion()));
    public static final Block WATER_CAULDRON = register("water_cauldron", new LayeredCauldronBlock(BlockBase.Info.copy(Blocks.CAULDRON), LayeredCauldronBlock.RAIN, CauldronInteraction.WATER));
    public static final Block LAVA_CAULDRON = register("lava_cauldron", new LavaCauldronBlock(BlockBase.Info.copy(Blocks.CAULDRON).lightLevel((iblockdata) -> {
        return 15;
    })));
    public static final Block POWDER_SNOW_CAULDRON = register("powder_snow_cauldron", new PowderSnowCauldronBlock(BlockBase.Info.copy(Blocks.CAULDRON), LayeredCauldronBlock.SNOW, CauldronInteraction.POWDER_SNOW));
    public static final Block END_PORTAL = register("end_portal", new BlockEnderPortal(BlockBase.Info.of(Material.PORTAL, MaterialMapColor.COLOR_BLACK).noCollission().lightLevel((iblockdata) -> {
        return 15;
    }).strength(-1.0F, 3600000.0F).noDrops()));
    public static final Block END_PORTAL_FRAME = register("end_portal_frame", new BlockEnderPortalFrame(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GREEN).sound(SoundEffectType.GLASS).lightLevel((iblockdata) -> {
        return 1;
    }).strength(-1.0F, 3600000.0F).noDrops()));
    public static final Block END_STONE = register("end_stone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(3.0F, 9.0F)));
    public static final Block DRAGON_EGG = register("dragon_egg", new BlockDragonEgg(BlockBase.Info.of(Material.EGG, MaterialMapColor.COLOR_BLACK).strength(3.0F, 9.0F).lightLevel((iblockdata) -> {
        return 1;
    }).noOcclusion()));
    public static final Block REDSTONE_LAMP = register("redstone_lamp", new BlockRedstoneLamp(BlockBase.Info.of(Material.BUILDABLE_GLASS).lightLevel(litBlockEmission(15)).strength(0.3F).sound(SoundEffectType.GLASS).isValidSpawn(Blocks::always)));
    public static final Block COCOA = register("cocoa", new BlockCocoa(BlockBase.Info.of(Material.PLANT).randomTicks().strength(0.2F, 3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block SANDSTONE_STAIRS = register("sandstone_stairs", new BlockStairs(Blocks.SANDSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.SANDSTONE)));
    public static final Block EMERALD_ORE = register("emerald_ore", new BlockOre(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final Block DEEPSLATE_EMERALD_ORE = register("deepslate_emerald_ore", new BlockOre(BlockBase.Info.copy(Blocks.EMERALD_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final Block ENDER_CHEST = register("ender_chest", new BlockEnderChest(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(22.5F, 600.0F).lightLevel((iblockdata) -> {
        return 7;
    })));
    public static final Block TRIPWIRE_HOOK = register("tripwire_hook", new BlockTripwireHook(BlockBase.Info.of(Material.DECORATION).noCollission()));
    public static final Block TRIPWIRE = register("tripwire", new BlockTripwire((BlockTripwireHook) Blocks.TRIPWIRE_HOOK, BlockBase.Info.of(Material.DECORATION).noCollission()));
    public static final Block EMERALD_BLOCK = register("emerald_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.EMERALD).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.METAL)));
    public static final Block SPRUCE_STAIRS = register("spruce_stairs", new BlockStairs(Blocks.SPRUCE_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.SPRUCE_PLANKS)));
    public static final Block BIRCH_STAIRS = register("birch_stairs", new BlockStairs(Blocks.BIRCH_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.BIRCH_PLANKS)));
    public static final Block JUNGLE_STAIRS = register("jungle_stairs", new BlockStairs(Blocks.JUNGLE_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.JUNGLE_PLANKS)));
    public static final Block COMMAND_BLOCK = register("command_block", new BlockCommand(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops(), false));
    public static final Block BEACON = register("beacon", new BlockBeacon(BlockBase.Info.of(Material.GLASS, MaterialMapColor.DIAMOND).strength(3.0F).lightLevel((iblockdata) -> {
        return 15;
    }).noOcclusion().isRedstoneConductor(Blocks::never)));
    public static final Block COBBLESTONE_WALL = register("cobblestone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.COBBLESTONE)));
    public static final Block MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.COBBLESTONE)));
    public static final Block FLOWER_POT = register("flower_pot", new BlockFlowerPot(Blocks.AIR, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_OAK_SAPLING = register("potted_oak_sapling", new BlockFlowerPot(Blocks.OAK_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_SPRUCE_SAPLING = register("potted_spruce_sapling", new BlockFlowerPot(Blocks.SPRUCE_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_BIRCH_SAPLING = register("potted_birch_sapling", new BlockFlowerPot(Blocks.BIRCH_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_JUNGLE_SAPLING = register("potted_jungle_sapling", new BlockFlowerPot(Blocks.JUNGLE_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_ACACIA_SAPLING = register("potted_acacia_sapling", new BlockFlowerPot(Blocks.ACACIA_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_DARK_OAK_SAPLING = register("potted_dark_oak_sapling", new BlockFlowerPot(Blocks.DARK_OAK_SAPLING, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_FERN = register("potted_fern", new BlockFlowerPot(Blocks.FERN, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_DANDELION = register("potted_dandelion", new BlockFlowerPot(Blocks.DANDELION, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_POPPY = register("potted_poppy", new BlockFlowerPot(Blocks.POPPY, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_BLUE_ORCHID = register("potted_blue_orchid", new BlockFlowerPot(Blocks.BLUE_ORCHID, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_ALLIUM = register("potted_allium", new BlockFlowerPot(Blocks.ALLIUM, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_AZURE_BLUET = register("potted_azure_bluet", new BlockFlowerPot(Blocks.AZURE_BLUET, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_RED_TULIP = register("potted_red_tulip", new BlockFlowerPot(Blocks.RED_TULIP, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_ORANGE_TULIP = register("potted_orange_tulip", new BlockFlowerPot(Blocks.ORANGE_TULIP, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_WHITE_TULIP = register("potted_white_tulip", new BlockFlowerPot(Blocks.WHITE_TULIP, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_PINK_TULIP = register("potted_pink_tulip", new BlockFlowerPot(Blocks.PINK_TULIP, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_OXEYE_DAISY = register("potted_oxeye_daisy", new BlockFlowerPot(Blocks.OXEYE_DAISY, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_CORNFLOWER = register("potted_cornflower", new BlockFlowerPot(Blocks.CORNFLOWER, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_LILY_OF_THE_VALLEY = register("potted_lily_of_the_valley", new BlockFlowerPot(Blocks.LILY_OF_THE_VALLEY, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_WITHER_ROSE = register("potted_wither_rose", new BlockFlowerPot(Blocks.WITHER_ROSE, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_RED_MUSHROOM = register("potted_red_mushroom", new BlockFlowerPot(Blocks.RED_MUSHROOM, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_BROWN_MUSHROOM = register("potted_brown_mushroom", new BlockFlowerPot(Blocks.BROWN_MUSHROOM, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_DEAD_BUSH = register("potted_dead_bush", new BlockFlowerPot(Blocks.DEAD_BUSH, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_CACTUS = register("potted_cactus", new BlockFlowerPot(Blocks.CACTUS, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block CARROTS = register("carrots", new BlockCarrots(BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.CROP)));
    public static final Block POTATOES = register("potatoes", new BlockPotatoes(BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.CROP)));
    public static final Block OAK_BUTTON = register("oak_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_BUTTON = register("spruce_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_BUTTON = register("birch_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_BUTTON = register("jungle_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_BUTTON = register("acacia_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_BUTTON = register("dark_oak_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block SKELETON_SKULL = register("skeleton_skull", new BlockSkull(BlockSkull.Type.SKELETON, BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block SKELETON_WALL_SKULL = register("skeleton_wall_skull", new BlockSkullWall(BlockSkull.Type.SKELETON, BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.SKELETON_SKULL)));
    public static final Block WITHER_SKELETON_SKULL = register("wither_skeleton_skull", new BlockWitherSkull(BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block WITHER_SKELETON_WALL_SKULL = register("wither_skeleton_wall_skull", new BlockWitherSkullWall(BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.WITHER_SKELETON_SKULL)));
    public static final Block ZOMBIE_HEAD = register("zombie_head", new BlockSkull(BlockSkull.Type.ZOMBIE, BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block ZOMBIE_WALL_HEAD = register("zombie_wall_head", new BlockSkullWall(BlockSkull.Type.ZOMBIE, BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.ZOMBIE_HEAD)));
    public static final Block PLAYER_HEAD = register("player_head", new BlockSkullPlayer(BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block PLAYER_WALL_HEAD = register("player_wall_head", new BlockSkullPlayerWall(BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.PLAYER_HEAD)));
    public static final Block CREEPER_HEAD = register("creeper_head", new BlockSkull(BlockSkull.Type.CREEPER, BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block CREEPER_WALL_HEAD = register("creeper_wall_head", new BlockSkullWall(BlockSkull.Type.CREEPER, BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.CREEPER_HEAD)));
    public static final Block DRAGON_HEAD = register("dragon_head", new BlockSkull(BlockSkull.Type.DRAGON, BlockBase.Info.of(Material.DECORATION).strength(1.0F)));
    public static final Block DRAGON_WALL_HEAD = register("dragon_wall_head", new BlockSkullWall(BlockSkull.Type.DRAGON, BlockBase.Info.of(Material.DECORATION).strength(1.0F).dropsLike(Blocks.DRAGON_HEAD)));
    public static final Block ANVIL = register("anvil", new BlockAnvil(BlockBase.Info.of(Material.HEAVY_METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundEffectType.ANVIL)));
    public static final Block CHIPPED_ANVIL = register("chipped_anvil", new BlockAnvil(BlockBase.Info.of(Material.HEAVY_METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundEffectType.ANVIL)));
    public static final Block DAMAGED_ANVIL = register("damaged_anvil", new BlockAnvil(BlockBase.Info.of(Material.HEAVY_METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundEffectType.ANVIL)));
    public static final Block TRAPPED_CHEST = register("trapped_chest", new BlockChestTrapped(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = register("light_weighted_pressure_plate", new BlockPressurePlateWeighted(15, BlockBase.Info.of(Material.METAL, MaterialMapColor.GOLD).requiresCorrectToolForDrops().noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = register("heavy_weighted_pressure_plate", new BlockPressurePlateWeighted(150, BlockBase.Info.of(Material.METAL).requiresCorrectToolForDrops().noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block COMPARATOR = register("comparator", new BlockRedstoneComparator(BlockBase.Info.of(Material.DECORATION).instabreak().sound(SoundEffectType.WOOD)));
    public static final Block DAYLIGHT_DETECTOR = register("daylight_detector", new BlockDaylightDetector(BlockBase.Info.of(Material.WOOD).strength(0.2F).sound(SoundEffectType.WOOD)));
    public static final Block REDSTONE_BLOCK = register("redstone_block", new BlockPowered(BlockBase.Info.of(Material.METAL, MaterialMapColor.FIRE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundEffectType.METAL).isRedstoneConductor(Blocks::never)));
    public static final Block NETHER_QUARTZ_ORE = register("nether_quartz_ore", new BlockOre(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundEffectType.NETHER_ORE), UniformInt.of(2, 5)));
    public static final Block HOPPER = register("hopper", new BlockHopper(BlockBase.Info.of(Material.METAL, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 4.8F).sound(SoundEffectType.METAL).noOcclusion()));
    public static final Block QUARTZ_BLOCK = register("quartz_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block CHISELED_QUARTZ_BLOCK = register("chiseled_quartz_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block QUARTZ_PILLAR = register("quartz_pillar", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block QUARTZ_STAIRS = register("quartz_stairs", new BlockStairs(Blocks.QUARTZ_BLOCK.defaultBlockState(), BlockBase.Info.copy(Blocks.QUARTZ_BLOCK)));
    public static final Block ACTIVATOR_RAIL = register("activator_rail", new BlockPoweredRail(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundEffectType.METAL)));
    public static final Block DROPPER = register("dropper", new BlockDropper(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F)));
    public static final Block WHITE_TERRACOTTA = register("white_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block ORANGE_TERRACOTTA = register("orange_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_ORANGE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block MAGENTA_TERRACOTTA = register("magenta_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_MAGENTA).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block LIGHT_BLUE_TERRACOTTA = register("light_blue_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block YELLOW_TERRACOTTA = register("yellow_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_YELLOW).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block LIME_TERRACOTTA = register("lime_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_GREEN).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block PINK_TERRACOTTA = register("pink_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_PINK).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block GRAY_TERRACOTTA = register("gray_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_GRAY).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block LIGHT_GRAY_TERRACOTTA = register("light_gray_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block CYAN_TERRACOTTA = register("cyan_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_CYAN).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block PURPLE_TERRACOTTA = register("purple_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_PURPLE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block BLUE_TERRACOTTA = register("blue_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_BLUE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block BROWN_TERRACOTTA = register("brown_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block GREEN_TERRACOTTA = register("green_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_GREEN).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block RED_TERRACOTTA = register("red_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_RED).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block BLACK_TERRACOTTA = register("black_terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_BLACK).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block WHITE_STAINED_GLASS_PANE = register("white_stained_glass_pane", new BlockStainedGlassPane(EnumColor.WHITE, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block ORANGE_STAINED_GLASS_PANE = register("orange_stained_glass_pane", new BlockStainedGlassPane(EnumColor.ORANGE, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block MAGENTA_STAINED_GLASS_PANE = register("magenta_stained_glass_pane", new BlockStainedGlassPane(EnumColor.MAGENTA, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = register("light_blue_stained_glass_pane", new BlockStainedGlassPane(EnumColor.LIGHT_BLUE, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block YELLOW_STAINED_GLASS_PANE = register("yellow_stained_glass_pane", new BlockStainedGlassPane(EnumColor.YELLOW, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block LIME_STAINED_GLASS_PANE = register("lime_stained_glass_pane", new BlockStainedGlassPane(EnumColor.LIME, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block PINK_STAINED_GLASS_PANE = register("pink_stained_glass_pane", new BlockStainedGlassPane(EnumColor.PINK, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block GRAY_STAINED_GLASS_PANE = register("gray_stained_glass_pane", new BlockStainedGlassPane(EnumColor.GRAY, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = register("light_gray_stained_glass_pane", new BlockStainedGlassPane(EnumColor.LIGHT_GRAY, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block CYAN_STAINED_GLASS_PANE = register("cyan_stained_glass_pane", new BlockStainedGlassPane(EnumColor.CYAN, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block PURPLE_STAINED_GLASS_PANE = register("purple_stained_glass_pane", new BlockStainedGlassPane(EnumColor.PURPLE, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block BLUE_STAINED_GLASS_PANE = register("blue_stained_glass_pane", new BlockStainedGlassPane(EnumColor.BLUE, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block BROWN_STAINED_GLASS_PANE = register("brown_stained_glass_pane", new BlockStainedGlassPane(EnumColor.BROWN, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block GREEN_STAINED_GLASS_PANE = register("green_stained_glass_pane", new BlockStainedGlassPane(EnumColor.GREEN, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block RED_STAINED_GLASS_PANE = register("red_stained_glass_pane", new BlockStainedGlassPane(EnumColor.RED, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block BLACK_STAINED_GLASS_PANE = register("black_stained_glass_pane", new BlockStainedGlassPane(EnumColor.BLACK, BlockBase.Info.of(Material.GLASS).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion()));
    public static final Block ACACIA_STAIRS = register("acacia_stairs", new BlockStairs(Blocks.ACACIA_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.ACACIA_PLANKS)));
    public static final Block DARK_OAK_STAIRS = register("dark_oak_stairs", new BlockStairs(Blocks.DARK_OAK_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.DARK_OAK_PLANKS)));
    public static final Block SLIME_BLOCK = register("slime_block", new BlockSlime(BlockBase.Info.of(Material.CLAY, MaterialMapColor.GRASS).friction(0.8F).sound(SoundEffectType.SLIME_BLOCK).noOcclusion()));
    public static final Block BARRIER = register("barrier", new BlockBarrier(BlockBase.Info.of(Material.BARRIER).strength(-1.0F, 3600000.8F).noDrops().noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block LIGHT = register("light", new LightBlock(BlockBase.Info.of(Material.AIR).strength(-1.0F, 3600000.8F).noDrops().noOcclusion().lightLevel(LightBlock.LIGHT_EMISSION)));
    public static final Block IRON_TRAPDOOR = register("iron_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundEffectType.METAL).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block PRISMARINE = register("prismarine", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block PRISMARINE_BRICKS = register("prismarine_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIAMOND).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DARK_PRISMARINE = register("dark_prismarine", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIAMOND).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block PRISMARINE_STAIRS = register("prismarine_stairs", new BlockStairs(Blocks.PRISMARINE.defaultBlockState(), BlockBase.Info.copy(Blocks.PRISMARINE)));
    public static final Block PRISMARINE_BRICK_STAIRS = register("prismarine_brick_stairs", new BlockStairs(Blocks.PRISMARINE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.PRISMARINE_BRICKS)));
    public static final Block DARK_PRISMARINE_STAIRS = register("dark_prismarine_stairs", new BlockStairs(Blocks.DARK_PRISMARINE.defaultBlockState(), BlockBase.Info.copy(Blocks.DARK_PRISMARINE)));
    public static final Block PRISMARINE_SLAB = register("prismarine_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block PRISMARINE_BRICK_SLAB = register("prismarine_brick_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIAMOND).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DARK_PRISMARINE_SLAB = register("dark_prismarine_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.DIAMOND).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block SEA_LANTERN = register("sea_lantern", new Block(BlockBase.Info.of(Material.GLASS, MaterialMapColor.QUARTZ).strength(0.3F).sound(SoundEffectType.GLASS).lightLevel((iblockdata) -> {
        return 15;
    })));
    public static final Block HAY_BLOCK = register("hay_block", new BlockHay(BlockBase.Info.of(Material.GRASS, MaterialMapColor.COLOR_YELLOW).strength(0.5F).sound(SoundEffectType.GRASS)));
    public static final Block WHITE_CARPET = register("white_carpet", new BlockCarpet(EnumColor.WHITE, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.SNOW).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block ORANGE_CARPET = register("orange_carpet", new BlockCarpet(EnumColor.ORANGE, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_ORANGE).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block MAGENTA_CARPET = register("magenta_carpet", new BlockCarpet(EnumColor.MAGENTA, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_MAGENTA).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block LIGHT_BLUE_CARPET = register("light_blue_carpet", new BlockCarpet(EnumColor.LIGHT_BLUE, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_BLUE).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block YELLOW_CARPET = register("yellow_carpet", new BlockCarpet(EnumColor.YELLOW, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_YELLOW).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block LIME_CARPET = register("lime_carpet", new BlockCarpet(EnumColor.LIME, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_GREEN).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block PINK_CARPET = register("pink_carpet", new BlockCarpet(EnumColor.PINK, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_PINK).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block GRAY_CARPET = register("gray_carpet", new BlockCarpet(EnumColor.GRAY, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_GRAY).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block LIGHT_GRAY_CARPET = register("light_gray_carpet", new BlockCarpet(EnumColor.LIGHT_GRAY, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_GRAY).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block CYAN_CARPET = register("cyan_carpet", new BlockCarpet(EnumColor.CYAN, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_CYAN).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block PURPLE_CARPET = register("purple_carpet", new BlockCarpet(EnumColor.PURPLE, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_PURPLE).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block BLUE_CARPET = register("blue_carpet", new BlockCarpet(EnumColor.BLUE, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BLUE).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block BROWN_CARPET = register("brown_carpet", new BlockCarpet(EnumColor.BROWN, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BROWN).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block GREEN_CARPET = register("green_carpet", new BlockCarpet(EnumColor.GREEN, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_GREEN).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block RED_CARPET = register("red_carpet", new BlockCarpet(EnumColor.RED, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_RED).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block BLACK_CARPET = register("black_carpet", new BlockCarpet(EnumColor.BLACK, BlockBase.Info.of(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BLACK).strength(0.1F).sound(SoundEffectType.WOOL)));
    public static final Block TERRACOTTA = register("terracotta", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(1.25F, 4.2F)));
    public static final Block COAL_BLOCK = register("coal_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
    public static final Block PACKED_ICE = register("packed_ice", new Block(BlockBase.Info.of(Material.ICE_SOLID).friction(0.98F).strength(0.5F).sound(SoundEffectType.GLASS)));
    public static final Block SUNFLOWER = register("sunflower", new BlockTallPlantFlower(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block LILAC = register("lilac", new BlockTallPlantFlower(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block ROSE_BUSH = register("rose_bush", new BlockTallPlantFlower(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block PEONY = register("peony", new BlockTallPlantFlower(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block TALL_GRASS = register("tall_grass", new BlockTallPlant(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block LARGE_FERN = register("large_fern", new BlockTallPlant(BlockBase.Info.of(Material.REPLACEABLE_PLANT).noCollission().instabreak().sound(SoundEffectType.GRASS)));
    public static final Block WHITE_BANNER = register("white_banner", new BlockBanner(EnumColor.WHITE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block ORANGE_BANNER = register("orange_banner", new BlockBanner(EnumColor.ORANGE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block MAGENTA_BANNER = register("magenta_banner", new BlockBanner(EnumColor.MAGENTA, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block LIGHT_BLUE_BANNER = register("light_blue_banner", new BlockBanner(EnumColor.LIGHT_BLUE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block YELLOW_BANNER = register("yellow_banner", new BlockBanner(EnumColor.YELLOW, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block LIME_BANNER = register("lime_banner", new BlockBanner(EnumColor.LIME, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block PINK_BANNER = register("pink_banner", new BlockBanner(EnumColor.PINK, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block GRAY_BANNER = register("gray_banner", new BlockBanner(EnumColor.GRAY, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block LIGHT_GRAY_BANNER = register("light_gray_banner", new BlockBanner(EnumColor.LIGHT_GRAY, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block CYAN_BANNER = register("cyan_banner", new BlockBanner(EnumColor.CYAN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block PURPLE_BANNER = register("purple_banner", new BlockBanner(EnumColor.PURPLE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block BLUE_BANNER = register("blue_banner", new BlockBanner(EnumColor.BLUE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block BROWN_BANNER = register("brown_banner", new BlockBanner(EnumColor.BROWN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block GREEN_BANNER = register("green_banner", new BlockBanner(EnumColor.GREEN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block RED_BANNER = register("red_banner", new BlockBanner(EnumColor.RED, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block BLACK_BANNER = register("black_banner", new BlockBanner(EnumColor.BLACK, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD)));
    public static final Block WHITE_WALL_BANNER = register("white_wall_banner", new BlockBannerWall(EnumColor.WHITE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.WHITE_BANNER)));
    public static final Block ORANGE_WALL_BANNER = register("orange_wall_banner", new BlockBannerWall(EnumColor.ORANGE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.ORANGE_BANNER)));
    public static final Block MAGENTA_WALL_BANNER = register("magenta_wall_banner", new BlockBannerWall(EnumColor.MAGENTA, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.MAGENTA_BANNER)));
    public static final Block LIGHT_BLUE_WALL_BANNER = register("light_blue_wall_banner", new BlockBannerWall(EnumColor.LIGHT_BLUE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.LIGHT_BLUE_BANNER)));
    public static final Block YELLOW_WALL_BANNER = register("yellow_wall_banner", new BlockBannerWall(EnumColor.YELLOW, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.YELLOW_BANNER)));
    public static final Block LIME_WALL_BANNER = register("lime_wall_banner", new BlockBannerWall(EnumColor.LIME, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.LIME_BANNER)));
    public static final Block PINK_WALL_BANNER = register("pink_wall_banner", new BlockBannerWall(EnumColor.PINK, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.PINK_BANNER)));
    public static final Block GRAY_WALL_BANNER = register("gray_wall_banner", new BlockBannerWall(EnumColor.GRAY, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.GRAY_BANNER)));
    public static final Block LIGHT_GRAY_WALL_BANNER = register("light_gray_wall_banner", new BlockBannerWall(EnumColor.LIGHT_GRAY, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.LIGHT_GRAY_BANNER)));
    public static final Block CYAN_WALL_BANNER = register("cyan_wall_banner", new BlockBannerWall(EnumColor.CYAN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.CYAN_BANNER)));
    public static final Block PURPLE_WALL_BANNER = register("purple_wall_banner", new BlockBannerWall(EnumColor.PURPLE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.PURPLE_BANNER)));
    public static final Block BLUE_WALL_BANNER = register("blue_wall_banner", new BlockBannerWall(EnumColor.BLUE, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.BLUE_BANNER)));
    public static final Block BROWN_WALL_BANNER = register("brown_wall_banner", new BlockBannerWall(EnumColor.BROWN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.BROWN_BANNER)));
    public static final Block GREEN_WALL_BANNER = register("green_wall_banner", new BlockBannerWall(EnumColor.GREEN, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.GREEN_BANNER)));
    public static final Block RED_WALL_BANNER = register("red_wall_banner", new BlockBannerWall(EnumColor.RED, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.RED_BANNER)));
    public static final Block BLACK_WALL_BANNER = register("black_wall_banner", new BlockBannerWall(EnumColor.BLACK, BlockBase.Info.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.BLACK_BANNER)));
    public static final Block RED_SANDSTONE = register("red_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block CHISELED_RED_SANDSTONE = register("chiseled_red_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block CUT_RED_SANDSTONE = register("cut_red_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(0.8F)));
    public static final Block RED_SANDSTONE_STAIRS = register("red_sandstone_stairs", new BlockStairs(Blocks.RED_SANDSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.RED_SANDSTONE)));
    public static final Block OAK_SLAB = register("oak_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.WOOD).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_SLAB = register("spruce_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_SLAB = register("birch_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.SAND).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_SLAB = register("jungle_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.DIRT).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_SLAB = register("acacia_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_ORANGE).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_SLAB = register("dark_oak_slab", new BlockStepAbstract(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_BROWN).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block STONE_SLAB = register("stone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SMOOTH_STONE_SLAB = register("smooth_stone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SANDSTONE_SLAB = register("sandstone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block CUT_SANDSTONE_SLAB = register("cut_sandstone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block PETRIFIED_OAK_SLAB = register("petrified_oak_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.WOOD).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block COBBLESTONE_SLAB = register("cobblestone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block BRICK_SLAB = register("brick_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block STONE_BRICK_SLAB = register("stone_brick_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block NETHER_BRICK_SLAB = register("nether_brick_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block QUARTZ_SLAB = register("quartz_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block RED_SANDSTONE_SLAB = register("red_sandstone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block CUT_RED_SANDSTONE_SLAB = register("cut_red_sandstone_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block PURPUR_SLAB = register("purpur_slab", new BlockStepAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_MAGENTA).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SMOOTH_STONE = register("smooth_stone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SMOOTH_SANDSTONE = register("smooth_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SMOOTH_QUARTZ = register("smooth_quartz", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.QUARTZ).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SMOOTH_RED_SANDSTONE = register("smooth_red_sandstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Block SPRUCE_FENCE_GATE = register("spruce_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_FENCE_GATE = register("birch_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_FENCE_GATE = register("jungle_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_FENCE_GATE = register("acacia_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_FENCE_GATE = register("dark_oak_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_FENCE = register("spruce_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block BIRCH_FENCE = register("birch_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block JUNGLE_FENCE = register("jungle_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block ACACIA_FENCE = register("acacia_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_FENCE = register("dark_oak_fence", new BlockFence(BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block SPRUCE_DOOR = register("spruce_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block BIRCH_DOOR = register("birch_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block JUNGLE_DOOR = register("jungle_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.JUNGLE_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block ACACIA_DOOR = register("acacia_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block DARK_OAK_DOOR = register("dark_oak_door", new BlockDoor(BlockBase.Info.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block END_ROD = register("end_rod", new BlockEndRod(BlockBase.Info.of(Material.DECORATION).instabreak().lightLevel((iblockdata) -> {
        return 14;
    }).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block CHORUS_PLANT = register("chorus_plant", new BlockChorusFruit(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_PURPLE).strength(0.4F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block CHORUS_FLOWER = register("chorus_flower", new BlockChorusFlower((BlockChorusFruit) Blocks.CHORUS_PLANT, BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_PURPLE).randomTicks().strength(0.4F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block PURPUR_BLOCK = register("purpur_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_MAGENTA).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block PURPUR_PILLAR = register("purpur_pillar", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_MAGENTA).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block PURPUR_STAIRS = register("purpur_stairs", new BlockStairs(Blocks.PURPUR_BLOCK.defaultBlockState(), BlockBase.Info.copy(Blocks.PURPUR_BLOCK)));
    public static final Block END_STONE_BRICKS = register("end_stone_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(3.0F, 9.0F)));
    public static final Block BEETROOTS = register("beetroots", new BlockBeetroot(BlockBase.Info.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.CROP)));
    public static final Block DIRT_PATH = register("dirt_path", new BlockGrassPath(BlockBase.Info.of(Material.DIRT).strength(0.65F).sound(SoundEffectType.GRASS).isViewBlocking(Blocks::always).isSuffocating(Blocks::always)));
    public static final Block END_GATEWAY = register("end_gateway", new BlockEndGateway(BlockBase.Info.of(Material.PORTAL, MaterialMapColor.COLOR_BLACK).noCollission().lightLevel((iblockdata) -> {
        return 15;
    }).strength(-1.0F, 3600000.0F).noDrops()));
    public static final Block REPEATING_COMMAND_BLOCK = register("repeating_command_block", new BlockCommand(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops(), false));
    public static final Block CHAIN_COMMAND_BLOCK = register("chain_command_block", new BlockCommand(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_GREEN).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops(), true));
    public static final Block FROSTED_ICE = register("frosted_ice", new BlockIceFrost(BlockBase.Info.of(Material.ICE).friction(0.98F).randomTicks().strength(0.5F).sound(SoundEffectType.GLASS).noOcclusion().isValidSpawn((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    })));
    public static final Block MAGMA_BLOCK = register("magma_block", new BlockMagma(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().lightLevel((iblockdata) -> {
        return 3;
    }).randomTicks().strength(0.5F).isValidSpawn((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes.fireImmune();
    }).hasPostProcess(Blocks::always).emissiveRendering(Blocks::always)));
    public static final Block NETHER_WART_BLOCK = register("nether_wart_block", new Block(BlockBase.Info.of(Material.GRASS, MaterialMapColor.COLOR_RED).strength(1.0F).sound(SoundEffectType.WART_BLOCK)));
    public static final Block RED_NETHER_BRICKS = register("red_nether_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block BONE_BLOCK = register("bone_block", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.SAND).requiresCorrectToolForDrops().strength(2.0F).sound(SoundEffectType.BONE_BLOCK)));
    public static final Block STRUCTURE_VOID = register("structure_void", new BlockStructureVoid(BlockBase.Info.of(Material.STRUCTURAL_AIR).noCollission().noDrops()));
    public static final Block OBSERVER = register("observer", new BlockObserver(BlockBase.Info.of(Material.STONE).strength(3.0F).requiresCorrectToolForDrops().isRedstoneConductor(Blocks::never)));
    public static final Block SHULKER_BOX = register("shulker_box", shulkerBox((EnumColor) null, BlockBase.Info.of(Material.SHULKER_SHELL)));
    public static final Block WHITE_SHULKER_BOX = register("white_shulker_box", shulkerBox(EnumColor.WHITE, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.SNOW)));
    public static final Block ORANGE_SHULKER_BOX = register("orange_shulker_box", shulkerBox(EnumColor.ORANGE, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_ORANGE)));
    public static final Block MAGENTA_SHULKER_BOX = register("magenta_shulker_box", shulkerBox(EnumColor.MAGENTA, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_MAGENTA)));
    public static final Block LIGHT_BLUE_SHULKER_BOX = register("light_blue_shulker_box", shulkerBox(EnumColor.LIGHT_BLUE, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_BLUE)));
    public static final Block YELLOW_SHULKER_BOX = register("yellow_shulker_box", shulkerBox(EnumColor.YELLOW, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_YELLOW)));
    public static final Block LIME_SHULKER_BOX = register("lime_shulker_box", shulkerBox(EnumColor.LIME, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_GREEN)));
    public static final Block PINK_SHULKER_BOX = register("pink_shulker_box", shulkerBox(EnumColor.PINK, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_PINK)));
    public static final Block GRAY_SHULKER_BOX = register("gray_shulker_box", shulkerBox(EnumColor.GRAY, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_GRAY)));
    public static final Block LIGHT_GRAY_SHULKER_BOX = register("light_gray_shulker_box", shulkerBox(EnumColor.LIGHT_GRAY, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_GRAY)));
    public static final Block CYAN_SHULKER_BOX = register("cyan_shulker_box", shulkerBox(EnumColor.CYAN, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_CYAN)));
    public static final Block PURPLE_SHULKER_BOX = register("purple_shulker_box", shulkerBox(EnumColor.PURPLE, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.TERRACOTTA_PURPLE)));
    public static final Block BLUE_SHULKER_BOX = register("blue_shulker_box", shulkerBox(EnumColor.BLUE, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BLUE)));
    public static final Block BROWN_SHULKER_BOX = register("brown_shulker_box", shulkerBox(EnumColor.BROWN, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BROWN)));
    public static final Block GREEN_SHULKER_BOX = register("green_shulker_box", shulkerBox(EnumColor.GREEN, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_GREEN)));
    public static final Block RED_SHULKER_BOX = register("red_shulker_box", shulkerBox(EnumColor.RED, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_RED)));
    public static final Block BLACK_SHULKER_BOX = register("black_shulker_box", shulkerBox(EnumColor.BLACK, BlockBase.Info.of(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BLACK)));
    public static final Block WHITE_GLAZED_TERRACOTTA = register("white_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.WHITE).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block ORANGE_GLAZED_TERRACOTTA = register("orange_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.ORANGE).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block MAGENTA_GLAZED_TERRACOTTA = register("magenta_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.MAGENTA).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA = register("light_blue_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block YELLOW_GLAZED_TERRACOTTA = register("yellow_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.YELLOW).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block LIME_GLAZED_TERRACOTTA = register("lime_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.LIME).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block PINK_GLAZED_TERRACOTTA = register("pink_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.PINK).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block GRAY_GLAZED_TERRACOTTA = register("gray_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.GRAY).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA = register("light_gray_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block CYAN_GLAZED_TERRACOTTA = register("cyan_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.CYAN).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block PURPLE_GLAZED_TERRACOTTA = register("purple_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.PURPLE).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block BLUE_GLAZED_TERRACOTTA = register("blue_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.BLUE).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block BROWN_GLAZED_TERRACOTTA = register("brown_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.BROWN).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block GREEN_GLAZED_TERRACOTTA = register("green_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.GREEN).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block RED_GLAZED_TERRACOTTA = register("red_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.RED).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block BLACK_GLAZED_TERRACOTTA = register("black_glazed_terracotta", new BlockGlazedTerracotta(BlockBase.Info.of(Material.STONE, EnumColor.BLACK).requiresCorrectToolForDrops().strength(1.4F)));
    public static final Block WHITE_CONCRETE = register("white_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.WHITE).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block ORANGE_CONCRETE = register("orange_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.ORANGE).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block MAGENTA_CONCRETE = register("magenta_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.MAGENTA).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block LIGHT_BLUE_CONCRETE = register("light_blue_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block YELLOW_CONCRETE = register("yellow_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.YELLOW).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block LIME_CONCRETE = register("lime_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.LIME).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block PINK_CONCRETE = register("pink_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.PINK).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block GRAY_CONCRETE = register("gray_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.GRAY).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block LIGHT_GRAY_CONCRETE = register("light_gray_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block CYAN_CONCRETE = register("cyan_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.CYAN).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block PURPLE_CONCRETE = register("purple_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.PURPLE).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block BLUE_CONCRETE = register("blue_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.BLUE).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block BROWN_CONCRETE = register("brown_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.BROWN).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block GREEN_CONCRETE = register("green_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.GREEN).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block RED_CONCRETE = register("red_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.RED).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block BLACK_CONCRETE = register("black_concrete", new Block(BlockBase.Info.of(Material.STONE, EnumColor.BLACK).requiresCorrectToolForDrops().strength(1.8F)));
    public static final Block WHITE_CONCRETE_POWDER = register("white_concrete_powder", new BlockConcretePowder(Blocks.WHITE_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.WHITE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block ORANGE_CONCRETE_POWDER = register("orange_concrete_powder", new BlockConcretePowder(Blocks.ORANGE_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.ORANGE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block MAGENTA_CONCRETE_POWDER = register("magenta_concrete_powder", new BlockConcretePowder(Blocks.MAGENTA_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.MAGENTA).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = register("light_blue_concrete_powder", new BlockConcretePowder(Blocks.LIGHT_BLUE_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.LIGHT_BLUE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block YELLOW_CONCRETE_POWDER = register("yellow_concrete_powder", new BlockConcretePowder(Blocks.YELLOW_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.YELLOW).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block LIME_CONCRETE_POWDER = register("lime_concrete_powder", new BlockConcretePowder(Blocks.LIME_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.LIME).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block PINK_CONCRETE_POWDER = register("pink_concrete_powder", new BlockConcretePowder(Blocks.PINK_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.PINK).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block GRAY_CONCRETE_POWDER = register("gray_concrete_powder", new BlockConcretePowder(Blocks.GRAY_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.GRAY).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = register("light_gray_concrete_powder", new BlockConcretePowder(Blocks.LIGHT_GRAY_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.LIGHT_GRAY).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block CYAN_CONCRETE_POWDER = register("cyan_concrete_powder", new BlockConcretePowder(Blocks.CYAN_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.CYAN).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block PURPLE_CONCRETE_POWDER = register("purple_concrete_powder", new BlockConcretePowder(Blocks.PURPLE_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.PURPLE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block BLUE_CONCRETE_POWDER = register("blue_concrete_powder", new BlockConcretePowder(Blocks.BLUE_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.BLUE).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block BROWN_CONCRETE_POWDER = register("brown_concrete_powder", new BlockConcretePowder(Blocks.BROWN_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.BROWN).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block GREEN_CONCRETE_POWDER = register("green_concrete_powder", new BlockConcretePowder(Blocks.GREEN_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.GREEN).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block RED_CONCRETE_POWDER = register("red_concrete_powder", new BlockConcretePowder(Blocks.RED_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.RED).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block BLACK_CONCRETE_POWDER = register("black_concrete_powder", new BlockConcretePowder(Blocks.BLACK_CONCRETE, BlockBase.Info.of(Material.SAND, EnumColor.BLACK).strength(0.5F).sound(SoundEffectType.SAND)));
    public static final Block KELP = register("kelp", new BlockKelp(BlockBase.Info.of(Material.WATER_PLANT).noCollission().randomTicks().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block KELP_PLANT = register("kelp_plant", new BlockKelpPlant(BlockBase.Info.of(Material.WATER_PLANT).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block DRIED_KELP_BLOCK = register("dried_kelp_block", new Block(BlockBase.Info.of(Material.GRASS, MaterialMapColor.COLOR_GREEN).strength(0.5F, 2.5F).sound(SoundEffectType.GRASS)));
    public static final Block TURTLE_EGG = register("turtle_egg", new BlockTurtleEgg(BlockBase.Info.of(Material.EGG, MaterialMapColor.SAND).strength(0.5F).sound(SoundEffectType.METAL).randomTicks().noOcclusion()));
    public static final Block DEAD_TUBE_CORAL_BLOCK = register("dead_tube_coral_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DEAD_BRAIN_CORAL_BLOCK = register("dead_brain_coral_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DEAD_BUBBLE_CORAL_BLOCK = register("dead_bubble_coral_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DEAD_FIRE_CORAL_BLOCK = register("dead_fire_coral_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block DEAD_HORN_CORAL_BLOCK = register("dead_horn_coral_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block TUBE_CORAL_BLOCK = register("tube_coral_block", new BlockCoral(Blocks.DEAD_TUBE_CORAL_BLOCK, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block BRAIN_CORAL_BLOCK = register("brain_coral_block", new BlockCoral(Blocks.DEAD_BRAIN_CORAL_BLOCK, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_PINK).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block BUBBLE_CORAL_BLOCK = register("bubble_coral_block", new BlockCoral(Blocks.DEAD_BUBBLE_CORAL_BLOCK, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block FIRE_CORAL_BLOCK = register("fire_coral_block", new BlockCoral(Blocks.DEAD_FIRE_CORAL_BLOCK, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_RED).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block HORN_CORAL_BLOCK = register("horn_coral_block", new BlockCoral(Blocks.DEAD_HORN_CORAL_BLOCK, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block DEAD_TUBE_CORAL = register("dead_tube_coral", new BlockCoralDead(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_BRAIN_CORAL = register("dead_brain_coral", new BlockCoralDead(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_BUBBLE_CORAL = register("dead_bubble_coral", new BlockCoralDead(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_FIRE_CORAL = register("dead_fire_coral", new BlockCoralDead(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_HORN_CORAL = register("dead_horn_coral", new BlockCoralDead(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block TUBE_CORAL = register("tube_coral", new BlockCoralPlant(Blocks.DEAD_TUBE_CORAL, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block BRAIN_CORAL = register("brain_coral", new BlockCoralPlant(Blocks.DEAD_BRAIN_CORAL, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block BUBBLE_CORAL = register("bubble_coral", new BlockCoralPlant(Blocks.DEAD_BUBBLE_CORAL, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block FIRE_CORAL = register("fire_coral", new BlockCoralPlant(Blocks.DEAD_FIRE_CORAL, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block HORN_CORAL = register("horn_coral", new BlockCoralPlant(Blocks.DEAD_HORN_CORAL, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block DEAD_TUBE_CORAL_FAN = register("dead_tube_coral_fan", new BlockCoralFanAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_BRAIN_CORAL_FAN = register("dead_brain_coral_fan", new BlockCoralFanAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_BUBBLE_CORAL_FAN = register("dead_bubble_coral_fan", new BlockCoralFanAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_FIRE_CORAL_FAN = register("dead_fire_coral_fan", new BlockCoralFanAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block DEAD_HORN_CORAL_FAN = register("dead_horn_coral_fan", new BlockCoralFanAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak()));
    public static final Block TUBE_CORAL_FAN = register("tube_coral_fan", new BlockCoralFan(Blocks.DEAD_TUBE_CORAL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block BRAIN_CORAL_FAN = register("brain_coral_fan", new BlockCoralFan(Blocks.DEAD_BRAIN_CORAL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block BUBBLE_CORAL_FAN = register("bubble_coral_fan", new BlockCoralFan(Blocks.DEAD_BUBBLE_CORAL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block FIRE_CORAL_FAN = register("fire_coral_fan", new BlockCoralFan(Blocks.DEAD_FIRE_CORAL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block HORN_CORAL_FAN = register("horn_coral_fan", new BlockCoralFan(Blocks.DEAD_HORN_CORAL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundEffectType.WET_GRASS)));
    public static final Block DEAD_TUBE_CORAL_WALL_FAN = register("dead_tube_coral_wall_fan", new BlockCoralFanWallAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(Blocks.DEAD_TUBE_CORAL_FAN)));
    public static final Block DEAD_BRAIN_CORAL_WALL_FAN = register("dead_brain_coral_wall_fan", new BlockCoralFanWallAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(Blocks.DEAD_BRAIN_CORAL_FAN)));
    public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = register("dead_bubble_coral_wall_fan", new BlockCoralFanWallAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(Blocks.DEAD_BUBBLE_CORAL_FAN)));
    public static final Block DEAD_FIRE_CORAL_WALL_FAN = register("dead_fire_coral_wall_fan", new BlockCoralFanWallAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(Blocks.DEAD_FIRE_CORAL_FAN)));
    public static final Block DEAD_HORN_CORAL_WALL_FAN = register("dead_horn_coral_wall_fan", new BlockCoralFanWallAbstract(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(Blocks.DEAD_HORN_CORAL_FAN)));
    public static final Block TUBE_CORAL_WALL_FAN = register("tube_coral_wall_fan", new BlockCoralFanWall(Blocks.DEAD_TUBE_CORAL_WALL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS).dropsLike(Blocks.TUBE_CORAL_FAN)));
    public static final Block BRAIN_CORAL_WALL_FAN = register("brain_coral_wall_fan", new BlockCoralFanWall(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).noCollission().instabreak().sound(SoundEffectType.WET_GRASS).dropsLike(Blocks.BRAIN_CORAL_FAN)));
    public static final Block BUBBLE_CORAL_WALL_FAN = register("bubble_coral_wall_fan", new BlockCoralFanWall(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundEffectType.WET_GRASS).dropsLike(Blocks.BUBBLE_CORAL_FAN)));
    public static final Block FIRE_CORAL_WALL_FAN = register("fire_coral_wall_fan", new BlockCoralFanWall(Blocks.DEAD_FIRE_CORAL_WALL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).noCollission().instabreak().sound(SoundEffectType.WET_GRASS).dropsLike(Blocks.FIRE_CORAL_FAN)));
    public static final Block HORN_CORAL_WALL_FAN = register("horn_coral_wall_fan", new BlockCoralFanWall(Blocks.DEAD_HORN_CORAL_WALL_FAN, BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundEffectType.WET_GRASS).dropsLike(Blocks.HORN_CORAL_FAN)));
    public static final Block SEA_PICKLE = register("sea_pickle", new BlockSeaPickle(BlockBase.Info.of(Material.WATER_PLANT, MaterialMapColor.COLOR_GREEN).lightLevel((iblockdata) -> {
        return BlockSeaPickle.isDead(iblockdata) ? 0 : 3 + 3 * (Integer) iblockdata.getValue(BlockSeaPickle.PICKLES);
    }).sound(SoundEffectType.SLIME_BLOCK).noOcclusion()));
    public static final Block BLUE_ICE = register("blue_ice", new BlockHalfTransparent(BlockBase.Info.of(Material.ICE_SOLID).strength(2.8F).friction(0.989F).sound(SoundEffectType.GLASS)));
    public static final Block CONDUIT = register("conduit", new BlockConduit(BlockBase.Info.of(Material.GLASS, MaterialMapColor.DIAMOND).strength(3.0F).lightLevel((iblockdata) -> {
        return 15;
    }).noOcclusion()));
    public static final Block BAMBOO_SAPLING = register("bamboo_sapling", new BlockBambooSapling(BlockBase.Info.of(Material.BAMBOO_SAPLING).randomTicks().instabreak().noCollission().strength(1.0F).sound(SoundEffectType.BAMBOO_SAPLING)));
    public static final Block BAMBOO = register("bamboo", new BlockBamboo(BlockBase.Info.of(Material.BAMBOO, MaterialMapColor.PLANT).randomTicks().instabreak().strength(1.0F).sound(SoundEffectType.BAMBOO).noOcclusion().dynamicShape()));
    public static final Block POTTED_BAMBOO = register("potted_bamboo", new BlockFlowerPot(Blocks.BAMBOO, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block VOID_AIR = register("void_air", new BlockAir(BlockBase.Info.of(Material.AIR).noCollission().noDrops().air()));
    public static final Block CAVE_AIR = register("cave_air", new BlockAir(BlockBase.Info.of(Material.AIR).noCollission().noDrops().air()));
    public static final Block BUBBLE_COLUMN = register("bubble_column", new BlockBubbleColumn(BlockBase.Info.of(Material.BUBBLE_COLUMN).noCollission().noDrops()));
    public static final Block POLISHED_GRANITE_STAIRS = register("polished_granite_stairs", new BlockStairs(Blocks.POLISHED_GRANITE.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_GRANITE)));
    public static final Block SMOOTH_RED_SANDSTONE_STAIRS = register("smooth_red_sandstone_stairs", new BlockStairs(Blocks.SMOOTH_RED_SANDSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.SMOOTH_RED_SANDSTONE)));
    public static final Block MOSSY_STONE_BRICK_STAIRS = register("mossy_stone_brick_stairs", new BlockStairs(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static final Block POLISHED_DIORITE_STAIRS = register("polished_diorite_stairs", new BlockStairs(Blocks.POLISHED_DIORITE.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_DIORITE)));
    public static final Block MOSSY_COBBLESTONE_STAIRS = register("mossy_cobblestone_stairs", new BlockStairs(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.MOSSY_COBBLESTONE)));
    public static final Block END_STONE_BRICK_STAIRS = register("end_stone_brick_stairs", new BlockStairs(Blocks.END_STONE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.END_STONE_BRICKS)));
    public static final Block STONE_STAIRS = register("stone_stairs", new BlockStairs(Blocks.STONE.defaultBlockState(), BlockBase.Info.copy(Blocks.STONE)));
    public static final Block SMOOTH_SANDSTONE_STAIRS = register("smooth_sandstone_stairs", new BlockStairs(Blocks.SMOOTH_SANDSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.SMOOTH_SANDSTONE)));
    public static final Block SMOOTH_QUARTZ_STAIRS = register("smooth_quartz_stairs", new BlockStairs(Blocks.SMOOTH_QUARTZ.defaultBlockState(), BlockBase.Info.copy(Blocks.SMOOTH_QUARTZ)));
    public static final Block GRANITE_STAIRS = register("granite_stairs", new BlockStairs(Blocks.GRANITE.defaultBlockState(), BlockBase.Info.copy(Blocks.GRANITE)));
    public static final Block ANDESITE_STAIRS = register("andesite_stairs", new BlockStairs(Blocks.ANDESITE.defaultBlockState(), BlockBase.Info.copy(Blocks.ANDESITE)));
    public static final Block RED_NETHER_BRICK_STAIRS = register("red_nether_brick_stairs", new BlockStairs(Blocks.RED_NETHER_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.RED_NETHER_BRICKS)));
    public static final Block POLISHED_ANDESITE_STAIRS = register("polished_andesite_stairs", new BlockStairs(Blocks.POLISHED_ANDESITE.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_ANDESITE)));
    public static final Block DIORITE_STAIRS = register("diorite_stairs", new BlockStairs(Blocks.DIORITE.defaultBlockState(), BlockBase.Info.copy(Blocks.DIORITE)));
    public static final Block POLISHED_GRANITE_SLAB = register("polished_granite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_GRANITE)));
    public static final Block SMOOTH_RED_SANDSTONE_SLAB = register("smooth_red_sandstone_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.SMOOTH_RED_SANDSTONE)));
    public static final Block MOSSY_STONE_BRICK_SLAB = register("mossy_stone_brick_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static final Block POLISHED_DIORITE_SLAB = register("polished_diorite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_DIORITE)));
    public static final Block MOSSY_COBBLESTONE_SLAB = register("mossy_cobblestone_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.MOSSY_COBBLESTONE)));
    public static final Block END_STONE_BRICK_SLAB = register("end_stone_brick_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.END_STONE_BRICKS)));
    public static final Block SMOOTH_SANDSTONE_SLAB = register("smooth_sandstone_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.SMOOTH_SANDSTONE)));
    public static final Block SMOOTH_QUARTZ_SLAB = register("smooth_quartz_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.SMOOTH_QUARTZ)));
    public static final Block GRANITE_SLAB = register("granite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.GRANITE)));
    public static final Block ANDESITE_SLAB = register("andesite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.ANDESITE)));
    public static final Block RED_NETHER_BRICK_SLAB = register("red_nether_brick_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.RED_NETHER_BRICKS)));
    public static final Block POLISHED_ANDESITE_SLAB = register("polished_andesite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_ANDESITE)));
    public static final Block DIORITE_SLAB = register("diorite_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.DIORITE)));
    public static final Block BRICK_WALL = register("brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.BRICKS)));
    public static final Block PRISMARINE_WALL = register("prismarine_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.PRISMARINE)));
    public static final Block RED_SANDSTONE_WALL = register("red_sandstone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.RED_SANDSTONE)));
    public static final Block MOSSY_STONE_BRICK_WALL = register("mossy_stone_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.MOSSY_STONE_BRICKS)));
    public static final Block GRANITE_WALL = register("granite_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.GRANITE)));
    public static final Block STONE_BRICK_WALL = register("stone_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.STONE_BRICKS)));
    public static final Block NETHER_BRICK_WALL = register("nether_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.NETHER_BRICKS)));
    public static final Block ANDESITE_WALL = register("andesite_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.ANDESITE)));
    public static final Block RED_NETHER_BRICK_WALL = register("red_nether_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.RED_NETHER_BRICKS)));
    public static final Block SANDSTONE_WALL = register("sandstone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.SANDSTONE)));
    public static final Block END_STONE_BRICK_WALL = register("end_stone_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.END_STONE_BRICKS)));
    public static final Block DIORITE_WALL = register("diorite_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.DIORITE)));
    public static final Block SCAFFOLDING = register("scaffolding", new BlockScaffolding(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.SAND).noCollission().sound(SoundEffectType.SCAFFOLDING).dynamicShape()));
    public static final Block LOOM = register("loom", new BlockLoom(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block BARREL = register("barrel", new BlockBarrel(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block SMOKER = register("smoker", new BlockSmoker(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))));
    public static final Block BLAST_FURNACE = register("blast_furnace", new BlockBlastFurnace(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))));
    public static final Block CARTOGRAPHY_TABLE = register("cartography_table", new BlockCartographyTable(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block FLETCHING_TABLE = register("fletching_table", new BlockFletchingTable(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block GRINDSTONE = register("grindstone", new BlockGrindstone(BlockBase.Info.of(Material.HEAVY_METAL, MaterialMapColor.METAL).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.STONE)));
    public static final Block LECTERN = register("lectern", new BlockLectern(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block SMITHING_TABLE = register("smithing_table", new BlockSmithingTable(BlockBase.Info.of(Material.WOOD).strength(2.5F).sound(SoundEffectType.WOOD)));
    public static final Block STONECUTTER = register("stonecutter", new BlockStonecutter(BlockBase.Info.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F)));
    public static final Block BELL = register("bell", new BlockBell(BlockBase.Info.of(Material.METAL, MaterialMapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F).sound(SoundEffectType.ANVIL)));
    public static final Block LANTERN = register("lantern", new BlockLantern(BlockBase.Info.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundEffectType.LANTERN).lightLevel((iblockdata) -> {
        return 15;
    }).noOcclusion()));
    public static final Block SOUL_LANTERN = register("soul_lantern", new BlockLantern(BlockBase.Info.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundEffectType.LANTERN).lightLevel((iblockdata) -> {
        return 10;
    }).noOcclusion()));
    public static final Block CAMPFIRE = register("campfire", new BlockCampfire(true, 1, BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F).sound(SoundEffectType.WOOD).lightLevel(litBlockEmission(15)).noOcclusion()));
    public static final Block SOUL_CAMPFIRE = register("soul_campfire", new BlockCampfire(false, 2, BlockBase.Info.of(Material.WOOD, MaterialMapColor.PODZOL).strength(2.0F).sound(SoundEffectType.WOOD).lightLevel(litBlockEmission(10)).noOcclusion()));
    public static final Block SWEET_BERRY_BUSH = register("sweet_berry_bush", new BlockSweetBerryBush(BlockBase.Info.of(Material.PLANT).randomTicks().noCollission().sound(SoundEffectType.SWEET_BERRY_BUSH)));
    public static final Block WARPED_STEM = register("warped_stem", netherStem(MaterialMapColor.WARPED_STEM));
    public static final Block STRIPPED_WARPED_STEM = register("stripped_warped_stem", netherStem(MaterialMapColor.WARPED_STEM));
    public static final Block WARPED_HYPHAE = register("warped_hyphae", new BlockRotatable(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.WARPED_HYPHAE).strength(2.0F).sound(SoundEffectType.STEM)));
    public static final Block STRIPPED_WARPED_HYPHAE = register("stripped_warped_hyphae", new BlockRotatable(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.WARPED_HYPHAE).strength(2.0F).sound(SoundEffectType.STEM)));
    public static final Block WARPED_NYLIUM = register("warped_nylium", new BlockNylium(BlockBase.Info.of(Material.STONE, MaterialMapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(0.4F).sound(SoundEffectType.NYLIUM).randomTicks()));
    public static final Block WARPED_FUNGUS = register("warped_fungus", new BlockFungi(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_CYAN).instabreak().noCollission().sound(SoundEffectType.FUNGUS), () -> {
        return TreeFeatures.WARPED_FUNGUS_PLANTED;
    }));
    public static final Block WARPED_WART_BLOCK = register("warped_wart_block", new Block(BlockBase.Info.of(Material.GRASS, MaterialMapColor.WARPED_WART_BLOCK).strength(1.0F).sound(SoundEffectType.WART_BLOCK)));
    public static final Block WARPED_ROOTS = register("warped_roots", new BlockRoots(BlockBase.Info.of(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.COLOR_CYAN).noCollission().instabreak().sound(SoundEffectType.ROOTS)));
    public static final Block NETHER_SPROUTS = register("nether_sprouts", new BlockNetherSprouts(BlockBase.Info.of(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.COLOR_CYAN).noCollission().instabreak().sound(SoundEffectType.NETHER_SPROUTS)));
    public static final Block CRIMSON_STEM = register("crimson_stem", netherStem(MaterialMapColor.CRIMSON_STEM));
    public static final Block STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", netherStem(MaterialMapColor.CRIMSON_STEM));
    public static final Block CRIMSON_HYPHAE = register("crimson_hyphae", new BlockRotatable(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_HYPHAE).strength(2.0F).sound(SoundEffectType.STEM)));
    public static final Block STRIPPED_CRIMSON_HYPHAE = register("stripped_crimson_hyphae", new BlockRotatable(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_HYPHAE).strength(2.0F).sound(SoundEffectType.STEM)));
    public static final Block CRIMSON_NYLIUM = register("crimson_nylium", new BlockNylium(BlockBase.Info.of(Material.STONE, MaterialMapColor.CRIMSON_NYLIUM).requiresCorrectToolForDrops().strength(0.4F).sound(SoundEffectType.NYLIUM).randomTicks()));
    public static final Block CRIMSON_FUNGUS = register("crimson_fungus", new BlockFungi(BlockBase.Info.of(Material.PLANT, MaterialMapColor.NETHER).instabreak().noCollission().sound(SoundEffectType.FUNGUS), () -> {
        return TreeFeatures.CRIMSON_FUNGUS_PLANTED;
    }));
    public static final Block SHROOMLIGHT = register("shroomlight", new Block(BlockBase.Info.of(Material.GRASS, MaterialMapColor.COLOR_RED).strength(1.0F).sound(SoundEffectType.SHROOMLIGHT).lightLevel((iblockdata) -> {
        return 15;
    })));
    public static final Block WEEPING_VINES = register("weeping_vines", new BlockWeepingVines(BlockBase.Info.of(Material.PLANT, MaterialMapColor.NETHER).randomTicks().noCollission().instabreak().sound(SoundEffectType.WEEPING_VINES)));
    public static final Block WEEPING_VINES_PLANT = register("weeping_vines_plant", new BlockWeepingVinesPlant(BlockBase.Info.of(Material.PLANT, MaterialMapColor.NETHER).noCollission().instabreak().sound(SoundEffectType.WEEPING_VINES)));
    public static final Block TWISTING_VINES = register("twisting_vines", new BlockTwistingVines(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_CYAN).randomTicks().noCollission().instabreak().sound(SoundEffectType.WEEPING_VINES)));
    public static final Block TWISTING_VINES_PLANT = register("twisting_vines_plant", new BlockTwistingVinesPlant(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_CYAN).noCollission().instabreak().sound(SoundEffectType.WEEPING_VINES)));
    public static final Block CRIMSON_ROOTS = register("crimson_roots", new BlockRoots(BlockBase.Info.of(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.NETHER).noCollission().instabreak().sound(SoundEffectType.ROOTS)));
    public static final Block CRIMSON_PLANKS = register("crimson_planks", new Block(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_STEM).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_PLANKS = register("warped_planks", new Block(BlockBase.Info.of(Material.NETHER_WOOD, MaterialMapColor.WARPED_STEM).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_SLAB = register("crimson_slab", new BlockStepAbstract(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_SLAB = register("warped_slab", new BlockStepAbstract(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_PRESSURE_PLATE = register("crimson_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_PRESSURE_PLATE = register("warped_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_FENCE = register("crimson_fence", new BlockFence(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_FENCE = register("warped_fence", new BlockFence(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_TRAPDOOR = register("crimson_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block WARPED_TRAPDOOR = register("warped_trapdoor", new BlockTrapdoor(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion().isValidSpawn(Blocks::never)));
    public static final Block CRIMSON_FENCE_GATE = register("crimson_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_FENCE_GATE = register("warped_fence_gate", new BlockFenceGate(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_STAIRS = register("crimson_stairs", new BlockStairs(Blocks.CRIMSON_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.CRIMSON_PLANKS)));
    public static final Block WARPED_STAIRS = register("warped_stairs", new BlockStairs(Blocks.WARPED_PLANKS.defaultBlockState(), BlockBase.Info.copy(Blocks.WARPED_PLANKS)));
    public static final Block CRIMSON_BUTTON = register("crimson_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block WARPED_BUTTON = register("warped_button", new BlockWoodButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundEffectType.WOOD)));
    public static final Block CRIMSON_DOOR = register("crimson_door", new BlockDoor(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block WARPED_DOOR = register("warped_door", new BlockDoor(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundEffectType.WOOD).noOcclusion()));
    public static final Block CRIMSON_SIGN = register("crimson_sign", new BlockFloorSign(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.CRIMSON));
    public static final Block WARPED_SIGN = register("warped_sign", new BlockFloorSign(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD), BlockPropertyWood.WARPED));
    public static final Block CRIMSON_WALL_SIGN = register("crimson_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.CRIMSON_SIGN), BlockPropertyWood.CRIMSON));
    public static final Block WARPED_WALL_SIGN = register("warped_wall_sign", new BlockWallSign(BlockBase.Info.of(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundEffectType.WOOD).dropsLike(Blocks.WARPED_SIGN), BlockPropertyWood.WARPED));
    public static final Block STRUCTURE_BLOCK = register("structure_block", new BlockStructure(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops()));
    public static final Block JIGSAW = register("jigsaw", new BlockJigsaw(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noDrops()));
    public static final Block COMPOSTER = register("composter", new BlockComposter(BlockBase.Info.of(Material.WOOD).strength(0.6F).sound(SoundEffectType.WOOD)));
    public static final Block TARGET = register("target", new BlockTarget(BlockBase.Info.of(Material.GRASS, MaterialMapColor.QUARTZ).strength(0.5F).sound(SoundEffectType.GRASS)));
    public static final Block BEE_NEST = register("bee_nest", new BlockBeehive(BlockBase.Info.of(Material.WOOD, MaterialMapColor.COLOR_YELLOW).strength(0.3F).sound(SoundEffectType.WOOD)));
    public static final Block BEEHIVE = register("beehive", new BlockBeehive(BlockBase.Info.of(Material.WOOD).strength(0.6F).sound(SoundEffectType.WOOD)));
    public static final Block HONEY_BLOCK = register("honey_block", new BlockHoney(BlockBase.Info.of(Material.CLAY, MaterialMapColor.COLOR_ORANGE).speedFactor(0.4F).jumpFactor(0.5F).noOcclusion().sound(SoundEffectType.HONEY_BLOCK)));
    public static final Block HONEYCOMB_BLOCK = register("honeycomb_block", new Block(BlockBase.Info.of(Material.CLAY, MaterialMapColor.COLOR_ORANGE).strength(0.6F).sound(SoundEffectType.CORAL_BLOCK)));
    public static final Block NETHERITE_BLOCK = register("netherite_block", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).sound(SoundEffectType.NETHERITE_BLOCK)));
    public static final Block ANCIENT_DEBRIS = register("ancient_debris", new Block(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundEffectType.ANCIENT_DEBRIS)));
    public static final Block CRYING_OBSIDIAN = register("crying_obsidian", new BlockCryingObsidian(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((iblockdata) -> {
        return 10;
    })));
    public static final Block RESPAWN_ANCHOR = register("respawn_anchor", new BlockRespawnAnchor(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((iblockdata) -> {
        return BlockRespawnAnchor.getScaledChargeLevel(iblockdata, 15);
    })));
    public static final Block POTTED_CRIMSON_FUNGUS = register("potted_crimson_fungus", new BlockFlowerPot(Blocks.CRIMSON_FUNGUS, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_WARPED_FUNGUS = register("potted_warped_fungus", new BlockFlowerPot(Blocks.WARPED_FUNGUS, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_CRIMSON_ROOTS = register("potted_crimson_roots", new BlockFlowerPot(Blocks.CRIMSON_ROOTS, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_WARPED_ROOTS = register("potted_warped_roots", new BlockFlowerPot(Blocks.WARPED_ROOTS, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block LODESTONE = register("lodestone", new Block(BlockBase.Info.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundEffectType.LODESTONE)));
    public static final Block BLACKSTONE = register("blackstone", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block BLACKSTONE_STAIRS = register("blackstone_stairs", new BlockStairs(Blocks.BLACKSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.BLACKSTONE)));
    public static final Block BLACKSTONE_WALL = register("blackstone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.BLACKSTONE)));
    public static final Block BLACKSTONE_SLAB = register("blackstone_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.BLACKSTONE).strength(2.0F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE = register("polished_blackstone", new Block(BlockBase.Info.copy(Blocks.BLACKSTONE).strength(2.0F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE_BRICKS = register("polished_blackstone_bricks", new Block(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE).strength(1.5F, 6.0F)));
    public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS = register("cracked_polished_blackstone_bricks", new Block(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final Block CHISELED_POLISHED_BLACKSTONE = register("chiseled_polished_blackstone", new Block(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE).strength(1.5F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE_BRICK_SLAB = register("polished_blackstone_brick_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE_BRICKS).strength(2.0F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS = register("polished_blackstone_brick_stairs", new BlockStairs(Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final Block POLISHED_BLACKSTONE_BRICK_WALL = register("polished_blackstone_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final Block GILDED_BLACKSTONE = register("gilded_blackstone", new Block(BlockBase.Info.copy(Blocks.BLACKSTONE).sound(SoundEffectType.GILDED_BLACKSTONE)));
    public static final Block POLISHED_BLACKSTONE_STAIRS = register("polished_blackstone_stairs", new BlockStairs(Blocks.POLISHED_BLACKSTONE.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final Block POLISHED_BLACKSTONE_SLAB = register("polished_blackstone_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = register("polished_blackstone_pressure_plate", new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_BLACK).requiresCorrectToolForDrops().noCollission().strength(0.5F)));
    public static final Block POLISHED_BLACKSTONE_BUTTON = register("polished_blackstone_button", new BlockStoneButton(BlockBase.Info.of(Material.DECORATION).noCollission().strength(0.5F)));
    public static final Block POLISHED_BLACKSTONE_WALL = register("polished_blackstone_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final Block CHISELED_NETHER_BRICKS = register("chiseled_nether_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block CRACKED_NETHER_BRICKS = register("cracked_nether_bricks", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.NETHER).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundEffectType.NETHER_BRICKS)));
    public static final Block QUARTZ_BRICKS = register("quartz_bricks", new Block(BlockBase.Info.copy(Blocks.QUARTZ_BLOCK)));
    public static final Block CANDLE = register("candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.SAND).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block WHITE_CANDLE = register("white_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.WOOL).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block ORANGE_CANDLE = register("orange_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_ORANGE).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block MAGENTA_CANDLE = register("magenta_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_MAGENTA).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block LIGHT_BLUE_CANDLE = register("light_blue_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_BLUE).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block YELLOW_CANDLE = register("yellow_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_YELLOW).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block LIME_CANDLE = register("lime_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_GREEN).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block PINK_CANDLE = register("pink_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_PINK).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block GRAY_CANDLE = register("gray_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_GRAY).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block LIGHT_GRAY_CANDLE = register("light_gray_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block CYAN_CANDLE = register("cyan_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_CYAN).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block PURPLE_CANDLE = register("purple_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_PURPLE).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block BLUE_CANDLE = register("blue_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_BLUE).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block BROWN_CANDLE = register("brown_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_BROWN).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block GREEN_CANDLE = register("green_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_GREEN).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block RED_CANDLE = register("red_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_RED).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block BLACK_CANDLE = register("black_candle", new CandleBlock(BlockBase.Info.of(Material.DECORATION, MaterialMapColor.COLOR_BLACK).noOcclusion().strength(0.1F).sound(SoundEffectType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION)));
    public static final Block CANDLE_CAKE = register("candle_cake", new CandleCakeBlock(Blocks.CANDLE, BlockBase.Info.copy(Blocks.CAKE).lightLevel(litBlockEmission(3))));
    public static final Block WHITE_CANDLE_CAKE = register("white_candle_cake", new CandleCakeBlock(Blocks.WHITE_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block ORANGE_CANDLE_CAKE = register("orange_candle_cake", new CandleCakeBlock(Blocks.ORANGE_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block MAGENTA_CANDLE_CAKE = register("magenta_candle_cake", new CandleCakeBlock(Blocks.MAGENTA_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block LIGHT_BLUE_CANDLE_CAKE = register("light_blue_candle_cake", new CandleCakeBlock(Blocks.LIGHT_BLUE_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block YELLOW_CANDLE_CAKE = register("yellow_candle_cake", new CandleCakeBlock(Blocks.YELLOW_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block LIME_CANDLE_CAKE = register("lime_candle_cake", new CandleCakeBlock(Blocks.LIME_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block PINK_CANDLE_CAKE = register("pink_candle_cake", new CandleCakeBlock(Blocks.PINK_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block GRAY_CANDLE_CAKE = register("gray_candle_cake", new CandleCakeBlock(Blocks.GRAY_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block LIGHT_GRAY_CANDLE_CAKE = register("light_gray_candle_cake", new CandleCakeBlock(Blocks.LIGHT_GRAY_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block CYAN_CANDLE_CAKE = register("cyan_candle_cake", new CandleCakeBlock(Blocks.CYAN_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block PURPLE_CANDLE_CAKE = register("purple_candle_cake", new CandleCakeBlock(Blocks.PURPLE_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block BLUE_CANDLE_CAKE = register("blue_candle_cake", new CandleCakeBlock(Blocks.BLUE_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block BROWN_CANDLE_CAKE = register("brown_candle_cake", new CandleCakeBlock(Blocks.BROWN_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block GREEN_CANDLE_CAKE = register("green_candle_cake", new CandleCakeBlock(Blocks.GREEN_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block RED_CANDLE_CAKE = register("red_candle_cake", new CandleCakeBlock(Blocks.RED_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block BLACK_CANDLE_CAKE = register("black_candle_cake", new CandleCakeBlock(Blocks.BLACK_CANDLE, BlockBase.Info.copy(Blocks.CANDLE_CAKE)));
    public static final Block AMETHYST_BLOCK = register("amethyst_block", new AmethystBlock(BlockBase.Info.of(Material.AMETHYST, MaterialMapColor.COLOR_PURPLE).strength(1.5F).sound(SoundEffectType.AMETHYST).requiresCorrectToolForDrops()));
    public static final Block BUDDING_AMETHYST = register("budding_amethyst", new BuddingAmethystBlock(BlockBase.Info.of(Material.AMETHYST).randomTicks().strength(1.5F).sound(SoundEffectType.AMETHYST).requiresCorrectToolForDrops()));
    public static final Block AMETHYST_CLUSTER = register("amethyst_cluster", new AmethystClusterBlock(7, 3, BlockBase.Info.of(Material.AMETHYST).noOcclusion().randomTicks().sound(SoundEffectType.AMETHYST_CLUSTER).strength(1.5F).lightLevel((iblockdata) -> {
        return 5;
    })));
    public static final Block LARGE_AMETHYST_BUD = register("large_amethyst_bud", new AmethystClusterBlock(5, 3, BlockBase.Info.copy(Blocks.AMETHYST_CLUSTER).sound(SoundEffectType.MEDIUM_AMETHYST_BUD).lightLevel((iblockdata) -> {
        return 4;
    })));
    public static final Block MEDIUM_AMETHYST_BUD = register("medium_amethyst_bud", new AmethystClusterBlock(4, 3, BlockBase.Info.copy(Blocks.AMETHYST_CLUSTER).sound(SoundEffectType.LARGE_AMETHYST_BUD).lightLevel((iblockdata) -> {
        return 2;
    })));
    public static final Block SMALL_AMETHYST_BUD = register("small_amethyst_bud", new AmethystClusterBlock(3, 4, BlockBase.Info.copy(Blocks.AMETHYST_CLUSTER).sound(SoundEffectType.SMALL_AMETHYST_BUD).lightLevel((iblockdata) -> {
        return 1;
    })));
    public static final Block TUFF = register("tuff", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_GRAY).sound(SoundEffectType.TUFF).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Block CALCITE = register("calcite", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_WHITE).sound(SoundEffectType.CALCITE).requiresCorrectToolForDrops().strength(0.75F)));
    public static final Block TINTED_GLASS = register("tinted_glass", new TintedGlassBlock(BlockBase.Info.copy(Blocks.GLASS).color(MaterialMapColor.COLOR_GRAY).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)));
    public static final Block POWDER_SNOW = register("powder_snow", new PowderSnowBlock(BlockBase.Info.of(Material.POWDER_SNOW).strength(0.25F).sound(SoundEffectType.POWDER_SNOW).dynamicShape()));
    public static final Block SCULK_SENSOR = register("sculk_sensor", new SculkSensorBlock(BlockBase.Info.of(Material.SCULK, MaterialMapColor.COLOR_CYAN).strength(1.5F).sound(SoundEffectType.SCULK_SENSOR).lightLevel((iblockdata) -> {
        return 1;
    }).emissiveRendering((iblockdata, iblockaccess, blockposition) -> {
        return SculkSensorBlock.getPhase(iblockdata) == SculkSensorPhase.ACTIVE;
    }), 8));
    public static final Block OXIDIZED_COPPER = register("oxidized_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.of(Material.METAL, MaterialMapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.COPPER)));
    public static final Block WEATHERED_COPPER = register("weathered_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.of(Material.METAL, MaterialMapColor.WARPED_STEM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.COPPER)));
    public static final Block EXPOSED_COPPER = register("exposed_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.of(Material.METAL, MaterialMapColor.TERRACOTTA_LIGHT_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.COPPER)));
    public static final Block COPPER_BLOCK = register("copper_block", new WeatheringCopperFullBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.COPPER)));
    public static final Block COPPER_ORE = register("copper_ore", new BlockOre(BlockBase.Info.copy(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_COPPER_ORE = register("deepslate_copper_ore", new BlockOre(BlockBase.Info.copy(Blocks.COPPER_ORE).color(MaterialMapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundEffectType.DEEPSLATE)));
    public static final Block OXIDIZED_CUT_COPPER = register("oxidized_cut_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.copy(Blocks.OXIDIZED_COPPER)));
    public static final Block WEATHERED_CUT_COPPER = register("weathered_cut_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.copy(Blocks.WEATHERED_COPPER)));
    public static final Block EXPOSED_CUT_COPPER = register("exposed_cut_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.copy(Blocks.EXPOSED_COPPER)));
    public static final Block CUT_COPPER = register("cut_copper", new WeatheringCopperFullBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.copy(Blocks.COPPER_BLOCK)));
    public static final Block OXIDIZED_CUT_COPPER_STAIRS = register("oxidized_cut_copper_stairs", new WeatheringCopperStairBlock(WeatheringCopper.a.OXIDIZED, Blocks.OXIDIZED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.OXIDIZED_CUT_COPPER)));
    public static final Block WEATHERED_CUT_COPPER_STAIRS = register("weathered_cut_copper_stairs", new WeatheringCopperStairBlock(WeatheringCopper.a.WEATHERED, Blocks.WEATHERED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.WEATHERED_COPPER)));
    public static final Block EXPOSED_CUT_COPPER_STAIRS = register("exposed_cut_copper_stairs", new WeatheringCopperStairBlock(WeatheringCopper.a.EXPOSED, Blocks.EXPOSED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.EXPOSED_COPPER)));
    public static final Block CUT_COPPER_STAIRS = register("cut_copper_stairs", new WeatheringCopperStairBlock(WeatheringCopper.a.UNAFFECTED, Blocks.CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.COPPER_BLOCK)));
    public static final Block OXIDIZED_CUT_COPPER_SLAB = register("oxidized_cut_copper_slab", new WeatheringCopperSlabBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.copy(Blocks.OXIDIZED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block WEATHERED_CUT_COPPER_SLAB = register("weathered_cut_copper_slab", new WeatheringCopperSlabBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.copy(Blocks.WEATHERED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block EXPOSED_CUT_COPPER_SLAB = register("exposed_cut_copper_slab", new WeatheringCopperSlabBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.copy(Blocks.EXPOSED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block CUT_COPPER_SLAB = register("cut_copper_slab", new WeatheringCopperSlabBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.copy(Blocks.CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block WAXED_COPPER_BLOCK = register("waxed_copper_block", new Block(BlockBase.Info.copy(Blocks.COPPER_BLOCK)));
    public static final Block WAXED_WEATHERED_COPPER = register("waxed_weathered_copper", new Block(BlockBase.Info.copy(Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_EXPOSED_COPPER = register("waxed_exposed_copper", new Block(BlockBase.Info.copy(Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_OXIDIZED_COPPER = register("waxed_oxidized_copper", new Block(BlockBase.Info.copy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_OXIDIZED_CUT_COPPER = register("waxed_oxidized_cut_copper", new Block(BlockBase.Info.copy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_WEATHERED_CUT_COPPER = register("waxed_weathered_cut_copper", new Block(BlockBase.Info.copy(Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_EXPOSED_CUT_COPPER = register("waxed_exposed_cut_copper", new Block(BlockBase.Info.copy(Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_CUT_COPPER = register("waxed_cut_copper", new Block(BlockBase.Info.copy(Blocks.COPPER_BLOCK)));
    public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = register("waxed_oxidized_cut_copper_stairs", new BlockStairs(Blocks.WAXED_OXIDIZED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = register("waxed_weathered_cut_copper_stairs", new BlockStairs(Blocks.WAXED_WEATHERED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = register("waxed_exposed_cut_copper_stairs", new BlockStairs(Blocks.WAXED_EXPOSED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_CUT_COPPER_STAIRS = register("waxed_cut_copper_stairs", new BlockStairs(Blocks.WAXED_CUT_COPPER.defaultBlockState(), BlockBase.Info.copy(Blocks.COPPER_BLOCK)));
    public static final Block WAXED_OXIDIZED_CUT_COPPER_SLAB = register("waxed_oxidized_cut_copper_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.WAXED_OXIDIZED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block WAXED_WEATHERED_CUT_COPPER_SLAB = register("waxed_weathered_cut_copper_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.WAXED_WEATHERED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block WAXED_EXPOSED_CUT_COPPER_SLAB = register("waxed_exposed_cut_copper_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.WAXED_EXPOSED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block WAXED_CUT_COPPER_SLAB = register("waxed_cut_copper_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.WAXED_CUT_COPPER).requiresCorrectToolForDrops()));
    public static final Block LIGHTNING_ROD = register("lightning_rod", new LightningRodBlock(BlockBase.Info.of(Material.METAL, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.COPPER).noOcclusion()));
    public static final Block POINTED_DRIPSTONE = register("pointed_dripstone", new PointedDripstoneBlock(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).noOcclusion().sound(SoundEffectType.POINTED_DRIPSTONE).randomTicks().strength(1.5F, 3.0F).dynamicShape()));
    public static final Block DRIPSTONE_BLOCK = register("dripstone_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).sound(SoundEffectType.DRIPSTONE_BLOCK).requiresCorrectToolForDrops().strength(1.5F, 1.0F)));
    public static final Block CAVE_VINES = register("cave_vines", new CaveVinesBlock(BlockBase.Info.of(Material.PLANT).randomTicks().noCollission().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundEffectType.CAVE_VINES)));
    public static final Block CAVE_VINES_PLANT = register("cave_vines_plant", new CaveVinesPlantBlock(BlockBase.Info.of(Material.PLANT).noCollission().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundEffectType.CAVE_VINES)));
    public static final Block SPORE_BLOSSOM = register("spore_blossom", new SporeBlossomBlock(BlockBase.Info.of(Material.PLANT).instabreak().noCollission().sound(SoundEffectType.SPORE_BLOSSOM)));
    public static final Block AZALEA = register("azalea", new AzaleaBlock(BlockBase.Info.of(Material.PLANT).instabreak().sound(SoundEffectType.AZALEA).noOcclusion()));
    public static final Block FLOWERING_AZALEA = register("flowering_azalea", new AzaleaBlock(BlockBase.Info.of(Material.PLANT).instabreak().sound(SoundEffectType.FLOWERING_AZALEA).noOcclusion()));
    public static final Block MOSS_CARPET = register("moss_carpet", new CarpetBlock(BlockBase.Info.of(Material.PLANT, MaterialMapColor.COLOR_GREEN).strength(0.1F).sound(SoundEffectType.MOSS_CARPET)));
    public static final Block MOSS_BLOCK = register("moss_block", new MossBlock(BlockBase.Info.of(Material.MOSS, MaterialMapColor.COLOR_GREEN).strength(0.1F).sound(SoundEffectType.MOSS)));
    public static final Block BIG_DRIPLEAF = register("big_dripleaf", new BigDripleafBlock(BlockBase.Info.of(Material.PLANT).strength(0.1F).sound(SoundEffectType.BIG_DRIPLEAF)));
    public static final Block BIG_DRIPLEAF_STEM = register("big_dripleaf_stem", new BigDripleafStemBlock(BlockBase.Info.of(Material.PLANT).noCollission().strength(0.1F).sound(SoundEffectType.BIG_DRIPLEAF)));
    public static final Block SMALL_DRIPLEAF = register("small_dripleaf", new SmallDripleafBlock(BlockBase.Info.of(Material.PLANT).noCollission().instabreak().sound(SoundEffectType.SMALL_DRIPLEAF)));
    public static final Block HANGING_ROOTS = register("hanging_roots", new HangingRootsBlock(BlockBase.Info.of(Material.REPLACEABLE_PLANT, MaterialMapColor.DIRT).noCollission().instabreak().sound(SoundEffectType.HANGING_ROOTS)));
    public static final Block ROOTED_DIRT = register("rooted_dirt", new RootedDirtBlock(BlockBase.Info.of(Material.DIRT, MaterialMapColor.DIRT).strength(0.5F).sound(SoundEffectType.ROOTED_DIRT)));
    public static final Block DEEPSLATE = register("deepslate", new BlockRotatable(BlockBase.Info.of(Material.STONE, MaterialMapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundEffectType.DEEPSLATE)));
    public static final Block COBBLED_DEEPSLATE = register("cobbled_deepslate", new Block(BlockBase.Info.copy(Blocks.DEEPSLATE).strength(3.5F, 6.0F)));
    public static final Block COBBLED_DEEPSLATE_STAIRS = register("cobbled_deepslate_stairs", new BlockStairs(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final Block COBBLED_DEEPSLATE_SLAB = register("cobbled_deepslate_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final Block COBBLED_DEEPSLATE_WALL = register("cobbled_deepslate_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final Block POLISHED_DEEPSLATE = register("polished_deepslate", new Block(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE).sound(SoundEffectType.POLISHED_DEEPSLATE)));
    public static final Block POLISHED_DEEPSLATE_STAIRS = register("polished_deepslate_stairs", new BlockStairs(Blocks.POLISHED_DEEPSLATE.defaultBlockState(), BlockBase.Info.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final Block POLISHED_DEEPSLATE_SLAB = register("polished_deepslate_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final Block POLISHED_DEEPSLATE_WALL = register("polished_deepslate_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final Block DEEPSLATE_TILES = register("deepslate_tiles", new Block(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE).sound(SoundEffectType.DEEPSLATE_TILES)));
    public static final Block DEEPSLATE_TILE_STAIRS = register("deepslate_tile_stairs", new BlockStairs(Blocks.DEEPSLATE_TILES.defaultBlockState(), BlockBase.Info.copy(Blocks.DEEPSLATE_TILES)));
    public static final Block DEEPSLATE_TILE_SLAB = register("deepslate_tile_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.DEEPSLATE_TILES)));
    public static final Block DEEPSLATE_TILE_WALL = register("deepslate_tile_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.DEEPSLATE_TILES)));
    public static final Block DEEPSLATE_BRICKS = register("deepslate_bricks", new Block(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE).sound(SoundEffectType.DEEPSLATE_BRICKS)));
    public static final Block DEEPSLATE_BRICK_STAIRS = register("deepslate_brick_stairs", new BlockStairs(Blocks.DEEPSLATE_BRICKS.defaultBlockState(), BlockBase.Info.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final Block DEEPSLATE_BRICK_SLAB = register("deepslate_brick_slab", new BlockStepAbstract(BlockBase.Info.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final Block DEEPSLATE_BRICK_WALL = register("deepslate_brick_wall", new BlockCobbleWall(BlockBase.Info.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final Block CHISELED_DEEPSLATE = register("chiseled_deepslate", new Block(BlockBase.Info.copy(Blocks.COBBLED_DEEPSLATE).sound(SoundEffectType.DEEPSLATE_BRICKS)));
    public static final Block CRACKED_DEEPSLATE_BRICKS = register("cracked_deepslate_bricks", new Block(BlockBase.Info.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final Block CRACKED_DEEPSLATE_TILES = register("cracked_deepslate_tiles", new Block(BlockBase.Info.copy(Blocks.DEEPSLATE_TILES)));
    public static final Block INFESTED_DEEPSLATE = register("infested_deepslate", new InfestedRotatedPillarBlock(Blocks.DEEPSLATE, BlockBase.Info.of(Material.CLAY, MaterialMapColor.DEEPSLATE).sound(SoundEffectType.DEEPSLATE)));
    public static final Block SMOOTH_BASALT = register("smooth_basalt", new Block(BlockBase.Info.copy(Blocks.BASALT)));
    public static final Block RAW_IRON_BLOCK = register("raw_iron_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.RAW_IRON).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
    public static final Block RAW_COPPER_BLOCK = register("raw_copper_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
    public static final Block RAW_GOLD_BLOCK = register("raw_gold_block", new Block(BlockBase.Info.of(Material.STONE, MaterialMapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
    public static final Block POTTED_AZALEA = register("potted_azalea_bush", new BlockFlowerPot(Blocks.AZALEA, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Block POTTED_FLOWERING_AZALEA = register("potted_flowering_azalea_bush", new BlockFlowerPot(Blocks.FLOWERING_AZALEA, BlockBase.Info.of(Material.DECORATION).instabreak().noOcclusion()));

    public Blocks() {}

    private static ToIntFunction<IBlockData> litBlockEmission(int i) {
        return (iblockdata) -> {
            return (Boolean) iblockdata.getValue(BlockProperties.LIT) ? i : 0;
        };
    }

    private static Boolean never(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return false;
    }

    private static Boolean always(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return true;
    }

    private static Boolean ocelotOrParrot(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.OCELOT || entitytypes == EntityTypes.PARROT;
    }

    private static BlockBed bed(EnumColor enumcolor) {
        return new BlockBed(enumcolor, BlockBase.Info.of(Material.WOOL, (iblockdata) -> {
            return iblockdata.getValue(BlockBed.PART) == BlockPropertyBedPart.FOOT ? enumcolor.getMaterialColor() : MaterialMapColor.WOOL;
        }).sound(SoundEffectType.WOOD).strength(0.2F).noOcclusion());
    }

    private static BlockRotatable log(MaterialMapColor materialmapcolor, MaterialMapColor materialmapcolor1) {
        return new BlockRotatable(BlockBase.Info.of(Material.WOOD, (iblockdata) -> {
            return iblockdata.getValue(BlockRotatable.AXIS) == EnumDirection.EnumAxis.Y ? materialmapcolor : materialmapcolor1;
        }).strength(2.0F).sound(SoundEffectType.WOOD));
    }

    private static Block netherStem(MaterialMapColor materialmapcolor) {
        return new BlockRotatable(BlockBase.Info.of(Material.NETHER_WOOD, (iblockdata) -> {
            return materialmapcolor;
        }).strength(2.0F).sound(SoundEffectType.STEM));
    }

    private static boolean always(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    private static boolean never(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    private static BlockStainedGlass stainedGlass(EnumColor enumcolor) {
        return new BlockStainedGlass(enumcolor, BlockBase.Info.of(Material.GLASS, enumcolor).strength(0.3F).sound(SoundEffectType.GLASS).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
    }

    private static BlockLeaves leaves(SoundEffectType soundeffecttype) {
        return new BlockLeaves(BlockBase.Info.of(Material.LEAVES).strength(0.2F).randomTicks().sound(soundeffecttype).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
    }

    private static BlockShulkerBox shulkerBox(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        BlockBase.e blockbase_e = (iblockdata, iblockaccess, blockposition) -> {
            TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

            if (!(tileentity instanceof TileEntityShulkerBox)) {
                return true;
            } else {
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

                return tileentityshulkerbox.isClosed();
            }
        };

        return new BlockShulkerBox(enumcolor, blockbase_info.strength(2.0F).dynamicShape().noOcclusion().isSuffocating(blockbase_e).isViewBlocking(blockbase_e));
    }

    private static BlockPiston pistonBase(boolean flag) {
        BlockBase.e blockbase_e = (iblockdata, iblockaccess, blockposition) -> {
            return !(Boolean) iblockdata.getValue(BlockPiston.EXTENDED);
        };

        return new BlockPiston(flag, BlockBase.Info.of(Material.PISTON).strength(1.5F).isRedstoneConductor(Blocks::never).isSuffocating(blockbase_e).isViewBlocking(blockbase_e));
    }

    private static Block register(String s, Block block) {
        return (Block) IRegistry.register(IRegistry.BLOCK, s, block);
    }

    public static void rebuildCache() {
        Block.BLOCK_STATE_REGISTRY.forEach(BlockBase.BlockData::initCache);
    }

    static {
        Iterator iterator = IRegistry.BLOCK.iterator();

        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            UnmodifiableIterator unmodifiableiterator = block.getStateDefinition().getPossibleStates().iterator();

            while (unmodifiableiterator.hasNext()) {
                IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

                Block.BLOCK_STATE_REGISTRY.add(iblockdata);
            }

            block.getLootTable();
        }

    }
}

package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
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

    public static final Block AIR = a("air", (Block) (new BlockAir(BlockBase.Info.a(Material.AIR).a().f().g())));
    public static final Block STONE = a("stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(1.5F, 6.0F)));
    public static final Block GRANITE = a("granite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIRT).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_GRANITE = a("polished_granite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIRT).h().a(1.5F, 6.0F)));
    public static final Block DIORITE = a("diorite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_DIORITE = a("polished_diorite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().a(1.5F, 6.0F)));
    public static final Block ANDESITE = a("andesite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_ANDESITE = a("polished_andesite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(1.5F, 6.0F)));
    public static final Block GRASS_BLOCK = a("grass_block", (Block) (new BlockGrass(BlockBase.Info.a(Material.GRASS).d().d(0.6F).a(SoundEffectType.GRASS))));
    public static final Block DIRT = a("dirt", new Block(BlockBase.Info.a(Material.DIRT, MaterialMapColor.DIRT).d(0.5F).a(SoundEffectType.GRAVEL)));
    public static final Block COARSE_DIRT = a("coarse_dirt", new Block(BlockBase.Info.a(Material.DIRT, MaterialMapColor.DIRT).d(0.5F).a(SoundEffectType.GRAVEL)));
    public static final Block PODZOL = a("podzol", (Block) (new BlockDirtSnow(BlockBase.Info.a(Material.DIRT, MaterialMapColor.PODZOL).d(0.5F).a(SoundEffectType.GRAVEL))));
    public static final Block COBBLESTONE = a("cobblestone", new Block(BlockBase.Info.a(Material.STONE).h().a(2.0F, 6.0F)));
    public static final Block OAK_PLANKS = a("oak_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOD).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block SPRUCE_PLANKS = a("spruce_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block BIRCH_PLANKS = a("birch_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block JUNGLE_PLANKS = a("jungle_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block ACACIA_PLANKS = a("acacia_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block DARK_OAK_PLANKS = a("dark_oak_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_BROWN).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block OAK_SAPLING = a("oak_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderOak(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block SPRUCE_SAPLING = a("spruce_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderSpruce(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block BIRCH_SAPLING = a("birch_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderBirch(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block JUNGLE_SAPLING = a("jungle_sapling", (Block) (new BlockSapling(new WorldGenMegaTreeProviderJungle(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block ACACIA_SAPLING = a("acacia_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderAcacia(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block DARK_OAK_SAPLING = a("dark_oak_sapling", (Block) (new BlockSapling(new WorldGenMegaTreeProviderDarkOak(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block BEDROCK = a("bedrock", new Block(BlockBase.Info.a(Material.STONE).a(-1.0F, 3600000.0F).f().a(Blocks::a)));
    public static final Block WATER = a("water", (Block) (new BlockFluids(FluidTypes.WATER, BlockBase.Info.a(Material.WATER).a().d(100.0F).f())));
    public static final Block LAVA = a("lava", (Block) (new BlockFluids(FluidTypes.LAVA, BlockBase.Info.a(Material.LAVA).a().d().d(100.0F).a((iblockdata) -> {
        return 15;
    }).f())));
    public static final Block SAND = a("sand", (Block) (new BlockSand(14406560, BlockBase.Info.a(Material.SAND, MaterialMapColor.SAND).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block RED_SAND = a("red_sand", (Block) (new BlockSand(11098145, BlockBase.Info.a(Material.SAND, MaterialMapColor.COLOR_ORANGE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block GRAVEL = a("gravel", (Block) (new BlockGravel(BlockBase.Info.a(Material.SAND, MaterialMapColor.STONE).d(0.6F).a(SoundEffectType.GRAVEL))));
    public static final Block GOLD_ORE = a("gold_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block DEEPSLATE_GOLD_ORE = a("deepslate_gold_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.GOLD_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE))));
    public static final Block IRON_ORE = a("iron_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block DEEPSLATE_IRON_ORE = a("deepslate_iron_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.IRON_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE))));
    public static final Block COAL_ORE = a("coal_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F), UniformInt.a(0, 2))));
    public static final Block DEEPSLATE_COAL_ORE = a("deepslate_coal_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.COAL_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE), UniformInt.a(0, 2))));
    public static final Block NETHER_GOLD_ORE = a("nether_gold_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(3.0F, 3.0F).a(SoundEffectType.NETHER_GOLD_ORE), UniformInt.a(0, 1))));
    public static final Block OAK_LOG = a("oak_log", (Block) a(MaterialMapColor.WOOD, MaterialMapColor.PODZOL));
    public static final Block SPRUCE_LOG = a("spruce_log", (Block) a(MaterialMapColor.PODZOL, MaterialMapColor.COLOR_BROWN));
    public static final Block BIRCH_LOG = a("birch_log", (Block) a(MaterialMapColor.SAND, MaterialMapColor.QUARTZ));
    public static final Block JUNGLE_LOG = a("jungle_log", (Block) a(MaterialMapColor.DIRT, MaterialMapColor.PODZOL));
    public static final Block ACACIA_LOG = a("acacia_log", (Block) a(MaterialMapColor.COLOR_ORANGE, MaterialMapColor.STONE));
    public static final Block DARK_OAK_LOG = a("dark_oak_log", (Block) a(MaterialMapColor.COLOR_BROWN, MaterialMapColor.COLOR_BROWN));
    public static final Block STRIPPED_SPRUCE_LOG = a("stripped_spruce_log", (Block) a(MaterialMapColor.PODZOL, MaterialMapColor.PODZOL));
    public static final Block STRIPPED_BIRCH_LOG = a("stripped_birch_log", (Block) a(MaterialMapColor.SAND, MaterialMapColor.SAND));
    public static final Block STRIPPED_JUNGLE_LOG = a("stripped_jungle_log", (Block) a(MaterialMapColor.DIRT, MaterialMapColor.DIRT));
    public static final Block STRIPPED_ACACIA_LOG = a("stripped_acacia_log", (Block) a(MaterialMapColor.COLOR_ORANGE, MaterialMapColor.COLOR_ORANGE));
    public static final Block STRIPPED_DARK_OAK_LOG = a("stripped_dark_oak_log", (Block) a(MaterialMapColor.COLOR_BROWN, MaterialMapColor.COLOR_BROWN));
    public static final Block STRIPPED_OAK_LOG = a("stripped_oak_log", (Block) a(MaterialMapColor.WOOD, MaterialMapColor.WOOD));
    public static final Block OAK_WOOD = a("oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOD).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_WOOD = a("spruce_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_WOOD = a("birch_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_WOOD = a("jungle_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_WOOD = a("acacia_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_GRAY).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_WOOD = a("dark_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_BROWN).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_OAK_WOOD = a("stripped_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOD).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_SPRUCE_WOOD = a("stripped_spruce_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_BIRCH_WOOD = a("stripped_birch_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_JUNGLE_WOOD = a("stripped_jungle_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_ACACIA_WOOD = a("stripped_acacia_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block STRIPPED_DARK_OAK_WOOD = a("stripped_dark_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_BROWN).d(2.0F).a(SoundEffectType.WOOD))));
    public static final Block OAK_LEAVES = a("oak_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block SPRUCE_LEAVES = a("spruce_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block BIRCH_LEAVES = a("birch_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block JUNGLE_LEAVES = a("jungle_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block ACACIA_LEAVES = a("acacia_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block DARK_OAK_LEAVES = a("dark_oak_leaves", (Block) a(SoundEffectType.GRASS));
    public static final Block AZALEA_LEAVES = a("azalea_leaves", (Block) a(SoundEffectType.AZALEA_LEAVES));
    public static final Block FLOWERING_AZALEA_LEAVES = a("flowering_azalea_leaves", (Block) a(SoundEffectType.AZALEA_LEAVES));
    public static final Block SPONGE = a("sponge", (Block) (new BlockSponge(BlockBase.Info.a(Material.SPONGE).d(0.6F).a(SoundEffectType.GRASS))));
    public static final Block WET_SPONGE = a("wet_sponge", (Block) (new BlockWetSponge(BlockBase.Info.a(Material.SPONGE).d(0.6F).a(SoundEffectType.GRASS))));
    public static final Block GLASS = a("glass", (Block) (new BlockGlass(BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b().a(Blocks::a).a(Blocks::b).b(Blocks::b).c(Blocks::b))));
    public static final Block LAPIS_ORE = a("lapis_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F), UniformInt.a(2, 5))));
    public static final Block DEEPSLATE_LAPIS_ORE = a("deepslate_lapis_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.LAPIS_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE), UniformInt.a(2, 5))));
    public static final Block LAPIS_BLOCK = a("lapis_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.LAPIS).h().a(3.0F, 3.0F)));
    public static final Block DISPENSER = a("dispenser", (Block) (new BlockDispenser(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block SANDSTONE = a("sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().d(0.8F)));
    public static final Block CHISELED_SANDSTONE = a("chiseled_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().d(0.8F)));
    public static final Block CUT_SANDSTONE = a("cut_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().d(0.8F)));
    public static final Block NOTE_BLOCK = a("note_block", (Block) (new BlockNote(BlockBase.Info.a(Material.WOOD).a(SoundEffectType.WOOD).d(0.8F))));
    public static final Block WHITE_BED = a("white_bed", (Block) a(EnumColor.WHITE));
    public static final Block ORANGE_BED = a("orange_bed", (Block) a(EnumColor.ORANGE));
    public static final Block MAGENTA_BED = a("magenta_bed", (Block) a(EnumColor.MAGENTA));
    public static final Block LIGHT_BLUE_BED = a("light_blue_bed", (Block) a(EnumColor.LIGHT_BLUE));
    public static final Block YELLOW_BED = a("yellow_bed", (Block) a(EnumColor.YELLOW));
    public static final Block LIME_BED = a("lime_bed", (Block) a(EnumColor.LIME));
    public static final Block PINK_BED = a("pink_bed", (Block) a(EnumColor.PINK));
    public static final Block GRAY_BED = a("gray_bed", (Block) a(EnumColor.GRAY));
    public static final Block LIGHT_GRAY_BED = a("light_gray_bed", (Block) a(EnumColor.LIGHT_GRAY));
    public static final Block CYAN_BED = a("cyan_bed", (Block) a(EnumColor.CYAN));
    public static final Block PURPLE_BED = a("purple_bed", (Block) a(EnumColor.PURPLE));
    public static final Block BLUE_BED = a("blue_bed", (Block) a(EnumColor.BLUE));
    public static final Block BROWN_BED = a("brown_bed", (Block) a(EnumColor.BROWN));
    public static final Block GREEN_BED = a("green_bed", (Block) a(EnumColor.GREEN));
    public static final Block RED_BED = a("red_bed", (Block) a(EnumColor.RED));
    public static final Block BLACK_BED = a("black_bed", (Block) a(EnumColor.BLACK));
    public static final Block POWERED_RAIL = a("powered_rail", (Block) (new BlockPoweredRail(BlockBase.Info.a(Material.DECORATION).a().d(0.7F).a(SoundEffectType.METAL))));
    public static final Block DETECTOR_RAIL = a("detector_rail", (Block) (new BlockMinecartDetector(BlockBase.Info.a(Material.DECORATION).a().d(0.7F).a(SoundEffectType.METAL))));
    public static final Block STICKY_PISTON = a("sticky_piston", (Block) a(true));
    public static final Block COBWEB = a("cobweb", (Block) (new BlockWeb(BlockBase.Info.a(Material.WEB).a().h().d(4.0F))));
    public static final Block GRASS = a("grass", (Block) (new BlockLongGrass(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block FERN = a("fern", (Block) (new BlockLongGrass(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block DEAD_BUSH = a("dead_bush", (Block) (new BlockDeadBush(BlockBase.Info.a(Material.REPLACEABLE_PLANT, MaterialMapColor.WOOD).a().c().a(SoundEffectType.GRASS))));
    public static final Block SEAGRASS = a("seagrass", (Block) (new SeagrassBlock(BlockBase.Info.a(Material.REPLACEABLE_WATER_PLANT).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block TALL_SEAGRASS = a("tall_seagrass", (Block) (new TallSeagrassBlock(BlockBase.Info.a(Material.REPLACEABLE_WATER_PLANT).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block PISTON = a("piston", (Block) a(false));
    public static final Block PISTON_HEAD = a("piston_head", (Block) (new BlockPistonExtension(BlockBase.Info.a(Material.PISTON).d(1.5F).f())));
    public static final Block WHITE_WOOL = a("white_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.SNOW).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block ORANGE_WOOL = a("orange_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_ORANGE).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block MAGENTA_WOOL = a("magenta_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_MAGENTA).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block LIGHT_BLUE_WOOL = a("light_blue_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_LIGHT_BLUE).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block YELLOW_WOOL = a("yellow_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_YELLOW).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block LIME_WOOL = a("lime_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_LIGHT_GREEN).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block PINK_WOOL = a("pink_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_PINK).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block GRAY_WOOL = a("gray_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_GRAY).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block LIGHT_GRAY_WOOL = a("light_gray_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_LIGHT_GRAY).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block CYAN_WOOL = a("cyan_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_CYAN).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block PURPLE_WOOL = a("purple_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_PURPLE).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block BLUE_WOOL = a("blue_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_BLUE).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block BROWN_WOOL = a("brown_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_BROWN).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block GREEN_WOOL = a("green_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_GREEN).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block RED_WOOL = a("red_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_RED).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block BLACK_WOOL = a("black_wool", new Block(BlockBase.Info.a(Material.WOOL, MaterialMapColor.COLOR_BLACK).d(0.8F).a(SoundEffectType.WOOL)));
    public static final Block MOVING_PISTON = a("moving_piston", (Block) (new BlockPistonMoving(BlockBase.Info.a(Material.PISTON).d(-1.0F).e().f().b().a(Blocks::b).b(Blocks::b).c(Blocks::b))));
    public static final Block DANDELION = a("dandelion", (Block) (new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block POPPY = a("poppy", (Block) (new BlockFlowers(MobEffects.NIGHT_VISION, 5, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block BLUE_ORCHID = a("blue_orchid", (Block) (new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block ALLIUM = a("allium", (Block) (new BlockFlowers(MobEffects.FIRE_RESISTANCE, 4, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block AZURE_BLUET = a("azure_bluet", (Block) (new BlockFlowers(MobEffects.BLINDNESS, 8, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block RED_TULIP = a("red_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block ORANGE_TULIP = a("orange_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block WHITE_TULIP = a("white_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block PINK_TULIP = a("pink_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block OXEYE_DAISY = a("oxeye_daisy", (Block) (new BlockFlowers(MobEffects.REGENERATION, 8, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block CORNFLOWER = a("cornflower", (Block) (new BlockFlowers(MobEffects.JUMP, 6, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block WITHER_ROSE = a("wither_rose", (Block) (new BlockWitherRose(MobEffects.WITHER, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block LILY_OF_THE_VALLEY = a("lily_of_the_valley", (Block) (new BlockFlowers(MobEffects.POISON, 12, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block BROWN_MUSHROOM = a("brown_mushroom", (Block) (new BlockMushroom(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_BROWN).a().d().c().a(SoundEffectType.GRASS).a((iblockdata) -> {
        return 1;
    }).d(Blocks::a), () -> {
        return BiomeDecoratorGroups.HUGE_BROWN_MUSHROOM;
    })));
    public static final Block RED_MUSHROOM = a("red_mushroom", (Block) (new BlockMushroom(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_RED).a().d().c().a(SoundEffectType.GRASS).d(Blocks::a), () -> {
        return BiomeDecoratorGroups.HUGE_RED_MUSHROOM;
    })));
    public static final Block GOLD_BLOCK = a("gold_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.GOLD).h().a(3.0F, 6.0F).a(SoundEffectType.METAL)));
    public static final Block IRON_BLOCK = a("iron_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.METAL).h().a(5.0F, 6.0F).a(SoundEffectType.METAL)));
    public static final Block BRICKS = a("bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_RED).h().a(2.0F, 6.0F)));
    public static final Block TNT = a("tnt", (Block) (new BlockTNT(BlockBase.Info.a(Material.EXPLOSIVE).c().a(SoundEffectType.GRASS))));
    public static final Block BOOKSHELF = a("bookshelf", new Block(BlockBase.Info.a(Material.WOOD).d(1.5F).a(SoundEffectType.WOOD)));
    public static final Block MOSSY_COBBLESTONE = a("mossy_cobblestone", new Block(BlockBase.Info.a(Material.STONE).h().a(2.0F, 6.0F)));
    public static final Block OBSIDIAN = a("obsidian", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(50.0F, 1200.0F)));
    public static final Block TORCH = a("torch", (Block) (new BlockTorch(BlockBase.Info.a(Material.DECORATION).a().c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.WOOD), Particles.FLAME)));
    public static final Block WALL_TORCH = a("wall_torch", (Block) (new BlockTorchWall(BlockBase.Info.a(Material.DECORATION).a().c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.WOOD).a(Blocks.TORCH), Particles.FLAME)));
    public static final Block FIRE = a("fire", (Block) (new BlockFire(BlockBase.Info.a(Material.FIRE, MaterialMapColor.FIRE).a().c().a((iblockdata) -> {
        return 15;
    }).a(SoundEffectType.WOOL))));
    public static final Block SOUL_FIRE = a("soul_fire", (Block) (new BlockSoulFire(BlockBase.Info.a(Material.FIRE, MaterialMapColor.COLOR_LIGHT_BLUE).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.WOOL))));
    public static final Block SPAWNER = a("spawner", (Block) (new BlockMobSpawner(BlockBase.Info.a(Material.STONE).h().d(5.0F).a(SoundEffectType.METAL).b())));
    public static final Block OAK_STAIRS = a("oak_stairs", (Block) (new BlockStairs(Blocks.OAK_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.OAK_PLANKS))));
    public static final Block CHEST = a("chest", (Block) (new BlockChest(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD), () -> {
        return TileEntityTypes.CHEST;
    })));
    public static final Block REDSTONE_WIRE = a("redstone_wire", (Block) (new BlockRedstoneWire(BlockBase.Info.a(Material.DECORATION).a().c())));
    public static final Block DIAMOND_ORE = a("diamond_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F), UniformInt.a(3, 7))));
    public static final Block DEEPSLATE_DIAMOND_ORE = a("deepslate_diamond_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.DIAMOND_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE), UniformInt.a(3, 7))));
    public static final Block DIAMOND_BLOCK = a("diamond_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.DIAMOND).h().a(5.0F, 6.0F).a(SoundEffectType.METAL)));
    public static final Block CRAFTING_TABLE = a("crafting_table", (Block) (new BlockWorkbench(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block WHEAT = a("wheat", (Block) (new BlockCrops(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.CROP))));
    public static final Block FARMLAND = a("farmland", (Block) (new BlockSoil(BlockBase.Info.a(Material.DIRT).d().d(0.6F).a(SoundEffectType.GRAVEL).c(Blocks::a).b(Blocks::a))));
    public static final Block FURNACE = a("furnace", (Block) (new BlockFurnaceFurace(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block OAK_SIGN = a("oak_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.OAK)));
    public static final Block SPRUCE_SIGN = a("spruce_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.SPRUCE)));
    public static final Block BIRCH_SIGN = a("birch_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.BIRCH)));
    public static final Block ACACIA_SIGN = a("acacia_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.ACACIA)));
    public static final Block JUNGLE_SIGN = a("jungle_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.JUNGLE)));
    public static final Block DARK_OAK_SIGN = a("dark_oak_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.DARK_OAK)));
    public static final Block OAK_DOOR = a("oak_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block LADDER = a("ladder", (Block) (new BlockLadder(BlockBase.Info.a(Material.DECORATION).d(0.4F).a(SoundEffectType.LADDER).b())));
    public static final Block RAIL = a("rail", (Block) (new BlockMinecartTrack(BlockBase.Info.a(Material.DECORATION).a().d(0.7F).a(SoundEffectType.METAL))));
    public static final Block COBBLESTONE_STAIRS = a("cobblestone_stairs", (Block) (new BlockStairs(Blocks.COBBLESTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block OAK_WALL_SIGN = a("oak_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.OAK_SIGN), BlockPropertyWood.OAK)));
    public static final Block SPRUCE_WALL_SIGN = a("spruce_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.SPRUCE_SIGN), BlockPropertyWood.SPRUCE)));
    public static final Block BIRCH_WALL_SIGN = a("birch_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.BIRCH_SIGN), BlockPropertyWood.BIRCH)));
    public static final Block ACACIA_WALL_SIGN = a("acacia_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.ACACIA_SIGN), BlockPropertyWood.ACACIA)));
    public static final Block JUNGLE_WALL_SIGN = a("jungle_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.JUNGLE_SIGN), BlockPropertyWood.JUNGLE)));
    public static final Block DARK_OAK_WALL_SIGN = a("dark_oak_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_LOG.s()).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.DARK_OAK_SIGN), BlockPropertyWood.DARK_OAK)));
    public static final Block LEVER = a("lever", (Block) (new BlockLever(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block STONE_PRESSURE_PLATE = a("stone_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.a(Material.STONE).h().a().d(0.5F))));
    public static final Block IRON_DOOR = a("iron_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.METAL, MaterialMapColor.METAL).h().d(5.0F).a(SoundEffectType.METAL).b())));
    public static final Block OAK_PRESSURE_PLATE = a("oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_PRESSURE_PLATE = a("spruce_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_PRESSURE_PLATE = a("birch_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_PRESSURE_PLATE = a("jungle_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_PRESSURE_PLATE = a("acacia_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_PRESSURE_PLATE = a("dark_oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block REDSTONE_ORE = a("redstone_ore", (Block) (new BlockRedstoneOre(BlockBase.Info.a(Material.STONE).h().d().a(a(9)).a(3.0F, 3.0F))));
    public static final Block DEEPSLATE_REDSTONE_ORE = a("deepslate_redstone_ore", (Block) (new BlockRedstoneOre(BlockBase.Info.a((BlockBase) Blocks.REDSTONE_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE))));
    public static final Block REDSTONE_TORCH = a("redstone_torch", (Block) (new BlockRedstoneTorch(BlockBase.Info.a(Material.DECORATION).a().c().a(a(7)).a(SoundEffectType.WOOD))));
    public static final Block REDSTONE_WALL_TORCH = a("redstone_wall_torch", (Block) (new BlockRedstoneTorchWall(BlockBase.Info.a(Material.DECORATION).a().c().a(a(7)).a(SoundEffectType.WOOD).a(Blocks.REDSTONE_TORCH))));
    public static final Block STONE_BUTTON = a("stone_button", (Block) (new BlockStoneButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F))));
    public static final Block SNOW = a("snow", (Block) (new BlockSnow(BlockBase.Info.a(Material.TOP_SNOW).d().d(0.1F).h().a(SoundEffectType.SNOW))));
    public static final Block ICE = a("ice", (Block) (new BlockIce(BlockBase.Info.a(Material.ICE).a(0.98F).d().d(0.5F).a(SoundEffectType.GLASS).b().a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    }))));
    public static final Block SNOW_BLOCK = a("snow_block", new Block(BlockBase.Info.a(Material.SNOW).h().d(0.2F).a(SoundEffectType.SNOW)));
    public static final Block CACTUS = a("cactus", (Block) (new BlockCactus(BlockBase.Info.a(Material.CACTUS).d().d(0.4F).a(SoundEffectType.WOOL))));
    public static final Block CLAY = a("clay", new Block(BlockBase.Info.a(Material.CLAY).d(0.6F).a(SoundEffectType.GRAVEL)));
    public static final Block SUGAR_CANE = a("sugar_cane", (Block) (new BlockReed(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.GRASS))));
    public static final Block JUKEBOX = a("jukebox", (Block) (new BlockJukeBox(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).a(2.0F, 6.0F))));
    public static final Block OAK_FENCE = a("oak_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block PUMPKIN = a("pumpkin", (Block) (new BlockPumpkin(BlockBase.Info.a(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block NETHERRACK = a("netherrack", (Block) (new BlockNetherrack(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().d(0.4F).a(SoundEffectType.NETHERRACK))));
    public static final Block SOUL_SAND = a("soul_sand", (Block) (new BlockSlowSand(BlockBase.Info.a(Material.SAND, MaterialMapColor.COLOR_BROWN).d(0.5F).b(0.4F).a(SoundEffectType.SOUL_SAND).a(Blocks::b).a(Blocks::a).c(Blocks::a).b(Blocks::a))));
    public static final Block SOUL_SOIL = a("soul_soil", new Block(BlockBase.Info.a(Material.DIRT, MaterialMapColor.COLOR_BROWN).d(0.5F).a(SoundEffectType.SOUL_SOIL)));
    public static final Block BASALT = a("basalt", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(1.25F, 4.2F).a(SoundEffectType.BASALT))));
    public static final Block POLISHED_BASALT = a("polished_basalt", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(1.25F, 4.2F).a(SoundEffectType.BASALT))));
    public static final Block SOUL_TORCH = a("soul_torch", (Block) (new BlockTorch(BlockBase.Info.a(Material.DECORATION).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.WOOD), Particles.SOUL_FIRE_FLAME)));
    public static final Block SOUL_WALL_TORCH = a("soul_wall_torch", (Block) (new BlockTorchWall(BlockBase.Info.a(Material.DECORATION).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.WOOD).a(Blocks.SOUL_TORCH), Particles.SOUL_FIRE_FLAME)));
    public static final Block GLOWSTONE = a("glowstone", new Block(BlockBase.Info.a(Material.GLASS, MaterialMapColor.SAND).d(0.3F).a(SoundEffectType.GLASS).a((iblockdata) -> {
        return 15;
    })));
    public static final Block NETHER_PORTAL = a("nether_portal", (Block) (new BlockPortal(BlockBase.Info.a(Material.PORTAL).a().d().d(-1.0F).a(SoundEffectType.GLASS).a((iblockdata) -> {
        return 11;
    }))));
    public static final Block CARVED_PUMPKIN = a("carved_pumpkin", (Block) (new BlockPumpkinCarved(BlockBase.Info.a(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).d(1.0F).a(SoundEffectType.WOOD).a(Blocks::b))));
    public static final Block JACK_O_LANTERN = a("jack_o_lantern", (Block) (new BlockPumpkinCarved(BlockBase.Info.a(Material.VEGETABLE, MaterialMapColor.COLOR_ORANGE).d(1.0F).a(SoundEffectType.WOOD).a((iblockdata) -> {
        return 15;
    }).a(Blocks::b))));
    public static final Block CAKE = a("cake", (Block) (new BlockCake(BlockBase.Info.a(Material.CAKE).d(0.5F).a(SoundEffectType.WOOL))));
    public static final Block REPEATER = a("repeater", (Block) (new BlockRepeater(BlockBase.Info.a(Material.DECORATION).c().a(SoundEffectType.WOOD))));
    public static final Block WHITE_STAINED_GLASS = a("white_stained_glass", (Block) b(EnumColor.WHITE));
    public static final Block ORANGE_STAINED_GLASS = a("orange_stained_glass", (Block) b(EnumColor.ORANGE));
    public static final Block MAGENTA_STAINED_GLASS = a("magenta_stained_glass", (Block) b(EnumColor.MAGENTA));
    public static final Block LIGHT_BLUE_STAINED_GLASS = a("light_blue_stained_glass", (Block) b(EnumColor.LIGHT_BLUE));
    public static final Block YELLOW_STAINED_GLASS = a("yellow_stained_glass", (Block) b(EnumColor.YELLOW));
    public static final Block LIME_STAINED_GLASS = a("lime_stained_glass", (Block) b(EnumColor.LIME));
    public static final Block PINK_STAINED_GLASS = a("pink_stained_glass", (Block) b(EnumColor.PINK));
    public static final Block GRAY_STAINED_GLASS = a("gray_stained_glass", (Block) b(EnumColor.GRAY));
    public static final Block LIGHT_GRAY_STAINED_GLASS = a("light_gray_stained_glass", (Block) b(EnumColor.LIGHT_GRAY));
    public static final Block CYAN_STAINED_GLASS = a("cyan_stained_glass", (Block) b(EnumColor.CYAN));
    public static final Block PURPLE_STAINED_GLASS = a("purple_stained_glass", (Block) b(EnumColor.PURPLE));
    public static final Block BLUE_STAINED_GLASS = a("blue_stained_glass", (Block) b(EnumColor.BLUE));
    public static final Block BROWN_STAINED_GLASS = a("brown_stained_glass", (Block) b(EnumColor.BROWN));
    public static final Block GREEN_STAINED_GLASS = a("green_stained_glass", (Block) b(EnumColor.GREEN));
    public static final Block RED_STAINED_GLASS = a("red_stained_glass", (Block) b(EnumColor.RED));
    public static final Block BLACK_STAINED_GLASS = a("black_stained_glass", (Block) b(EnumColor.BLACK));
    public static final Block OAK_TRAPDOOR = a("oak_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOD).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block SPRUCE_TRAPDOOR = a("spruce_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block BIRCH_TRAPDOOR = a("birch_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block JUNGLE_TRAPDOOR = a("jungle_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block ACACIA_TRAPDOOR = a("acacia_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block DARK_OAK_TRAPDOOR = a("dark_oak_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_BROWN).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block STONE_BRICKS = a("stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block MOSSY_STONE_BRICKS = a("mossy_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block CRACKED_STONE_BRICKS = a("cracked_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block CHISELED_STONE_BRICKS = a("chiseled_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block INFESTED_STONE = a("infested_stone", (Block) (new BlockMonsterEggs(Blocks.STONE, BlockBase.Info.a(Material.CLAY))));
    public static final Block INFESTED_COBBLESTONE = a("infested_cobblestone", (Block) (new BlockMonsterEggs(Blocks.COBBLESTONE, BlockBase.Info.a(Material.CLAY))));
    public static final Block INFESTED_STONE_BRICKS = a("infested_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.STONE_BRICKS, BlockBase.Info.a(Material.CLAY))));
    public static final Block INFESTED_MOSSY_STONE_BRICKS = a("infested_mossy_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.MOSSY_STONE_BRICKS, BlockBase.Info.a(Material.CLAY))));
    public static final Block INFESTED_CRACKED_STONE_BRICKS = a("infested_cracked_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.CRACKED_STONE_BRICKS, BlockBase.Info.a(Material.CLAY))));
    public static final Block INFESTED_CHISELED_STONE_BRICKS = a("infested_chiseled_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.CHISELED_STONE_BRICKS, BlockBase.Info.a(Material.CLAY))));
    public static final Block BROWN_MUSHROOM_BLOCK = a("brown_mushroom_block", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).d(0.2F).a(SoundEffectType.WOOD))));
    public static final Block RED_MUSHROOM_BLOCK = a("red_mushroom_block", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_RED).d(0.2F).a(SoundEffectType.WOOD))));
    public static final Block MUSHROOM_STEM = a("mushroom_stem", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOL).d(0.2F).a(SoundEffectType.WOOD))));
    public static final Block IRON_BARS = a("iron_bars", (Block) (new BlockIronBars(BlockBase.Info.a(Material.METAL, MaterialMapColor.NONE).h().a(5.0F, 6.0F).a(SoundEffectType.METAL).b())));
    public static final Block CHAIN = a("chain", (Block) (new BlockChain(BlockBase.Info.a(Material.METAL, MaterialMapColor.NONE).h().a(5.0F, 6.0F).a(SoundEffectType.CHAIN).b())));
    public static final Block GLASS_PANE = a("glass_pane", (Block) (new BlockIronBars(BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block MELON = a("melon", (Block) (new BlockMelon(BlockBase.Info.a(Material.VEGETABLE, MaterialMapColor.COLOR_LIGHT_GREEN).d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block ATTACHED_PUMPKIN_STEM = a("attached_pumpkin_stem", (Block) (new BlockStemAttached((BlockStemmed) Blocks.PUMPKIN, () -> {
        return Items.PUMPKIN_SEEDS;
    }, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.WOOD))));
    public static final Block ATTACHED_MELON_STEM = a("attached_melon_stem", (Block) (new BlockStemAttached((BlockStemmed) Blocks.MELON, () -> {
        return Items.MELON_SEEDS;
    }, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.WOOD))));
    public static final Block PUMPKIN_STEM = a("pumpkin_stem", (Block) (new BlockStem((BlockStemmed) Blocks.PUMPKIN, () -> {
        return Items.PUMPKIN_SEEDS;
    }, BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.HARD_CROP))));
    public static final Block MELON_STEM = a("melon_stem", (Block) (new BlockStem((BlockStemmed) Blocks.MELON, () -> {
        return Items.MELON_SEEDS;
    }, BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.HARD_CROP))));
    public static final Block VINE = a("vine", (Block) (new BlockVine(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().d().d(0.2F).a(SoundEffectType.VINE))));
    public static final Block GLOW_LICHEN = a("glow_lichen", (Block) (new GlowLichenBlock(BlockBase.Info.a(Material.REPLACEABLE_PLANT, MaterialMapColor.GLOW_LICHEN).a().d(0.2F).a(SoundEffectType.GLOW_LICHEN).a(GlowLichenBlock.b(7)))));
    public static final Block OAK_FENCE_GATE = a("oak_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block BRICK_STAIRS = a("brick_stairs", (Block) (new BlockStairs(Blocks.BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.BRICKS))));
    public static final Block STONE_BRICK_STAIRS = a("stone_brick_stairs", (Block) (new BlockStairs(Blocks.STONE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.STONE_BRICKS))));
    public static final Block MYCELIUM = a("mycelium", (Block) (new BlockMycel(BlockBase.Info.a(Material.GRASS, MaterialMapColor.COLOR_PURPLE).d().d(0.6F).a(SoundEffectType.GRASS))));
    public static final Block LILY_PAD = a("lily_pad", (Block) (new BlockWaterLily(BlockBase.Info.a(Material.PLANT).c().a(SoundEffectType.LILY_PAD).b())));
    public static final Block NETHER_BRICKS = a("nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS)));
    public static final Block NETHER_BRICK_FENCE = a("nether_brick_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS))));
    public static final Block NETHER_BRICK_STAIRS = a("nether_brick_stairs", (Block) (new BlockStairs(Blocks.NETHER_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.NETHER_BRICKS))));
    public static final Block NETHER_WART = a("nether_wart", (Block) (new BlockNetherWart(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_RED).a().d().a(SoundEffectType.NETHER_WART))));
    public static final Block ENCHANTING_TABLE = a("enchanting_table", (Block) (new BlockEnchantmentTable(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_RED).h().a(5.0F, 1200.0F))));
    public static final Block BREWING_STAND = a("brewing_stand", (Block) (new BlockBrewingStand(BlockBase.Info.a(Material.METAL).h().d(0.5F).a((iblockdata) -> {
        return 1;
    }).b())));
    public static final Block CAULDRON = a("cauldron", (Block) (new BlockCauldron(BlockBase.Info.a(Material.METAL, MaterialMapColor.STONE).h().d(2.0F).b())));
    public static final Block WATER_CAULDRON = a("water_cauldron", (Block) (new LayeredCauldronBlock(BlockBase.Info.a((BlockBase) Blocks.CAULDRON), LayeredCauldronBlock.RAIN, CauldronInteraction.WATER)));
    public static final Block LAVA_CAULDRON = a("lava_cauldron", (Block) (new LavaCauldronBlock(BlockBase.Info.a((BlockBase) Blocks.CAULDRON).a((iblockdata) -> {
        return 15;
    }))));
    public static final Block POWDER_SNOW_CAULDRON = a("powder_snow_cauldron", (Block) (new PowderSnowCauldronBlock(BlockBase.Info.a((BlockBase) Blocks.CAULDRON), LayeredCauldronBlock.SNOW, CauldronInteraction.POWDER_SNOW)));
    public static final Block END_PORTAL = a("end_portal", (Block) (new BlockEnderPortal(BlockBase.Info.a(Material.PORTAL, MaterialMapColor.COLOR_BLACK).a().a((iblockdata) -> {
        return 15;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block END_PORTAL_FRAME = a("end_portal_frame", (Block) (new BlockEnderPortalFrame(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GREEN).a(SoundEffectType.GLASS).a((iblockdata) -> {
        return 1;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block END_STONE = a("end_stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().a(3.0F, 9.0F)));
    public static final Block DRAGON_EGG = a("dragon_egg", (Block) (new BlockDragonEgg(BlockBase.Info.a(Material.EGG, MaterialMapColor.COLOR_BLACK).a(3.0F, 9.0F).a((iblockdata) -> {
        return 1;
    }).b())));
    public static final Block REDSTONE_LAMP = a("redstone_lamp", (Block) (new BlockRedstoneLamp(BlockBase.Info.a(Material.BUILDABLE_GLASS).a(a(15)).d(0.3F).a(SoundEffectType.GLASS).a(Blocks::b))));
    public static final Block COCOA = a("cocoa", (Block) (new BlockCocoa(BlockBase.Info.a(Material.PLANT).d().a(0.2F, 3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block SANDSTONE_STAIRS = a("sandstone_stairs", (Block) (new BlockStairs(Blocks.SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SANDSTONE))));
    public static final Block EMERALD_ORE = a("emerald_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F), UniformInt.a(3, 7))));
    public static final Block DEEPSLATE_EMERALD_ORE = a("deepslate_emerald_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.EMERALD_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE), UniformInt.a(3, 7))));
    public static final Block ENDER_CHEST = a("ender_chest", (Block) (new BlockEnderChest(BlockBase.Info.a(Material.STONE).h().a(22.5F, 600.0F).a((iblockdata) -> {
        return 7;
    }))));
    public static final Block TRIPWIRE_HOOK = a("tripwire_hook", (Block) (new BlockTripwireHook(BlockBase.Info.a(Material.DECORATION).a())));
    public static final Block TRIPWIRE = a("tripwire", (Block) (new BlockTripwire((BlockTripwireHook) Blocks.TRIPWIRE_HOOK, BlockBase.Info.a(Material.DECORATION).a())));
    public static final Block EMERALD_BLOCK = a("emerald_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.EMERALD).h().a(5.0F, 6.0F).a(SoundEffectType.METAL)));
    public static final Block SPRUCE_STAIRS = a("spruce_stairs", (Block) (new BlockStairs(Blocks.SPRUCE_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SPRUCE_PLANKS))));
    public static final Block BIRCH_STAIRS = a("birch_stairs", (Block) (new BlockStairs(Blocks.BIRCH_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.BIRCH_PLANKS))));
    public static final Block JUNGLE_STAIRS = a("jungle_stairs", (Block) (new BlockStairs(Blocks.JUNGLE_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.JUNGLE_PLANKS))));
    public static final Block COMMAND_BLOCK = a("command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_BROWN).h().a(-1.0F, 3600000.0F).f(), false)));
    public static final Block BEACON = a("beacon", (Block) (new BlockBeacon(BlockBase.Info.a(Material.GLASS, MaterialMapColor.DIAMOND).d(3.0F).a((iblockdata) -> {
        return 15;
    }).b().a(Blocks::b))));
    public static final Block COBBLESTONE_WALL = a("cobblestone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block MOSSY_COBBLESTONE_WALL = a("mossy_cobblestone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block FLOWER_POT = a("flower_pot", (Block) (new BlockFlowerPot(Blocks.AIR, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_OAK_SAPLING = a("potted_oak_sapling", (Block) (new BlockFlowerPot(Blocks.OAK_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_SPRUCE_SAPLING = a("potted_spruce_sapling", (Block) (new BlockFlowerPot(Blocks.SPRUCE_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_BIRCH_SAPLING = a("potted_birch_sapling", (Block) (new BlockFlowerPot(Blocks.BIRCH_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_JUNGLE_SAPLING = a("potted_jungle_sapling", (Block) (new BlockFlowerPot(Blocks.JUNGLE_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_ACACIA_SAPLING = a("potted_acacia_sapling", (Block) (new BlockFlowerPot(Blocks.ACACIA_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_DARK_OAK_SAPLING = a("potted_dark_oak_sapling", (Block) (new BlockFlowerPot(Blocks.DARK_OAK_SAPLING, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_FERN = a("potted_fern", (Block) (new BlockFlowerPot(Blocks.FERN, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_DANDELION = a("potted_dandelion", (Block) (new BlockFlowerPot(Blocks.DANDELION, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_POPPY = a("potted_poppy", (Block) (new BlockFlowerPot(Blocks.POPPY, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_BLUE_ORCHID = a("potted_blue_orchid", (Block) (new BlockFlowerPot(Blocks.BLUE_ORCHID, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_ALLIUM = a("potted_allium", (Block) (new BlockFlowerPot(Blocks.ALLIUM, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_AZURE_BLUET = a("potted_azure_bluet", (Block) (new BlockFlowerPot(Blocks.AZURE_BLUET, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_RED_TULIP = a("potted_red_tulip", (Block) (new BlockFlowerPot(Blocks.RED_TULIP, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_ORANGE_TULIP = a("potted_orange_tulip", (Block) (new BlockFlowerPot(Blocks.ORANGE_TULIP, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_WHITE_TULIP = a("potted_white_tulip", (Block) (new BlockFlowerPot(Blocks.WHITE_TULIP, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_PINK_TULIP = a("potted_pink_tulip", (Block) (new BlockFlowerPot(Blocks.PINK_TULIP, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_OXEYE_DAISY = a("potted_oxeye_daisy", (Block) (new BlockFlowerPot(Blocks.OXEYE_DAISY, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_CORNFLOWER = a("potted_cornflower", (Block) (new BlockFlowerPot(Blocks.CORNFLOWER, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_LILY_OF_THE_VALLEY = a("potted_lily_of_the_valley", (Block) (new BlockFlowerPot(Blocks.LILY_OF_THE_VALLEY, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_WITHER_ROSE = a("potted_wither_rose", (Block) (new BlockFlowerPot(Blocks.WITHER_ROSE, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_RED_MUSHROOM = a("potted_red_mushroom", (Block) (new BlockFlowerPot(Blocks.RED_MUSHROOM, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_BROWN_MUSHROOM = a("potted_brown_mushroom", (Block) (new BlockFlowerPot(Blocks.BROWN_MUSHROOM, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_DEAD_BUSH = a("potted_dead_bush", (Block) (new BlockFlowerPot(Blocks.DEAD_BUSH, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_CACTUS = a("potted_cactus", (Block) (new BlockFlowerPot(Blocks.CACTUS, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block CARROTS = a("carrots", (Block) (new BlockCarrots(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.CROP))));
    public static final Block POTATOES = a("potatoes", (Block) (new BlockPotatoes(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.CROP))));
    public static final Block OAK_BUTTON = a("oak_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_BUTTON = a("spruce_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_BUTTON = a("birch_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_BUTTON = a("jungle_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_BUTTON = a("acacia_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_BUTTON = a("dark_oak_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block SKELETON_SKULL = a("skeleton_skull", (Block) (new BlockSkull(BlockSkull.Type.SKELETON, BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block SKELETON_WALL_SKULL = a("skeleton_wall_skull", (Block) (new BlockSkullWall(BlockSkull.Type.SKELETON, BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.SKELETON_SKULL))));
    public static final Block WITHER_SKELETON_SKULL = a("wither_skeleton_skull", (Block) (new BlockWitherSkull(BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block WITHER_SKELETON_WALL_SKULL = a("wither_skeleton_wall_skull", (Block) (new BlockWitherSkullWall(BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.WITHER_SKELETON_SKULL))));
    public static final Block ZOMBIE_HEAD = a("zombie_head", (Block) (new BlockSkull(BlockSkull.Type.ZOMBIE, BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block ZOMBIE_WALL_HEAD = a("zombie_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.ZOMBIE, BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.ZOMBIE_HEAD))));
    public static final Block PLAYER_HEAD = a("player_head", (Block) (new BlockSkullPlayer(BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block PLAYER_WALL_HEAD = a("player_wall_head", (Block) (new BlockSkullPlayerWall(BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.PLAYER_HEAD))));
    public static final Block CREEPER_HEAD = a("creeper_head", (Block) (new BlockSkull(BlockSkull.Type.CREEPER, BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block CREEPER_WALL_HEAD = a("creeper_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.CREEPER, BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.CREEPER_HEAD))));
    public static final Block DRAGON_HEAD = a("dragon_head", (Block) (new BlockSkull(BlockSkull.Type.DRAGON, BlockBase.Info.a(Material.DECORATION).d(1.0F))));
    public static final Block DRAGON_WALL_HEAD = a("dragon_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.DRAGON, BlockBase.Info.a(Material.DECORATION).d(1.0F).a(Blocks.DRAGON_HEAD))));
    public static final Block ANVIL = a("anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY_METAL, MaterialMapColor.METAL).h().a(5.0F, 1200.0F).a(SoundEffectType.ANVIL))));
    public static final Block CHIPPED_ANVIL = a("chipped_anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY_METAL, MaterialMapColor.METAL).h().a(5.0F, 1200.0F).a(SoundEffectType.ANVIL))));
    public static final Block DAMAGED_ANVIL = a("damaged_anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY_METAL, MaterialMapColor.METAL).h().a(5.0F, 1200.0F).a(SoundEffectType.ANVIL))));
    public static final Block TRAPPED_CHEST = a("trapped_chest", (Block) (new BlockChestTrapped(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = a("light_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(15, BlockBase.Info.a(Material.METAL, MaterialMapColor.GOLD).h().a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = a("heavy_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(150, BlockBase.Info.a(Material.METAL).h().a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block COMPARATOR = a("comparator", (Block) (new BlockRedstoneComparator(BlockBase.Info.a(Material.DECORATION).c().a(SoundEffectType.WOOD))));
    public static final Block DAYLIGHT_DETECTOR = a("daylight_detector", (Block) (new BlockDaylightDetector(BlockBase.Info.a(Material.WOOD).d(0.2F).a(SoundEffectType.WOOD))));
    public static final Block REDSTONE_BLOCK = a("redstone_block", (Block) (new BlockPowered(BlockBase.Info.a(Material.METAL, MaterialMapColor.FIRE).h().a(5.0F, 6.0F).a(SoundEffectType.METAL).a(Blocks::b))));
    public static final Block NETHER_QUARTZ_ORE = a("nether_quartz_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(3.0F, 3.0F).a(SoundEffectType.NETHER_ORE), UniformInt.a(2, 5))));
    public static final Block HOPPER = a("hopper", (Block) (new BlockHopper(BlockBase.Info.a(Material.METAL, MaterialMapColor.STONE).h().a(3.0F, 4.8F).a(SoundEffectType.METAL).b())));
    public static final Block QUARTZ_BLOCK = a("quartz_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().d(0.8F)));
    public static final Block CHISELED_QUARTZ_BLOCK = a("chiseled_quartz_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().d(0.8F)));
    public static final Block QUARTZ_PILLAR = a("quartz_pillar", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().d(0.8F))));
    public static final Block QUARTZ_STAIRS = a("quartz_stairs", (Block) (new BlockStairs(Blocks.QUARTZ_BLOCK.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.QUARTZ_BLOCK))));
    public static final Block ACTIVATOR_RAIL = a("activator_rail", (Block) (new BlockPoweredRail(BlockBase.Info.a(Material.DECORATION).a().d(0.7F).a(SoundEffectType.METAL))));
    public static final Block DROPPER = a("dropper", (Block) (new BlockDropper(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block WHITE_TERRACOTTA = a("white_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_WHITE).h().a(1.25F, 4.2F)));
    public static final Block ORANGE_TERRACOTTA = a("orange_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_ORANGE).h().a(1.25F, 4.2F)));
    public static final Block MAGENTA_TERRACOTTA = a("magenta_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_MAGENTA).h().a(1.25F, 4.2F)));
    public static final Block LIGHT_BLUE_TERRACOTTA = a("light_blue_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_BLUE).h().a(1.25F, 4.2F)));
    public static final Block YELLOW_TERRACOTTA = a("yellow_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_YELLOW).h().a(1.25F, 4.2F)));
    public static final Block LIME_TERRACOTTA = a("lime_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_GREEN).h().a(1.25F, 4.2F)));
    public static final Block PINK_TERRACOTTA = a("pink_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_PINK).h().a(1.25F, 4.2F)));
    public static final Block GRAY_TERRACOTTA = a("gray_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_GRAY).h().a(1.25F, 4.2F)));
    public static final Block LIGHT_GRAY_TERRACOTTA = a("light_gray_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_LIGHT_GRAY).h().a(1.25F, 4.2F)));
    public static final Block CYAN_TERRACOTTA = a("cyan_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_CYAN).h().a(1.25F, 4.2F)));
    public static final Block PURPLE_TERRACOTTA = a("purple_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_PURPLE).h().a(1.25F, 4.2F)));
    public static final Block BLUE_TERRACOTTA = a("blue_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_BLUE).h().a(1.25F, 4.2F)));
    public static final Block BROWN_TERRACOTTA = a("brown_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).h().a(1.25F, 4.2F)));
    public static final Block GREEN_TERRACOTTA = a("green_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_GREEN).h().a(1.25F, 4.2F)));
    public static final Block RED_TERRACOTTA = a("red_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_RED).h().a(1.25F, 4.2F)));
    public static final Block BLACK_TERRACOTTA = a("black_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_BLACK).h().a(1.25F, 4.2F)));
    public static final Block WHITE_STAINED_GLASS_PANE = a("white_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.WHITE, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block ORANGE_STAINED_GLASS_PANE = a("orange_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.ORANGE, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block MAGENTA_STAINED_GLASS_PANE = a("magenta_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.MAGENTA, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = a("light_blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block YELLOW_STAINED_GLASS_PANE = a("yellow_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.YELLOW, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block LIME_STAINED_GLASS_PANE = a("lime_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIME, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block PINK_STAINED_GLASS_PANE = a("pink_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PINK, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block GRAY_STAINED_GLASS_PANE = a("gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GRAY, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = a("light_gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block CYAN_STAINED_GLASS_PANE = a("cyan_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.CYAN, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block PURPLE_STAINED_GLASS_PANE = a("purple_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PURPLE, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block BLUE_STAINED_GLASS_PANE = a("blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLUE, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block BROWN_STAINED_GLASS_PANE = a("brown_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BROWN, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block GREEN_STAINED_GLASS_PANE = a("green_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GREEN, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block RED_STAINED_GLASS_PANE = a("red_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.RED, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block BLACK_STAINED_GLASS_PANE = a("black_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLACK, BlockBase.Info.a(Material.GLASS).d(0.3F).a(SoundEffectType.GLASS).b())));
    public static final Block ACACIA_STAIRS = a("acacia_stairs", (Block) (new BlockStairs(Blocks.ACACIA_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.ACACIA_PLANKS))));
    public static final Block DARK_OAK_STAIRS = a("dark_oak_stairs", (Block) (new BlockStairs(Blocks.DARK_OAK_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DARK_OAK_PLANKS))));
    public static final Block SLIME_BLOCK = a("slime_block", (Block) (new BlockSlime(BlockBase.Info.a(Material.CLAY, MaterialMapColor.GRASS).a(0.8F).a(SoundEffectType.SLIME_BLOCK).b())));
    public static final Block BARRIER = a("barrier", (Block) (new BlockBarrier(BlockBase.Info.a(Material.BARRIER).a(-1.0F, 3600000.8F).f().b().a(Blocks::a))));
    public static final Block LIGHT = a("light", (Block) (new LightBlock(BlockBase.Info.a(Material.AIR).a(-1.0F, 3600000.8F).f().b().a(LightBlock.LIGHT_EMISSION))));
    public static final Block IRON_TRAPDOOR = a("iron_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.METAL).h().d(5.0F).a(SoundEffectType.METAL).b().a(Blocks::a))));
    public static final Block PRISMARINE = a("prismarine", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_CYAN).h().a(1.5F, 6.0F)));
    public static final Block PRISMARINE_BRICKS = a("prismarine_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIAMOND).h().a(1.5F, 6.0F)));
    public static final Block DARK_PRISMARINE = a("dark_prismarine", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIAMOND).h().a(1.5F, 6.0F)));
    public static final Block PRISMARINE_STAIRS = a("prismarine_stairs", (Block) (new BlockStairs(Blocks.PRISMARINE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PRISMARINE))));
    public static final Block PRISMARINE_BRICK_STAIRS = a("prismarine_brick_stairs", (Block) (new BlockStairs(Blocks.PRISMARINE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PRISMARINE_BRICKS))));
    public static final Block DARK_PRISMARINE_STAIRS = a("dark_prismarine_stairs", (Block) (new BlockStairs(Blocks.DARK_PRISMARINE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DARK_PRISMARINE))));
    public static final Block PRISMARINE_SLAB = a("prismarine_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_CYAN).h().a(1.5F, 6.0F))));
    public static final Block PRISMARINE_BRICK_SLAB = a("prismarine_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIAMOND).h().a(1.5F, 6.0F))));
    public static final Block DARK_PRISMARINE_SLAB = a("dark_prismarine_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.DIAMOND).h().a(1.5F, 6.0F))));
    public static final Block SEA_LANTERN = a("sea_lantern", new Block(BlockBase.Info.a(Material.GLASS, MaterialMapColor.QUARTZ).d(0.3F).a(SoundEffectType.GLASS).a((iblockdata) -> {
        return 15;
    })));
    public static final Block HAY_BLOCK = a("hay_block", (Block) (new BlockHay(BlockBase.Info.a(Material.GRASS, MaterialMapColor.COLOR_YELLOW).d(0.5F).a(SoundEffectType.GRASS))));
    public static final Block WHITE_CARPET = a("white_carpet", (Block) (new BlockCarpet(EnumColor.WHITE, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.SNOW).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block ORANGE_CARPET = a("orange_carpet", (Block) (new BlockCarpet(EnumColor.ORANGE, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_ORANGE).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block MAGENTA_CARPET = a("magenta_carpet", (Block) (new BlockCarpet(EnumColor.MAGENTA, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_MAGENTA).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block LIGHT_BLUE_CARPET = a("light_blue_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_BLUE).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block YELLOW_CARPET = a("yellow_carpet", (Block) (new BlockCarpet(EnumColor.YELLOW, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_YELLOW).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block LIME_CARPET = a("lime_carpet", (Block) (new BlockCarpet(EnumColor.LIME, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_GREEN).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block PINK_CARPET = a("pink_carpet", (Block) (new BlockCarpet(EnumColor.PINK, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_PINK).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block GRAY_CARPET = a("gray_carpet", (Block) (new BlockCarpet(EnumColor.GRAY, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_GRAY).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block LIGHT_GRAY_CARPET = a("light_gray_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_LIGHT_GRAY).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block CYAN_CARPET = a("cyan_carpet", (Block) (new BlockCarpet(EnumColor.CYAN, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_CYAN).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block PURPLE_CARPET = a("purple_carpet", (Block) (new BlockCarpet(EnumColor.PURPLE, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_PURPLE).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block BLUE_CARPET = a("blue_carpet", (Block) (new BlockCarpet(EnumColor.BLUE, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BLUE).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block BROWN_CARPET = a("brown_carpet", (Block) (new BlockCarpet(EnumColor.BROWN, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BROWN).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block GREEN_CARPET = a("green_carpet", (Block) (new BlockCarpet(EnumColor.GREEN, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_GREEN).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block RED_CARPET = a("red_carpet", (Block) (new BlockCarpet(EnumColor.RED, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_RED).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block BLACK_CARPET = a("black_carpet", (Block) (new BlockCarpet(EnumColor.BLACK, BlockBase.Info.a(Material.CLOTH_DECORATION, MaterialMapColor.COLOR_BLACK).d(0.1F).a(SoundEffectType.WOOL))));
    public static final Block TERRACOTTA = a("terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().a(1.25F, 4.2F)));
    public static final Block COAL_BLOCK = a("coal_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(5.0F, 6.0F)));
    public static final Block PACKED_ICE = a("packed_ice", new Block(BlockBase.Info.a(Material.ICE_SOLID).a(0.98F).d(0.5F).a(SoundEffectType.GLASS)));
    public static final Block SUNFLOWER = a("sunflower", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block LILAC = a("lilac", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block ROSE_BUSH = a("rose_bush", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block PEONY = a("peony", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block TALL_GRASS = a("tall_grass", (Block) (new BlockTallPlant(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block LARGE_FERN = a("large_fern", (Block) (new BlockTallPlant(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.GRASS))));
    public static final Block WHITE_BANNER = a("white_banner", (Block) (new BlockBanner(EnumColor.WHITE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block ORANGE_BANNER = a("orange_banner", (Block) (new BlockBanner(EnumColor.ORANGE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block MAGENTA_BANNER = a("magenta_banner", (Block) (new BlockBanner(EnumColor.MAGENTA, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block LIGHT_BLUE_BANNER = a("light_blue_banner", (Block) (new BlockBanner(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block YELLOW_BANNER = a("yellow_banner", (Block) (new BlockBanner(EnumColor.YELLOW, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block LIME_BANNER = a("lime_banner", (Block) (new BlockBanner(EnumColor.LIME, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block PINK_BANNER = a("pink_banner", (Block) (new BlockBanner(EnumColor.PINK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block GRAY_BANNER = a("gray_banner", (Block) (new BlockBanner(EnumColor.GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block LIGHT_GRAY_BANNER = a("light_gray_banner", (Block) (new BlockBanner(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block CYAN_BANNER = a("cyan_banner", (Block) (new BlockBanner(EnumColor.CYAN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block PURPLE_BANNER = a("purple_banner", (Block) (new BlockBanner(EnumColor.PURPLE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block BLUE_BANNER = a("blue_banner", (Block) (new BlockBanner(EnumColor.BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block BROWN_BANNER = a("brown_banner", (Block) (new BlockBanner(EnumColor.BROWN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block GREEN_BANNER = a("green_banner", (Block) (new BlockBanner(EnumColor.GREEN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block RED_BANNER = a("red_banner", (Block) (new BlockBanner(EnumColor.RED, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block BLACK_BANNER = a("black_banner", (Block) (new BlockBanner(EnumColor.BLACK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD))));
    public static final Block WHITE_WALL_BANNER = a("white_wall_banner", (Block) (new BlockBannerWall(EnumColor.WHITE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.WHITE_BANNER))));
    public static final Block ORANGE_WALL_BANNER = a("orange_wall_banner", (Block) (new BlockBannerWall(EnumColor.ORANGE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.ORANGE_BANNER))));
    public static final Block MAGENTA_WALL_BANNER = a("magenta_wall_banner", (Block) (new BlockBannerWall(EnumColor.MAGENTA, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.MAGENTA_BANNER))));
    public static final Block LIGHT_BLUE_WALL_BANNER = a("light_blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.LIGHT_BLUE_BANNER))));
    public static final Block YELLOW_WALL_BANNER = a("yellow_wall_banner", (Block) (new BlockBannerWall(EnumColor.YELLOW, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.YELLOW_BANNER))));
    public static final Block LIME_WALL_BANNER = a("lime_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIME, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.LIME_BANNER))));
    public static final Block PINK_WALL_BANNER = a("pink_wall_banner", (Block) (new BlockBannerWall(EnumColor.PINK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.PINK_BANNER))));
    public static final Block GRAY_WALL_BANNER = a("gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.GRAY_BANNER))));
    public static final Block LIGHT_GRAY_WALL_BANNER = a("light_gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.LIGHT_GRAY_BANNER))));
    public static final Block CYAN_WALL_BANNER = a("cyan_wall_banner", (Block) (new BlockBannerWall(EnumColor.CYAN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.CYAN_BANNER))));
    public static final Block PURPLE_WALL_BANNER = a("purple_wall_banner", (Block) (new BlockBannerWall(EnumColor.PURPLE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.PURPLE_BANNER))));
    public static final Block BLUE_WALL_BANNER = a("blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.BLUE_BANNER))));
    public static final Block BROWN_WALL_BANNER = a("brown_wall_banner", (Block) (new BlockBannerWall(EnumColor.BROWN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.BROWN_BANNER))));
    public static final Block GREEN_WALL_BANNER = a("green_wall_banner", (Block) (new BlockBannerWall(EnumColor.GREEN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.GREEN_BANNER))));
    public static final Block RED_WALL_BANNER = a("red_wall_banner", (Block) (new BlockBannerWall(EnumColor.RED, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.RED_BANNER))));
    public static final Block BLACK_WALL_BANNER = a("black_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLACK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.BLACK_BANNER))));
    public static final Block RED_SANDSTONE = a("red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().d(0.8F)));
    public static final Block CHISELED_RED_SANDSTONE = a("chiseled_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().d(0.8F)));
    public static final Block CUT_RED_SANDSTONE = a("cut_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().d(0.8F)));
    public static final Block RED_SANDSTONE_STAIRS = a("red_sandstone_stairs", (Block) (new BlockStairs(Blocks.RED_SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.RED_SANDSTONE))));
    public static final Block OAK_SLAB = a("oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.WOOD).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_SLAB = a("spruce_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_SLAB = a("birch_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.SAND).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_SLAB = a("jungle_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.DIRT).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_SLAB = a("acacia_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_ORANGE).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_SLAB = a("dark_oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_BROWN).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block STONE_SLAB = a("stone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(2.0F, 6.0F))));
    public static final Block SMOOTH_STONE_SLAB = a("smooth_stone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(2.0F, 6.0F))));
    public static final Block SANDSTONE_SLAB = a("sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().a(2.0F, 6.0F))));
    public static final Block CUT_SANDSTONE_SLAB = a("cut_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().a(2.0F, 6.0F))));
    public static final Block PETRIFIED_OAK_SLAB = a("petrified_oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.WOOD).h().a(2.0F, 6.0F))));
    public static final Block COBBLESTONE_SLAB = a("cobblestone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(2.0F, 6.0F))));
    public static final Block BRICK_SLAB = a("brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_RED).h().a(2.0F, 6.0F))));
    public static final Block STONE_BRICK_SLAB = a("stone_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(2.0F, 6.0F))));
    public static final Block NETHER_BRICK_SLAB = a("nether_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS))));
    public static final Block QUARTZ_SLAB = a("quartz_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().a(2.0F, 6.0F))));
    public static final Block RED_SANDSTONE_SLAB = a("red_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().a(2.0F, 6.0F))));
    public static final Block CUT_RED_SANDSTONE_SLAB = a("cut_red_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().a(2.0F, 6.0F))));
    public static final Block PURPUR_SLAB = a("purpur_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_MAGENTA).h().a(2.0F, 6.0F))));
    public static final Block SMOOTH_STONE = a("smooth_stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.STONE).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_SANDSTONE = a("smooth_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_QUARTZ = a("smooth_quartz", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.QUARTZ).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_RED_SANDSTONE = a("smooth_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().a(2.0F, 6.0F)));
    public static final Block SPRUCE_FENCE_GATE = a("spruce_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_FENCE_GATE = a("birch_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_FENCE_GATE = a("jungle_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_FENCE_GATE = a("acacia_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_FENCE_GATE = a("dark_oak_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_FENCE = a("spruce_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block BIRCH_FENCE = a("birch_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block JUNGLE_FENCE = a("jungle_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block ACACIA_FENCE = a("acacia_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block DARK_OAK_FENCE = a("dark_oak_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block SPRUCE_DOOR = a("spruce_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block BIRCH_DOOR = a("birch_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block JUNGLE_DOOR = a("jungle_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block ACACIA_DOOR = a("acacia_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block DARK_OAK_DOOR = a("dark_oak_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block END_ROD = a("end_rod", (Block) (new BlockEndRod(BlockBase.Info.a(Material.DECORATION).c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.WOOD).b())));
    public static final Block CHORUS_PLANT = a("chorus_plant", (Block) (new BlockChorusFruit(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_PURPLE).d(0.4F).a(SoundEffectType.WOOD).b())));
    public static final Block CHORUS_FLOWER = a("chorus_flower", (Block) (new BlockChorusFlower((BlockChorusFruit) Blocks.CHORUS_PLANT, BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_PURPLE).d().d(0.4F).a(SoundEffectType.WOOD).b())));
    public static final Block PURPUR_BLOCK = a("purpur_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_MAGENTA).h().a(1.5F, 6.0F)));
    public static final Block PURPUR_PILLAR = a("purpur_pillar", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_MAGENTA).h().a(1.5F, 6.0F))));
    public static final Block PURPUR_STAIRS = a("purpur_stairs", (Block) (new BlockStairs(Blocks.PURPUR_BLOCK.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PURPUR_BLOCK))));
    public static final Block END_STONE_BRICKS = a("end_stone_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().a(3.0F, 9.0F)));
    public static final Block BEETROOTS = a("beetroots", (Block) (new BlockBeetroot(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.CROP))));
    public static final Block DIRT_PATH = a("dirt_path", (Block) (new BlockGrassPath(BlockBase.Info.a(Material.DIRT).d(0.65F).a(SoundEffectType.GRASS).c(Blocks::a).b(Blocks::a))));
    public static final Block END_GATEWAY = a("end_gateway", (Block) (new BlockEndGateway(BlockBase.Info.a(Material.PORTAL, MaterialMapColor.COLOR_BLACK).a().a((iblockdata) -> {
        return 15;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block REPEATING_COMMAND_BLOCK = a("repeating_command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_PURPLE).h().a(-1.0F, 3600000.0F).f(), false)));
    public static final Block CHAIN_COMMAND_BLOCK = a("chain_command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_GREEN).h().a(-1.0F, 3600000.0F).f(), true)));
    public static final Block FROSTED_ICE = a("frosted_ice", (Block) (new BlockIceFrost(BlockBase.Info.a(Material.ICE).a(0.98F).d().d(0.5F).a(SoundEffectType.GLASS).b().a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    }))));
    public static final Block MAGMA_BLOCK = a("magma_block", (Block) (new BlockMagma(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a((iblockdata) -> {
        return 3;
    }).d().d(0.5F).a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes.d();
    }).d(Blocks::a).e(Blocks::a))));
    public static final Block NETHER_WART_BLOCK = a("nether_wart_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.COLOR_RED).d(1.0F).a(SoundEffectType.WART_BLOCK)));
    public static final Block RED_NETHER_BRICKS = a("red_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS)));
    public static final Block BONE_BLOCK = a("bone_block", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.SAND).h().d(2.0F).a(SoundEffectType.BONE_BLOCK))));
    public static final Block STRUCTURE_VOID = a("structure_void", (Block) (new BlockStructureVoid(BlockBase.Info.a(Material.STRUCTURAL_AIR).a().f())));
    public static final Block OBSERVER = a("observer", (Block) (new BlockObserver(BlockBase.Info.a(Material.STONE).d(3.0F).h().a(Blocks::b))));
    public static final Block SHULKER_BOX = a("shulker_box", (Block) a((EnumColor) null, BlockBase.Info.a(Material.SHULKER_SHELL)));
    public static final Block WHITE_SHULKER_BOX = a("white_shulker_box", (Block) a(EnumColor.WHITE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.SNOW)));
    public static final Block ORANGE_SHULKER_BOX = a("orange_shulker_box", (Block) a(EnumColor.ORANGE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_ORANGE)));
    public static final Block MAGENTA_SHULKER_BOX = a("magenta_shulker_box", (Block) a(EnumColor.MAGENTA, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_MAGENTA)));
    public static final Block LIGHT_BLUE_SHULKER_BOX = a("light_blue_shulker_box", (Block) a(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_BLUE)));
    public static final Block YELLOW_SHULKER_BOX = a("yellow_shulker_box", (Block) a(EnumColor.YELLOW, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_YELLOW)));
    public static final Block LIME_SHULKER_BOX = a("lime_shulker_box", (Block) a(EnumColor.LIME, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_GREEN)));
    public static final Block PINK_SHULKER_BOX = a("pink_shulker_box", (Block) a(EnumColor.PINK, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_PINK)));
    public static final Block GRAY_SHULKER_BOX = a("gray_shulker_box", (Block) a(EnumColor.GRAY, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_GRAY)));
    public static final Block LIGHT_GRAY_SHULKER_BOX = a("light_gray_shulker_box", (Block) a(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_LIGHT_GRAY)));
    public static final Block CYAN_SHULKER_BOX = a("cyan_shulker_box", (Block) a(EnumColor.CYAN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_CYAN)));
    public static final Block PURPLE_SHULKER_BOX = a("purple_shulker_box", (Block) a(EnumColor.PURPLE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.TERRACOTTA_PURPLE)));
    public static final Block BLUE_SHULKER_BOX = a("blue_shulker_box", (Block) a(EnumColor.BLUE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BLUE)));
    public static final Block BROWN_SHULKER_BOX = a("brown_shulker_box", (Block) a(EnumColor.BROWN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BROWN)));
    public static final Block GREEN_SHULKER_BOX = a("green_shulker_box", (Block) a(EnumColor.GREEN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_GREEN)));
    public static final Block RED_SHULKER_BOX = a("red_shulker_box", (Block) a(EnumColor.RED, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_RED)));
    public static final Block BLACK_SHULKER_BOX = a("black_shulker_box", (Block) a(EnumColor.BLACK, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.COLOR_BLACK)));
    public static final Block WHITE_GLAZED_TERRACOTTA = a("white_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.WHITE).h().d(1.4F))));
    public static final Block ORANGE_GLAZED_TERRACOTTA = a("orange_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.ORANGE).h().d(1.4F))));
    public static final Block MAGENTA_GLAZED_TERRACOTTA = a("magenta_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.MAGENTA).h().d(1.4F))));
    public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA = a("light_blue_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.LIGHT_BLUE).h().d(1.4F))));
    public static final Block YELLOW_GLAZED_TERRACOTTA = a("yellow_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.YELLOW).h().d(1.4F))));
    public static final Block LIME_GLAZED_TERRACOTTA = a("lime_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.LIME).h().d(1.4F))));
    public static final Block PINK_GLAZED_TERRACOTTA = a("pink_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.PINK).h().d(1.4F))));
    public static final Block GRAY_GLAZED_TERRACOTTA = a("gray_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.GRAY).h().d(1.4F))));
    public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA = a("light_gray_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.LIGHT_GRAY).h().d(1.4F))));
    public static final Block CYAN_GLAZED_TERRACOTTA = a("cyan_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.CYAN).h().d(1.4F))));
    public static final Block PURPLE_GLAZED_TERRACOTTA = a("purple_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.PURPLE).h().d(1.4F))));
    public static final Block BLUE_GLAZED_TERRACOTTA = a("blue_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.BLUE).h().d(1.4F))));
    public static final Block BROWN_GLAZED_TERRACOTTA = a("brown_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.BROWN).h().d(1.4F))));
    public static final Block GREEN_GLAZED_TERRACOTTA = a("green_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.GREEN).h().d(1.4F))));
    public static final Block RED_GLAZED_TERRACOTTA = a("red_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.RED).h().d(1.4F))));
    public static final Block BLACK_GLAZED_TERRACOTTA = a("black_glazed_terracotta", (Block) (new BlockGlazedTerracotta(BlockBase.Info.a(Material.STONE, EnumColor.BLACK).h().d(1.4F))));
    public static final Block WHITE_CONCRETE = a("white_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.WHITE).h().d(1.8F)));
    public static final Block ORANGE_CONCRETE = a("orange_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.ORANGE).h().d(1.8F)));
    public static final Block MAGENTA_CONCRETE = a("magenta_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.MAGENTA).h().d(1.8F)));
    public static final Block LIGHT_BLUE_CONCRETE = a("light_blue_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.LIGHT_BLUE).h().d(1.8F)));
    public static final Block YELLOW_CONCRETE = a("yellow_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.YELLOW).h().d(1.8F)));
    public static final Block LIME_CONCRETE = a("lime_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.LIME).h().d(1.8F)));
    public static final Block PINK_CONCRETE = a("pink_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.PINK).h().d(1.8F)));
    public static final Block GRAY_CONCRETE = a("gray_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.GRAY).h().d(1.8F)));
    public static final Block LIGHT_GRAY_CONCRETE = a("light_gray_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.LIGHT_GRAY).h().d(1.8F)));
    public static final Block CYAN_CONCRETE = a("cyan_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.CYAN).h().d(1.8F)));
    public static final Block PURPLE_CONCRETE = a("purple_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.PURPLE).h().d(1.8F)));
    public static final Block BLUE_CONCRETE = a("blue_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.BLUE).h().d(1.8F)));
    public static final Block BROWN_CONCRETE = a("brown_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.BROWN).h().d(1.8F)));
    public static final Block GREEN_CONCRETE = a("green_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.GREEN).h().d(1.8F)));
    public static final Block RED_CONCRETE = a("red_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.RED).h().d(1.8F)));
    public static final Block BLACK_CONCRETE = a("black_concrete", new Block(BlockBase.Info.a(Material.STONE, EnumColor.BLACK).h().d(1.8F)));
    public static final Block WHITE_CONCRETE_POWDER = a("white_concrete_powder", (Block) (new BlockConcretePowder(Blocks.WHITE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.WHITE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block ORANGE_CONCRETE_POWDER = a("orange_concrete_powder", (Block) (new BlockConcretePowder(Blocks.ORANGE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.ORANGE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block MAGENTA_CONCRETE_POWDER = a("magenta_concrete_powder", (Block) (new BlockConcretePowder(Blocks.MAGENTA_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.MAGENTA).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = a("light_blue_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIGHT_BLUE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIGHT_BLUE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block YELLOW_CONCRETE_POWDER = a("yellow_concrete_powder", (Block) (new BlockConcretePowder(Blocks.YELLOW_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.YELLOW).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block LIME_CONCRETE_POWDER = a("lime_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIME_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIME).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block PINK_CONCRETE_POWDER = a("pink_concrete_powder", (Block) (new BlockConcretePowder(Blocks.PINK_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.PINK).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block GRAY_CONCRETE_POWDER = a("gray_concrete_powder", (Block) (new BlockConcretePowder(Blocks.GRAY_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.GRAY).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = a("light_gray_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIGHT_GRAY_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIGHT_GRAY).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block CYAN_CONCRETE_POWDER = a("cyan_concrete_powder", (Block) (new BlockConcretePowder(Blocks.CYAN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.CYAN).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block PURPLE_CONCRETE_POWDER = a("purple_concrete_powder", (Block) (new BlockConcretePowder(Blocks.PURPLE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.PURPLE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block BLUE_CONCRETE_POWDER = a("blue_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BLUE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BLUE).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block BROWN_CONCRETE_POWDER = a("brown_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BROWN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BROWN).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block GREEN_CONCRETE_POWDER = a("green_concrete_powder", (Block) (new BlockConcretePowder(Blocks.GREEN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.GREEN).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block RED_CONCRETE_POWDER = a("red_concrete_powder", (Block) (new BlockConcretePowder(Blocks.RED_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.RED).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block BLACK_CONCRETE_POWDER = a("black_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BLACK_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BLACK).d(0.5F).a(SoundEffectType.SAND))));
    public static final Block KELP = a("kelp", (Block) (new BlockKelp(BlockBase.Info.a(Material.WATER_PLANT).a().d().c().a(SoundEffectType.WET_GRASS))));
    public static final Block KELP_PLANT = a("kelp_plant", (Block) (new BlockKelpPlant(BlockBase.Info.a(Material.WATER_PLANT).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block DRIED_KELP_BLOCK = a("dried_kelp_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.COLOR_GREEN).a(0.5F, 2.5F).a(SoundEffectType.GRASS)));
    public static final Block TURTLE_EGG = a("turtle_egg", (Block) (new BlockTurtleEgg(BlockBase.Info.a(Material.EGG, MaterialMapColor.SAND).d(0.5F).a(SoundEffectType.METAL).d().b())));
    public static final Block DEAD_TUBE_CORAL_BLOCK = a("dead_tube_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a(1.5F, 6.0F)));
    public static final Block DEAD_BRAIN_CORAL_BLOCK = a("dead_brain_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a(1.5F, 6.0F)));
    public static final Block DEAD_BUBBLE_CORAL_BLOCK = a("dead_bubble_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a(1.5F, 6.0F)));
    public static final Block DEAD_FIRE_CORAL_BLOCK = a("dead_fire_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a(1.5F, 6.0F)));
    public static final Block DEAD_HORN_CORAL_BLOCK = a("dead_horn_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a(1.5F, 6.0F)));
    public static final Block TUBE_CORAL_BLOCK = a("tube_coral_block", (Block) (new BlockCoral(Blocks.DEAD_TUBE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLUE).h().a(1.5F, 6.0F).a(SoundEffectType.CORAL_BLOCK))));
    public static final Block BRAIN_CORAL_BLOCK = a("brain_coral_block", (Block) (new BlockCoral(Blocks.DEAD_BRAIN_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_PINK).h().a(1.5F, 6.0F).a(SoundEffectType.CORAL_BLOCK))));
    public static final Block BUBBLE_CORAL_BLOCK = a("bubble_coral_block", (Block) (new BlockCoral(Blocks.DEAD_BUBBLE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_PURPLE).h().a(1.5F, 6.0F).a(SoundEffectType.CORAL_BLOCK))));
    public static final Block FIRE_CORAL_BLOCK = a("fire_coral_block", (Block) (new BlockCoral(Blocks.DEAD_FIRE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_RED).h().a(1.5F, 6.0F).a(SoundEffectType.CORAL_BLOCK))));
    public static final Block HORN_CORAL_BLOCK = a("horn_coral_block", (Block) (new BlockCoral(Blocks.DEAD_HORN_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_YELLOW).h().a(1.5F, 6.0F).a(SoundEffectType.CORAL_BLOCK))));
    public static final Block DEAD_TUBE_CORAL = a("dead_tube_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_BRAIN_CORAL = a("dead_brain_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_BUBBLE_CORAL = a("dead_bubble_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_FIRE_CORAL = a("dead_fire_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_HORN_CORAL = a("dead_horn_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block TUBE_CORAL = a("tube_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_TUBE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block BRAIN_CORAL = a("brain_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_BRAIN_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block BUBBLE_CORAL = a("bubble_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_BUBBLE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block FIRE_CORAL = a("fire_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_FIRE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block HORN_CORAL = a("horn_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_HORN_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block DEAD_TUBE_CORAL_FAN = a("dead_tube_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_BRAIN_CORAL_FAN = a("dead_brain_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_BUBBLE_CORAL_FAN = a("dead_bubble_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_FIRE_CORAL_FAN = a("dead_fire_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block DEAD_HORN_CORAL_FAN = a("dead_horn_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c())));
    public static final Block TUBE_CORAL_FAN = a("tube_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_TUBE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block BRAIN_CORAL_FAN = a("brain_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_BRAIN_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block BUBBLE_CORAL_FAN = a("bubble_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_BUBBLE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block FIRE_CORAL_FAN = a("fire_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_FIRE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block HORN_CORAL_FAN = a("horn_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_HORN_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).a().c().a(SoundEffectType.WET_GRASS))));
    public static final Block DEAD_TUBE_CORAL_WALL_FAN = a("dead_tube_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c().a(Blocks.DEAD_TUBE_CORAL_FAN))));
    public static final Block DEAD_BRAIN_CORAL_WALL_FAN = a("dead_brain_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c().a(Blocks.DEAD_BRAIN_CORAL_FAN))));
    public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = a("dead_bubble_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c().a(Blocks.DEAD_BUBBLE_CORAL_FAN))));
    public static final Block DEAD_FIRE_CORAL_WALL_FAN = a("dead_fire_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c().a(Blocks.DEAD_FIRE_CORAL_FAN))));
    public static final Block DEAD_HORN_CORAL_WALL_FAN = a("dead_horn_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_GRAY).h().a().c().a(Blocks.DEAD_HORN_CORAL_FAN))));
    public static final Block TUBE_CORAL_WALL_FAN = a("tube_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_TUBE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_BLUE).a().c().a(SoundEffectType.WET_GRASS).a(Blocks.TUBE_CORAL_FAN))));
    public static final Block BRAIN_CORAL_WALL_FAN = a("brain_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PINK).a().c().a(SoundEffectType.WET_GRASS).a(Blocks.BRAIN_CORAL_FAN))));
    public static final Block BUBBLE_CORAL_WALL_FAN = a("bubble_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_PURPLE).a().c().a(SoundEffectType.WET_GRASS).a(Blocks.BUBBLE_CORAL_FAN))));
    public static final Block FIRE_CORAL_WALL_FAN = a("fire_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_FIRE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_RED).a().c().a(SoundEffectType.WET_GRASS).a(Blocks.FIRE_CORAL_FAN))));
    public static final Block HORN_CORAL_WALL_FAN = a("horn_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_HORN_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_YELLOW).a().c().a(SoundEffectType.WET_GRASS).a(Blocks.HORN_CORAL_FAN))));
    public static final Block SEA_PICKLE = a("sea_pickle", (Block) (new BlockSeaPickle(BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.COLOR_GREEN).a((iblockdata) -> {
        return BlockSeaPickle.h(iblockdata) ? 0 : 3 + 3 * (Integer) iblockdata.get(BlockSeaPickle.PICKLES);
    }).a(SoundEffectType.SLIME_BLOCK).b())));
    public static final Block BLUE_ICE = a("blue_ice", (Block) (new BlockHalfTransparent(BlockBase.Info.a(Material.ICE_SOLID).d(2.8F).a(0.989F).a(SoundEffectType.GLASS))));
    public static final Block CONDUIT = a("conduit", (Block) (new BlockConduit(BlockBase.Info.a(Material.GLASS, MaterialMapColor.DIAMOND).d(3.0F).a((iblockdata) -> {
        return 15;
    }).b())));
    public static final Block BAMBOO_SAPLING = a("bamboo_sapling", (Block) (new BlockBambooSapling(BlockBase.Info.a(Material.BAMBOO_SAPLING).d().c().a().d(1.0F).a(SoundEffectType.BAMBOO_SAPLING))));
    public static final Block BAMBOO = a("bamboo", (Block) (new BlockBamboo(BlockBase.Info.a(Material.BAMBOO, MaterialMapColor.PLANT).d().c().d(1.0F).a(SoundEffectType.BAMBOO).b().e())));
    public static final Block POTTED_BAMBOO = a("potted_bamboo", (Block) (new BlockFlowerPot(Blocks.BAMBOO, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block VOID_AIR = a("void_air", (Block) (new BlockAir(BlockBase.Info.a(Material.AIR).a().f().g())));
    public static final Block CAVE_AIR = a("cave_air", (Block) (new BlockAir(BlockBase.Info.a(Material.AIR).a().f().g())));
    public static final Block BUBBLE_COLUMN = a("bubble_column", (Block) (new BlockBubbleColumn(BlockBase.Info.a(Material.BUBBLE_COLUMN).a().f())));
    public static final Block POLISHED_GRANITE_STAIRS = a("polished_granite_stairs", (Block) (new BlockStairs(Blocks.POLISHED_GRANITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_GRANITE))));
    public static final Block SMOOTH_RED_SANDSTONE_STAIRS = a("smooth_red_sandstone_stairs", (Block) (new BlockStairs(Blocks.SMOOTH_RED_SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SMOOTH_RED_SANDSTONE))));
    public static final Block MOSSY_STONE_BRICK_STAIRS = a("mossy_stone_brick_stairs", (Block) (new BlockStairs(Blocks.MOSSY_STONE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.MOSSY_STONE_BRICKS))));
    public static final Block POLISHED_DIORITE_STAIRS = a("polished_diorite_stairs", (Block) (new BlockStairs(Blocks.POLISHED_DIORITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_DIORITE))));
    public static final Block MOSSY_COBBLESTONE_STAIRS = a("mossy_cobblestone_stairs", (Block) (new BlockStairs(Blocks.MOSSY_COBBLESTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.MOSSY_COBBLESTONE))));
    public static final Block END_STONE_BRICK_STAIRS = a("end_stone_brick_stairs", (Block) (new BlockStairs(Blocks.END_STONE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.END_STONE_BRICKS))));
    public static final Block STONE_STAIRS = a("stone_stairs", (Block) (new BlockStairs(Blocks.STONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.STONE))));
    public static final Block SMOOTH_SANDSTONE_STAIRS = a("smooth_sandstone_stairs", (Block) (new BlockStairs(Blocks.SMOOTH_SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SMOOTH_SANDSTONE))));
    public static final Block SMOOTH_QUARTZ_STAIRS = a("smooth_quartz_stairs", (Block) (new BlockStairs(Blocks.SMOOTH_QUARTZ.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SMOOTH_QUARTZ))));
    public static final Block GRANITE_STAIRS = a("granite_stairs", (Block) (new BlockStairs(Blocks.GRANITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.GRANITE))));
    public static final Block ANDESITE_STAIRS = a("andesite_stairs", (Block) (new BlockStairs(Blocks.ANDESITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.ANDESITE))));
    public static final Block RED_NETHER_BRICK_STAIRS = a("red_nether_brick_stairs", (Block) (new BlockStairs(Blocks.RED_NETHER_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.RED_NETHER_BRICKS))));
    public static final Block POLISHED_ANDESITE_STAIRS = a("polished_andesite_stairs", (Block) (new BlockStairs(Blocks.POLISHED_ANDESITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_ANDESITE))));
    public static final Block DIORITE_STAIRS = a("diorite_stairs", (Block) (new BlockStairs(Blocks.DIORITE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DIORITE))));
    public static final Block POLISHED_GRANITE_SLAB = a("polished_granite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_GRANITE))));
    public static final Block SMOOTH_RED_SANDSTONE_SLAB = a("smooth_red_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.SMOOTH_RED_SANDSTONE))));
    public static final Block MOSSY_STONE_BRICK_SLAB = a("mossy_stone_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.MOSSY_STONE_BRICKS))));
    public static final Block POLISHED_DIORITE_SLAB = a("polished_diorite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_DIORITE))));
    public static final Block MOSSY_COBBLESTONE_SLAB = a("mossy_cobblestone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.MOSSY_COBBLESTONE))));
    public static final Block END_STONE_BRICK_SLAB = a("end_stone_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.END_STONE_BRICKS))));
    public static final Block SMOOTH_SANDSTONE_SLAB = a("smooth_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.SMOOTH_SANDSTONE))));
    public static final Block SMOOTH_QUARTZ_SLAB = a("smooth_quartz_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.SMOOTH_QUARTZ))));
    public static final Block GRANITE_SLAB = a("granite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.GRANITE))));
    public static final Block ANDESITE_SLAB = a("andesite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.ANDESITE))));
    public static final Block RED_NETHER_BRICK_SLAB = a("red_nether_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.RED_NETHER_BRICKS))));
    public static final Block POLISHED_ANDESITE_SLAB = a("polished_andesite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_ANDESITE))));
    public static final Block DIORITE_SLAB = a("diorite_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.DIORITE))));
    public static final Block BRICK_WALL = a("brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.BRICKS))));
    public static final Block PRISMARINE_WALL = a("prismarine_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.PRISMARINE))));
    public static final Block RED_SANDSTONE_WALL = a("red_sandstone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.RED_SANDSTONE))));
    public static final Block MOSSY_STONE_BRICK_WALL = a("mossy_stone_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.MOSSY_STONE_BRICKS))));
    public static final Block GRANITE_WALL = a("granite_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.GRANITE))));
    public static final Block STONE_BRICK_WALL = a("stone_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.STONE_BRICKS))));
    public static final Block NETHER_BRICK_WALL = a("nether_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.NETHER_BRICKS))));
    public static final Block ANDESITE_WALL = a("andesite_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.ANDESITE))));
    public static final Block RED_NETHER_BRICK_WALL = a("red_nether_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.RED_NETHER_BRICKS))));
    public static final Block SANDSTONE_WALL = a("sandstone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.SANDSTONE))));
    public static final Block END_STONE_BRICK_WALL = a("end_stone_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.END_STONE_BRICKS))));
    public static final Block DIORITE_WALL = a("diorite_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.DIORITE))));
    public static final Block SCAFFOLDING = a("scaffolding", (Block) (new BlockScaffolding(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.SAND).a().a(SoundEffectType.SCAFFOLDING).e())));
    public static final Block LOOM = a("loom", (Block) (new BlockLoom(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block BARREL = a("barrel", (Block) (new BlockBarrel(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block SMOKER = a("smoker", (Block) (new BlockSmoker(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block BLAST_FURNACE = a("blast_furnace", (Block) (new BlockBlastFurnace(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block CARTOGRAPHY_TABLE = a("cartography_table", (Block) (new BlockCartographyTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block FLETCHING_TABLE = a("fletching_table", (Block) (new BlockFletchingTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block GRINDSTONE = a("grindstone", (Block) (new BlockGrindstone(BlockBase.Info.a(Material.HEAVY_METAL, MaterialMapColor.METAL).h().a(2.0F, 6.0F).a(SoundEffectType.STONE))));
    public static final Block LECTERN = a("lectern", (Block) (new BlockLectern(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block SMITHING_TABLE = a("smithing_table", (Block) (new BlockSmithingTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.WOOD))));
    public static final Block STONECUTTER = a("stonecutter", (Block) (new BlockStonecutter(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block BELL = a("bell", (Block) (new BlockBell(BlockBase.Info.a(Material.METAL, MaterialMapColor.GOLD).h().d(5.0F).a(SoundEffectType.ANVIL))));
    public static final Block LANTERN = a("lantern", (Block) (new BlockLantern(BlockBase.Info.a(Material.METAL).h().d(3.5F).a(SoundEffectType.LANTERN).a((iblockdata) -> {
        return 15;
    }).b())));
    public static final Block SOUL_LANTERN = a("soul_lantern", (Block) (new BlockLantern(BlockBase.Info.a(Material.METAL).h().d(3.5F).a(SoundEffectType.LANTERN).a((iblockdata) -> {
        return 10;
    }).b())));
    public static final Block CAMPFIRE = a("campfire", (Block) (new BlockCampfire(true, 1, BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).d(2.0F).a(SoundEffectType.WOOD).a(a(15)).b())));
    public static final Block SOUL_CAMPFIRE = a("soul_campfire", (Block) (new BlockCampfire(false, 2, BlockBase.Info.a(Material.WOOD, MaterialMapColor.PODZOL).d(2.0F).a(SoundEffectType.WOOD).a(a(10)).b())));
    public static final Block SWEET_BERRY_BUSH = a("sweet_berry_bush", (Block) (new BlockSweetBerryBush(BlockBase.Info.a(Material.PLANT).d().a().a(SoundEffectType.SWEET_BERRY_BUSH))));
    public static final Block WARPED_STEM = a("warped_stem", a(MaterialMapColor.WARPED_STEM));
    public static final Block STRIPPED_WARPED_STEM = a("stripped_warped_stem", a(MaterialMapColor.WARPED_STEM));
    public static final Block WARPED_HYPHAE = a("warped_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.WARPED_HYPHAE).d(2.0F).a(SoundEffectType.STEM))));
    public static final Block STRIPPED_WARPED_HYPHAE = a("stripped_warped_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.WARPED_HYPHAE).d(2.0F).a(SoundEffectType.STEM))));
    public static final Block WARPED_NYLIUM = a("warped_nylium", (Block) (new BlockNylium(BlockBase.Info.a(Material.STONE, MaterialMapColor.WARPED_NYLIUM).h().d(0.4F).a(SoundEffectType.NYLIUM).d())));
    public static final Block WARPED_FUNGUS = a("warped_fungus", (Block) (new BlockFungi(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_CYAN).c().a().a(SoundEffectType.FUNGUS), () -> {
        return BiomeDecoratorGroups.WARPED_FUNGI_PLANTED;
    })));
    public static final Block WARPED_WART_BLOCK = a("warped_wart_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.WARPED_WART_BLOCK).d(1.0F).a(SoundEffectType.WART_BLOCK)));
    public static final Block WARPED_ROOTS = a("warped_roots", (Block) (new BlockRoots(BlockBase.Info.a(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.COLOR_CYAN).a().c().a(SoundEffectType.ROOTS))));
    public static final Block NETHER_SPROUTS = a("nether_sprouts", (Block) (new BlockNetherSprouts(BlockBase.Info.a(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.COLOR_CYAN).a().c().a(SoundEffectType.NETHER_SPROUTS))));
    public static final Block CRIMSON_STEM = a("crimson_stem", a(MaterialMapColor.CRIMSON_STEM));
    public static final Block STRIPPED_CRIMSON_STEM = a("stripped_crimson_stem", a(MaterialMapColor.CRIMSON_STEM));
    public static final Block CRIMSON_HYPHAE = a("crimson_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_HYPHAE).d(2.0F).a(SoundEffectType.STEM))));
    public static final Block STRIPPED_CRIMSON_HYPHAE = a("stripped_crimson_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_HYPHAE).d(2.0F).a(SoundEffectType.STEM))));
    public static final Block CRIMSON_NYLIUM = a("crimson_nylium", (Block) (new BlockNylium(BlockBase.Info.a(Material.STONE, MaterialMapColor.CRIMSON_NYLIUM).h().d(0.4F).a(SoundEffectType.NYLIUM).d())));
    public static final Block CRIMSON_FUNGUS = a("crimson_fungus", (Block) (new BlockFungi(BlockBase.Info.a(Material.PLANT, MaterialMapColor.NETHER).c().a().a(SoundEffectType.FUNGUS), () -> {
        return BiomeDecoratorGroups.CRIMSON_FUNGI_PLANTED;
    })));
    public static final Block SHROOMLIGHT = a("shroomlight", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.COLOR_RED).d(1.0F).a(SoundEffectType.SHROOMLIGHT).a((iblockdata) -> {
        return 15;
    })));
    public static final Block WEEPING_VINES = a("weeping_vines", (Block) (new BlockWeepingVines(BlockBase.Info.a(Material.PLANT, MaterialMapColor.NETHER).d().a().c().a(SoundEffectType.WEEPING_VINES))));
    public static final Block WEEPING_VINES_PLANT = a("weeping_vines_plant", (Block) (new BlockWeepingVinesPlant(BlockBase.Info.a(Material.PLANT, MaterialMapColor.NETHER).a().c().a(SoundEffectType.WEEPING_VINES))));
    public static final Block TWISTING_VINES = a("twisting_vines", (Block) (new BlockTwistingVines(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_CYAN).d().a().c().a(SoundEffectType.WEEPING_VINES))));
    public static final Block TWISTING_VINES_PLANT = a("twisting_vines_plant", (Block) (new BlockTwistingVinesPlant(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_CYAN).a().c().a(SoundEffectType.WEEPING_VINES))));
    public static final Block CRIMSON_ROOTS = a("crimson_roots", (Block) (new BlockRoots(BlockBase.Info.a(Material.REPLACEABLE_FIREPROOF_PLANT, MaterialMapColor.NETHER).a().c().a(SoundEffectType.ROOTS))));
    public static final Block CRIMSON_PLANKS = a("crimson_planks", new Block(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.CRIMSON_STEM).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block WARPED_PLANKS = a("warped_planks", new Block(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.WARPED_STEM).a(2.0F, 3.0F).a(SoundEffectType.WOOD)));
    public static final Block CRIMSON_SLAB = a("crimson_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block WARPED_SLAB = a("warped_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block CRIMSON_PRESSURE_PLATE = a("crimson_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block WARPED_PRESSURE_PLATE = a("warped_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block CRIMSON_FENCE = a("crimson_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block WARPED_FENCE = a("warped_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block CRIMSON_TRAPDOOR = a("crimson_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block WARPED_TRAPDOOR = a("warped_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b().a(Blocks::a))));
    public static final Block CRIMSON_FENCE_GATE = a("crimson_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block WARPED_FENCE_GATE = a("warped_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.WOOD))));
    public static final Block CRIMSON_STAIRS = a("crimson_stairs", (Block) (new BlockStairs(Blocks.CRIMSON_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.CRIMSON_PLANKS))));
    public static final Block WARPED_STAIRS = a("warped_stairs", (Block) (new BlockStairs(Blocks.WARPED_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.WARPED_PLANKS))));
    public static final Block CRIMSON_BUTTON = a("crimson_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block WARPED_BUTTON = a("warped_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F).a(SoundEffectType.WOOD))));
    public static final Block CRIMSON_DOOR = a("crimson_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block WARPED_DOOR = a("warped_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).d(3.0F).a(SoundEffectType.WOOD).b())));
    public static final Block CRIMSON_SIGN = a("crimson_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.CRIMSON)));
    public static final Block WARPED_SIGN = a("warped_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(1.0F).a(SoundEffectType.WOOD), BlockPropertyWood.WARPED)));
    public static final Block CRIMSON_WALL_SIGN = a("crimson_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.CRIMSON_SIGN), BlockPropertyWood.CRIMSON)));
    public static final Block WARPED_WALL_SIGN = a("warped_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(1.0F).a(SoundEffectType.WOOD).a(Blocks.WARPED_SIGN), BlockPropertyWood.WARPED)));
    public static final Block STRUCTURE_BLOCK = a("structure_block", (Block) (new BlockStructure(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_LIGHT_GRAY).h().a(-1.0F, 3600000.0F).f())));
    public static final Block JIGSAW = a("jigsaw", (Block) (new BlockJigsaw(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_LIGHT_GRAY).h().a(-1.0F, 3600000.0F).f())));
    public static final Block COMPOSTER = a("composter", (Block) (new BlockComposter(BlockBase.Info.a(Material.WOOD).d(0.6F).a(SoundEffectType.WOOD))));
    public static final Block TARGET = a("target", (Block) (new BlockTarget(BlockBase.Info.a(Material.GRASS, MaterialMapColor.QUARTZ).d(0.5F).a(SoundEffectType.GRASS))));
    public static final Block BEE_NEST = a("bee_nest", (Block) (new BlockBeehive(BlockBase.Info.a(Material.WOOD, MaterialMapColor.COLOR_YELLOW).d(0.3F).a(SoundEffectType.WOOD))));
    public static final Block BEEHIVE = a("beehive", (Block) (new BlockBeehive(BlockBase.Info.a(Material.WOOD).d(0.6F).a(SoundEffectType.WOOD))));
    public static final Block HONEY_BLOCK = a("honey_block", (Block) (new BlockHoney(BlockBase.Info.a(Material.CLAY, MaterialMapColor.COLOR_ORANGE).b(0.4F).c(0.5F).b().a(SoundEffectType.HONEY_BLOCK))));
    public static final Block HONEYCOMB_BLOCK = a("honeycomb_block", new Block(BlockBase.Info.a(Material.CLAY, MaterialMapColor.COLOR_ORANGE).d(0.6F).a(SoundEffectType.CORAL_BLOCK)));
    public static final Block NETHERITE_BLOCK = a("netherite_block", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_BLACK).h().a(50.0F, 1200.0F).a(SoundEffectType.NETHERITE_BLOCK)));
    public static final Block ANCIENT_DEBRIS = a("ancient_debris", new Block(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_BLACK).h().a(30.0F, 1200.0F).a(SoundEffectType.ANCIENT_DEBRIS)));
    public static final Block CRYING_OBSIDIAN = a("crying_obsidian", (Block) (new BlockCryingObsidian(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(50.0F, 1200.0F).a((iblockdata) -> {
        return 10;
    }))));
    public static final Block RESPAWN_ANCHOR = a("respawn_anchor", (Block) (new BlockRespawnAnchor(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(50.0F, 1200.0F).a((iblockdata) -> {
        return BlockRespawnAnchor.a(iblockdata, 15);
    }))));
    public static final Block POTTED_CRIMSON_FUNGUS = a("potted_crimson_fungus", (Block) (new BlockFlowerPot(Blocks.CRIMSON_FUNGUS, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_WARPED_FUNGUS = a("potted_warped_fungus", (Block) (new BlockFlowerPot(Blocks.WARPED_FUNGUS, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_CRIMSON_ROOTS = a("potted_crimson_roots", (Block) (new BlockFlowerPot(Blocks.CRIMSON_ROOTS, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_WARPED_ROOTS = a("potted_warped_roots", (Block) (new BlockFlowerPot(Blocks.WARPED_ROOTS, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block LODESTONE = a("lodestone", new Block(BlockBase.Info.a(Material.HEAVY_METAL).h().d(3.5F).a(SoundEffectType.LODESTONE)));
    public static final Block BLACKSTONE = a("blackstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a(1.5F, 6.0F)));
    public static final Block BLACKSTONE_STAIRS = a("blackstone_stairs", (Block) (new BlockStairs(Blocks.BLACKSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE))));
    public static final Block BLACKSTONE_WALL = a("blackstone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE))));
    public static final Block BLACKSTONE_SLAB = a("blackstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE).a(2.0F, 6.0F))));
    public static final Block POLISHED_BLACKSTONE = a("polished_blackstone", new Block(BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE).a(2.0F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE_BRICKS = a("polished_blackstone_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE).a(1.5F, 6.0F)));
    public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS = a("cracked_polished_blackstone_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final Block CHISELED_POLISHED_BLACKSTONE = a("chiseled_polished_blackstone", new Block(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE).a(1.5F, 6.0F)));
    public static final Block POLISHED_BLACKSTONE_BRICK_SLAB = a("polished_blackstone_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE_BRICKS).a(2.0F, 6.0F))));
    public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS = a("polished_blackstone_brick_stairs", (Block) (new BlockStairs(Blocks.POLISHED_BLACKSTONE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE_BRICKS))));
    public static final Block POLISHED_BLACKSTONE_BRICK_WALL = a("polished_blackstone_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE_BRICKS))));
    public static final Block GILDED_BLACKSTONE = a("gilded_blackstone", new Block(BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE).a(SoundEffectType.GILDED_BLACKSTONE)));
    public static final Block POLISHED_BLACKSTONE_STAIRS = a("polished_blackstone_stairs", (Block) (new BlockStairs(Blocks.POLISHED_BLACKSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block POLISHED_BLACKSTONE_SLAB = a("polished_blackstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = a("polished_blackstone_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_BLACK).h().a().d(0.5F))));
    public static final Block POLISHED_BLACKSTONE_BUTTON = a("polished_blackstone_button", (Block) (new BlockStoneButton(BlockBase.Info.a(Material.DECORATION).a().d(0.5F))));
    public static final Block POLISHED_BLACKSTONE_WALL = a("polished_blackstone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block CHISELED_NETHER_BRICKS = a("chiseled_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS)));
    public static final Block CRACKED_NETHER_BRICKS = a("cracked_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.NETHER).h().a(2.0F, 6.0F).a(SoundEffectType.NETHER_BRICKS)));
    public static final Block QUARTZ_BRICKS = a("quartz_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.QUARTZ_BLOCK)));
    public static final Block CANDLE = a("candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.SAND).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block WHITE_CANDLE = a("white_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.WOOL).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block ORANGE_CANDLE = a("orange_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_ORANGE).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block MAGENTA_CANDLE = a("magenta_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_MAGENTA).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block LIGHT_BLUE_CANDLE = a("light_blue_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_BLUE).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block YELLOW_CANDLE = a("yellow_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_YELLOW).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block LIME_CANDLE = a("lime_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_GREEN).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block PINK_CANDLE = a("pink_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_PINK).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block GRAY_CANDLE = a("gray_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_GRAY).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block LIGHT_GRAY_CANDLE = a("light_gray_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_LIGHT_GRAY).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block CYAN_CANDLE = a("cyan_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_CYAN).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block PURPLE_CANDLE = a("purple_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_PURPLE).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block BLUE_CANDLE = a("blue_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_BLUE).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block BROWN_CANDLE = a("brown_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_BROWN).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block GREEN_CANDLE = a("green_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_GREEN).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block RED_CANDLE = a("red_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_RED).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block BLACK_CANDLE = a("black_candle", (Block) (new CandleBlock(BlockBase.Info.a(Material.DECORATION, MaterialMapColor.COLOR_BLACK).b().d(0.1F).a(SoundEffectType.CANDLE).a(CandleBlock.LIGHT_EMISSION))));
    public static final Block CANDLE_CAKE = a("candle_cake", (Block) (new CandleCakeBlock(Blocks.CANDLE, BlockBase.Info.a((BlockBase) Blocks.CAKE).a(a(3)))));
    public static final Block WHITE_CANDLE_CAKE = a("white_candle_cake", (Block) (new CandleCakeBlock(Blocks.WHITE_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block ORANGE_CANDLE_CAKE = a("orange_candle_cake", (Block) (new CandleCakeBlock(Blocks.ORANGE_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block MAGENTA_CANDLE_CAKE = a("magenta_candle_cake", (Block) (new CandleCakeBlock(Blocks.MAGENTA_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block LIGHT_BLUE_CANDLE_CAKE = a("light_blue_candle_cake", (Block) (new CandleCakeBlock(Blocks.LIGHT_BLUE_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block YELLOW_CANDLE_CAKE = a("yellow_candle_cake", (Block) (new CandleCakeBlock(Blocks.YELLOW_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block LIME_CANDLE_CAKE = a("lime_candle_cake", (Block) (new CandleCakeBlock(Blocks.LIME_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block PINK_CANDLE_CAKE = a("pink_candle_cake", (Block) (new CandleCakeBlock(Blocks.PINK_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block GRAY_CANDLE_CAKE = a("gray_candle_cake", (Block) (new CandleCakeBlock(Blocks.GRAY_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block LIGHT_GRAY_CANDLE_CAKE = a("light_gray_candle_cake", (Block) (new CandleCakeBlock(Blocks.LIGHT_GRAY_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block CYAN_CANDLE_CAKE = a("cyan_candle_cake", (Block) (new CandleCakeBlock(Blocks.CYAN_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block PURPLE_CANDLE_CAKE = a("purple_candle_cake", (Block) (new CandleCakeBlock(Blocks.PURPLE_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block BLUE_CANDLE_CAKE = a("blue_candle_cake", (Block) (new CandleCakeBlock(Blocks.BLUE_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block BROWN_CANDLE_CAKE = a("brown_candle_cake", (Block) (new CandleCakeBlock(Blocks.BROWN_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block GREEN_CANDLE_CAKE = a("green_candle_cake", (Block) (new CandleCakeBlock(Blocks.GREEN_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block RED_CANDLE_CAKE = a("red_candle_cake", (Block) (new CandleCakeBlock(Blocks.RED_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block BLACK_CANDLE_CAKE = a("black_candle_cake", (Block) (new CandleCakeBlock(Blocks.BLACK_CANDLE, BlockBase.Info.a((BlockBase) Blocks.CANDLE_CAKE))));
    public static final Block AMETHYST_BLOCK = a("amethyst_block", (Block) (new AmethystBlock(BlockBase.Info.a(Material.AMETHYST, MaterialMapColor.COLOR_PURPLE).d(1.5F).a(SoundEffectType.AMETHYST).h())));
    public static final Block BUDDING_AMETHYST = a("budding_amethyst", (Block) (new BuddingAmethystBlock(BlockBase.Info.a(Material.AMETHYST).d().d(1.5F).a(SoundEffectType.AMETHYST).h())));
    public static final Block AMETHYST_CLUSTER = a("amethyst_cluster", (Block) (new AmethystClusterBlock(7, 3, BlockBase.Info.a(Material.AMETHYST).b().d().a(SoundEffectType.AMETHYST_CLUSTER).d(1.5F).a((iblockdata) -> {
        return 5;
    }))));
    public static final Block LARGE_AMETHYST_BUD = a("large_amethyst_bud", (Block) (new AmethystClusterBlock(5, 3, BlockBase.Info.a((BlockBase) Blocks.AMETHYST_CLUSTER).a(SoundEffectType.MEDIUM_AMETHYST_BUD).a((iblockdata) -> {
        return 4;
    }))));
    public static final Block MEDIUM_AMETHYST_BUD = a("medium_amethyst_bud", (Block) (new AmethystClusterBlock(4, 3, BlockBase.Info.a((BlockBase) Blocks.AMETHYST_CLUSTER).a(SoundEffectType.LARGE_AMETHYST_BUD).a((iblockdata) -> {
        return 2;
    }))));
    public static final Block SMALL_AMETHYST_BUD = a("small_amethyst_bud", (Block) (new AmethystClusterBlock(3, 4, BlockBase.Info.a((BlockBase) Blocks.AMETHYST_CLUSTER).a(SoundEffectType.SMALL_AMETHYST_BUD).a((iblockdata) -> {
        return 1;
    }))));
    public static final Block TUFF = a("tuff", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_GRAY).a(SoundEffectType.TUFF).h().a(1.5F, 6.0F)));
    public static final Block CALCITE = a("calcite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_WHITE).a(SoundEffectType.CALCITE).h().d(0.75F)));
    public static final Block TINTED_GLASS = a("tinted_glass", (Block) (new TintedGlassBlock(BlockBase.Info.a((BlockBase) Blocks.GLASS).a(MaterialMapColor.COLOR_GRAY).b().a(Blocks::a).a(Blocks::b).b(Blocks::b).c(Blocks::b))));
    public static final Block POWDER_SNOW = a("powder_snow", (Block) (new PowderSnowBlock(BlockBase.Info.a(Material.POWDER_SNOW).d(0.25F).a(SoundEffectType.POWDER_SNOW).e())));
    public static final Block SCULK_SENSOR = a("sculk_sensor", (Block) (new SculkSensorBlock(BlockBase.Info.a(Material.SCULK, MaterialMapColor.COLOR_CYAN).d(1.5F).a(SoundEffectType.SCULK_SENSOR).a((iblockdata) -> {
        return 1;
    }).e((iblockdata, iblockaccess, blockposition) -> {
        return SculkSensorBlock.h(iblockdata) == SculkSensorPhase.ACTIVE;
    }), 8)));
    public static final Block OXIDIZED_COPPER = a("oxidized_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.a(Material.METAL, MaterialMapColor.WARPED_NYLIUM).h().a(3.0F, 6.0F).a(SoundEffectType.COPPER))));
    public static final Block WEATHERED_COPPER = a("weathered_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.a(Material.METAL, MaterialMapColor.WARPED_STEM).h().a(3.0F, 6.0F).a(SoundEffectType.COPPER))));
    public static final Block EXPOSED_COPPER = a("exposed_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.a(Material.METAL, MaterialMapColor.TERRACOTTA_LIGHT_GRAY).h().a(3.0F, 6.0F).a(SoundEffectType.COPPER))));
    public static final Block COPPER_BLOCK = a("copper_block", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_ORANGE).h().a(3.0F, 6.0F).a(SoundEffectType.COPPER))));
    public static final Block COPPER_ORE = a("copper_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.IRON_ORE))));
    public static final Block DEEPSLATE_COPPER_ORE = a("deepslate_copper_ore", (Block) (new BlockOre(BlockBase.Info.a((BlockBase) Blocks.COPPER_ORE).a(MaterialMapColor.DEEPSLATE).a(4.5F, 3.0F).a(SoundEffectType.DEEPSLATE))));
    public static final Block OXIDIZED_CUT_COPPER = a("oxidized_cut_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_COPPER))));
    public static final Block WEATHERED_CUT_COPPER = a("weathered_cut_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.a((BlockBase) Blocks.WEATHERED_COPPER))));
    public static final Block EXPOSED_CUT_COPPER = a("exposed_cut_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.a((BlockBase) Blocks.EXPOSED_COPPER))));
    public static final Block CUT_COPPER = a("cut_copper", (Block) (new WeatheringCopperFullBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.a((BlockBase) Blocks.COPPER_BLOCK))));
    public static final Block OXIDIZED_CUT_COPPER_STAIRS = a("oxidized_cut_copper_stairs", (Block) (new WeatheringCopperStairBlock(WeatheringCopper.a.OXIDIZED, Blocks.OXIDIZED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_CUT_COPPER))));
    public static final Block WEATHERED_CUT_COPPER_STAIRS = a("weathered_cut_copper_stairs", (Block) (new WeatheringCopperStairBlock(WeatheringCopper.a.WEATHERED, Blocks.WEATHERED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.WEATHERED_COPPER))));
    public static final Block EXPOSED_CUT_COPPER_STAIRS = a("exposed_cut_copper_stairs", (Block) (new WeatheringCopperStairBlock(WeatheringCopper.a.EXPOSED, Blocks.EXPOSED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.EXPOSED_COPPER))));
    public static final Block CUT_COPPER_STAIRS = a("cut_copper_stairs", (Block) (new WeatheringCopperStairBlock(WeatheringCopper.a.UNAFFECTED, Blocks.CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.COPPER_BLOCK))));
    public static final Block OXIDIZED_CUT_COPPER_SLAB = a("oxidized_cut_copper_slab", (Block) (new WeatheringCopperSlabBlock(WeatheringCopper.a.OXIDIZED, BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_CUT_COPPER).h())));
    public static final Block WEATHERED_CUT_COPPER_SLAB = a("weathered_cut_copper_slab", (Block) (new WeatheringCopperSlabBlock(WeatheringCopper.a.WEATHERED, BlockBase.Info.a((BlockBase) Blocks.WEATHERED_CUT_COPPER).h())));
    public static final Block EXPOSED_CUT_COPPER_SLAB = a("exposed_cut_copper_slab", (Block) (new WeatheringCopperSlabBlock(WeatheringCopper.a.EXPOSED, BlockBase.Info.a((BlockBase) Blocks.EXPOSED_CUT_COPPER).h())));
    public static final Block CUT_COPPER_SLAB = a("cut_copper_slab", (Block) (new WeatheringCopperSlabBlock(WeatheringCopper.a.UNAFFECTED, BlockBase.Info.a((BlockBase) Blocks.CUT_COPPER).h())));
    public static final Block WAXED_COPPER_BLOCK = a("waxed_copper_block", new Block(BlockBase.Info.a((BlockBase) Blocks.COPPER_BLOCK)));
    public static final Block WAXED_WEATHERED_COPPER = a("waxed_weathered_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_EXPOSED_COPPER = a("waxed_exposed_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_OXIDIZED_COPPER = a("waxed_oxidized_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_OXIDIZED_CUT_COPPER = a("waxed_oxidized_cut_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_WEATHERED_CUT_COPPER = a("waxed_weathered_cut_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_EXPOSED_CUT_COPPER = a("waxed_exposed_cut_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_CUT_COPPER = a("waxed_cut_copper", new Block(BlockBase.Info.a((BlockBase) Blocks.COPPER_BLOCK)));
    public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = a("waxed_oxidized_cut_copper_stairs", (Block) (new BlockStairs(Blocks.WAXED_OXIDIZED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.OXIDIZED_COPPER))));
    public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = a("waxed_weathered_cut_copper_stairs", (Block) (new BlockStairs(Blocks.WAXED_WEATHERED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.WEATHERED_COPPER))));
    public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = a("waxed_exposed_cut_copper_stairs", (Block) (new BlockStairs(Blocks.WAXED_EXPOSED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.EXPOSED_COPPER))));
    public static final Block WAXED_CUT_COPPER_STAIRS = a("waxed_cut_copper_stairs", (Block) (new BlockStairs(Blocks.WAXED_CUT_COPPER.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.COPPER_BLOCK))));
    public static final Block WAXED_OXIDIZED_CUT_COPPER_SLAB = a("waxed_oxidized_cut_copper_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.WAXED_OXIDIZED_CUT_COPPER).h())));
    public static final Block WAXED_WEATHERED_CUT_COPPER_SLAB = a("waxed_weathered_cut_copper_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.WAXED_WEATHERED_CUT_COPPER).h())));
    public static final Block WAXED_EXPOSED_CUT_COPPER_SLAB = a("waxed_exposed_cut_copper_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.WAXED_EXPOSED_CUT_COPPER).h())));
    public static final Block WAXED_CUT_COPPER_SLAB = a("waxed_cut_copper_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.WAXED_CUT_COPPER).h())));
    public static final Block LIGHTNING_ROD = a("lightning_rod", (Block) (new LightningRodBlock(BlockBase.Info.a(Material.METAL, MaterialMapColor.COLOR_ORANGE).h().a(3.0F, 6.0F).a(SoundEffectType.COPPER).b())));
    public static final Block POINTED_DRIPSTONE = a("pointed_dripstone", (Block) (new PointedDripstoneBlock(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).b().a(SoundEffectType.POINTED_DRIPSTONE).d().a(1.5F, 3.0F).e())));
    public static final Block DRIPSTONE_BLOCK = a("dripstone_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.TERRACOTTA_BROWN).a(SoundEffectType.DRIPSTONE_BLOCK).h().a(1.5F, 1.0F)));
    public static final Block CAVE_VINES = a("cave_vines", (Block) (new CaveVinesBlock(BlockBase.Info.a(Material.PLANT).d().a().a(CaveVines.c_(14)).c().a(SoundEffectType.CAVE_VINES))));
    public static final Block CAVE_VINES_PLANT = a("cave_vines_plant", (Block) (new CaveVinesPlantBlock(BlockBase.Info.a(Material.PLANT).a().a(CaveVines.c_(14)).c().a(SoundEffectType.CAVE_VINES))));
    public static final Block SPORE_BLOSSOM = a("spore_blossom", (Block) (new SporeBlossomBlock(BlockBase.Info.a(Material.PLANT).c().a().a(SoundEffectType.SPORE_BLOSSOM))));
    public static final Block AZALEA = a("azalea", (Block) (new AzaleaBlock(BlockBase.Info.a(Material.PLANT).c().a(SoundEffectType.AZALEA).b())));
    public static final Block FLOWERING_AZALEA = a("flowering_azalea", (Block) (new AzaleaBlock(BlockBase.Info.a(Material.PLANT).c().a(SoundEffectType.FLOWERING_AZALEA).b())));
    public static final Block MOSS_CARPET = a("moss_carpet", (Block) (new CarpetBlock(BlockBase.Info.a(Material.PLANT, MaterialMapColor.COLOR_GREEN).d(0.1F).a(SoundEffectType.MOSS_CARPET))));
    public static final Block MOSS_BLOCK = a("moss_block", (Block) (new MossBlock(BlockBase.Info.a(Material.MOSS, MaterialMapColor.COLOR_GREEN).d(0.1F).a(SoundEffectType.MOSS))));
    public static final Block BIG_DRIPLEAF = a("big_dripleaf", (Block) (new BigDripleafBlock(BlockBase.Info.a(Material.PLANT).d(0.1F).a(SoundEffectType.BIG_DRIPLEAF))));
    public static final Block BIG_DRIPLEAF_STEM = a("big_dripleaf_stem", (Block) (new BigDripleafStemBlock(BlockBase.Info.a(Material.PLANT).a().d(0.1F).a(SoundEffectType.BIG_DRIPLEAF))));
    public static final Block SMALL_DRIPLEAF = a("small_dripleaf", (Block) (new SmallDripleafBlock(BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.SMALL_DRIPLEAF))));
    public static final Block HANGING_ROOTS = a("hanging_roots", (Block) (new HangingRootsBlock(BlockBase.Info.a(Material.REPLACEABLE_PLANT, MaterialMapColor.DIRT).a().c().a(SoundEffectType.HANGING_ROOTS))));
    public static final Block ROOTED_DIRT = a("rooted_dirt", (Block) (new RootedDirtBlock(BlockBase.Info.a(Material.DIRT, MaterialMapColor.DIRT).d(0.5F).a(SoundEffectType.ROOTED_DIRT))));
    public static final Block DEEPSLATE = a("deepslate", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.DEEPSLATE).h().a(3.0F, 6.0F).a(SoundEffectType.DEEPSLATE))));
    public static final Block COBBLED_DEEPSLATE = a("cobbled_deepslate", new Block(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE).a(3.5F, 6.0F)));
    public static final Block COBBLED_DEEPSLATE_STAIRS = a("cobbled_deepslate_stairs", (Block) (new BlockStairs(Blocks.COBBLED_DEEPSLATE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE))));
    public static final Block COBBLED_DEEPSLATE_SLAB = a("cobbled_deepslate_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE))));
    public static final Block COBBLED_DEEPSLATE_WALL = a("cobbled_deepslate_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE))));
    public static final Block POLISHED_DEEPSLATE = a("polished_deepslate", new Block(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE).a(SoundEffectType.POLISHED_DEEPSLATE)));
    public static final Block POLISHED_DEEPSLATE_STAIRS = a("polished_deepslate_stairs", (Block) (new BlockStairs(Blocks.POLISHED_DEEPSLATE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_DEEPSLATE))));
    public static final Block POLISHED_DEEPSLATE_SLAB = a("polished_deepslate_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_DEEPSLATE))));
    public static final Block POLISHED_DEEPSLATE_WALL = a("polished_deepslate_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.POLISHED_DEEPSLATE))));
    public static final Block DEEPSLATE_TILES = a("deepslate_tiles", new Block(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE).a(SoundEffectType.DEEPSLATE_TILES)));
    public static final Block DEEPSLATE_TILE_STAIRS = a("deepslate_tile_stairs", (Block) (new BlockStairs(Blocks.DEEPSLATE_TILES.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_TILES))));
    public static final Block DEEPSLATE_TILE_SLAB = a("deepslate_tile_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_TILES))));
    public static final Block DEEPSLATE_TILE_WALL = a("deepslate_tile_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_TILES))));
    public static final Block DEEPSLATE_BRICKS = a("deepslate_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE).a(SoundEffectType.DEEPSLATE_BRICKS)));
    public static final Block DEEPSLATE_BRICK_STAIRS = a("deepslate_brick_stairs", (Block) (new BlockStairs(Blocks.DEEPSLATE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_BRICKS))));
    public static final Block DEEPSLATE_BRICK_SLAB = a("deepslate_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_BRICKS))));
    public static final Block DEEPSLATE_BRICK_WALL = a("deepslate_brick_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_BRICKS))));
    public static final Block CHISELED_DEEPSLATE = a("chiseled_deepslate", new Block(BlockBase.Info.a((BlockBase) Blocks.COBBLED_DEEPSLATE).a(SoundEffectType.DEEPSLATE_BRICKS)));
    public static final Block CRACKED_DEEPSLATE_BRICKS = a("cracked_deepslate_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_BRICKS)));
    public static final Block CRACKED_DEEPSLATE_TILES = a("cracked_deepslate_tiles", new Block(BlockBase.Info.a((BlockBase) Blocks.DEEPSLATE_TILES)));
    public static final Block INFESTED_DEEPSLATE = a("infested_deepslate", (Block) (new InfestedRotatedPillarBlock(Blocks.DEEPSLATE, BlockBase.Info.a(Material.CLAY, MaterialMapColor.DEEPSLATE).a(SoundEffectType.DEEPSLATE))));
    public static final Block SMOOTH_BASALT = a("smooth_basalt", new Block(BlockBase.Info.a((BlockBase) Blocks.BASALT)));
    public static final Block RAW_IRON_BLOCK = a("raw_iron_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.RAW_IRON).h().a(5.0F, 6.0F)));
    public static final Block RAW_COPPER_BLOCK = a("raw_copper_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.COLOR_ORANGE).h().a(5.0F, 6.0F)));
    public static final Block RAW_GOLD_BLOCK = a("raw_gold_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.GOLD).h().a(5.0F, 6.0F)));
    public static final Block POTTED_AZALEA = a("potted_azalea_bush", (Block) (new BlockFlowerPot(Blocks.AZALEA, BlockBase.Info.a(Material.DECORATION).c().b())));
    public static final Block POTTED_FLOWERING_AZALEA = a("potted_flowering_azalea_bush", (Block) (new BlockFlowerPot(Blocks.FLOWERING_AZALEA, BlockBase.Info.a(Material.DECORATION).c().b())));

    public Blocks() {}

    private static ToIntFunction<IBlockData> a(int i) {
        return (iblockdata) -> {
            return (Boolean) iblockdata.get(BlockProperties.LIT) ? i : 0;
        };
    }

    private static Boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return false;
    }

    private static Boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return true;
    }

    private static Boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.OCELOT || entitytypes == EntityTypes.PARROT;
    }

    private static BlockBed a(EnumColor enumcolor) {
        return new BlockBed(enumcolor, BlockBase.Info.a(Material.WOOL, (iblockdata) -> {
            return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.FOOT ? enumcolor.e() : MaterialMapColor.WOOL;
        }).a(SoundEffectType.WOOD).d(0.2F).b());
    }

    private static BlockRotatable a(MaterialMapColor materialmapcolor, MaterialMapColor materialmapcolor1) {
        return new BlockRotatable(BlockBase.Info.a(Material.WOOD, (iblockdata) -> {
            return iblockdata.get(BlockRotatable.AXIS) == EnumDirection.EnumAxis.Y ? materialmapcolor : materialmapcolor1;
        }).d(2.0F).a(SoundEffectType.WOOD));
    }

    private static Block a(MaterialMapColor materialmapcolor) {
        return new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, (iblockdata) -> {
            return materialmapcolor;
        }).d(2.0F).a(SoundEffectType.STEM));
    }

    private static boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    private static boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    private static BlockStainedGlass b(EnumColor enumcolor) {
        return new BlockStainedGlass(enumcolor, BlockBase.Info.a(Material.GLASS, enumcolor).d(0.3F).a(SoundEffectType.GLASS).b().a(Blocks::a).a(Blocks::b).b(Blocks::b).c(Blocks::b));
    }

    private static BlockLeaves a(SoundEffectType soundeffecttype) {
        return new BlockLeaves(BlockBase.Info.a(Material.LEAVES).d(0.2F).d().a(soundeffecttype).b().a(Blocks::c).b(Blocks::b).c(Blocks::b));
    }

    private static BlockShulkerBox a(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        BlockBase.e blockbase_e = (iblockdata, iblockaccess, blockposition) -> {
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntityShulkerBox)) {
                return true;
            } else {
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

                return tileentityshulkerbox.j();
            }
        };

        return new BlockShulkerBox(enumcolor, blockbase_info.d(2.0F).e().b().b(blockbase_e).c(blockbase_e));
    }

    private static BlockPiston a(boolean flag) {
        BlockBase.e blockbase_e = (iblockdata, iblockaccess, blockposition) -> {
            return !(Boolean) iblockdata.get(BlockPiston.EXTENDED);
        };

        return new BlockPiston(flag, BlockBase.Info.a(Material.PISTON).d(1.5F).a(Blocks::b).b(blockbase_e).c(blockbase_e));
    }

    private static Block a(String s, Block block) {
        return (Block) IRegistry.a((IRegistry) IRegistry.BLOCK, s, (Object) block);
    }

    public static void a() {
        Block.BLOCK_STATE_REGISTRY.forEach(BlockBase.BlockData::a);
    }

    static {
        Iterator iterator = IRegistry.BLOCK.iterator();

        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            UnmodifiableIterator unmodifiableiterator = block.getStates().a().iterator();

            while (unmodifiableiterator.hasNext()) {
                IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

                Block.BLOCK_STATE_REGISTRY.b(iblockdata);
            }

            block.r();
        }

    }
}

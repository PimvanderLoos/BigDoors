package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.EnumColor;
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
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialMapColor;

public class Blocks {

    public static final Block AIR = a("air", (Block) (new BlockAir(BlockBase.Info.a(Material.AIR).a().f().g())));
    public static final Block STONE = a("stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(1.5F, 6.0F)));
    public static final Block GRANITE = a("granite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.l).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_GRANITE = a("polished_granite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.l).h().a(1.5F, 6.0F)));
    public static final Block DIORITE = a("diorite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_DIORITE = a("polished_diorite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().a(1.5F, 6.0F)));
    public static final Block ANDESITE = a("andesite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(1.5F, 6.0F)));
    public static final Block POLISHED_ANDESITE = a("polished_andesite", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(1.5F, 6.0F)));
    public static final Block GRASS_BLOCK = a("grass_block", (Block) (new BlockGrass(BlockBase.Info.a(Material.GRASS).d().d(0.6F).a(SoundEffectType.c))));
    public static final Block DIRT = a("dirt", new Block(BlockBase.Info.a(Material.EARTH, MaterialMapColor.l).d(0.5F).a(SoundEffectType.b)));
    public static final Block COARSE_DIRT = a("coarse_dirt", new Block(BlockBase.Info.a(Material.EARTH, MaterialMapColor.l).d(0.5F).a(SoundEffectType.b)));
    public static final Block PODZOL = a("podzol", (Block) (new BlockDirtSnow(BlockBase.Info.a(Material.EARTH, MaterialMapColor.J).d(0.5F).a(SoundEffectType.b))));
    public static final Block COBBLESTONE = a("cobblestone", new Block(BlockBase.Info.a(Material.STONE).h().a(2.0F, 6.0F)));
    public static final Block OAK_PLANKS = a("oak_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.o).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block SPRUCE_PLANKS = a("spruce_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block BIRCH_PLANKS = a("birch_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block JUNGLE_PLANKS = a("jungle_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block ACACIA_PLANKS = a("acacia_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block DARK_OAK_PLANKS = a("dark_oak_planks", new Block(BlockBase.Info.a(Material.WOOD, MaterialMapColor.B).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block OAK_SAPLING = a("oak_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderOak(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block SPRUCE_SAPLING = a("spruce_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderSpruce(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block BIRCH_SAPLING = a("birch_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderBirch(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block JUNGLE_SAPLING = a("jungle_sapling", (Block) (new BlockSapling(new WorldGenMegaTreeProviderJungle(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block ACACIA_SAPLING = a("acacia_sapling", (Block) (new BlockSapling(new WorldGenTreeProviderAcacia(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block DARK_OAK_SAPLING = a("dark_oak_sapling", (Block) (new BlockSapling(new WorldGenMegaTreeProviderDarkOak(), BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block BEDROCK = a("bedrock", new Block(BlockBase.Info.a(Material.STONE).a(-1.0F, 3600000.0F).f().a(Blocks::a)));
    public static final Block WATER = a("water", (Block) (new BlockFluids(FluidTypes.WATER, BlockBase.Info.a(Material.WATER).a().d(100.0F).f())));
    public static final Block LAVA = a("lava", (Block) (new BlockFluids(FluidTypes.LAVA, BlockBase.Info.a(Material.LAVA).a().d().d(100.0F).a((iblockdata) -> {
        return 15;
    }).f())));
    public static final Block SAND = a("sand", (Block) (new BlockSand(14406560, BlockBase.Info.a(Material.SAND, MaterialMapColor.d).d(0.5F).a(SoundEffectType.i))));
    public static final Block RED_SAND = a("red_sand", (Block) (new BlockSand(11098145, BlockBase.Info.a(Material.SAND, MaterialMapColor.q).d(0.5F).a(SoundEffectType.i))));
    public static final Block GRAVEL = a("gravel", (Block) (new BlockGravel(BlockBase.Info.a(Material.SAND, MaterialMapColor.m).d(0.6F).a(SoundEffectType.b))));
    public static final Block GOLD_ORE = a("gold_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block IRON_ORE = a("iron_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block COAL_ORE = a("coal_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block NETHER_GOLD_ORE = a("nether_gold_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(3.0F, 3.0F).a(SoundEffectType.T))));
    public static final Block OAK_LOG = a("oak_log", (Block) a(MaterialMapColor.o, MaterialMapColor.J));
    public static final Block SPRUCE_LOG = a("spruce_log", (Block) a(MaterialMapColor.J, MaterialMapColor.B));
    public static final Block BIRCH_LOG = a("birch_log", (Block) a(MaterialMapColor.d, MaterialMapColor.p));
    public static final Block JUNGLE_LOG = a("jungle_log", (Block) a(MaterialMapColor.l, MaterialMapColor.J));
    public static final Block ACACIA_LOG = a("acacia_log", (Block) a(MaterialMapColor.q, MaterialMapColor.m));
    public static final Block DARK_OAK_LOG = a("dark_oak_log", (Block) a(MaterialMapColor.B, MaterialMapColor.B));
    public static final Block STRIPPED_SPRUCE_LOG = a("stripped_spruce_log", (Block) a(MaterialMapColor.J, MaterialMapColor.J));
    public static final Block STRIPPED_BIRCH_LOG = a("stripped_birch_log", (Block) a(MaterialMapColor.d, MaterialMapColor.d));
    public static final Block STRIPPED_JUNGLE_LOG = a("stripped_jungle_log", (Block) a(MaterialMapColor.l, MaterialMapColor.l));
    public static final Block STRIPPED_ACACIA_LOG = a("stripped_acacia_log", (Block) a(MaterialMapColor.q, MaterialMapColor.q));
    public static final Block STRIPPED_DARK_OAK_LOG = a("stripped_dark_oak_log", (Block) a(MaterialMapColor.B, MaterialMapColor.B));
    public static final Block STRIPPED_OAK_LOG = a("stripped_oak_log", (Block) a(MaterialMapColor.o, MaterialMapColor.o));
    public static final Block OAK_WOOD = a("oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.o).d(2.0F).a(SoundEffectType.a))));
    public static final Block SPRUCE_WOOD = a("spruce_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).d(2.0F).a(SoundEffectType.a))));
    public static final Block BIRCH_WOOD = a("birch_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).d(2.0F).a(SoundEffectType.a))));
    public static final Block JUNGLE_WOOD = a("jungle_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).d(2.0F).a(SoundEffectType.a))));
    public static final Block ACACIA_WOOD = a("acacia_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.w).d(2.0F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_WOOD = a("dark_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.B).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_OAK_WOOD = a("stripped_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.o).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_SPRUCE_WOOD = a("stripped_spruce_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_BIRCH_WOOD = a("stripped_birch_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_JUNGLE_WOOD = a("stripped_jungle_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_ACACIA_WOOD = a("stripped_acacia_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).d(2.0F).a(SoundEffectType.a))));
    public static final Block STRIPPED_DARK_OAK_WOOD = a("stripped_dark_oak_wood", (Block) (new BlockRotatable(BlockBase.Info.a(Material.WOOD, MaterialMapColor.B).d(2.0F).a(SoundEffectType.a))));
    public static final Block OAK_LEAVES = a("oak_leaves", (Block) b());
    public static final Block SPRUCE_LEAVES = a("spruce_leaves", (Block) b());
    public static final Block BIRCH_LEAVES = a("birch_leaves", (Block) b());
    public static final Block JUNGLE_LEAVES = a("jungle_leaves", (Block) b());
    public static final Block ACACIA_LEAVES = a("acacia_leaves", (Block) b());
    public static final Block DARK_OAK_LEAVES = a("dark_oak_leaves", (Block) b());
    public static final Block SPONGE = a("sponge", (Block) (new BlockSponge(BlockBase.Info.a(Material.SPONGE).d(0.6F).a(SoundEffectType.c))));
    public static final Block WET_SPONGE = a("wet_sponge", (Block) (new BlockWetSponge(BlockBase.Info.a(Material.SPONGE).d(0.6F).a(SoundEffectType.c))));
    public static final Block GLASS = a("glass", (Block) (new BlockGlass(BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b().a(Blocks::a).a(Blocks::b).b(Blocks::b).c(Blocks::b))));
    public static final Block LAPIS_ORE = a("lapis_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block LAPIS_BLOCK = a("lapis_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.H).h().a(3.0F, 3.0F)));
    public static final Block DISPENSER = a("dispenser", (Block) (new BlockDispenser(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block SANDSTONE = a("sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().d(0.8F)));
    public static final Block CHISELED_SANDSTONE = a("chiseled_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().d(0.8F)));
    public static final Block CUT_SANDSTONE = a("cut_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().d(0.8F)));
    public static final Block NOTE_BLOCK = a("note_block", (Block) (new BlockNote(BlockBase.Info.a(Material.WOOD).a(SoundEffectType.a).d(0.8F))));
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
    public static final Block POWERED_RAIL = a("powered_rail", (Block) (new BlockPoweredRail(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.7F).a(SoundEffectType.f))));
    public static final Block DETECTOR_RAIL = a("detector_rail", (Block) (new BlockMinecartDetector(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.7F).a(SoundEffectType.f))));
    public static final Block STICKY_PISTON = a("sticky_piston", (Block) a(true));
    public static final Block COBWEB = a("cobweb", (Block) (new BlockWeb(BlockBase.Info.a(Material.WEB).a().h().d(4.0F))));
    public static final Block GRASS = a("grass", (Block) (new BlockLongGrass(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block FERN = a("fern", (Block) (new BlockLongGrass(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block DEAD_BUSH = a("dead_bush", (Block) (new BlockDeadBush(BlockBase.Info.a(Material.REPLACEABLE_PLANT, MaterialMapColor.o).a().c().a(SoundEffectType.c))));
    public static final Block SEAGRASS = a("seagrass", (Block) (new BlockSeaGrass(BlockBase.Info.a(Material.REPLACEABLE_WATER_PLANT).a().c().a(SoundEffectType.o))));
    public static final Block TALL_SEAGRASS = a("tall_seagrass", (Block) (new BlockTallSeaGrass(BlockBase.Info.a(Material.REPLACEABLE_WATER_PLANT).a().c().a(SoundEffectType.o))));
    public static final Block PISTON = a("piston", (Block) a(false));
    public static final Block PISTON_HEAD = a("piston_head", (Block) (new BlockPistonExtension(BlockBase.Info.a(Material.PISTON).d(1.5F).f())));
    public static final Block WHITE_WOOL = a("white_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.j).d(0.8F).a(SoundEffectType.h)));
    public static final Block ORANGE_WOOL = a("orange_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.q).d(0.8F).a(SoundEffectType.h)));
    public static final Block MAGENTA_WOOL = a("magenta_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.r).d(0.8F).a(SoundEffectType.h)));
    public static final Block LIGHT_BLUE_WOOL = a("light_blue_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.s).d(0.8F).a(SoundEffectType.h)));
    public static final Block YELLOW_WOOL = a("yellow_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.t).d(0.8F).a(SoundEffectType.h)));
    public static final Block LIME_WOOL = a("lime_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.u).d(0.8F).a(SoundEffectType.h)));
    public static final Block PINK_WOOL = a("pink_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.v).d(0.8F).a(SoundEffectType.h)));
    public static final Block GRAY_WOOL = a("gray_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.w).d(0.8F).a(SoundEffectType.h)));
    public static final Block LIGHT_GRAY_WOOL = a("light_gray_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.x).d(0.8F).a(SoundEffectType.h)));
    public static final Block CYAN_WOOL = a("cyan_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.y).d(0.8F).a(SoundEffectType.h)));
    public static final Block PURPLE_WOOL = a("purple_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.z).d(0.8F).a(SoundEffectType.h)));
    public static final Block BLUE_WOOL = a("blue_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.A).d(0.8F).a(SoundEffectType.h)));
    public static final Block BROWN_WOOL = a("brown_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.B).d(0.8F).a(SoundEffectType.h)));
    public static final Block GREEN_WOOL = a("green_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.C).d(0.8F).a(SoundEffectType.h)));
    public static final Block RED_WOOL = a("red_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.D).d(0.8F).a(SoundEffectType.h)));
    public static final Block BLACK_WOOL = a("black_wool", new Block(BlockBase.Info.a(Material.CLOTH, MaterialMapColor.E).d(0.8F).a(SoundEffectType.h)));
    public static final Block MOVING_PISTON = a("moving_piston", (Block) (new BlockPistonMoving(BlockBase.Info.a(Material.PISTON).d(-1.0F).e().f().b().a(Blocks::b).b(Blocks::b).c(Blocks::b))));
    public static final Block DANDELION = a("dandelion", (Block) (new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block POPPY = a("poppy", (Block) (new BlockFlowers(MobEffects.NIGHT_VISION, 5, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block BLUE_ORCHID = a("blue_orchid", (Block) (new BlockFlowers(MobEffects.SATURATION, 7, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block ALLIUM = a("allium", (Block) (new BlockFlowers(MobEffects.FIRE_RESISTANCE, 4, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block AZURE_BLUET = a("azure_bluet", (Block) (new BlockFlowers(MobEffects.BLINDNESS, 8, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block RED_TULIP = a("red_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block ORANGE_TULIP = a("orange_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block WHITE_TULIP = a("white_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block PINK_TULIP = a("pink_tulip", (Block) (new BlockFlowers(MobEffects.WEAKNESS, 9, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block OXEYE_DAISY = a("oxeye_daisy", (Block) (new BlockFlowers(MobEffects.REGENERATION, 8, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block CORNFLOWER = a("cornflower", (Block) (new BlockFlowers(MobEffects.JUMP, 6, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block WITHER_ROSE = a("wither_rose", (Block) (new BlockWitherRose(MobEffects.WITHER, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block LILY_OF_THE_VALLEY = a("lily_of_the_valley", (Block) (new BlockFlowers(MobEffects.POISON, 12, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.c))));
    public static final Block BROWN_MUSHROOM = a("brown_mushroom", (Block) (new BlockMushroom(BlockBase.Info.a(Material.PLANT, MaterialMapColor.B).a().d().c().a(SoundEffectType.c).a((iblockdata) -> {
        return 1;
    }).d(Blocks::a))));
    public static final Block RED_MUSHROOM = a("red_mushroom", (Block) (new BlockMushroom(BlockBase.Info.a(Material.PLANT, MaterialMapColor.D).a().d().c().a(SoundEffectType.c).d(Blocks::a))));
    public static final Block GOLD_BLOCK = a("gold_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.F).h().a(3.0F, 6.0F).a(SoundEffectType.f)));
    public static final Block IRON_BLOCK = a("iron_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.h).h().a(5.0F, 6.0F).a(SoundEffectType.f)));
    public static final Block BRICKS = a("bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.D).h().a(2.0F, 6.0F)));
    public static final Block TNT = a("tnt", (Block) (new BlockTNT(BlockBase.Info.a(Material.TNT).c().a(SoundEffectType.c))));
    public static final Block BOOKSHELF = a("bookshelf", new Block(BlockBase.Info.a(Material.WOOD).d(1.5F).a(SoundEffectType.a)));
    public static final Block MOSSY_COBBLESTONE = a("mossy_cobblestone", new Block(BlockBase.Info.a(Material.STONE).h().a(2.0F, 6.0F)));
    public static final Block OBSIDIAN = a("obsidian", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(50.0F, 1200.0F)));
    public static final Block TORCH = a("torch", (Block) (new BlockTorch(BlockBase.Info.a(Material.ORIENTABLE).a().c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.a), Particles.FLAME)));
    public static final Block WALL_TORCH = a("wall_torch", (Block) (new BlockTorchWall(BlockBase.Info.a(Material.ORIENTABLE).a().c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.a).a(Blocks.TORCH), Particles.FLAME)));
    public static final Block FIRE = a("fire", (Block) (new BlockFire(BlockBase.Info.a(Material.FIRE, MaterialMapColor.f).a().c().a((iblockdata) -> {
        return 15;
    }).a(SoundEffectType.h))));
    public static final Block SOUL_FIRE = a("soul_fire", (Block) (new BlockSoulFire(BlockBase.Info.a(Material.FIRE, MaterialMapColor.s).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.h))));
    public static final Block SPAWNER = a("spawner", (Block) (new BlockMobSpawner(BlockBase.Info.a(Material.STONE).h().d(5.0F).a(SoundEffectType.f).b())));
    public static final Block OAK_STAIRS = a("oak_stairs", (Block) (new BlockStairs(Blocks.OAK_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.OAK_PLANKS))));
    public static final Block CHEST = a("chest", (Block) (new BlockChest(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a), () -> {
        return TileEntityTypes.CHEST;
    })));
    public static final Block REDSTONE_WIRE = a("redstone_wire", (Block) (new BlockRedstoneWire(BlockBase.Info.a(Material.ORIENTABLE).a().c())));
    public static final Block DIAMOND_ORE = a("diamond_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block DIAMOND_BLOCK = a("diamond_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.G).h().a(5.0F, 6.0F).a(SoundEffectType.f)));
    public static final Block CRAFTING_TABLE = a("crafting_table", (Block) (new BlockWorkbench(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block WHEAT = a("wheat", (Block) (new BlockCrops(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.u))));
    public static final Block FARMLAND = a("farmland", (Block) (new BlockSoil(BlockBase.Info.a(Material.EARTH).d().d(0.6F).a(SoundEffectType.b).c(Blocks::a).b(Blocks::a))));
    public static final Block FURNACE = a("furnace", (Block) (new BlockFurnaceFurace(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block OAK_SIGN = a("oak_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.a)));
    public static final Block SPRUCE_SIGN = a("spruce_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_LOG.s()).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.b)));
    public static final Block BIRCH_SIGN = a("birch_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.c)));
    public static final Block ACACIA_SIGN = a("acacia_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.d)));
    public static final Block JUNGLE_SIGN = a("jungle_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_LOG.s()).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.e)));
    public static final Block DARK_OAK_SIGN = a("dark_oak_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_LOG.s()).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.f)));
    public static final Block OAK_DOOR = a("oak_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block LADDER = a("ladder", (Block) (new BlockLadder(BlockBase.Info.a(Material.ORIENTABLE).d(0.4F).a(SoundEffectType.k).b())));
    public static final Block RAIL = a("rail", (Block) (new BlockMinecartTrack(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.7F).a(SoundEffectType.f))));
    public static final Block COBBLESTONE_STAIRS = a("cobblestone_stairs", (Block) (new BlockStairs(Blocks.COBBLESTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block OAK_WALL_SIGN = a("oak_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.OAK_SIGN), BlockPropertyWood.a)));
    public static final Block SPRUCE_WALL_SIGN = a("spruce_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_LOG.s()).a().d(1.0F).a(SoundEffectType.a).a(Blocks.SPRUCE_SIGN), BlockPropertyWood.b)));
    public static final Block BIRCH_WALL_SIGN = a("birch_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).a().d(1.0F).a(SoundEffectType.a).a(Blocks.BIRCH_SIGN), BlockPropertyWood.c)));
    public static final Block ACACIA_WALL_SIGN = a("acacia_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).a().d(1.0F).a(SoundEffectType.a).a(Blocks.ACACIA_SIGN), BlockPropertyWood.d)));
    public static final Block JUNGLE_WALL_SIGN = a("jungle_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_LOG.s()).a().d(1.0F).a(SoundEffectType.a).a(Blocks.JUNGLE_SIGN), BlockPropertyWood.e)));
    public static final Block DARK_OAK_WALL_SIGN = a("dark_oak_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_LOG.s()).a().d(1.0F).a(SoundEffectType.a).a(Blocks.DARK_OAK_SIGN), BlockPropertyWood.f)));
    public static final Block LEVER = a("lever", (Block) (new BlockLever(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block STONE_PRESSURE_PLATE = a("stone_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.a(Material.STONE).h().a().d(0.5F))));
    public static final Block IRON_DOOR = a("iron_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.ORE, MaterialMapColor.h).h().d(5.0F).a(SoundEffectType.f).b())));
    public static final Block OAK_PRESSURE_PLATE = a("oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block SPRUCE_PRESSURE_PLATE = a("spruce_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block BIRCH_PRESSURE_PLATE = a("birch_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block JUNGLE_PRESSURE_PLATE = a("jungle_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block ACACIA_PRESSURE_PLATE = a("acacia_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_PRESSURE_PLATE = a("dark_oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block REDSTONE_ORE = a("redstone_ore", (Block) (new BlockRedstoneOre(BlockBase.Info.a(Material.STONE).h().d().a(a(9)).a(3.0F, 3.0F))));
    public static final Block REDSTONE_TORCH = a("redstone_torch", (Block) (new BlockRedstoneTorch(BlockBase.Info.a(Material.ORIENTABLE).a().c().a(a(7)).a(SoundEffectType.a))));
    public static final Block REDSTONE_WALL_TORCH = a("redstone_wall_torch", (Block) (new BlockRedstoneTorchWall(BlockBase.Info.a(Material.ORIENTABLE).a().c().a(a(7)).a(SoundEffectType.a).a(Blocks.REDSTONE_TORCH))));
    public static final Block STONE_BUTTON = a("stone_button", (Block) (new BlockStoneButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F))));
    public static final Block SNOW = a("snow", (Block) (new BlockSnow(BlockBase.Info.a(Material.PACKED_ICE).d().d(0.1F).h().a(SoundEffectType.j))));
    public static final Block ICE = a("ice", (Block) (new BlockIce(BlockBase.Info.a(Material.ICE).a(0.98F).d().d(0.5F).a(SoundEffectType.g).b().a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    }))));
    public static final Block SNOW_BLOCK = a("snow_block", new Block(BlockBase.Info.a(Material.SNOW_BLOCK).h().d(0.2F).a(SoundEffectType.j)));
    public static final Block CACTUS = a("cactus", (Block) (new BlockCactus(BlockBase.Info.a(Material.CACTUS).d().d(0.4F).a(SoundEffectType.h))));
    public static final Block CLAY = a("clay", new Block(BlockBase.Info.a(Material.CLAY).d(0.6F).a(SoundEffectType.b)));
    public static final Block SUGAR_CANE = a("sugar_cane", (Block) (new BlockReed(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.c))));
    public static final Block JUKEBOX = a("jukebox", (Block) (new BlockJukeBox(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 6.0F))));
    public static final Block OAK_FENCE = a("oak_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block PUMPKIN = a("pumpkin", (Block) (new BlockPumpkin(BlockBase.Info.a(Material.PUMPKIN, MaterialMapColor.q).d(1.0F).a(SoundEffectType.a))));
    public static final Block NETHERRACK = a("netherrack", (Block) (new BlockNetherrack(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().d(0.4F).a(SoundEffectType.K))));
    public static final Block SOUL_SAND = a("soul_sand", (Block) (new BlockSlowSand(BlockBase.Info.a(Material.SAND, MaterialMapColor.B).d(0.5F).b(0.4F).a(SoundEffectType.G).a(Blocks::b).a(Blocks::a).c(Blocks::a).b(Blocks::a))));
    public static final Block SOUL_SOIL = a("soul_soil", new Block(BlockBase.Info.a(Material.EARTH, MaterialMapColor.B).d(0.5F).a(SoundEffectType.H)));
    public static final Block BASALT = a("basalt", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(1.25F, 4.2F).a(SoundEffectType.I))));
    public static final Block cP = a("polished_basalt", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(1.25F, 4.2F).a(SoundEffectType.I))));
    public static final Block SOUL_TORCH = a("soul_torch", (Block) (new BlockTorch(BlockBase.Info.a(Material.ORIENTABLE).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.a), Particles.SOUL_FIRE_FLAME)));
    public static final Block SOUL_WALL_TORCH = a("soul_wall_torch", (Block) (new BlockTorchWall(BlockBase.Info.a(Material.ORIENTABLE).a().c().a((iblockdata) -> {
        return 10;
    }).a(SoundEffectType.a).a(Blocks.SOUL_TORCH), Particles.SOUL_FIRE_FLAME)));
    public static final Block GLOWSTONE = a("glowstone", new Block(BlockBase.Info.a(Material.SHATTERABLE, MaterialMapColor.d).d(0.3F).a(SoundEffectType.g).a((iblockdata) -> {
        return 15;
    })));
    public static final Block NETHER_PORTAL = a("nether_portal", (Block) (new BlockPortal(BlockBase.Info.a(Material.PORTAL).a().d().d(-1.0F).a(SoundEffectType.g).a((iblockdata) -> {
        return 11;
    }))));
    public static final Block CARVED_PUMPKIN = a("carved_pumpkin", (Block) (new BlockPumpkinCarved(BlockBase.Info.a(Material.PUMPKIN, MaterialMapColor.q).d(1.0F).a(SoundEffectType.a).a(Blocks::b))));
    public static final Block JACK_O_LANTERN = a("jack_o_lantern", (Block) (new BlockPumpkinCarved(BlockBase.Info.a(Material.PUMPKIN, MaterialMapColor.q).d(1.0F).a(SoundEffectType.a).a((iblockdata) -> {
        return 15;
    }).a(Blocks::b))));
    public static final Block CAKE = a("cake", (Block) (new BlockCake(BlockBase.Info.a(Material.CAKE).d(0.5F).a(SoundEffectType.h))));
    public static final Block REPEATER = a("repeater", (Block) (new BlockRepeater(BlockBase.Info.a(Material.ORIENTABLE).c().a(SoundEffectType.a))));
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
    public static final Block OAK_TRAPDOOR = a("oak_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.o).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block SPRUCE_TRAPDOOR = a("spruce_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block BIRCH_TRAPDOOR = a("birch_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block JUNGLE_TRAPDOOR = a("jungle_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block ACACIA_TRAPDOOR = a("acacia_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block DARK_OAK_TRAPDOOR = a("dark_oak_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.WOOD, MaterialMapColor.B).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block STONE_BRICKS = a("stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block MOSSY_STONE_BRICKS = a("mossy_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block CRACKED_STONE_BRICKS = a("cracked_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block CHISELED_STONE_BRICKS = a("chiseled_stone_bricks", new Block(BlockBase.Info.a(Material.STONE).h().a(1.5F, 6.0F)));
    public static final Block INFESTED_STONE = a("infested_stone", (Block) (new BlockMonsterEggs(Blocks.STONE, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block INFESTED_COBBLESTONE = a("infested_cobblestone", (Block) (new BlockMonsterEggs(Blocks.COBBLESTONE, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block INFESTED_STONE_BRICKS = a("infested_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.STONE_BRICKS, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block INFESTED_MOSSY_STONE_BRICKS = a("infested_mossy_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.MOSSY_STONE_BRICKS, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block INFESTED_CRACKED_STONE_BRICKS = a("infested_cracked_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.CRACKED_STONE_BRICKS, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block INFESTED_CHISELED_STONE_BRICKS = a("infested_chiseled_stone_bricks", (Block) (new BlockMonsterEggs(Blocks.CHISELED_STONE_BRICKS, BlockBase.Info.a(Material.CLAY).a(0.0F, 0.75F))));
    public static final Block BROWN_MUSHROOM_BLOCK = a("brown_mushroom_block", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).d(0.2F).a(SoundEffectType.a))));
    public static final Block RED_MUSHROOM_BLOCK = a("red_mushroom_block", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.D).d(0.2F).a(SoundEffectType.a))));
    public static final Block MUSHROOM_STEM = a("mushroom_stem", (Block) (new BlockHugeMushroom(BlockBase.Info.a(Material.WOOD, MaterialMapColor.e).d(0.2F).a(SoundEffectType.a))));
    public static final Block IRON_BARS = a("iron_bars", (Block) (new BlockIronBars(BlockBase.Info.a(Material.ORE, MaterialMapColor.b).h().a(5.0F, 6.0F).a(SoundEffectType.f).b())));
    public static final Block CHAIN = a("chain", (Block) (new BlockChain(BlockBase.Info.a(Material.ORE, MaterialMapColor.b).h().a(5.0F, 6.0F).a(SoundEffectType.S).b())));
    public static final Block GLASS_PANE = a("glass_pane", (Block) (new BlockIronBars(BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block MELON = a("melon", (Block) (new BlockMelon(BlockBase.Info.a(Material.PUMPKIN, MaterialMapColor.u).d(1.0F).a(SoundEffectType.a))));
    public static final Block ATTACHED_PUMPKIN_STEM = a("attached_pumpkin_stem", (Block) (new BlockStemAttached((BlockStemmed) Blocks.PUMPKIN, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.a))));
    public static final Block ATTACHED_MELON_STEM = a("attached_melon_stem", (Block) (new BlockStemAttached((BlockStemmed) Blocks.MELON, BlockBase.Info.a(Material.PLANT).a().c().a(SoundEffectType.a))));
    public static final Block PUMPKIN_STEM = a("pumpkin_stem", (Block) (new BlockStem((BlockStemmed) Blocks.PUMPKIN, BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.v))));
    public static final Block MELON_STEM = a("melon_stem", (Block) (new BlockStem((BlockStemmed) Blocks.MELON, BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.v))));
    public static final Block VINE = a("vine", (Block) (new BlockVine(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().d().d(0.2F).a(SoundEffectType.w))));
    public static final Block OAK_FENCE_GATE = a("oak_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block BRICK_STAIRS = a("brick_stairs", (Block) (new BlockStairs(Blocks.BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.BRICKS))));
    public static final Block STONE_BRICK_STAIRS = a("stone_brick_stairs", (Block) (new BlockStairs(Blocks.STONE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.STONE_BRICKS))));
    public static final Block MYCELIUM = a("mycelium", (Block) (new BlockMycel(BlockBase.Info.a(Material.GRASS, MaterialMapColor.z).d().d(0.6F).a(SoundEffectType.c))));
    public static final Block LILY_PAD = a("lily_pad", (Block) (new BlockWaterLily(BlockBase.Info.a(Material.PLANT).c().a(SoundEffectType.d).b())));
    public static final Block NETHER_BRICKS = a("nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L)));
    public static final Block NETHER_BRICK_FENCE = a("nether_brick_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L))));
    public static final Block NETHER_BRICK_STAIRS = a("nether_brick_stairs", (Block) (new BlockStairs(Blocks.NETHER_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.NETHER_BRICKS))));
    public static final Block NETHER_WART = a("nether_wart", (Block) (new BlockNetherWart(BlockBase.Info.a(Material.PLANT, MaterialMapColor.D).a().d().a(SoundEffectType.x))));
    public static final Block ENCHANTING_TABLE = a("enchanting_table", (Block) (new BlockEnchantmentTable(BlockBase.Info.a(Material.STONE, MaterialMapColor.D).h().a(5.0F, 1200.0F))));
    public static final Block BREWING_STAND = a("brewing_stand", (Block) (new BlockBrewingStand(BlockBase.Info.a(Material.ORE).h().d(0.5F).a((iblockdata) -> {
        return 1;
    }).b())));
    public static final Block CAULDRON = a("cauldron", (Block) (new BlockCauldron(BlockBase.Info.a(Material.ORE, MaterialMapColor.m).h().d(2.0F).b())));
    public static final Block END_PORTAL = a("end_portal", (Block) (new BlockEnderPortal(BlockBase.Info.a(Material.PORTAL, MaterialMapColor.E).a().a((iblockdata) -> {
        return 15;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block END_PORTAL_FRAME = a("end_portal_frame", (Block) (new BlockEnderPortalFrame(BlockBase.Info.a(Material.STONE, MaterialMapColor.C).a(SoundEffectType.g).a((iblockdata) -> {
        return 1;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block END_STONE = a("end_stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().a(3.0F, 9.0F)));
    public static final Block DRAGON_EGG = a("dragon_egg", (Block) (new BlockDragonEgg(BlockBase.Info.a(Material.DRAGON_EGG, MaterialMapColor.E).a(3.0F, 9.0F).a((iblockdata) -> {
        return 1;
    }).b())));
    public static final Block REDSTONE_LAMP = a("redstone_lamp", (Block) (new BlockRedstoneLamp(BlockBase.Info.a(Material.BUILDABLE_GLASS).a(a(15)).d(0.3F).a(SoundEffectType.g).a(Blocks::b))));
    public static final Block COCOA = a("cocoa", (Block) (new BlockCocoa(BlockBase.Info.a(Material.PLANT).d().a(0.2F, 3.0F).a(SoundEffectType.a).b())));
    public static final Block SANDSTONE_STAIRS = a("sandstone_stairs", (Block) (new BlockStairs(Blocks.SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SANDSTONE))));
    public static final Block EMERALD_ORE = a("emerald_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE).h().a(3.0F, 3.0F))));
    public static final Block ENDER_CHEST = a("ender_chest", (Block) (new BlockEnderChest(BlockBase.Info.a(Material.STONE).h().a(22.5F, 600.0F).a((iblockdata) -> {
        return 7;
    }))));
    public static final Block TRIPWIRE_HOOK = a("tripwire_hook", (Block) (new BlockTripwireHook(BlockBase.Info.a(Material.ORIENTABLE).a())));
    public static final Block TRIPWIRE = a("tripwire", (Block) (new BlockTripwire((BlockTripwireHook) Blocks.TRIPWIRE_HOOK, BlockBase.Info.a(Material.ORIENTABLE).a())));
    public static final Block EMERALD_BLOCK = a("emerald_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.I).h().a(5.0F, 6.0F).a(SoundEffectType.f)));
    public static final Block SPRUCE_STAIRS = a("spruce_stairs", (Block) (new BlockStairs(Blocks.SPRUCE_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.SPRUCE_PLANKS))));
    public static final Block BIRCH_STAIRS = a("birch_stairs", (Block) (new BlockStairs(Blocks.BIRCH_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.BIRCH_PLANKS))));
    public static final Block JUNGLE_STAIRS = a("jungle_stairs", (Block) (new BlockStairs(Blocks.JUNGLE_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.JUNGLE_PLANKS))));
    public static final Block COMMAND_BLOCK = a("command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.ORE, MaterialMapColor.B).h().a(-1.0F, 3600000.0F).f())));
    public static final Block BEACON = a("beacon", (Block) (new BlockBeacon(BlockBase.Info.a(Material.SHATTERABLE, MaterialMapColor.G).d(3.0F).a((iblockdata) -> {
        return 15;
    }).b().a(Blocks::b))));
    public static final Block COBBLESTONE_WALL = a("cobblestone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block MOSSY_COBBLESTONE_WALL = a("mossy_cobblestone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.COBBLESTONE))));
    public static final Block FLOWER_POT = a("flower_pot", (Block) (new BlockFlowerPot(Blocks.AIR, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_OAK_SAPLING = a("potted_oak_sapling", (Block) (new BlockFlowerPot(Blocks.OAK_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_SPRUCE_SAPLING = a("potted_spruce_sapling", (Block) (new BlockFlowerPot(Blocks.SPRUCE_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_BIRCH_SAPLING = a("potted_birch_sapling", (Block) (new BlockFlowerPot(Blocks.BIRCH_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_JUNGLE_SAPLING = a("potted_jungle_sapling", (Block) (new BlockFlowerPot(Blocks.JUNGLE_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_ACACIA_SAPLING = a("potted_acacia_sapling", (Block) (new BlockFlowerPot(Blocks.ACACIA_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_DARK_OAK_SAPLING = a("potted_dark_oak_sapling", (Block) (new BlockFlowerPot(Blocks.DARK_OAK_SAPLING, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_FERN = a("potted_fern", (Block) (new BlockFlowerPot(Blocks.FERN, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_DANDELION = a("potted_dandelion", (Block) (new BlockFlowerPot(Blocks.DANDELION, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_POPPY = a("potted_poppy", (Block) (new BlockFlowerPot(Blocks.POPPY, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_BLUE_ORCHID = a("potted_blue_orchid", (Block) (new BlockFlowerPot(Blocks.BLUE_ORCHID, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_ALLIUM = a("potted_allium", (Block) (new BlockFlowerPot(Blocks.ALLIUM, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_AZURE_BLUET = a("potted_azure_bluet", (Block) (new BlockFlowerPot(Blocks.AZURE_BLUET, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_RED_TULIP = a("potted_red_tulip", (Block) (new BlockFlowerPot(Blocks.RED_TULIP, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_ORANGE_TULIP = a("potted_orange_tulip", (Block) (new BlockFlowerPot(Blocks.ORANGE_TULIP, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_WHITE_TULIP = a("potted_white_tulip", (Block) (new BlockFlowerPot(Blocks.WHITE_TULIP, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_PINK_TULIP = a("potted_pink_tulip", (Block) (new BlockFlowerPot(Blocks.PINK_TULIP, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_OXEYE_DAISY = a("potted_oxeye_daisy", (Block) (new BlockFlowerPot(Blocks.OXEYE_DAISY, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_CORNFLOWER = a("potted_cornflower", (Block) (new BlockFlowerPot(Blocks.CORNFLOWER, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_LILY_OF_THE_VALLEY = a("potted_lily_of_the_valley", (Block) (new BlockFlowerPot(Blocks.LILY_OF_THE_VALLEY, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_WITHER_ROSE = a("potted_wither_rose", (Block) (new BlockFlowerPot(Blocks.WITHER_ROSE, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_RED_MUSHROOM = a("potted_red_mushroom", (Block) (new BlockFlowerPot(Blocks.RED_MUSHROOM, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_BROWN_MUSHROOM = a("potted_brown_mushroom", (Block) (new BlockFlowerPot(Blocks.BROWN_MUSHROOM, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_DEAD_BUSH = a("potted_dead_bush", (Block) (new BlockFlowerPot(Blocks.DEAD_BUSH, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_CACTUS = a("potted_cactus", (Block) (new BlockFlowerPot(Blocks.CACTUS, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block CARROTS = a("carrots", (Block) (new BlockCarrots(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.u))));
    public static final Block POTATOES = a("potatoes", (Block) (new BlockPotatoes(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.u))));
    public static final Block OAK_BUTTON = a("oak_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block SPRUCE_BUTTON = a("spruce_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block BIRCH_BUTTON = a("birch_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block JUNGLE_BUTTON = a("jungle_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block ACACIA_BUTTON = a("acacia_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_BUTTON = a("dark_oak_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block SKELETON_SKULL = a("skeleton_skull", (Block) (new BlockSkull(BlockSkull.Type.SKELETON, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block SKELETON_WALL_SKULL = a("skeleton_wall_skull", (Block) (new BlockSkullWall(BlockSkull.Type.SKELETON, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.SKELETON_SKULL))));
    public static final Block WITHER_SKELETON_SKULL = a("wither_skeleton_skull", (Block) (new BlockWitherSkull(BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block WITHER_SKELETON_WALL_SKULL = a("wither_skeleton_wall_skull", (Block) (new BlockWitherSkullWall(BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.WITHER_SKELETON_SKULL))));
    public static final Block ZOMBIE_HEAD = a("zombie_head", (Block) (new BlockSkull(BlockSkull.Type.ZOMBIE, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block ZOMBIE_WALL_HEAD = a("zombie_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.ZOMBIE, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.ZOMBIE_HEAD))));
    public static final Block PLAYER_HEAD = a("player_head", (Block) (new BlockSkullPlayer(BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block PLAYER_WALL_HEAD = a("player_wall_head", (Block) (new BlockSkullPlayerWall(BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.PLAYER_HEAD))));
    public static final Block CREEPER_HEAD = a("creeper_head", (Block) (new BlockSkull(BlockSkull.Type.CREEPER, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block CREEPER_WALL_HEAD = a("creeper_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.CREEPER, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.CREEPER_HEAD))));
    public static final Block DRAGON_HEAD = a("dragon_head", (Block) (new BlockSkull(BlockSkull.Type.DRAGON, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F))));
    public static final Block DRAGON_WALL_HEAD = a("dragon_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.DRAGON, BlockBase.Info.a(Material.ORIENTABLE).d(1.0F).a(Blocks.DRAGON_HEAD))));
    public static final Block ANVIL = a("anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY, MaterialMapColor.h).h().a(5.0F, 1200.0F).a(SoundEffectType.l))));
    public static final Block CHIPPED_ANVIL = a("chipped_anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY, MaterialMapColor.h).h().a(5.0F, 1200.0F).a(SoundEffectType.l))));
    public static final Block DAMAGED_ANVIL = a("damaged_anvil", (Block) (new BlockAnvil(BlockBase.Info.a(Material.HEAVY, MaterialMapColor.h).h().a(5.0F, 1200.0F).a(SoundEffectType.l))));
    public static final Block TRAPPED_CHEST = a("trapped_chest", (Block) (new BlockChestTrapped(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE = a("light_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(15, BlockBase.Info.a(Material.ORE, MaterialMapColor.F).h().a().d(0.5F).a(SoundEffectType.a))));
    public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE = a("heavy_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(150, BlockBase.Info.a(Material.ORE).h().a().d(0.5F).a(SoundEffectType.a))));
    public static final Block COMPARATOR = a("comparator", (Block) (new BlockRedstoneComparator(BlockBase.Info.a(Material.ORIENTABLE).c().a(SoundEffectType.a))));
    public static final Block DAYLIGHT_DETECTOR = a("daylight_detector", (Block) (new BlockDaylightDetector(BlockBase.Info.a(Material.WOOD).d(0.2F).a(SoundEffectType.a))));
    public static final Block REDSTONE_BLOCK = a("redstone_block", (Block) (new BlockPowered(BlockBase.Info.a(Material.ORE, MaterialMapColor.f).h().a(5.0F, 6.0F).a(SoundEffectType.f).a(Blocks::b))));
    public static final Block NETHER_QUARTZ_ORE = a("nether_quartz_ore", (Block) (new BlockOre(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(3.0F, 3.0F).a(SoundEffectType.N))));
    public static final Block HOPPER = a("hopper", (Block) (new BlockHopper(BlockBase.Info.a(Material.ORE, MaterialMapColor.m).h().a(3.0F, 4.8F).a(SoundEffectType.f).b())));
    public static final Block QUARTZ_BLOCK = a("quartz_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().d(0.8F)));
    public static final Block CHISELED_QUARTZ_BLOCK = a("chiseled_quartz_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().d(0.8F)));
    public static final Block QUARTZ_PILLAR = a("quartz_pillar", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().d(0.8F))));
    public static final Block QUARTZ_STAIRS = a("quartz_stairs", (Block) (new BlockStairs(Blocks.QUARTZ_BLOCK.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.QUARTZ_BLOCK))));
    public static final Block ACTIVATOR_RAIL = a("activator_rail", (Block) (new BlockPoweredRail(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.7F).a(SoundEffectType.f))));
    public static final Block DROPPER = a("dropper", (Block) (new BlockDropper(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block WHITE_TERRACOTTA = a("white_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.L).h().a(1.25F, 4.2F)));
    public static final Block ORANGE_TERRACOTTA = a("orange_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.M).h().a(1.25F, 4.2F)));
    public static final Block MAGENTA_TERRACOTTA = a("magenta_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.N).h().a(1.25F, 4.2F)));
    public static final Block LIGHT_BLUE_TERRACOTTA = a("light_blue_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.O).h().a(1.25F, 4.2F)));
    public static final Block YELLOW_TERRACOTTA = a("yellow_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.P).h().a(1.25F, 4.2F)));
    public static final Block LIME_TERRACOTTA = a("lime_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.Q).h().a(1.25F, 4.2F)));
    public static final Block PINK_TERRACOTTA = a("pink_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.R).h().a(1.25F, 4.2F)));
    public static final Block GRAY_TERRACOTTA = a("gray_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.S).h().a(1.25F, 4.2F)));
    public static final Block LIGHT_GRAY_TERRACOTTA = a("light_gray_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.T).h().a(1.25F, 4.2F)));
    public static final Block CYAN_TERRACOTTA = a("cyan_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.U).h().a(1.25F, 4.2F)));
    public static final Block PURPLE_TERRACOTTA = a("purple_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.V).h().a(1.25F, 4.2F)));
    public static final Block BLUE_TERRACOTTA = a("blue_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.W).h().a(1.25F, 4.2F)));
    public static final Block BROWN_TERRACOTTA = a("brown_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.X).h().a(1.25F, 4.2F)));
    public static final Block GREEN_TERRACOTTA = a("green_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.Y).h().a(1.25F, 4.2F)));
    public static final Block RED_TERRACOTTA = a("red_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.Z).h().a(1.25F, 4.2F)));
    public static final Block BLACK_TERRACOTTA = a("black_terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.aa).h().a(1.25F, 4.2F)));
    public static final Block WHITE_STAINED_GLASS_PANE = a("white_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.WHITE, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block ORANGE_STAINED_GLASS_PANE = a("orange_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.ORANGE, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block MAGENTA_STAINED_GLASS_PANE = a("magenta_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.MAGENTA, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block LIGHT_BLUE_STAINED_GLASS_PANE = a("light_blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block YELLOW_STAINED_GLASS_PANE = a("yellow_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.YELLOW, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block LIME_STAINED_GLASS_PANE = a("lime_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIME, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block PINK_STAINED_GLASS_PANE = a("pink_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PINK, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block GRAY_STAINED_GLASS_PANE = a("gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GRAY, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block LIGHT_GRAY_STAINED_GLASS_PANE = a("light_gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block CYAN_STAINED_GLASS_PANE = a("cyan_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.CYAN, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block PURPLE_STAINED_GLASS_PANE = a("purple_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PURPLE, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block BLUE_STAINED_GLASS_PANE = a("blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLUE, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block BROWN_STAINED_GLASS_PANE = a("brown_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BROWN, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block GREEN_STAINED_GLASS_PANE = a("green_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GREEN, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block RED_STAINED_GLASS_PANE = a("red_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.RED, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block BLACK_STAINED_GLASS_PANE = a("black_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLACK, BlockBase.Info.a(Material.SHATTERABLE).d(0.3F).a(SoundEffectType.g).b())));
    public static final Block ACACIA_STAIRS = a("acacia_stairs", (Block) (new BlockStairs(Blocks.ACACIA_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.ACACIA_PLANKS))));
    public static final Block DARK_OAK_STAIRS = a("dark_oak_stairs", (Block) (new BlockStairs(Blocks.DARK_OAK_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DARK_OAK_PLANKS))));
    public static final Block SLIME_BLOCK = a("slime_block", (Block) (new BlockSlime(BlockBase.Info.a(Material.CLAY, MaterialMapColor.c).a(0.8F).a(SoundEffectType.m).b())));
    public static final Block BARRIER = a("barrier", (Block) (new BlockBarrier(BlockBase.Info.a(Material.BANNER).a(-1.0F, 3600000.8F).f().b().a(Blocks::a))));
    public static final Block IRON_TRAPDOOR = a("iron_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.ORE).h().d(5.0F).a(SoundEffectType.f).b().a(Blocks::a))));
    public static final Block PRISMARINE = a("prismarine", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.y).h().a(1.5F, 6.0F)));
    public static final Block PRISMARINE_BRICKS = a("prismarine_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.G).h().a(1.5F, 6.0F)));
    public static final Block DARK_PRISMARINE = a("dark_prismarine", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.G).h().a(1.5F, 6.0F)));
    public static final Block PRISMARINE_STAIRS = a("prismarine_stairs", (Block) (new BlockStairs(Blocks.PRISMARINE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PRISMARINE))));
    public static final Block PRISMARINE_BRICK_STAIRS = a("prismarine_brick_stairs", (Block) (new BlockStairs(Blocks.PRISMARINE_BRICKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PRISMARINE_BRICKS))));
    public static final Block DARK_PRISMARINE_STAIRS = a("dark_prismarine_stairs", (Block) (new BlockStairs(Blocks.DARK_PRISMARINE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.DARK_PRISMARINE))));
    public static final Block PRISMARINE_SLAB = a("prismarine_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.y).h().a(1.5F, 6.0F))));
    public static final Block PRISMARINE_BRICK_SLAB = a("prismarine_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.G).h().a(1.5F, 6.0F))));
    public static final Block DARK_PRISMARINE_SLAB = a("dark_prismarine_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.G).h().a(1.5F, 6.0F))));
    public static final Block SEA_LANTERN = a("sea_lantern", new Block(BlockBase.Info.a(Material.SHATTERABLE, MaterialMapColor.p).d(0.3F).a(SoundEffectType.g).a((iblockdata) -> {
        return 15;
    })));
    public static final Block HAY_BLOCK = a("hay_block", (Block) (new BlockHay(BlockBase.Info.a(Material.GRASS, MaterialMapColor.t).d(0.5F).a(SoundEffectType.c))));
    public static final Block WHITE_CARPET = a("white_carpet", (Block) (new BlockCarpet(EnumColor.WHITE, BlockBase.Info.a(Material.WOOL, MaterialMapColor.j).d(0.1F).a(SoundEffectType.h))));
    public static final Block ORANGE_CARPET = a("orange_carpet", (Block) (new BlockCarpet(EnumColor.ORANGE, BlockBase.Info.a(Material.WOOL, MaterialMapColor.q).d(0.1F).a(SoundEffectType.h))));
    public static final Block MAGENTA_CARPET = a("magenta_carpet", (Block) (new BlockCarpet(EnumColor.MAGENTA, BlockBase.Info.a(Material.WOOL, MaterialMapColor.r).d(0.1F).a(SoundEffectType.h))));
    public static final Block LIGHT_BLUE_CARPET = a("light_blue_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.WOOL, MaterialMapColor.s).d(0.1F).a(SoundEffectType.h))));
    public static final Block YELLOW_CARPET = a("yellow_carpet", (Block) (new BlockCarpet(EnumColor.YELLOW, BlockBase.Info.a(Material.WOOL, MaterialMapColor.t).d(0.1F).a(SoundEffectType.h))));
    public static final Block LIME_CARPET = a("lime_carpet", (Block) (new BlockCarpet(EnumColor.LIME, BlockBase.Info.a(Material.WOOL, MaterialMapColor.u).d(0.1F).a(SoundEffectType.h))));
    public static final Block PINK_CARPET = a("pink_carpet", (Block) (new BlockCarpet(EnumColor.PINK, BlockBase.Info.a(Material.WOOL, MaterialMapColor.v).d(0.1F).a(SoundEffectType.h))));
    public static final Block GRAY_CARPET = a("gray_carpet", (Block) (new BlockCarpet(EnumColor.GRAY, BlockBase.Info.a(Material.WOOL, MaterialMapColor.w).d(0.1F).a(SoundEffectType.h))));
    public static final Block LIGHT_GRAY_CARPET = a("light_gray_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.WOOL, MaterialMapColor.x).d(0.1F).a(SoundEffectType.h))));
    public static final Block CYAN_CARPET = a("cyan_carpet", (Block) (new BlockCarpet(EnumColor.CYAN, BlockBase.Info.a(Material.WOOL, MaterialMapColor.y).d(0.1F).a(SoundEffectType.h))));
    public static final Block PURPLE_CARPET = a("purple_carpet", (Block) (new BlockCarpet(EnumColor.PURPLE, BlockBase.Info.a(Material.WOOL, MaterialMapColor.z).d(0.1F).a(SoundEffectType.h))));
    public static final Block BLUE_CARPET = a("blue_carpet", (Block) (new BlockCarpet(EnumColor.BLUE, BlockBase.Info.a(Material.WOOL, MaterialMapColor.A).d(0.1F).a(SoundEffectType.h))));
    public static final Block BROWN_CARPET = a("brown_carpet", (Block) (new BlockCarpet(EnumColor.BROWN, BlockBase.Info.a(Material.WOOL, MaterialMapColor.B).d(0.1F).a(SoundEffectType.h))));
    public static final Block GREEN_CARPET = a("green_carpet", (Block) (new BlockCarpet(EnumColor.GREEN, BlockBase.Info.a(Material.WOOL, MaterialMapColor.C).d(0.1F).a(SoundEffectType.h))));
    public static final Block RED_CARPET = a("red_carpet", (Block) (new BlockCarpet(EnumColor.RED, BlockBase.Info.a(Material.WOOL, MaterialMapColor.D).d(0.1F).a(SoundEffectType.h))));
    public static final Block BLACK_CARPET = a("black_carpet", (Block) (new BlockCarpet(EnumColor.BLACK, BlockBase.Info.a(Material.WOOL, MaterialMapColor.E).d(0.1F).a(SoundEffectType.h))));
    public static final Block TERRACOTTA = a("terracotta", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().a(1.25F, 4.2F)));
    public static final Block COAL_BLOCK = a("coal_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(5.0F, 6.0F)));
    public static final Block PACKED_ICE = a("packed_ice", new Block(BlockBase.Info.a(Material.SNOW_LAYER).a(0.98F).d(0.5F).a(SoundEffectType.g)));
    public static final Block SUNFLOWER = a("sunflower", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block LILAC = a("lilac", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block ROSE_BUSH = a("rose_bush", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block PEONY = a("peony", (Block) (new BlockTallPlantFlower(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block TALL_GRASS = a("tall_grass", (Block) (new BlockTallPlant(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block LARGE_FERN = a("large_fern", (Block) (new BlockTallPlant(BlockBase.Info.a(Material.REPLACEABLE_PLANT).a().c().a(SoundEffectType.c))));
    public static final Block WHITE_BANNER = a("white_banner", (Block) (new BlockBanner(EnumColor.WHITE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block ORANGE_BANNER = a("orange_banner", (Block) (new BlockBanner(EnumColor.ORANGE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block MAGENTA_BANNER = a("magenta_banner", (Block) (new BlockBanner(EnumColor.MAGENTA, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block LIGHT_BLUE_BANNER = a("light_blue_banner", (Block) (new BlockBanner(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block YELLOW_BANNER = a("yellow_banner", (Block) (new BlockBanner(EnumColor.YELLOW, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block LIME_BANNER = a("lime_banner", (Block) (new BlockBanner(EnumColor.LIME, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block PINK_BANNER = a("pink_banner", (Block) (new BlockBanner(EnumColor.PINK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block GRAY_BANNER = a("gray_banner", (Block) (new BlockBanner(EnumColor.GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block LIGHT_GRAY_BANNER = a("light_gray_banner", (Block) (new BlockBanner(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block CYAN_BANNER = a("cyan_banner", (Block) (new BlockBanner(EnumColor.CYAN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block PURPLE_BANNER = a("purple_banner", (Block) (new BlockBanner(EnumColor.PURPLE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block BLUE_BANNER = a("blue_banner", (Block) (new BlockBanner(EnumColor.BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block BROWN_BANNER = a("brown_banner", (Block) (new BlockBanner(EnumColor.BROWN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block GREEN_BANNER = a("green_banner", (Block) (new BlockBanner(EnumColor.GREEN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block RED_BANNER = a("red_banner", (Block) (new BlockBanner(EnumColor.RED, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block BLACK_BANNER = a("black_banner", (Block) (new BlockBanner(EnumColor.BLACK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a))));
    public static final Block WHITE_WALL_BANNER = a("white_wall_banner", (Block) (new BlockBannerWall(EnumColor.WHITE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.WHITE_BANNER))));
    public static final Block ORANGE_WALL_BANNER = a("orange_wall_banner", (Block) (new BlockBannerWall(EnumColor.ORANGE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.ORANGE_BANNER))));
    public static final Block MAGENTA_WALL_BANNER = a("magenta_wall_banner", (Block) (new BlockBannerWall(EnumColor.MAGENTA, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.MAGENTA_BANNER))));
    public static final Block LIGHT_BLUE_WALL_BANNER = a("light_blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.LIGHT_BLUE_BANNER))));
    public static final Block YELLOW_WALL_BANNER = a("yellow_wall_banner", (Block) (new BlockBannerWall(EnumColor.YELLOW, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.YELLOW_BANNER))));
    public static final Block LIME_WALL_BANNER = a("lime_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIME, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.LIME_BANNER))));
    public static final Block PINK_WALL_BANNER = a("pink_wall_banner", (Block) (new BlockBannerWall(EnumColor.PINK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.PINK_BANNER))));
    public static final Block GRAY_WALL_BANNER = a("gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.GRAY_BANNER))));
    public static final Block LIGHT_GRAY_WALL_BANNER = a("light_gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.LIGHT_GRAY_BANNER))));
    public static final Block CYAN_WALL_BANNER = a("cyan_wall_banner", (Block) (new BlockBannerWall(EnumColor.CYAN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.CYAN_BANNER))));
    public static final Block PURPLE_WALL_BANNER = a("purple_wall_banner", (Block) (new BlockBannerWall(EnumColor.PURPLE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.PURPLE_BANNER))));
    public static final Block BLUE_WALL_BANNER = a("blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLUE, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.BLUE_BANNER))));
    public static final Block BROWN_WALL_BANNER = a("brown_wall_banner", (Block) (new BlockBannerWall(EnumColor.BROWN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.BROWN_BANNER))));
    public static final Block GREEN_WALL_BANNER = a("green_wall_banner", (Block) (new BlockBannerWall(EnumColor.GREEN, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.GREEN_BANNER))));
    public static final Block RED_WALL_BANNER = a("red_wall_banner", (Block) (new BlockBannerWall(EnumColor.RED, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.RED_BANNER))));
    public static final Block BLACK_WALL_BANNER = a("black_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLACK, BlockBase.Info.a(Material.WOOD).a().d(1.0F).a(SoundEffectType.a).a(Blocks.BLACK_BANNER))));
    public static final Block RED_SANDSTONE = a("red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().d(0.8F)));
    public static final Block CHISELED_RED_SANDSTONE = a("chiseled_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().d(0.8F)));
    public static final Block CUT_RED_SANDSTONE = a("cut_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().d(0.8F)));
    public static final Block RED_SANDSTONE_STAIRS = a("red_sandstone_stairs", (Block) (new BlockStairs(Blocks.RED_SANDSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.RED_SANDSTONE))));
    public static final Block OAK_SLAB = a("oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.o).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block SPRUCE_SLAB = a("spruce_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block BIRCH_SLAB = a("birch_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.d).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block JUNGLE_SLAB = a("jungle_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block ACACIA_SLAB = a("acacia_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.q).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_SLAB = a("dark_oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.WOOD, MaterialMapColor.B).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block STONE_SLAB = a("stone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(2.0F, 6.0F))));
    public static final Block SMOOTH_STONE_SLAB = a("smooth_stone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(2.0F, 6.0F))));
    public static final Block SANDSTONE_SLAB = a("sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().a(2.0F, 6.0F))));
    public static final Block CUT_SANDSTONE_SLAB = a("cut_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().a(2.0F, 6.0F))));
    public static final Block PETRIFIED_OAK_SLAB = a("petrified_oak_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.o).h().a(2.0F, 6.0F))));
    public static final Block COBBLESTONE_SLAB = a("cobblestone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(2.0F, 6.0F))));
    public static final Block BRICK_SLAB = a("brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.D).h().a(2.0F, 6.0F))));
    public static final Block STONE_BRICK_SLAB = a("stone_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(2.0F, 6.0F))));
    public static final Block NETHER_BRICK_SLAB = a("nether_brick_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L))));
    public static final Block QUARTZ_SLAB = a("quartz_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().a(2.0F, 6.0F))));
    public static final Block RED_SANDSTONE_SLAB = a("red_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().a(2.0F, 6.0F))));
    public static final Block CUT_RED_SANDSTONE_SLAB = a("cut_red_sandstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().a(2.0F, 6.0F))));
    public static final Block PURPUR_SLAB = a("purpur_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.r).h().a(2.0F, 6.0F))));
    public static final Block SMOOTH_STONE = a("smooth_stone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.m).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_SANDSTONE = a("smooth_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_QUARTZ = a("smooth_quartz", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.p).h().a(2.0F, 6.0F)));
    public static final Block SMOOTH_RED_SANDSTONE = a("smooth_red_sandstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.q).h().a(2.0F, 6.0F)));
    public static final Block SPRUCE_FENCE_GATE = a("spruce_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block BIRCH_FENCE_GATE = a("birch_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block JUNGLE_FENCE_GATE = a("jungle_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block ACACIA_FENCE_GATE = a("acacia_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_FENCE_GATE = a("dark_oak_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block SPRUCE_FENCE = a("spruce_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block BIRCH_FENCE = a("birch_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block JUNGLE_FENCE = a("jungle_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block ACACIA_FENCE = a("acacia_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block DARK_OAK_FENCE = a("dark_oak_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block SPRUCE_DOOR = a("spruce_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.SPRUCE_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block BIRCH_DOOR = a("birch_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.BIRCH_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block JUNGLE_DOOR = a("jungle_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.JUNGLE_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block ACACIA_DOOR = a("acacia_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.ACACIA_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block DARK_OAK_DOOR = a("dark_oak_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.WOOD, Blocks.DARK_OAK_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block END_ROD = a("end_rod", (Block) (new BlockEndRod(BlockBase.Info.a(Material.ORIENTABLE).c().a((iblockdata) -> {
        return 14;
    }).a(SoundEffectType.a).b())));
    public static final Block CHORUS_PLANT = a("chorus_plant", (Block) (new BlockChorusFruit(BlockBase.Info.a(Material.PLANT, MaterialMapColor.z).d(0.4F).a(SoundEffectType.a).b())));
    public static final Block CHORUS_FLOWER = a("chorus_flower", (Block) (new BlockChorusFlower((BlockChorusFruit) Blocks.CHORUS_PLANT, BlockBase.Info.a(Material.PLANT, MaterialMapColor.z).d().d(0.4F).a(SoundEffectType.a).b())));
    public static final Block PURPUR_BLOCK = a("purpur_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.r).h().a(1.5F, 6.0F)));
    public static final Block PURPUR_PILLAR = a("purpur_pillar", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.r).h().a(1.5F, 6.0F))));
    public static final Block PURPUR_STAIRS = a("purpur_stairs", (Block) (new BlockStairs(Blocks.PURPUR_BLOCK.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.PURPUR_BLOCK))));
    public static final Block END_STONE_BRICKS = a("end_stone_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().a(3.0F, 9.0F)));
    public static final Block BEETROOTS = a("beetroots", (Block) (new BlockBeetroot(BlockBase.Info.a(Material.PLANT).a().d().c().a(SoundEffectType.u))));
    public static final Block GRASS_PATH = a("grass_path", (Block) (new BlockGrassPath(BlockBase.Info.a(Material.EARTH).d(0.65F).a(SoundEffectType.c).c(Blocks::a).b(Blocks::a))));
    public static final Block END_GATEWAY = a("end_gateway", (Block) (new BlockEndGateway(BlockBase.Info.a(Material.PORTAL, MaterialMapColor.E).a().a((iblockdata) -> {
        return 15;
    }).a(-1.0F, 3600000.0F).f())));
    public static final Block REPEATING_COMMAND_BLOCK = a("repeating_command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.ORE, MaterialMapColor.z).h().a(-1.0F, 3600000.0F).f())));
    public static final Block CHAIN_COMMAND_BLOCK = a("chain_command_block", (Block) (new BlockCommand(BlockBase.Info.a(Material.ORE, MaterialMapColor.C).h().a(-1.0F, 3600000.0F).f())));
    public static final Block FROSTED_ICE = a("frosted_ice", (Block) (new BlockIceFrost(BlockBase.Info.a(Material.ICE).a(0.98F).d().d(0.5F).a(SoundEffectType.g).b().a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes == EntityTypes.POLAR_BEAR;
    }))));
    public static final Block MAGMA_BLOCK = a("magma_block", (Block) (new BlockMagma(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a((iblockdata) -> {
        return 3;
    }).d().d(0.5F).a((iblockdata, iblockaccess, blockposition, entitytypes) -> {
        return entitytypes.c();
    }).d(Blocks::a).e(Blocks::a))));
    public static final Block NETHER_WART_BLOCK = a("nether_wart_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.D).d(1.0F).a(SoundEffectType.J)));
    public static final Block RED_NETHER_BRICKS = a("red_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L)));
    public static final Block BONE_BLOCK = a("bone_block", (Block) (new BlockRotatable(BlockBase.Info.a(Material.STONE, MaterialMapColor.d).h().d(2.0F).a(SoundEffectType.O))));
    public static final Block STRUCTURE_VOID = a("structure_void", (Block) (new BlockStructureVoid(BlockBase.Info.a(Material.STRUCTURE_VOID).a().f())));
    public static final Block OBSERVER = a("observer", (Block) (new BlockObserver(BlockBase.Info.a(Material.STONE).d(3.0F).h().a(Blocks::b))));
    public static final Block SHULKER_BOX = a("shulker_box", (Block) a((EnumColor) null, BlockBase.Info.a(Material.SHULKER_SHELL)));
    public static final Block WHITE_SHULKER_BOX = a("white_shulker_box", (Block) a(EnumColor.WHITE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.j)));
    public static final Block ORANGE_SHULKER_BOX = a("orange_shulker_box", (Block) a(EnumColor.ORANGE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.q)));
    public static final Block MAGENTA_SHULKER_BOX = a("magenta_shulker_box", (Block) a(EnumColor.MAGENTA, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.r)));
    public static final Block LIGHT_BLUE_SHULKER_BOX = a("light_blue_shulker_box", (Block) a(EnumColor.LIGHT_BLUE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.s)));
    public static final Block YELLOW_SHULKER_BOX = a("yellow_shulker_box", (Block) a(EnumColor.YELLOW, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.t)));
    public static final Block LIME_SHULKER_BOX = a("lime_shulker_box", (Block) a(EnumColor.LIME, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.u)));
    public static final Block PINK_SHULKER_BOX = a("pink_shulker_box", (Block) a(EnumColor.PINK, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.v)));
    public static final Block GRAY_SHULKER_BOX = a("gray_shulker_box", (Block) a(EnumColor.GRAY, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.w)));
    public static final Block LIGHT_GRAY_SHULKER_BOX = a("light_gray_shulker_box", (Block) a(EnumColor.LIGHT_GRAY, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.x)));
    public static final Block CYAN_SHULKER_BOX = a("cyan_shulker_box", (Block) a(EnumColor.CYAN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.y)));
    public static final Block PURPLE_SHULKER_BOX = a("purple_shulker_box", (Block) a(EnumColor.PURPLE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.V)));
    public static final Block BLUE_SHULKER_BOX = a("blue_shulker_box", (Block) a(EnumColor.BLUE, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.A)));
    public static final Block BROWN_SHULKER_BOX = a("brown_shulker_box", (Block) a(EnumColor.BROWN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.B)));
    public static final Block GREEN_SHULKER_BOX = a("green_shulker_box", (Block) a(EnumColor.GREEN, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.C)));
    public static final Block RED_SHULKER_BOX = a("red_shulker_box", (Block) a(EnumColor.RED, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.D)));
    public static final Block BLACK_SHULKER_BOX = a("black_shulker_box", (Block) a(EnumColor.BLACK, BlockBase.Info.a(Material.SHULKER_SHELL, MaterialMapColor.E)));
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
    public static final Block WHITE_CONCRETE_POWDER = a("white_concrete_powder", (Block) (new BlockConcretePowder(Blocks.WHITE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.WHITE).d(0.5F).a(SoundEffectType.i))));
    public static final Block ORANGE_CONCRETE_POWDER = a("orange_concrete_powder", (Block) (new BlockConcretePowder(Blocks.ORANGE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.ORANGE).d(0.5F).a(SoundEffectType.i))));
    public static final Block MAGENTA_CONCRETE_POWDER = a("magenta_concrete_powder", (Block) (new BlockConcretePowder(Blocks.MAGENTA_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.MAGENTA).d(0.5F).a(SoundEffectType.i))));
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = a("light_blue_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIGHT_BLUE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIGHT_BLUE).d(0.5F).a(SoundEffectType.i))));
    public static final Block YELLOW_CONCRETE_POWDER = a("yellow_concrete_powder", (Block) (new BlockConcretePowder(Blocks.YELLOW_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.YELLOW).d(0.5F).a(SoundEffectType.i))));
    public static final Block LIME_CONCRETE_POWDER = a("lime_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIME_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIME).d(0.5F).a(SoundEffectType.i))));
    public static final Block PINK_CONCRETE_POWDER = a("pink_concrete_powder", (Block) (new BlockConcretePowder(Blocks.PINK_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.PINK).d(0.5F).a(SoundEffectType.i))));
    public static final Block GRAY_CONCRETE_POWDER = a("gray_concrete_powder", (Block) (new BlockConcretePowder(Blocks.GRAY_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.GRAY).d(0.5F).a(SoundEffectType.i))));
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = a("light_gray_concrete_powder", (Block) (new BlockConcretePowder(Blocks.LIGHT_GRAY_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.LIGHT_GRAY).d(0.5F).a(SoundEffectType.i))));
    public static final Block CYAN_CONCRETE_POWDER = a("cyan_concrete_powder", (Block) (new BlockConcretePowder(Blocks.CYAN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.CYAN).d(0.5F).a(SoundEffectType.i))));
    public static final Block PURPLE_CONCRETE_POWDER = a("purple_concrete_powder", (Block) (new BlockConcretePowder(Blocks.PURPLE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.PURPLE).d(0.5F).a(SoundEffectType.i))));
    public static final Block BLUE_CONCRETE_POWDER = a("blue_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BLUE_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BLUE).d(0.5F).a(SoundEffectType.i))));
    public static final Block BROWN_CONCRETE_POWDER = a("brown_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BROWN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BROWN).d(0.5F).a(SoundEffectType.i))));
    public static final Block GREEN_CONCRETE_POWDER = a("green_concrete_powder", (Block) (new BlockConcretePowder(Blocks.GREEN_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.GREEN).d(0.5F).a(SoundEffectType.i))));
    public static final Block RED_CONCRETE_POWDER = a("red_concrete_powder", (Block) (new BlockConcretePowder(Blocks.RED_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.RED).d(0.5F).a(SoundEffectType.i))));
    public static final Block BLACK_CONCRETE_POWDER = a("black_concrete_powder", (Block) (new BlockConcretePowder(Blocks.BLACK_CONCRETE, BlockBase.Info.a(Material.SAND, EnumColor.BLACK).d(0.5F).a(SoundEffectType.i))));
    public static final Block KELP = a("kelp", (Block) (new BlockKelp(BlockBase.Info.a(Material.WATER_PLANT).a().d().c().a(SoundEffectType.o))));
    public static final Block KELP_PLANT = a("kelp_plant", (Block) (new BlockKelpPlant(BlockBase.Info.a(Material.WATER_PLANT).a().c().a(SoundEffectType.o))));
    public static final Block DRIED_KELP_BLOCK = a("dried_kelp_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.C).a(0.5F, 2.5F).a(SoundEffectType.c)));
    public static final Block TURTLE_EGG = a("turtle_egg", (Block) (new BlockTurtleEgg(BlockBase.Info.a(Material.DRAGON_EGG, MaterialMapColor.d).d(0.5F).a(SoundEffectType.f).d().b())));
    public static final Block DEAD_TUBE_CORAL_BLOCK = a("dead_tube_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a(1.5F, 6.0F)));
    public static final Block DEAD_BRAIN_CORAL_BLOCK = a("dead_brain_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a(1.5F, 6.0F)));
    public static final Block DEAD_BUBBLE_CORAL_BLOCK = a("dead_bubble_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a(1.5F, 6.0F)));
    public static final Block DEAD_FIRE_CORAL_BLOCK = a("dead_fire_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a(1.5F, 6.0F)));
    public static final Block DEAD_HORN_CORAL_BLOCK = a("dead_horn_coral_block", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a(1.5F, 6.0F)));
    public static final Block TUBE_CORAL_BLOCK = a("tube_coral_block", (Block) (new BlockCoral(Blocks.DEAD_TUBE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.A).h().a(1.5F, 6.0F).a(SoundEffectType.p))));
    public static final Block BRAIN_CORAL_BLOCK = a("brain_coral_block", (Block) (new BlockCoral(Blocks.DEAD_BRAIN_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.v).h().a(1.5F, 6.0F).a(SoundEffectType.p))));
    public static final Block BUBBLE_CORAL_BLOCK = a("bubble_coral_block", (Block) (new BlockCoral(Blocks.DEAD_BUBBLE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.z).h().a(1.5F, 6.0F).a(SoundEffectType.p))));
    public static final Block FIRE_CORAL_BLOCK = a("fire_coral_block", (Block) (new BlockCoral(Blocks.DEAD_FIRE_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.D).h().a(1.5F, 6.0F).a(SoundEffectType.p))));
    public static final Block HORN_CORAL_BLOCK = a("horn_coral_block", (Block) (new BlockCoral(Blocks.DEAD_HORN_CORAL_BLOCK, BlockBase.Info.a(Material.STONE, MaterialMapColor.t).h().a(1.5F, 6.0F).a(SoundEffectType.p))));
    public static final Block DEAD_TUBE_CORAL = a("dead_tube_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_BRAIN_CORAL = a("dead_brain_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_BUBBLE_CORAL = a("dead_bubble_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_FIRE_CORAL = a("dead_fire_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_HORN_CORAL = a("dead_horn_coral", (Block) (new BlockCoralDead(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block TUBE_CORAL = a("tube_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_TUBE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().c().a(SoundEffectType.o))));
    public static final Block BRAIN_CORAL = a("brain_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_BRAIN_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().c().a(SoundEffectType.o))));
    public static final Block BUBBLE_CORAL = a("bubble_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_BUBBLE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().c().a(SoundEffectType.o))));
    public static final Block FIRE_CORAL = a("fire_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_FIRE_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().c().a(SoundEffectType.o))));
    public static final Block HORN_CORAL = a("horn_coral", (Block) (new BlockCoralPlant(Blocks.DEAD_HORN_CORAL, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().c().a(SoundEffectType.o))));
    public static final Block DEAD_TUBE_CORAL_FAN = a("dead_tube_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_BRAIN_CORAL_FAN = a("dead_brain_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_BUBBLE_CORAL_FAN = a("dead_bubble_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_FIRE_CORAL_FAN = a("dead_fire_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block DEAD_HORN_CORAL_FAN = a("dead_horn_coral_fan", (Block) (new BlockCoralFanAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c())));
    public static final Block TUBE_CORAL_FAN = a("tube_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_TUBE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().c().a(SoundEffectType.o))));
    public static final Block BRAIN_CORAL_FAN = a("brain_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_BRAIN_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().c().a(SoundEffectType.o))));
    public static final Block BUBBLE_CORAL_FAN = a("bubble_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_BUBBLE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().c().a(SoundEffectType.o))));
    public static final Block FIRE_CORAL_FAN = a("fire_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_FIRE_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().c().a(SoundEffectType.o))));
    public static final Block HORN_CORAL_FAN = a("horn_coral_fan", (Block) (new BlockCoralFan(Blocks.DEAD_HORN_CORAL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().c().a(SoundEffectType.o))));
    public static final Block DEAD_TUBE_CORAL_WALL_FAN = a("dead_tube_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c().a(Blocks.DEAD_TUBE_CORAL_FAN))));
    public static final Block DEAD_BRAIN_CORAL_WALL_FAN = a("dead_brain_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c().a(Blocks.DEAD_BRAIN_CORAL_FAN))));
    public static final Block DEAD_BUBBLE_CORAL_WALL_FAN = a("dead_bubble_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c().a(Blocks.DEAD_BUBBLE_CORAL_FAN))));
    public static final Block DEAD_FIRE_CORAL_WALL_FAN = a("dead_fire_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c().a(Blocks.DEAD_FIRE_CORAL_FAN))));
    public static final Block DEAD_HORN_CORAL_WALL_FAN = a("dead_horn_coral_wall_fan", (Block) (new BlockCoralFanWallAbstract(BlockBase.Info.a(Material.STONE, MaterialMapColor.w).h().a().c().a(Blocks.DEAD_HORN_CORAL_FAN))));
    public static final Block TUBE_CORAL_WALL_FAN = a("tube_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_TUBE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().c().a(SoundEffectType.o).a(Blocks.TUBE_CORAL_FAN))));
    public static final Block BRAIN_CORAL_WALL_FAN = a("brain_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().c().a(SoundEffectType.o).a(Blocks.BRAIN_CORAL_FAN))));
    public static final Block BUBBLE_CORAL_WALL_FAN = a("bubble_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().c().a(SoundEffectType.o).a(Blocks.BUBBLE_CORAL_FAN))));
    public static final Block FIRE_CORAL_WALL_FAN = a("fire_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_FIRE_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().c().a(SoundEffectType.o).a(Blocks.FIRE_CORAL_FAN))));
    public static final Block HORN_CORAL_WALL_FAN = a("horn_coral_wall_fan", (Block) (new BlockCoralFanWall(Blocks.DEAD_HORN_CORAL_WALL_FAN, BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().c().a(SoundEffectType.o).a(Blocks.HORN_CORAL_FAN))));
    public static final Block SEA_PICKLE = a("sea_pickle", (Block) (new BlockSeaPickle(BlockBase.Info.a(Material.WATER_PLANT, MaterialMapColor.C).a((iblockdata) -> {
        return BlockSeaPickle.h(iblockdata) ? 0 : 3 + 3 * (Integer) iblockdata.get(BlockSeaPickle.a);
    }).a(SoundEffectType.m).b())));
    public static final Block BLUE_ICE = a("blue_ice", (Block) (new BlockHalfTransparent(BlockBase.Info.a(Material.SNOW_LAYER).d(2.8F).a(0.989F).a(SoundEffectType.g))));
    public static final Block CONDUIT = a("conduit", (Block) (new BlockConduit(BlockBase.Info.a(Material.SHATTERABLE, MaterialMapColor.G).d(3.0F).a((iblockdata) -> {
        return 15;
    }).b())));
    public static final Block BAMBOO_SAPLING = a("bamboo_sapling", (Block) (new BlockBambooSapling(BlockBase.Info.a(Material.BAMBOO_SAPLING).d().c().a().d(1.0F).a(SoundEffectType.r))));
    public static final Block BAMBOO = a("bamboo", (Block) (new BlockBamboo(BlockBase.Info.a(Material.BAMBOO, MaterialMapColor.i).d().c().d(1.0F).a(SoundEffectType.q).b())));
    public static final Block POTTED_BAMBOO = a("potted_bamboo", (Block) (new BlockFlowerPot(Blocks.BAMBOO, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
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
    public static final Block SCAFFOLDING = a("scaffolding", (Block) (new BlockScaffolding(BlockBase.Info.a(Material.ORIENTABLE, MaterialMapColor.d).a().a(SoundEffectType.s).e())));
    public static final Block LOOM = a("loom", (Block) (new BlockLoom(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block BARREL = a("barrel", (Block) (new BlockBarrel(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block SMOKER = a("smoker", (Block) (new BlockSmoker(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block BLAST_FURNACE = a("blast_furnace", (Block) (new BlockBlastFurnace(BlockBase.Info.a(Material.STONE).h().d(3.5F).a(a(13)))));
    public static final Block CARTOGRAPHY_TABLE = a("cartography_table", (Block) (new BlockCartographyTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block FLETCHING_TABLE = a("fletching_table", (Block) (new BlockFletchingTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block GRINDSTONE = a("grindstone", (Block) (new BlockGrindstone(BlockBase.Info.a(Material.HEAVY, MaterialMapColor.h).h().a(2.0F, 6.0F).a(SoundEffectType.e))));
    public static final Block LECTERN = a("lectern", (Block) (new BlockLectern(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block SMITHING_TABLE = a("smithing_table", (Block) (new BlockSmithingTable(BlockBase.Info.a(Material.WOOD).d(2.5F).a(SoundEffectType.a))));
    public static final Block STONECUTTER = a("stonecutter", (Block) (new BlockStonecutter(BlockBase.Info.a(Material.STONE).h().d(3.5F))));
    public static final Block BELL = a("bell", (Block) (new BlockBell(BlockBase.Info.a(Material.ORE, MaterialMapColor.F).h().d(5.0F).a(SoundEffectType.l))));
    public static final Block LANTERN = a("lantern", (Block) (new BlockLantern(BlockBase.Info.a(Material.ORE).h().d(3.5F).a(SoundEffectType.y).a((iblockdata) -> {
        return 15;
    }).b())));
    public static final Block SOUL_LANTERN = a("soul_lantern", (Block) (new BlockLantern(BlockBase.Info.a(Material.ORE).h().d(3.5F).a(SoundEffectType.y).a((iblockdata) -> {
        return 10;
    }).b())));
    public static final Block CAMPFIRE = a("campfire", (Block) (new BlockCampfire(true, 1, BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).d(2.0F).a(SoundEffectType.a).a(a(15)).b())));
    public static final Block SOUL_CAMPFIRE = a("soul_campfire", (Block) (new BlockCampfire(false, 2, BlockBase.Info.a(Material.WOOD, MaterialMapColor.J).d(2.0F).a(SoundEffectType.a).a(a(10)).b())));
    public static final Block SWEET_BERRY_BUSH = a("sweet_berry_bush", (Block) (new BlockSweetBerryBush(BlockBase.Info.a(Material.PLANT).d().a().a(SoundEffectType.t))));
    public static final Block WARPED_STEM = a("warped_stem", a(MaterialMapColor.af));
    public static final Block STRIPPED_WARPED_STEM = a("stripped_warped_stem", a(MaterialMapColor.af));
    public static final Block WARPED_HYPHAE = a("warped_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.ag).d(2.0F).a(SoundEffectType.z))));
    public static final Block STRIPPED_WARPED_HYPHAE = a("stripped_warped_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.ag).d(2.0F).a(SoundEffectType.z))));
    public static final Block WARPED_NYLIUM = a("warped_nylium", (Block) (new BlockNylium(BlockBase.Info.a(Material.STONE, MaterialMapColor.ae).h().d(0.4F).a(SoundEffectType.A).d())));
    public static final Block WARPED_FUNGUS = a("warped_fungus", (Block) (new BlockFungi(BlockBase.Info.a(Material.PLANT, MaterialMapColor.y).c().a().a(SoundEffectType.B), () -> {
        return BiomeDecoratorGroups.WARPED_FUNGI_PLANTED;
    })));
    public static final Block WARPED_WART_BLOCK = a("warped_wart_block", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.ah).d(1.0F).a(SoundEffectType.J)));
    public static final Block WARPED_ROOTS = a("warped_roots", (Block) (new BlockRoots(BlockBase.Info.a(Material.h, MaterialMapColor.y).a().c().a(SoundEffectType.C))));
    public static final Block NETHER_SPROUTS = a("nether_sprouts", (Block) (new BlockNetherSprouts(BlockBase.Info.a(Material.h, MaterialMapColor.y).a().c().a(SoundEffectType.M))));
    public static final Block CRIMSON_STEM = a("crimson_stem", a(MaterialMapColor.ac));
    public static final Block STRIPPED_CRIMSON_STEM = a("stripped_crimson_stem", a(MaterialMapColor.ac));
    public static final Block CRIMSON_HYPHAE = a("crimson_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.ad).d(2.0F).a(SoundEffectType.z))));
    public static final Block STRIPPED_CRIMSON_HYPHAE = a("stripped_crimson_hyphae", (Block) (new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.ad).d(2.0F).a(SoundEffectType.z))));
    public static final Block CRIMSON_NYLIUM = a("crimson_nylium", (Block) (new BlockNylium(BlockBase.Info.a(Material.STONE, MaterialMapColor.ab).h().d(0.4F).a(SoundEffectType.A).d())));
    public static final Block CRIMSON_FUNGUS = a("crimson_fungus", (Block) (new BlockFungi(BlockBase.Info.a(Material.PLANT, MaterialMapColor.K).c().a().a(SoundEffectType.B), () -> {
        return BiomeDecoratorGroups.CRIMSON_FUNGI_PLANTED;
    })));
    public static final Block SHROOMLIGHT = a("shroomlight", new Block(BlockBase.Info.a(Material.GRASS, MaterialMapColor.D).d(1.0F).a(SoundEffectType.D).a((iblockdata) -> {
        return 15;
    })));
    public static final Block WEEPING_VINES = a("weeping_vines", (Block) (new BlockWeepingVines(BlockBase.Info.a(Material.PLANT, MaterialMapColor.K).d().a().c().a(SoundEffectType.E))));
    public static final Block WEEPING_VINES_PLANT = a("weeping_vines_plant", (Block) (new BlockWeepingVinesPlant(BlockBase.Info.a(Material.PLANT, MaterialMapColor.K).a().c().a(SoundEffectType.E))));
    public static final Block TWISTING_VINES = a("twisting_vines", (Block) (new BlockTwistingVines(BlockBase.Info.a(Material.PLANT, MaterialMapColor.y).d().a().c().a(SoundEffectType.E))));
    public static final Block TWISTING_VINES_PLANT = a("twisting_vines_plant", (Block) (new BlockTwistingVinesPlant(BlockBase.Info.a(Material.PLANT, MaterialMapColor.y).a().c().a(SoundEffectType.E))));
    public static final Block CRIMSON_ROOTS = a("crimson_roots", (Block) (new BlockRoots(BlockBase.Info.a(Material.h, MaterialMapColor.K).a().c().a(SoundEffectType.C))));
    public static final Block CRIMSON_PLANKS = a("crimson_planks", new Block(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.ac).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block WARPED_PLANKS = a("warped_planks", new Block(BlockBase.Info.a(Material.NETHER_WOOD, MaterialMapColor.af).a(2.0F, 3.0F).a(SoundEffectType.a)));
    public static final Block CRIMSON_SLAB = a("crimson_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block WARPED_SLAB = a("warped_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block CRIMSON_PRESSURE_PLATE = a("crimson_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block WARPED_PRESSURE_PLATE = a("warped_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block CRIMSON_FENCE = a("crimson_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block WARPED_FENCE = a("warped_fence", (Block) (new BlockFence(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block CRIMSON_TRAPDOOR = a("crimson_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block WARPED_TRAPDOOR = a("warped_trapdoor", (Block) (new BlockTrapdoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b().a(Blocks::a))));
    public static final Block CRIMSON_FENCE_GATE = a("crimson_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block WARPED_FENCE_GATE = a("warped_fence_gate", (Block) (new BlockFenceGate(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a(2.0F, 3.0F).a(SoundEffectType.a))));
    public static final Block CRIMSON_STAIRS = a("crimson_stairs", (Block) (new BlockStairs(Blocks.CRIMSON_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.CRIMSON_PLANKS))));
    public static final Block WARPED_STAIRS = a("warped_stairs", (Block) (new BlockStairs(Blocks.WARPED_PLANKS.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.WARPED_PLANKS))));
    public static final Block CRIMSON_BUTTON = a("crimson_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block WARPED_BUTTON = a("warped_button", (Block) (new BlockWoodButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F).a(SoundEffectType.a))));
    public static final Block CRIMSON_DOOR = a("crimson_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block WARPED_DOOR = a("warped_door", (Block) (new BlockDoor(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).d(3.0F).a(SoundEffectType.a).b())));
    public static final Block CRIMSON_SIGN = a("crimson_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.g)));
    public static final Block WARPED_SIGN = a("warped_sign", (Block) (new BlockFloorSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(1.0F).a(SoundEffectType.a), BlockPropertyWood.h)));
    public static final Block CRIMSON_WALL_SIGN = a("crimson_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.CRIMSON_PLANKS.s()).a().d(1.0F).a(SoundEffectType.a).a(Blocks.CRIMSON_SIGN), BlockPropertyWood.g)));
    public static final Block WARPED_WALL_SIGN = a("warped_wall_sign", (Block) (new BlockWallSign(BlockBase.Info.a(Material.NETHER_WOOD, Blocks.WARPED_PLANKS.s()).a().d(1.0F).a(SoundEffectType.a).a(Blocks.WARPED_SIGN), BlockPropertyWood.h)));
    public static final Block STRUCTURE_BLOCK = a("structure_block", (Block) (new BlockStructure(BlockBase.Info.a(Material.ORE, MaterialMapColor.x).h().a(-1.0F, 3600000.0F).f())));
    public static final Block JIGSAW = a("jigsaw", (Block) (new BlockJigsaw(BlockBase.Info.a(Material.ORE, MaterialMapColor.x).h().a(-1.0F, 3600000.0F).f())));
    public static final Block COMPOSTER = a("composter", (Block) (new BlockComposter(BlockBase.Info.a(Material.WOOD).d(0.6F).a(SoundEffectType.a))));
    public static final Block TARGET = a("target", (Block) (new BlockTarget(BlockBase.Info.a(Material.GRASS, MaterialMapColor.p).d(0.5F).a(SoundEffectType.c))));
    public static final Block BEE_NEST = a("bee_nest", (Block) (new BlockBeehive(BlockBase.Info.a(Material.WOOD, MaterialMapColor.t).d(0.3F).a(SoundEffectType.a))));
    public static final Block BEEHIVE = a("beehive", (Block) (new BlockBeehive(BlockBase.Info.a(Material.WOOD).d(0.6F).a(SoundEffectType.a))));
    public static final Block HONEY_BLOCK = a("honey_block", (Block) (new BlockHoney(BlockBase.Info.a(Material.CLAY, MaterialMapColor.q).b(0.4F).c(0.5F).b().a(SoundEffectType.n))));
    public static final Block HONEYCOMB_BLOCK = a("honeycomb_block", new Block(BlockBase.Info.a(Material.CLAY, MaterialMapColor.q).d(0.6F).a(SoundEffectType.p)));
    public static final Block NETHERITE_BLOCK = a("netherite_block", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.E).h().a(50.0F, 1200.0F).a(SoundEffectType.P)));
    public static final Block ANCIENT_DEBRIS = a("ancient_debris", new Block(BlockBase.Info.a(Material.ORE, MaterialMapColor.E).h().a(30.0F, 1200.0F).a(SoundEffectType.Q)));
    public static final Block CRYING_OBSIDIAN = a("crying_obsidian", (Block) (new BlockCryingObsidian(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(50.0F, 1200.0F).a((iblockdata) -> {
        return 10;
    }))));
    public static final Block RESPAWN_ANCHOR = a("respawn_anchor", (Block) (new BlockRespawnAnchor(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(50.0F, 1200.0F).a((iblockdata) -> {
        return BlockRespawnAnchor.a(iblockdata, 15);
    }))));
    public static final Block POTTED_CRIMSON_FUNGUS = a("potted_crimson_fungus", (Block) (new BlockFlowerPot(Blocks.CRIMSON_FUNGUS, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_WARPED_FUNGUS = a("potted_warped_fungus", (Block) (new BlockFlowerPot(Blocks.WARPED_FUNGUS, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_CRIMSON_ROOTS = a("potted_crimson_roots", (Block) (new BlockFlowerPot(Blocks.CRIMSON_ROOTS, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block POTTED_WARPED_ROOTS = a("potted_warped_roots", (Block) (new BlockFlowerPot(Blocks.WARPED_ROOTS, BlockBase.Info.a(Material.ORIENTABLE).c().b())));
    public static final Block LODESTONE = a("lodestone", new Block(BlockBase.Info.a(Material.HEAVY).h().d(3.5F).a(SoundEffectType.R)));
    public static final Block BLACKSTONE = a("blackstone", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a(1.5F, 6.0F)));
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
    public static final Block GILDED_BLACKSTONE = a("gilded_blackstone", new Block(BlockBase.Info.a((BlockBase) Blocks.BLACKSTONE).a(SoundEffectType.U)));
    public static final Block POLISHED_BLACKSTONE_STAIRS = a("polished_blackstone_stairs", (Block) (new BlockStairs(Blocks.POLISHED_BLACKSTONE.getBlockData(), BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block POLISHED_BLACKSTONE_SLAB = a("polished_blackstone_slab", (Block) (new BlockStepAbstract(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE = a("polished_blackstone_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, BlockBase.Info.a(Material.STONE, MaterialMapColor.E).h().a().d(0.5F))));
    public static final Block POLISHED_BLACKSTONE_BUTTON = a("polished_blackstone_button", (Block) (new BlockStoneButton(BlockBase.Info.a(Material.ORIENTABLE).a().d(0.5F))));
    public static final Block POLISHED_BLACKSTONE_WALL = a("polished_blackstone_wall", (Block) (new BlockCobbleWall(BlockBase.Info.a((BlockBase) Blocks.POLISHED_BLACKSTONE))));
    public static final Block CHISELED_NETHER_BRICKS = a("chiseled_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L)));
    public static final Block CRACKED_NETHER_BRICKS = a("cracked_nether_bricks", new Block(BlockBase.Info.a(Material.STONE, MaterialMapColor.K).h().a(2.0F, 6.0F).a(SoundEffectType.L)));
    public static final Block QUARTZ_BRICKS = a("quartz_bricks", new Block(BlockBase.Info.a((BlockBase) Blocks.QUARTZ_BLOCK)));

    private static ToIntFunction<IBlockData> a(int i) {
        return (iblockdata) -> {
            return (Boolean) iblockdata.get(BlockProperties.r) ? i : 0;
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
        return new BlockBed(enumcolor, BlockBase.Info.a(Material.CLOTH, (iblockdata) -> {
            return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.FOOT ? enumcolor.f() : MaterialMapColor.e;
        }).a(SoundEffectType.a).d(0.2F).b());
    }

    private static BlockRotatable a(MaterialMapColor materialmapcolor, MaterialMapColor materialmapcolor1) {
        return new BlockRotatable(BlockBase.Info.a(Material.WOOD, (iblockdata) -> {
            return iblockdata.get(BlockRotatable.AXIS) == EnumDirection.EnumAxis.Y ? materialmapcolor : materialmapcolor1;
        }).d(2.0F).a(SoundEffectType.a));
    }

    private static Block a(MaterialMapColor materialmapcolor) {
        return new BlockRotatable(BlockBase.Info.a(Material.NETHER_WOOD, (iblockdata) -> {
            return materialmapcolor;
        }).d(2.0F).a(SoundEffectType.z));
    }

    private static boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    private static boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    private static BlockStainedGlass b(EnumColor enumcolor) {
        return new BlockStainedGlass(enumcolor, BlockBase.Info.a(Material.SHATTERABLE, enumcolor).d(0.3F).a(SoundEffectType.g).b().a(Blocks::a).a(Blocks::b).b(Blocks::b).c(Blocks::b));
    }

    private static BlockLeaves b() {
        return new BlockLeaves(BlockBase.Info.a(Material.LEAVES).d(0.2F).d().a(SoundEffectType.c).b().a(Blocks::c).b(Blocks::b).c(Blocks::b));
    }

    private static BlockShulkerBox a(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        BlockBase.e blockbase_e = (iblockdata, iblockaccess, blockposition) -> {
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntityShulkerBox)) {
                return true;
            } else {
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

                return tileentityshulkerbox.l();
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
        Block.REGISTRY_ID.forEach(BlockBase.BlockData::a);
    }

    static {
        Iterator iterator = IRegistry.BLOCK.iterator();

        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            UnmodifiableIterator unmodifiableiterator = block.getStates().a().iterator();

            while (unmodifiableiterator.hasNext()) {
                IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

                Block.REGISTRY_ID.b(iblockdata);
            }

            block.r();
        }

    }
}

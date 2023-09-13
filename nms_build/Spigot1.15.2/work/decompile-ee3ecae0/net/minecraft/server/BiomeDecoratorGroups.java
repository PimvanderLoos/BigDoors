package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class BiomeDecoratorGroups {

    private static final IBlockData GRASS = Blocks.GRASS.getBlockData();
    private static final IBlockData FERN = Blocks.FERN.getBlockData();
    private static final IBlockData PODZOL = Blocks.PODZOL.getBlockData();
    private static final IBlockData OAK_LOG = Blocks.OAK_LOG.getBlockData();
    private static final IBlockData OAK_LEAVES = Blocks.OAK_LEAVES.getBlockData();
    private static final IBlockData JUNGLE_LOG = Blocks.JUNGLE_LOG.getBlockData();
    private static final IBlockData JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.getBlockData();
    private static final IBlockData SPRUCE_LOG = Blocks.SPRUCE_LOG.getBlockData();
    private static final IBlockData SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.getBlockData();
    private static final IBlockData ACACIA_LOG = Blocks.ACACIA_LOG.getBlockData();
    private static final IBlockData ACACIA_LEAVES = Blocks.ACACIA_LEAVES.getBlockData();
    private static final IBlockData BIRCH_LOG = Blocks.BIRCH_LOG.getBlockData();
    private static final IBlockData BIRCH_LEAVES = Blocks.BIRCH_LEAVES.getBlockData();
    private static final IBlockData DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getBlockData();
    private static final IBlockData DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getBlockData();
    private static final IBlockData WATER = Blocks.WATER.getBlockData();
    private static final IBlockData LAVA = Blocks.LAVA.getBlockData();
    private static final IBlockData DIRT = Blocks.DIRT.getBlockData();
    private static final IBlockData GRAVEL = Blocks.GRAVEL.getBlockData();
    private static final IBlockData GRANITE = Blocks.GRANITE.getBlockData();
    private static final IBlockData DIORITE = Blocks.DIORITE.getBlockData();
    private static final IBlockData ANDESITE = Blocks.ANDESITE.getBlockData();
    private static final IBlockData COAL_ORE = Blocks.COAL_ORE.getBlockData();
    private static final IBlockData IRON_ORE = Blocks.IRON_ORE.getBlockData();
    private static final IBlockData GOLD_ORE = Blocks.GOLD_ORE.getBlockData();
    private static final IBlockData REDSTONE_ORE = Blocks.REDSTONE_ORE.getBlockData();
    private static final IBlockData DIAMOND_ORE = Blocks.DIAMOND_ORE.getBlockData();
    private static final IBlockData LAPIS_ORE = Blocks.LAPIS_ORE.getBlockData();
    private static final IBlockData STONE = Blocks.STONE.getBlockData();
    private static final IBlockData EMERALD_ORE = Blocks.EMERALD_ORE.getBlockData();
    private static final IBlockData INFESTED_STONE = Blocks.INFESTED_STONE.getBlockData();
    private static final IBlockData SAND = Blocks.SAND.getBlockData();
    private static final IBlockData CLAY = Blocks.CLAY.getBlockData();
    private static final IBlockData GRASS_BLOCK = Blocks.GRASS_BLOCK.getBlockData();
    private static final IBlockData MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getBlockData();
    private static final IBlockData LARGE_FERN = Blocks.LARGE_FERN.getBlockData();
    private static final IBlockData TALL_GRASS = Blocks.TALL_GRASS.getBlockData();
    private static final IBlockData LILAC = Blocks.LILAC.getBlockData();
    private static final IBlockData ROSE_BUSH = Blocks.ROSE_BUSH.getBlockData();
    private static final IBlockData PEONY = Blocks.PEONY.getBlockData();
    private static final IBlockData BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.getBlockData();
    private static final IBlockData RED_MUSHROOM = Blocks.RED_MUSHROOM.getBlockData();
    private static final IBlockData SEAGRASS = Blocks.SEAGRASS.getBlockData();
    private static final IBlockData PACKED_ICE = Blocks.PACKED_ICE.getBlockData();
    private static final IBlockData BLUE_ICE = Blocks.BLUE_ICE.getBlockData();
    private static final IBlockData LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.getBlockData();
    private static final IBlockData BLUE_ORCHID = Blocks.BLUE_ORCHID.getBlockData();
    private static final IBlockData POPPY = Blocks.POPPY.getBlockData();
    private static final IBlockData DANDELION = Blocks.DANDELION.getBlockData();
    private static final IBlockData DEAD_BUSH = Blocks.DEAD_BUSH.getBlockData();
    private static final IBlockData MELON = Blocks.MELON.getBlockData();
    private static final IBlockData PUMPKIN = Blocks.PUMPKIN.getBlockData();
    private static final IBlockData SWEET_BERRY_BUSH = (IBlockData) Blocks.SWEET_BERRY_BUSH.getBlockData().set(BlockSweetBerryBush.a, 3);
    private static final IBlockData FIRE = Blocks.FIRE.getBlockData();
    private static final IBlockData NETHERRACK = Blocks.NETHERRACK.getBlockData();
    private static final IBlockData LILY_PAD = Blocks.LILY_PAD.getBlockData();
    private static final IBlockData SNOW = Blocks.SNOW.getBlockData();
    private static final IBlockData JACK_O_LANTERN = Blocks.JACK_O_LANTERN.getBlockData();
    private static final IBlockData SUNFLOWER = Blocks.SUNFLOWER.getBlockData();
    private static final IBlockData CACTUS = Blocks.CACTUS.getBlockData();
    private static final IBlockData SUGAR_CANE = Blocks.SUGAR_CANE.getBlockData();
    private static final IBlockData RED_MUSHROOM_BLOCK = (IBlockData) Blocks.RED_MUSHROOM_BLOCK.getBlockData().set(BlockHugeMushroom.f, false);
    private static final IBlockData BROWN_MUSHROOM_BLOCK = (IBlockData) ((IBlockData) Blocks.BROWN_MUSHROOM_BLOCK.getBlockData().set(BlockHugeMushroom.e, true)).set(BlockHugeMushroom.f, false);
    private static final IBlockData MUSHROOM_STEM = (IBlockData) ((IBlockData) Blocks.MUSHROOM_STEM.getBlockData().set(BlockHugeMushroom.e, false)).set(BlockHugeMushroom.f, false);
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration NORMAL_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(2).i(3).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration JUNGLE_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(8).i(3).a(ImmutableList.of(new WorldGenFeatureTreeCocoa(0.2F), new WorldGenFeatureTreeVineTrunk(), new WorldGenFeatureTreeVineLeaves())).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration JUNGLE_TREE_NOVINE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(8).i(3).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration PINE_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerPine(1, 0))).d(7).b(4).g(1).i(3).j(1).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration SPRUCE_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerSpruce(2, 1))).d(6).b(3).e(1).f(1).h(2).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration ACACIA_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ACACIA_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ACACIA_LEAVES), new WorldGenFoilagePlacerAcacia(2, 0))).d(5).b(2).c(2).e(0).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration BIRCH_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(5).b(2).i(3).a().b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration BIRCH_TREE_BEES_0002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(5).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.002F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration TALL_BIRCH_TREE_BEES_0002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(5).b(2).c(6).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.002F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration SWAMP_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(3, 0))).d(5).b(3).i(3).k(1).a(ImmutableList.of(new WorldGenFeatureTreeVineLeaves())).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration FANCY_TREE = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(0, 0))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration NORMAL_TREE_BEES_005 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.05F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration FANCY_TREE_BEES_0002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(0, 0))).a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.002F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration FANCY_TREE_BEES_005 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(0, 0))).a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.05F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration NORMAL_TREE_BEES_0002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.002F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration NORMAL_TREE_BEES_002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(4).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.02F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration FANCY_TREE_BEES_002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(0, 0))).a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.02F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration BIRCH_TREE_BEES_002 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(5).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.02F))).b();
    public static final WorldGenFeatureSmallTreeConfigurationConfiguration BIRCH_TREE_BEES_005 = (new WorldGenFeatureSmallTreeConfigurationConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0))).d(5).b(2).i(3).a().a(ImmutableList.of(new WorldGenFeatureTreeBeehive(0.05F))).b();
    public static final WorldGenFeatureTreeConfiguration JUNGLE_BUSH = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES))).d(4).b();
    public static final WorldGenMegaTreeConfiguration DARK_OAK_TREE = (new WorldGenMegaTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DARK_OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DARK_OAK_LEAVES))).d(6).b();
    public static final WorldGenMegaTreeConfiguration MEGA_SPRUCE_TREE = (new WorldGenMegaTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES))).d(13).b(15).c(13).a(ImmutableList.of(new WorldGenFeatureTreeAlterGround(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PODZOL)))).b();
    public static final WorldGenMegaTreeConfiguration MEGA_PINE_TREE = (new WorldGenMegaTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES))).d(13).b(15).c(3).a(ImmutableList.of(new WorldGenFeatureTreeAlterGround(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PODZOL)))).b();
    public static final WorldGenMegaTreeConfiguration MEGA_JUNGLE_TREE = (new WorldGenMegaTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES))).d(10).b(20).a(ImmutableList.of(new WorldGenFeatureTreeVineTrunk(), new WorldGenFeatureTreeVineLeaves())).b();
    public static final WorldGenFeatureRandomPatchConfiguration y = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.GRASS), new WorldGenBlockPlacerSimple())).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration z = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.GRASS, 1).a(BiomeDecoratorGroups.FERN, 4), new WorldGenBlockPlacerSimple())).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration A = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.GRASS, 3).a(BiomeDecoratorGroups.FERN, 1), new WorldGenBlockPlacerSimple())).b(ImmutableSet.of(BiomeDecoratorGroups.PODZOL)).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration B = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILY_OF_THE_VALLEY), new WorldGenBlockPlacerSimple())).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration C = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BLUE_ORCHID), new WorldGenBlockPlacerSimple())).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration D = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.POPPY, 2).a(BiomeDecoratorGroups.DANDELION, 1), new WorldGenBlockPlacerSimple())).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration E = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderPlainFlower(), new WorldGenBlockPlacerSimple())).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration F = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderForestFlower(), new WorldGenBlockPlacerSimple())).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration G = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DEAD_BUSH), new WorldGenBlockPlacerSimple())).a(4).d();
    public static final WorldGenFeatureRandomPatchConfiguration H = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MELON), new WorldGenBlockPlacerSimple())).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).a().b().d();
    public static final WorldGenFeatureRandomPatchConfiguration I = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PUMPKIN), new WorldGenBlockPlacerSimple())).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration J = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SWEET_BERRY_BUSH), new WorldGenBlockPlacerSimple())).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration K = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.FIRE), new WorldGenBlockPlacerSimple())).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.NETHERRACK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration L = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILY_PAD), new WorldGenBlockPlacerSimple())).a(10).d();
    public static final WorldGenFeatureRandomPatchConfiguration M = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.RED_MUSHROOM), new WorldGenBlockPlacerSimple())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration N = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BROWN_MUSHROOM), new WorldGenBlockPlacerSimple())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration O = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILAC), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration P = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ROSE_BUSH), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration Q = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PEONY), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration R = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SUNFLOWER), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration S = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.TALL_GRASS), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration T = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LARGE_FERN), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration U = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.CACTUS), new WorldGenBlockPlacerColumn(1, 2))).a(10).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration V = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SUGAR_CANE), new WorldGenBlockPlacerColumn(2, 2))).a(20).b(4).c(0).d(4).b().c().d();
    public static final WorldGenFeatureBlockPileConfiguration W = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderRotatedBlock(Blocks.HAY_BLOCK));
    public static final WorldGenFeatureBlockPileConfiguration X = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SNOW));
    public static final WorldGenFeatureBlockPileConfiguration Y = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MELON));
    public static final WorldGenFeatureBlockPileConfiguration Z = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.PUMPKIN, 19).a(BiomeDecoratorGroups.JACK_O_LANTERN, 1));
    public static final WorldGenFeatureBlockPileConfiguration aa = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.BLUE_ICE, 1).a(BiomeDecoratorGroups.PACKED_ICE, 5));
    public static final WorldGenFeatureHellFlowingLavaConfiguration ab = new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.WATER.h(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
    public static final WorldGenFeatureHellFlowingLavaConfiguration ac = new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.h(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
    public static final WorldGenFeatureHellFlowingLavaConfiguration ad = new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.h(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
    public static final WorldGenFeatureHellFlowingLavaConfiguration ae = new WorldGenFeatureHellFlowingLavaConfiguration(FluidTypes.LAVA.h(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
    public static final WorldGenFeatureMushroomConfiguration HUGE_RED_MUSHROOM = new WorldGenFeatureMushroomConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.RED_MUSHROOM_BLOCK), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MUSHROOM_STEM), 2);
    public static final WorldGenFeatureMushroomConfiguration HUGE_BROWN_MUSHROOM = new WorldGenFeatureMushroomConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BROWN_MUSHROOM_BLOCK), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MUSHROOM_STEM), 3);

    public static void a(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.a, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.14285715F))));
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.c, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
    }

    public static void b(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.a, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.c, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        biomebase.a(WorldGenStage.Features.LIQUID, BiomeBase.a(WorldGenCarverAbstract.d, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        biomebase.a(WorldGenStage.Features.LIQUID, BiomeBase.a(WorldGenCarverAbstract.e, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
    }

    public static void c(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004000000189989805D, WorldGenMineshaft.Type.NORMAL))).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.PILLAGER_OUTPOST.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.STRONGHOLD.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.SWAMP_HUT.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.DESERT_PYRAMID.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.JUNGLE_TEMPLE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.IGLOO.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.SHIPWRECK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false))).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.OCEAN_MONUMENT.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.WOODLAND_MANSION.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.OCEAN_RUIN.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F))).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.BURIED_TREASURE.b((WorldGenFeatureConfiguration) (new WorldGenBuriedTreasureConfiguration(0.01F))).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.VILLAGE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration("village/plains/town_centers", 6))).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }

    public static void d(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.WATER))).a(WorldGenDecorator.E.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.LAVA))).a(WorldGenDecorator.D.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(80)))));
    }

    public static void e(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.LAVA))).a(WorldGenDecorator.D.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(80)))));
    }

    public static void f(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.MONSTER_ROOM.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.F.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(8)))));
    }

    public static void g(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIRT, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 256)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GRAVEL, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 256)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GRANITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIORITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.ANDESITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
    }

    public static void h(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.COAL_ORE, 17))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 128)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.IRON_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 64)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GOLD_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(2, 0, 0, 32)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.REDSTONE_ORE, 8))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 16)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIAMOND_ORE, 8))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(1, 0, 0, 16)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.LAPIS_ORE, 7))).a(WorldGenDecorator.u.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorHeightAverageConfiguration(1, 16, 16)))));
    }

    public static void i(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GOLD_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 32, 32, 80)))));
    }

    public static void j(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.EMERALD_ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureReplaceBlockConfiguration(BiomeDecoratorGroups.STONE, BiomeDecoratorGroups.EMERALD_ORE))).a(WorldGenDecorator.C.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }

    public static void k(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.INFESTED_STONE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(7, 0, 0, 64)))));
    }

    public static void l(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.SAND, 7, 2, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.GRASS_BLOCK})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(3)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.CLAY, 4, 1, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.CLAY})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.GRAVEL, 6, 2, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.GRASS_BLOCK})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void m(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.CLAY, 4, 1, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.CLAY})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void n(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.FOREST_ROCK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureBlockOffsetConfiguration(BiomeDecoratorGroups.MOSSY_COBBLESTONE, 0))).a(WorldGenDecorator.z.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(3)))));
    }

    public static void o(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.T).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
    }

    public static void p(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.J).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(12)))));
    }

    public static void q(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.J).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void r(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.BAMBOO.b((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.0F))).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(16)))));
    }

    public static void s(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.BAMBOO.b((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(160, 80.0D, 0.3D, HeightMap.Type.WORLD_SURFACE_WG)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.05F), WorldGenerator.JUNGLE_GROUND_BUSH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.15F), WorldGenerator.MEGA_JUNGLE_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_JUNGLE_TREE).a(0.7F)), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.A)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(30, 0.1F, 1)))));
    }

    public static void t(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void u(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void v(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void w(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002).a(0.2F), WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE_BEES_0002).a(0.1F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE_BEES_0002)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void x(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.TALL_BIRCH_TREE_BEES_0002).a(0.5F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void y(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.ACACIA_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ACACIA_TREE).a(0.8F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(1, 0.1F, 1)))));
    }

    public static void z(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.ACACIA_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ACACIA_TREE).a(0.8F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
    }

    public static void A(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(0.666F), WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void B(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(0.666F), WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(3, 0.1F, 1)))));
    }

    public static void C(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F), WorldGenerator.JUNGLE_GROUND_BUSH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.5F), WorldGenerator.MEGA_JUNGLE_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_JUNGLE_TREE).a(0.33333334F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(50, 0.1F, 1)))));
    }

    public static void D(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F), WorldGenerator.JUNGLE_GROUND_BUSH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.5F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
    }

    public static void E(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(5, 0.1F, 1)))));
    }

    public static void F(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void G(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.MEGA_SPRUCE_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_SPRUCE_TREE).a(0.33333334F), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void H(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.MEGA_SPRUCE_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_SPRUCE_TREE).a(0.025641026F), WorldGenerator.MEGA_SPRUCE_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_PINE_TREE).a(0.30769232F), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void I(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.A).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(25)))));
    }

    public static void J(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.S).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
    }

    public static void K(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void L(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
    }

    public static void M(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.G).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
    }

    public static void N(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomConfiguration(ImmutableList.of(WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.O), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.P), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.Q), WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.B)), 0))).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void O(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void P(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SWAMP_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.C).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.G).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.L).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(8, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(8, 0.125F)))));
    }

    public static void Q(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_BOOLEAN_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureChoiceConfiguration(WorldGenerator.HUGE_RED_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_RED_MUSHROOM), WorldGenerator.HUGE_BROWN_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_BROWN_MUSHROOM)))).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.125F)))));
    }

    public static void R(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.FANCY_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE_BEES_005).a(0.33333334F)), WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE_BEES_005)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.05F, 1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.E).a(WorldGenDecorator.g.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 15, 4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.h.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 5, 10)))));
    }

    public static void S(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.G).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void T(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.z).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.G).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(3, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(3, 0.125F)))));
    }

    public static void U(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.D).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void V(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.D).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
    }

    public static void W(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.y).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void X(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.z).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.125F)))));
    }

    public static void Y(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.S).a(WorldGenDecorator.g.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 0, 7)))));
    }

    public static void Z(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.N).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.M).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(8)))));
    }

    public static void aa(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.V).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.I).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
    }

    public static void ab(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.V).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(13)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.I).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.U).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void ac(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.H).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.VINES.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.f.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(50)))));
    }

    public static void ad(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.V).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(60)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.I).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.U).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
    }

    public static void ae(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.V).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.I).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
    }

    public static void af(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.DESERT_WELL.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.i.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(1000)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.FOSSIL.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.k.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(64)))));
    }

    public static void ag(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.FOSSIL.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.k.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(64)))));
    }

    public static void ah(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.KELP.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(120, 80.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
    }

    public static void ai(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SIMPLE_BLOCK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureBlockConfiguration(BiomeDecoratorGroups.SEAGRASS, new IBlockData[]{BiomeDecoratorGroups.STONE}, new IBlockData[]{BiomeDecoratorGroups.WATER}, new IBlockData[]{BiomeDecoratorGroups.WATER}))).a(WorldGenDecorator.y.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorCarveMaskConfiguration(WorldGenStage.Features.LIQUID, 0.1F)))));
    }

    public static void aj(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEAGRASS.b((WorldGenFeatureConfiguration) (new WorldGenFeatureSeaGrassConfiguration(80, 0.3D))).a(WorldGenDecorator.v.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }

    public static void ak(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEAGRASS.b((WorldGenFeatureConfiguration) (new WorldGenFeatureSeaGrassConfiguration(80, 0.8D))).a(WorldGenDecorator.v.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }

    public static void al(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.KELP.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(80, 80.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
    }

    public static void am(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ab).a(WorldGenDecorator.o.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(50, 8, 8, 256)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ac).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)))));
    }

    public static void an(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.ICEBERG.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.PACKED_ICE))).a(WorldGenDecorator.H.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(16)))));
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.ICEBERG.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.BLUE_ICE))).a(WorldGenDecorator.H.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(200)))));
    }

    public static void ao(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.BLUE_ICE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.q.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 30, 32, 64)))));
    }

    public static void ap(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FREEZE_TOP_LAYER.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }

    public static void aq(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.END_CITY.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e).a(WorldGenDecorator.a.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.e)));
    }
}

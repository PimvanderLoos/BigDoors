package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.OptionalInt;

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
    private static final IBlockData SOUL_FIRE = Blocks.SOUL_FIRE.getBlockData();
    private static final IBlockData NETHERRACK = Blocks.NETHERRACK.getBlockData();
    private static final IBlockData SOUL_SOIL = Blocks.SOUL_SOIL.getBlockData();
    private static final IBlockData CRIMSON_ROOTS = Blocks.CRIMSON_ROOTS.getBlockData();
    private static final IBlockData LILY_PAD = Blocks.LILY_PAD.getBlockData();
    private static final IBlockData SNOW = Blocks.SNOW.getBlockData();
    private static final IBlockData JACK_O_LANTERN = Blocks.JACK_O_LANTERN.getBlockData();
    private static final IBlockData SUNFLOWER = Blocks.SUNFLOWER.getBlockData();
    private static final IBlockData CACTUS = Blocks.CACTUS.getBlockData();
    private static final IBlockData SUGAR_CANE = Blocks.SUGAR_CANE.getBlockData();
    private static final IBlockData RED_MUSHROOM_BLOCK = (IBlockData) Blocks.RED_MUSHROOM_BLOCK.getBlockData().set(BlockHugeMushroom.f, false);
    private static final IBlockData BROWN_MUSHROOM_BLOCK = (IBlockData) ((IBlockData) Blocks.BROWN_MUSHROOM_BLOCK.getBlockData().set(BlockHugeMushroom.e, true)).set(BlockHugeMushroom.f, false);
    private static final IBlockData MUSHROOM_STEM = (IBlockData) ((IBlockData) Blocks.MUSHROOM_STEM.getBlockData().set(BlockHugeMushroom.e, false)).set(BlockHugeMushroom.f, false);
    private static final IBlockData NETHER_GOLD_ORE = Blocks.NETHER_GOLD_ORE.getBlockData();
    private static final IBlockData NETHER_QUARTZ_ORE = Blocks.NETHER_QUARTZ_ORE.getBlockData();
    private static final IBlockData WARPED_STEM = Blocks.WARPED_STEM.getBlockData();
    private static final IBlockData WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.getBlockData();
    private static final IBlockData NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.getBlockData();
    private static final IBlockData CRIMSON_STEM = Blocks.CRIMSON_STEM.getBlockData();
    private static final IBlockData SHROOMLIGHT = Blocks.SHROOMLIGHT.getBlockData();
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> a = StructureGenerator.PILLAGER_OUTPOST.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> b = StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004000000189989805D, WorldGenMineshaft.Type.NORMAL)));
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> c = StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.MESA)));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> d = StructureGenerator.MANSION.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> e = StructureGenerator.JUNGLE_PYRAMID.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> f = StructureGenerator.DESERT_PYRAMID.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> g = StructureGenerator.IGLOO.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> h = StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false)));
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> i = StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(true)));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> j = StructureGenerator.SWAMP_HUT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> k = StructureGenerator.STRONGHOLD.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> l = StructureGenerator.MONUMENT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> m = StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F)));
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> n = StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F)));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> o = StructureGenerator.FORTRESS.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> p = StructureGenerator.NETHER_FOSSIL.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> q = StructureGenerator.ENDCITY.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b);
    public static final StructureFeature<WorldGenBuriedTreasureConfiguration, ? extends StructureGenerator<WorldGenBuriedTreasureConfiguration>> r = StructureGenerator.BURIED_TREASURE.a((WorldGenFeatureConfiguration) (new WorldGenBuriedTreasureConfiguration(0.01F)));
    public static final StructureFeature<WorldGenFeatureBastionRemnantConfiguration, ? extends StructureGenerator<WorldGenFeatureBastionRemnantConfiguration>> s = StructureGenerator.BASTION_REMNANT.a((WorldGenFeatureConfiguration) (new WorldGenFeatureBastionRemnantConfiguration(WorldGenFeatureBastionPieces.a)));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> t = StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(new MinecraftKey("village/plains/town_centers"), 6)));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> u = StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(new MinecraftKey("village/desert/town_centers"), 6)));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> v = StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(new MinecraftKey("village/savanna/town_centers"), 6)));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> w = StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(new MinecraftKey("village/snowy/town_centers"), 6)));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> x = StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(new MinecraftKey("village/taiga/town_centers"), 6)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> y = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.STANDARD)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> z = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.DESERT)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> A = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.JUNGLE)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> B = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.SWAMP)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> C = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.MOUNTAIN)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> D = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.OCEAN)));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> E = StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.NETHER)));
    public static final WorldGenFeatureTreeConfiguration NORMAL_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(2, 0, 0, 0, 3), new TrunkPlacerStraight(4, 2, 0), new FeatureSizeTwoLayers(1, 0, 1))).a().b();
    private static final WorldGenFeatureTreeBeehive cx = new WorldGenFeatureTreeBeehive(0.002F);
    private static final WorldGenFeatureTreeBeehive cy = new WorldGenFeatureTreeBeehive(0.02F);
    private static final WorldGenFeatureTreeBeehive cz = new WorldGenFeatureTreeBeehive(0.05F);
    public static final WorldGenFeatureTreeConfiguration NORMAL_TREE_BEES_0002 = BiomeDecoratorGroups.NORMAL_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cx));
    public static final WorldGenFeatureTreeConfiguration NORMAL_TREE_BEES_002 = BiomeDecoratorGroups.NORMAL_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cy));
    public static final WorldGenFeatureTreeConfiguration NORMAL_TREE_BEES_005 = BiomeDecoratorGroups.NORMAL_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cz));
    public static final WorldGenFeatureTreeConfiguration JUNGLE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES), new WorldGenFoilagePlacerBlob(2, 0, 0, 0, 3), new TrunkPlacerStraight(4, 8, 0), new FeatureSizeTwoLayers(1, 0, 1))).a((List) ImmutableList.of(new WorldGenFeatureTreeCocoa(0.2F), WorldGenFeatureTreeVineTrunk.b, WorldGenFeatureTreeVineLeaves.b)).a().b();
    public static final WorldGenFeatureTreeConfiguration JUNGLE_TREE_NOVINE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES), new WorldGenFoilagePlacerBlob(2, 0, 0, 0, 3), new TrunkPlacerStraight(4, 8, 0), new FeatureSizeTwoLayers(1, 0, 1))).a().b();
    public static final WorldGenFeatureTreeConfiguration PINE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerPine(1, 0, 1, 0, 3, 1), new TrunkPlacerStraight(6, 4, 0), new FeatureSizeTwoLayers(2, 0, 2))).a().b();
    public static final WorldGenFeatureTreeConfiguration SPRUCE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerSpruce(2, 1, 0, 2, 1, 1), new TrunkPlacerStraight(5, 2, 1), new FeatureSizeTwoLayers(2, 0, 2))).a().b();
    public static final WorldGenFeatureTreeConfiguration ACACIA_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ACACIA_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ACACIA_LEAVES), new WorldGenFoilagePlacerAcacia(2, 0, 0, 0), new TrunkPlacerForking(5, 2, 2), new FeatureSizeTwoLayers(1, 0, 2))).a().b();
    public static final WorldGenFeatureTreeConfiguration BIRCH_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0, 0, 0, 3), new TrunkPlacerStraight(5, 2, 0), new FeatureSizeTwoLayers(1, 0, 1))).a().b();
    public static final WorldGenFeatureTreeConfiguration BIRCH_TREE_BEES_0002 = BiomeDecoratorGroups.BIRCH_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cx));
    public static final WorldGenFeatureTreeConfiguration BIRCH_TREE_BEES_002 = BiomeDecoratorGroups.BIRCH_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cy));
    public static final WorldGenFeatureTreeConfiguration BIRCH_TREE_BEES_005 = BiomeDecoratorGroups.BIRCH_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cz));
    public static final WorldGenFeatureTreeConfiguration TALL_BIRCH_TREE_BEES_0002 = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BIRCH_LEAVES), new WorldGenFoilagePlacerBlob(2, 0, 0, 0, 3), new TrunkPlacerStraight(5, 2, 6), new FeatureSizeTwoLayers(1, 0, 1))).a().a((List) ImmutableList.of(BiomeDecoratorGroups.cx)).b();
    public static final WorldGenFeatureTreeConfiguration SWAMP_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBlob(3, 0, 0, 0, 3), new TrunkPlacerStraight(5, 3, 0), new FeatureSizeTwoLayers(1, 0, 1))).a(1).a((List) ImmutableList.of(WorldGenFeatureTreeVineLeaves.b)).b();
    public static final WorldGenFeatureTreeConfiguration FANCY_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerFancy(2, 0, 4, 0, 4), new TrunkPlacerFancy(3, 11, 0), new FeatureSizeTwoLayers(0, 0, 0, OptionalInt.of(4)))).a().a(HeightMap.Type.MOTION_BLOCKING).b();
    public static final WorldGenFeatureTreeConfiguration FANCY_TREE_BEES_0002 = BiomeDecoratorGroups.FANCY_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cx));
    public static final WorldGenFeatureTreeConfiguration FANCY_TREE_BEES_002 = BiomeDecoratorGroups.FANCY_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cy));
    public static final WorldGenFeatureTreeConfiguration FANCY_TREE_BEES_005 = BiomeDecoratorGroups.FANCY_TREE.a((List) ImmutableList.of(BiomeDecoratorGroups.cz));
    public static final WorldGenFeatureTreeConfiguration JUNGLE_BUSH = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.OAK_LEAVES), new WorldGenFoilagePlacerBush(2, 0, 1, 0, 2), new TrunkPlacerStraight(1, 0, 0), new FeatureSizeTwoLayers(0, 0, 0))).a(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES).b();
    public static final WorldGenFeatureTreeConfiguration DARK_OAK_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DARK_OAK_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DARK_OAK_LEAVES), new WorldGenFoilagePlacerDarkOak(0, 0, 0, 0), new TrunkPlacerDarkOak(6, 2, 1), new FeatureSizeThreeLayers(1, 1, 0, 1, 2, OptionalInt.empty()))).a(Integer.MAX_VALUE).a(HeightMap.Type.MOTION_BLOCKING).a().b();
    public static final WorldGenFeatureTreeConfiguration MEGA_SPRUCE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(0, 0, 0, 0, 4, 13), new TrunkPlacerGiant(13, 2, 14), new FeatureSizeTwoLayers(1, 1, 2))).a((List) ImmutableList.of(new WorldGenFeatureTreeAlterGround(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PODZOL)))).b();
    public static final WorldGenFeatureTreeConfiguration MEGA_PINE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(0, 0, 0, 0, 4, 3), new TrunkPlacerGiant(13, 2, 14), new FeatureSizeTwoLayers(1, 1, 2))).a((List) ImmutableList.of(new WorldGenFeatureTreeAlterGround(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PODZOL)))).b();
    public static final WorldGenFeatureTreeConfiguration MEGA_JUNGLE_TREE = (new WorldGenFeatureTreeConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LOG), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.JUNGLE_LEAVES), new WorldGenFoilagePlacerJungle(2, 0, 0, 0, 2), new TrunkPlacerMegaJungle(10, 2, 19), new FeatureSizeTwoLayers(1, 1, 2))).a((List) ImmutableList.of(WorldGenFeatureTreeVineTrunk.b, WorldGenFeatureTreeVineLeaves.b)).b();
    public static final WorldGenFeatureRandomPatchConfiguration ad = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.GRASS), WorldGenBlockPlacerSimple.c)).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration ae = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.GRASS, 1).a(BiomeDecoratorGroups.FERN, 4), WorldGenBlockPlacerSimple.c)).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration af = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.GRASS, 3).a(BiomeDecoratorGroups.FERN, 1), WorldGenBlockPlacerSimple.c)).b(ImmutableSet.of(BiomeDecoratorGroups.PODZOL)).a(32).d();
    public static final WorldGenFeatureRandomPatchConfiguration ag = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILY_OF_THE_VALLEY), WorldGenBlockPlacerSimple.c)).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration ah = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BLUE_ORCHID), WorldGenBlockPlacerSimple.c)).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration ai = (new WorldGenFeatureRandomPatchConfiguration.a((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.POPPY, 2).a(BiomeDecoratorGroups.DANDELION, 1), WorldGenBlockPlacerSimple.c)).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration aj = (new WorldGenFeatureRandomPatchConfiguration.a(WorldGenFeatureStateProviderPlainFlower.c, WorldGenBlockPlacerSimple.c)).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration ak = (new WorldGenFeatureRandomPatchConfiguration.a(WorldGenFeatureStateProviderForestFlower.c, WorldGenBlockPlacerSimple.c)).a(64).d();
    public static final WorldGenFeatureRandomPatchConfiguration al = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.DEAD_BUSH), WorldGenBlockPlacerSimple.c)).a(4).d();
    public static final WorldGenFeatureRandomPatchConfiguration am = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MELON), WorldGenBlockPlacerSimple.c)).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).a().b().d();
    public static final WorldGenFeatureRandomPatchConfiguration an = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PUMPKIN), WorldGenBlockPlacerSimple.c)).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration ao = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SWEET_BERRY_BUSH), WorldGenBlockPlacerSimple.c)).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.GRASS_BLOCK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration ap = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.FIRE), WorldGenBlockPlacerSimple.c)).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.NETHERRACK.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration aq = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SOUL_FIRE), new WorldGenBlockPlacerSimple())).a(64).a(ImmutableSet.of(BiomeDecoratorGroups.SOUL_SOIL.getBlock())).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration ar = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILY_PAD), WorldGenBlockPlacerSimple.c)).a(10).d();
    public static final WorldGenFeatureRandomPatchConfiguration as = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.RED_MUSHROOM), WorldGenBlockPlacerSimple.c)).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration at = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BROWN_MUSHROOM), WorldGenBlockPlacerSimple.c)).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration au = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.CRIMSON_ROOTS), new WorldGenBlockPlacerSimple())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration av = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LILAC), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration aw = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.ROSE_BUSH), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration ax = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.PEONY), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration ay = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SUNFLOWER), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration az = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.TALL_GRASS), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration aA = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.LARGE_FERN), new WorldGenBlockPlacerDoublePlant())).a(64).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration aB = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.CACTUS), new WorldGenBlockPlacerColumn(1, 2))).a(10).b().d();
    public static final WorldGenFeatureRandomPatchConfiguration aC = (new WorldGenFeatureRandomPatchConfiguration.a(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SUGAR_CANE), new WorldGenBlockPlacerColumn(2, 2))).a(20).b(4).c(0).d(4).b().c().d();
    public static final WorldGenFeatureBlockPileConfiguration aD = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderRotatedBlock(Blocks.HAY_BLOCK));
    public static final WorldGenFeatureBlockPileConfiguration aE = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.SNOW));
    public static final WorldGenFeatureBlockPileConfiguration aF = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MELON));
    public static final WorldGenFeatureBlockPileConfiguration aG = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.PUMPKIN, 19).a(BiomeDecoratorGroups.JACK_O_LANTERN, 1));
    public static final WorldGenFeatureBlockPileConfiguration aH = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(BiomeDecoratorGroups.BLUE_ICE, 1).a(BiomeDecoratorGroups.PACKED_ICE, 5));
    public static final Fluid aI = FluidTypes.WATER.h();
    public static final Fluid aJ = FluidTypes.LAVA.h();
    public static final WorldGenFeatureHellFlowingLavaConfiguration aK = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aI, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aL = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aM = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aN = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, true, 4, 1, ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aO = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aP = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, false, 4, 1, ImmutableSet.of(Blocks.SOUL_SAND));
    public static final WorldGenFeatureHellFlowingLavaConfiguration aQ = new WorldGenFeatureHellFlowingLavaConfiguration(BiomeDecoratorGroups.aJ, false, 5, 0, ImmutableSet.of(Blocks.SOUL_SAND));
    public static final WorldGenFeatureBasaltColumnsConfiguration aR = (new WorldGenFeatureBasaltColumnsConfiguration.a()).a(1).b(1, 4).a();
    public static final WorldGenFeatureBasaltColumnsConfiguration aS = (new WorldGenFeatureBasaltColumnsConfiguration.a()).a(2, 3).b(5, 10).a();
    public static final WorldGenFeatureNetherrackReplaceBlobsConfiguration aT = (new WorldGenFeatureNetherrackReplaceBlobsConfiguration.a()).a(new BaseBlockPosition(3, 3, 3)).b(new BaseBlockPosition(7, 7, 7)).a(Blocks.NETHERRACK.getBlockData()).b(Blocks.BASALT.getBlockData()).a();
    public static final WorldGenFeatureNetherrackReplaceBlobsConfiguration aU = (new WorldGenFeatureNetherrackReplaceBlobsConfiguration.a()).a(new BaseBlockPosition(3, 3, 3)).b(new BaseBlockPosition(7, 7, 7)).a(Blocks.NETHERRACK.getBlockData()).b(Blocks.BLACKSTONE.getBlockData()).a();
    public static final WorldGenFeatureDeltaConfiguration aV = (new WorldGenFeatureDeltaConfiguration.a()).a(Blocks.LAVA.getBlockData()).a(3, 7).a(Blocks.MAGMA_BLOCK.getBlockData(), 2).a();
    public static final WorldGenFeatureMushroomConfiguration HUGE_RED_MUSHROOM = new WorldGenFeatureMushroomConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.RED_MUSHROOM_BLOCK), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MUSHROOM_STEM), 2);
    public static final WorldGenFeatureMushroomConfiguration HUGE_BROWN_MUSHROOM = new WorldGenFeatureMushroomConfiguration(new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.BROWN_MUSHROOM_BLOCK), new WorldGenFeatureStateProviderSimpl(BiomeDecoratorGroups.MUSHROOM_STEM), 3);
    public static final WorldGenFeatureBlockPileConfiguration aY = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(Blocks.CRIMSON_ROOTS.getBlockData(), 87).a(Blocks.CRIMSON_FUNGUS.getBlockData(), 11).a(Blocks.WARPED_FUNGUS.getBlockData(), 1));
    public static final WorldGenFeatureBlockPileConfiguration aZ = new WorldGenFeatureBlockPileConfiguration((new WorldGenFeatureStateProviderWeighted()).a(Blocks.WARPED_ROOTS.getBlockData(), 85).a(Blocks.CRIMSON_ROOTS.getBlockData(), 1).a(Blocks.WARPED_FUNGUS.getBlockData(), 13).a(Blocks.CRIMSON_FUNGUS.getBlockData(), 1));
    public static final WorldGenFeatureBlockPileConfiguration ba = new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderSimpl(Blocks.NETHER_SPROUTS.getBlockData()));

    public static void a(BiomeBase biomebase) {
        biomebase.a(BiomeDecoratorGroups.c);
        biomebase.a(BiomeDecoratorGroups.k);
    }

    public static void b(BiomeBase biomebase) {
        biomebase.a(BiomeDecoratorGroups.b);
        biomebase.a(BiomeDecoratorGroups.k);
    }

    public static void c(BiomeBase biomebase) {
        biomebase.a(BiomeDecoratorGroups.b);
        biomebase.a(BiomeDecoratorGroups.h);
    }

    public static void d(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.a, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.14285715F))));
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.c, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
    }

    public static void e(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.a, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
        biomebase.a(WorldGenStage.Features.AIR, BiomeBase.a(WorldGenCarverAbstract.c, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        biomebase.a(WorldGenStage.Features.LIQUID, BiomeBase.a(WorldGenCarverAbstract.d, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        biomebase.a(WorldGenStage.Features.LIQUID, BiomeBase.a(WorldGenCarverAbstract.e, (WorldGenCarverConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
    }

    public static void f(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LAKES, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.WATER))).a(WorldGenDecorator.E.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.LAKES, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.LAVA))).a(WorldGenDecorator.D.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(80)))));
    }

    public static void g(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LAKES, WorldGenerator.LAKE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.LAVA))).a(WorldGenDecorator.D.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(80)))));
    }

    public static void h(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.MONSTER_ROOM.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.F.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(8)))));
    }

    public static void i(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIRT, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 256)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GRAVEL, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 256)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GRANITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIORITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.ANDESITE, 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)))));
    }

    public static void j(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.COAL_ORE, 17))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 128)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.IRON_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 64)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GOLD_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(2, 0, 0, 32)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.REDSTONE_ORE, 8))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 16)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.DIAMOND_ORE, 8))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(1, 0, 0, 16)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.LAPIS_ORE, 7))).a(WorldGenDecorator.u.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorHeightAverageConfiguration(1, 16, 16)))));
    }

    public static void k(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.GOLD_ORE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 32, 32, 80)))));
    }

    public static void l(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.EMERALD_ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureReplaceBlockConfiguration(BiomeDecoratorGroups.STONE, BiomeDecoratorGroups.EMERALD_ORE))).a(WorldGenDecorator.C.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
    }

    public static void m(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NATURAL_STONE, BiomeDecoratorGroups.INFESTED_STONE, 9))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(7, 0, 0, 64)))));
    }

    public static void n(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.SAND, 7, 2, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.GRASS_BLOCK})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(3)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.CLAY, 4, 1, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.CLAY})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.GRAVEL, 6, 2, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.GRASS_BLOCK})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void o(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_ORES, WorldGenerator.DISK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCircleConfiguration(BiomeDecoratorGroups.CLAY, 4, 1, Lists.newArrayList(new IBlockData[]{BiomeDecoratorGroups.DIRT, BiomeDecoratorGroups.CLAY})))).a(WorldGenDecorator.c.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void p(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.FOREST_ROCK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureBlockOffsetConfiguration(BiomeDecoratorGroups.MOSSY_COBBLESTONE, 0))).a(WorldGenDecorator.z.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(3)))));
    }

    public static void q(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aA).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
    }

    public static void r(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ao).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(12)))));
    }

    public static void s(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ao).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void t(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.BAMBOO.b((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.0F))).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(16)))));
    }

    public static void u(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.BAMBOO.b((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.2F))).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(160, 80.0D, 0.3D, HeightMap.Type.WORLD_SURFACE_WG)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.05F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.15F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_JUNGLE_TREE).a(0.7F)), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.af)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(30, 0.1F, 1)))));
    }

    public static void v(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void w(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void x(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void y(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002).a(0.2F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE_BEES_0002).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE_BEES_0002)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void z(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.TALL_BIRCH_TREE_BEES_0002).a(0.5F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.BIRCH_TREE_BEES_0002)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void A(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ACACIA_TREE).a(0.8F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(1, 0.1F, 1)))));
    }

    public static void B(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ACACIA_TREE).a(0.8F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
    }

    public static void C(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(0.666F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void D(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(0.666F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(3, 0.1F, 1)))));
    }

    public static void E(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.5F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_JUNGLE_TREE).a(0.33333334F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(50, 0.1F, 1)))));
    }

    public static void F(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE).a(0.1F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_BUSH).a(0.5F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
    }

    public static void G(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(5, 0.1F, 1)))));
    }

    public static void H(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)))));
    }

    public static void I(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_SPRUCE_TREE).a(0.33333334F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void J(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_SPRUCE_TREE).a(0.025641026F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_PINE_TREE).a(0.30769232F), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.PINE_TREE).a(0.33333334F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(10, 0.1F, 1)))));
    }

    public static void K(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.af).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(25)))));
    }

    public static void L(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.az).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
    }

    public static void M(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void N(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
    }

    public static void O(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.al).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
    }

    public static void P(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomConfiguration(ImmutableList.of(WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.av), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aw), WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ax), WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ag)), 0))).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void Q(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void R(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SWAMP_TREE).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(2, 0.1F, 1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ah).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.al).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ar).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(8, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(8, 0.125F)))));
    }

    public static void S(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_BOOLEAN_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureChoiceConfiguration(WorldGenerator.HUGE_RED_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_RED_MUSHROOM), WorldGenerator.HUGE_BROWN_MUSHROOM.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.HUGE_BROWN_MUSHROOM)))).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.125F)))));
    }

    public static void T(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_SELECTOR.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRandomChoiceConfiguration(ImmutableList.of(WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.FANCY_TREE_BEES_005).a(0.33333334F)), WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.NORMAL_TREE_BEES_005)))).a(WorldGenDecorator.m.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.05F, 1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aj).a(WorldGenDecorator.g.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 15, 4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.h.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 5, 10)))));
    }

    public static void U(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.al).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void V(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ae).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(7)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.al).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(3, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(3, 0.125F)))));
    }

    public static void W(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ai).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
    }

    public static void X(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.FLOWER.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ai).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
    }

    public static void Y(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ad).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
    }

    public static void Z(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ae).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.s.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.25F)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.t.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyChanceConfiguration(1, 0.125F)))));
    }

    public static void aa(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.az).a(WorldGenDecorator.g.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureDecoratorNoiseConfiguration(-0.8D, 0, 7)))));
    }

    public static void ab(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.at).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.as).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(8)))));
    }

    public static void ac(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aC).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.an).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
    }

    public static void ad(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aC).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(13)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.an).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aB).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
    }

    public static void ae(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.am).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(1)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.VINES.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.f.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(50)))));
    }

    public static void af(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aC).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(60)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.an).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aB).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
    }

    public static void ag(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aC).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(20)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.an).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
    }

    public static void ah(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.DESERT_WELL.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.i.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(1000)))));
    }

    public static void ai(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, WorldGenerator.FOSSIL.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.k.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(64)))));
    }

    public static void aj(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.KELP.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(120, 80.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
    }

    public static void ak(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SIMPLE_BLOCK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureBlockConfiguration(BiomeDecoratorGroups.SEAGRASS, ImmutableList.of(BiomeDecoratorGroups.STONE), ImmutableList.of(BiomeDecoratorGroups.WATER), ImmutableList.of(BiomeDecoratorGroups.WATER)))).a(WorldGenDecorator.y.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorCarveMaskConfiguration(WorldGenStage.Features.LIQUID, 0.1F)))));
    }

    public static void al(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEAGRASS.b((WorldGenFeatureConfiguration) (new WorldGenFeatureSeaGrassConfiguration(80, 0.3D))).a(WorldGenDecorator.v.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
    }

    public static void am(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SEAGRASS.b((WorldGenFeatureConfiguration) (new WorldGenFeatureSeaGrassConfiguration(80, 0.8D))).a(WorldGenDecorator.v.a((WorldGenFeatureDecoratorConfiguration) WorldGenFeatureDecoratorConfiguration.f)));
    }

    public static void an(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.KELP.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.x.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorNoiseConfiguration(80, 80.0D, 0.0D, HeightMap.Type.OCEAN_FLOOR_WG)))));
    }

    public static void ao(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aK).a(WorldGenDecorator.o.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(50, 8, 8, 256)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.SPRING_FEATURE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aL).a(WorldGenDecorator.p.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)))));
    }

    public static void ap(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.ICEBERG.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.PACKED_ICE))).a(WorldGenDecorator.H.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(16)))));
        biomebase.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, WorldGenerator.ICEBERG.b((WorldGenFeatureConfiguration) (new WorldGenFeatureLakeConfiguration(BiomeDecoratorGroups.BLUE_ICE))).a(WorldGenDecorator.H.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(200)))));
    }

    public static void aq(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.BLUE_ICE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.q.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(20, 30, 32, 64)))));
    }

    public static void ar(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, WorldGenerator.FREEZE_TOP_LAYER.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k));
    }

    public static void as(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.GRAVEL.getBlockData(), 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(2, 5, 0, 37)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, Blocks.BLACKSTONE.getBlockData(), 33))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(2, 5, 10, 37)))));
        a(biomebase, 10, 16);
        at(biomebase);
    }

    public static void a(BiomeBase biomebase, int i, int j) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, BiomeDecoratorGroups.NETHER_GOLD_ORE, 10))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(i, 10, 20, 128)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHERRACK, BiomeDecoratorGroups.NETHER_QUARTZ_ORE, 14))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(j, 10, 20, 128)))));
    }

    public static void at(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.NO_SURFACE_ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.getBlockData(), 3))).a(WorldGenDecorator.u.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorHeightAverageConfiguration(1, 16, 8)))));
        biomebase.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, WorldGenerator.NO_SURFACE_ORE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.getBlockData(), 2))).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(1, 8, 16, 128)))));
    }

    public static void au(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.HUGE_FUNGUS.b((WorldGenFeatureConfiguration) WorldGenFeatureHugeFungiConfiguration.c).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(8)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NETHER_FOREST_VEGETATION.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aY).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(6)))));
    }

    public static void av(BiomeBase biomebase) {
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.HUGE_FUNGUS.b((WorldGenFeatureConfiguration) WorldGenFeatureHugeFungiConfiguration.e).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(8)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NETHER_FOREST_VEGETATION.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aZ).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(5)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.NETHER_FOREST_VEGETATION.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ba).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(4)))));
        biomebase.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.O.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.n.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 128)))));
    }
}

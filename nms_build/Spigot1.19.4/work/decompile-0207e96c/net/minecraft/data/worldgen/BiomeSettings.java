package net.minecraft.data.worldgen;

import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.WorldGenStage;

public class BiomeSettings {

    public BiomeSettings() {}

    public static void addDefaultCarversAndLakes(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.CAVE);
        biomesettingsgeneration_a.addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.CAVE_EXTRA_UNDERGROUND);
        biomesettingsgeneration_a.addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.CANYON);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
    }

    public static void addDefaultMonsterRoom(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.MONSTER_ROOM);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.MONSTER_ROOM_DEEP);
    }

    public static void addDefaultUndergroundVariety(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIRT);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRAVEL);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRANITE_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRANITE_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIORITE_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIORITE_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_ANDESITE_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_ANDESITE_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_TUFF);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.GLOW_LICHEN);
    }

    public static void addDripstone(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, CavePlacements.LARGE_DRIPSTONE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, CavePlacements.DRIPSTONE_CLUSTER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, CavePlacements.POINTED_DRIPSTONE);
    }

    public static void addSculk(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, CavePlacements.SCULK_VEIN);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, CavePlacements.SCULK_PATCH_DEEP_DARK);
    }

    public static void addDefaultOres(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        addDefaultOres(biomesettingsgeneration_a, false);
    }

    public static void addDefaultOres(BiomeSettingsGeneration.a biomesettingsgeneration_a, boolean flag) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_MIDDLE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_SMALL);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE_LOWER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_LARGE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_BURIED);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS_BURIED);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, flag ? OrePlacements.ORE_COPPER_LARGE : OrePlacements.ORE_COPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, CavePlacements.UNDERWATER_MAGMA);
    }

    public static void addExtraGold(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_EXTRA);
    }

    public static void addExtraEmeralds(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_EMERALD);
    }

    public static void addInfestedStone(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_INFESTED);
    }

    public static void addDefaultSoftDisks(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_SAND);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRAVEL);
    }

    public static void addSwampClayDisk(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
    }

    public static void addMangroveSwampDisks(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRASS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
    }

    public static void addMossyStoneBlock(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.FOREST_ROCK);
    }

    public static void addFerns(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
    }

    public static void addRareBerryBushes(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_RARE);
    }

    public static void addCommonBerryBushes(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_BERRY_COMMON);
    }

    public static void addLightBambooVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO_LIGHT);
    }

    public static void addBambooVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO_VEGETATION);
    }

    public static void addTaigaTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_TAIGA);
    }

    public static void addGroveTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_GROVE);
    }

    public static void addWaterTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WATER);
    }

    public static void addBirchTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BIRCH);
    }

    public static void addOtherBirchTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BIRCH_AND_OAK);
    }

    public static void addTallBirchTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BIRCH_TALL);
    }

    public static void addSavannaTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SAVANNA);
    }

    public static void addShatteredSavannaTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_SAVANNA);
    }

    public static void addLushCavesVegetationFeatures(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CEILING_VEGETATION);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.CAVE_VINES);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_CLAY);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.LUSH_CAVES_VEGETATION);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.ROOTED_AZALEA_TREE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.SPORE_BLOSSOM);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, CavePlacements.CLASSIC_VINES);
    }

    public static void addLushCavesSpecialOres(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_CLAY);
    }

    public static void addMountainTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_HILLS);
    }

    public static void addMountainForestTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_FOREST);
    }

    public static void addJungleTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_JUNGLE);
    }

    public static void addSparseJungleTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SPARSE_JUNGLE);
    }

    public static void addBadlandsTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
    }

    public static void addSnowyTrees(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SNOWY);
    }

    public static void addJungleGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
    }

    public static void addSavannaGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS);
    }

    public static void addShatteredSavannaGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
    }

    public static void addSavannaExtraGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_SAVANNA);
    }

    public static void addBadlandGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_BADLANDS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_BADLANDS);
    }

    public static void addForestFlowers(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FOREST_FLOWERS);
    }

    public static void addForestGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
    }

    public static void addSwampVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SWAMP);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP);
    }

    public static void addMangroveSwampVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_MANGROVE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
    }

    public static void addMushroomFieldVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.MUSHROOM_ISLAND_VEGETATION);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
    }

    public static void addDesertVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_2);
    }

    public static void addGiantTaigaVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
    }

    public static void addDefaultFlowers(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_DEFAULT);
    }

    public static void addCherryGroveVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_CHERRY);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_CHERRY);
    }

    public static void addMeadowVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_MEADOW);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_MEADOW);
    }

    public static void addWarmFlowers(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_WARM);
    }

    public static void addDefaultGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_BADLANDS);
    }

    public static void addTaigaGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
    }

    public static void addPlainGrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_TALL_GRASS_2);
    }

    public static void addDefaultMushrooms(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
    }

    public static void addDefaultExtraVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
    }

    public static void addBadlandExtraVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_BADLANDS);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_CACTUS_DECORATED);
    }

    public static void addJungleMelons(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_MELON);
    }

    public static void addSparseJungleMelons(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_MELON_SPARSE);
    }

    public static void addJungleVines(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);
    }

    public static void addDesertExtraVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_DESERT);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_CACTUS_DESERT);
    }

    public static void addSwampExtraVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
    }

    public static void addDesertExtraDecoration(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.DESERT_WELL);
    }

    public static void addFossilDecoration(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.FOSSIL_UPPER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.FOSSIL_LOWER);
    }

    public static void addColdOceanExtraVegetation(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, AquaticPlacements.KELP_COLD);
    }

    public static void addDefaultSeagrass(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SIMPLE);
    }

    public static void addLukeWarmKelp(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, AquaticPlacements.KELP_WARM);
    }

    public static void addDefaultSprings(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA);
    }

    public static void addFrozenSprings(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA_FROZEN);
    }

    public static void addIcebergs(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.ICEBERG_PACKED);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.ICEBERG_BLUE);
    }

    public static void addBlueIce(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.BLUE_ICE);
    }

    public static void addSurfaceFreezing(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.FREEZE_TOP_LAYER);
    }

    public static void addNetherDefaultOres(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GRAVEL_NETHER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_BLACKSTONE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GOLD_NETHER);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_QUARTZ_NETHER);
        addAncientDebris(biomesettingsgeneration_a);
    }

    public static void addAncientDebris(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_SMALL);
    }

    public static void addDefaultCrystalFormations(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, CavePlacements.AMETHYST_GEODE);
    }

    public static void farmAnimals(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.SHEEP, 12, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PIG, 10, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.CHICKEN, 10, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.COW, 8, 4, 4));
    }

    public static void caveSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.AMBIENT, new BiomeSettingsMobs.c(EntityTypes.BAT, 10, 8, 8));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.UNDERGROUND_WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.GLOW_SQUID, 10, 4, 6));
    }

    public static void commonSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        caveSpawns(biomesettingsmobs_a);
        monsters(biomesettingsmobs_a, 95, 5, 100, false);
    }

    public static void oceanSpawns(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j, int k) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, i, 1, j));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.COD, k, 3, 6));
        commonSpawns(biomesettingsmobs_a);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 5, 1, 1));
    }

    public static void warmOceanSpawns(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, i, j, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.TROPICAL_FISH, 25, 8, 8));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.DOLPHIN, 2, 1, 2));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 5, 1, 1));
        commonSpawns(biomesettingsmobs_a);
    }

    public static void plainsSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        farmAnimals(biomesettingsmobs_a);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.HORSE, 5, 2, 6));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.DONKEY, 1, 1, 3));
        commonSpawns(biomesettingsmobs_a);
    }

    public static void snowySpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 10, 2, 3));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.POLAR_BEAR, 1, 1, 2));
        caveSpawns(biomesettingsmobs_a);
        monsters(biomesettingsmobs_a, 95, 5, 20, false);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.STRAY, 80, 4, 4));
    }

    public static void desertSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 4, 2, 3));
        caveSpawns(biomesettingsmobs_a);
        monsters(biomesettingsmobs_a, 19, 1, 100, false);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.HUSK, 80, 4, 4));
    }

    public static void dripstoneCavesSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        caveSpawns(biomesettingsmobs_a);
        boolean flag = true;

        monsters(biomesettingsmobs_a, 95, 5, 100, false);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 95, 4, 4));
    }

    public static void monsters(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j, int k, boolean flag) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SPIDER, 100, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(flag ? EntityTypes.DROWNED : EntityTypes.ZOMBIE, i, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIE_VILLAGER, j, 1, 1));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SKELETON, k, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.CREEPER, 100, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SLIME, 100, 4, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 10, 1, 4));
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.WITCH, 5, 1, 1));
    }

    public static void mooshroomSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.MOOSHROOM, 8, 4, 8));
        caveSpawns(biomesettingsmobs_a);
    }

    public static void baseJungleSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        farmAnimals(biomesettingsmobs_a);
        biomesettingsmobs_a.addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.CHICKEN, 10, 4, 4));
        commonSpawns(biomesettingsmobs_a);
    }

    public static void endSpawns(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}

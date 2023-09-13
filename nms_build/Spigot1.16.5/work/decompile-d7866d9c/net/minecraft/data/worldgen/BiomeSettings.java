package net.minecraft.data.worldgen;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.WorldGenStage;

public class BiomeSettings {

    public static void a(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(StructureFeatures.c);
        biomesettingsgeneration_a.a(StructureFeatures.k);
    }

    public static void b(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(StructureFeatures.b);
        biomesettingsgeneration_a.a(StructureFeatures.k);
    }

    public static void c(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(StructureFeatures.b);
        biomesettingsgeneration_a.a(StructureFeatures.h);
    }

    public static void d(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Features.AIR, WorldGenCarvers.a);
        biomesettingsgeneration_a.a(WorldGenStage.Features.AIR, WorldGenCarvers.b);
    }

    public static void e(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Features.AIR, WorldGenCarvers.c);
        biomesettingsgeneration_a.a(WorldGenStage.Features.AIR, WorldGenCarvers.b);
        biomesettingsgeneration_a.a(WorldGenStage.Features.LIQUID, WorldGenCarvers.d);
        biomesettingsgeneration_a.a(WorldGenStage.Features.LIQUID, WorldGenCarvers.e);
    }

    public static void f(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_WATER);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_LAVA);
    }

    public static void g(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LAKES, BiomeDecoratorGroups.LAKE_LAVA);
    }

    public static void h(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, BiomeDecoratorGroups.MONSTER_ROOM);
    }

    public static void i(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_DIRT);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_GRAVEL);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_GRANITE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_DIORITE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_ANDESITE);
    }

    public static void j(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_COAL);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_IRON);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_GOLD);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_REDSTONE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_DIAMOND);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_LAPIS);
    }

    public static void k(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_GOLD_EXTRA);
    }

    public static void l(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.ORE_EMERALD);
    }

    public static void m(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_INFESTED);
    }

    public static void n(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.DISK_SAND);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.DISK_CLAY);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.DISK_GRAVEL);
    }

    public static void o(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_ORES, BiomeDecoratorGroups.DISK_CLAY);
    }

    public static void p(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, BiomeDecoratorGroups.FOREST_ROCK);
    }

    public static void q(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_LARGE_FERN);
    }

    public static void r(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_BERRY_DECORATED);
    }

    public static void s(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_BERRY_SPARSE);
    }

    public static void t(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BAMBOO_LIGHT);
    }

    public static void u(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BAMBOO);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BAMBOO_VEGETATION);
    }

    public static void v(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TAIGA_VEGETATION);
    }

    public static void w(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_WATER);
    }

    public static void x(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_BIRCH);
    }

    public static void y(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BIRCH_OTHER);
    }

    public static void z(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BIRCH_TALL);
    }

    public static void A(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_SAVANNA);
    }

    public static void B(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_SHATTERED_SAVANNA);
    }

    public static void C(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_MOUNTAIN);
    }

    public static void D(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_MOUNTAIN_EDGE);
    }

    public static void E(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_JUNGLE);
    }

    public static void F(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TREES_JUNGLE_EDGE);
    }

    public static void G(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.OAK_BADLANDS);
    }

    public static void H(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRUCE_SNOWY);
    }

    public static void I(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_JUNGLE);
    }

    public static void J(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_TALL_GRASS);
    }

    public static void K(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_NORMAL);
    }

    public static void L(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_SAVANNA);
    }

    public static void M(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_BADLANDS);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_DEAD_BUSH_BADLANDS);
    }

    public static void N(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FOREST_FLOWER_VEGETATION);
    }

    public static void O(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_FOREST);
    }

    public static void P(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SWAMP_TREE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FLOWER_SWAMP);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_NORMAL);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_DEAD_BUSH);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_WATERLILLY);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_SWAMP);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_SWAMP);
    }

    public static void Q(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.MUSHROOM_FIELD_VEGETATION);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_TAIGA);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_TAIGA);
    }

    public static void R(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PLAIN_VEGETATION);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FLOWER_PLAIN_DECORATED);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_PLAIN);
    }

    public static void S(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_DEAD_BUSH_2);
    }

    public static void T(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_TAIGA);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_DEAD_BUSH);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_GIANT);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_GIANT);
    }

    public static void U(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FLOWER_DEFAULT);
    }

    public static void V(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FLOWER_WARM);
    }

    public static void W(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_BADLANDS);
    }

    public static void X(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_GRASS_TAIGA_2);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_TAIGA);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_TAIGA);
    }

    public static void Y(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_TALL_GRASS_2);
    }

    public static void Z(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_NORMAL);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_NORMAL);
    }

    public static void aa(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUGAR_CANE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_PUMPKIN);
    }

    public static void ab(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUGAR_CANE_BADLANDS);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_PUMPKIN);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_CACTUS_DECORATED);
    }

    public static void ac(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_MELON);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.VINES);
    }

    public static void ad(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUGAR_CANE_DESERT);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_PUMPKIN);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_CACTUS_DESERT);
    }

    public static void ae(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUGAR_CANE_SWAMP);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_PUMPKIN);
    }

    public static void af(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.DESERT_WELL);
    }

    public static void ag(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, BiomeDecoratorGroups.FOSSIL);
    }

    public static void ah(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.KELP_COLD);
    }

    public static void ai(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEAGRASS_SIMPLE);
    }

    public static void aj(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.KELP_WARM);
    }

    public static void ak(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_WATER);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA);
    }

    public static void al(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, BiomeDecoratorGroups.ICEBERG_PACKED);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, BiomeDecoratorGroups.ICEBERG_BLUE);
    }

    public static void am(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.BLUE_ICE);
    }

    public static void an(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, BiomeDecoratorGroups.FREEZE_TOP_LAYER);
    }

    public static void ao(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_GRAVEL_NETHER);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_BLACKSTONE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_GOLD_NETHER);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_QUARTZ_NETHER);
        ap(biomesettingsgeneration_a);
    }

    public static void ap(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_DEBRIS_LARGE);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_DEBRIS_SMALL);
    }

    public static void a(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.SHEEP, 12, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PIG, 10, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.CHICKEN, 10, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.COW, 8, 4, 4));
    }

    public static void b(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.AMBIENT, new BiomeSettingsMobs.c(EntityTypes.BAT, 10, 8, 8));
    }

    public static void c(BiomeSettingsMobs.a biomesettingsmobs_a) {
        b(biomesettingsmobs_a);
        b(biomesettingsmobs_a, 95, 5, 100);
    }

    public static void a(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j, int k) {
        biomesettingsmobs_a.a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, i, 1, j));
        biomesettingsmobs_a.a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.COD, k, 3, 6));
        c(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 5, 1, 1));
    }

    public static void a(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j) {
        biomesettingsmobs_a.a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, i, j, 4));
        biomesettingsmobs_a.a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.TROPICAL_FISH, 25, 8, 8));
        biomesettingsmobs_a.a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.DOLPHIN, 2, 1, 2));
        c(biomesettingsmobs_a);
    }

    public static void d(BiomeSettingsMobs.a biomesettingsmobs_a) {
        a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.HORSE, 5, 2, 6));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.DONKEY, 1, 1, 3));
        c(biomesettingsmobs_a);
    }

    public static void e(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 10, 2, 3));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.POLAR_BEAR, 1, 1, 2));
        b(biomesettingsmobs_a);
        b(biomesettingsmobs_a, 95, 5, 20);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.STRAY, 80, 4, 4));
    }

    public static void f(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 4, 2, 3));
        b(biomesettingsmobs_a);
        b(biomesettingsmobs_a, 19, 1, 100);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.HUSK, 80, 4, 4));
    }

    public static void b(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j, int k) {
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SPIDER, 100, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIE, i, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIE_VILLAGER, j, 1, 1));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SKELETON, k, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.CREEPER, 100, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SLIME, 100, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 10, 1, 4));
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.WITCH, 5, 1, 1));
    }

    public static void g(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.MOOSHROOM, 8, 4, 8));
        b(biomesettingsmobs_a);
    }

    public static void h(BiomeSettingsMobs.a biomesettingsmobs_a) {
        a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.CHICKEN, 10, 4, 4));
        c(biomesettingsmobs_a);
    }

    public static void i(BiomeSettingsMobs.a biomesettingsmobs_a) {
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}

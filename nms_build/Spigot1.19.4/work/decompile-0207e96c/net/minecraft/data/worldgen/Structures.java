package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.EndCityStructure;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

public class Structures {

    public Structures() {}

    private static Structure.c structure(HolderSet<BiomeBase> holderset, Map<EnumCreatureType, StructureSpawnOverride> map, WorldGenStage.Decoration worldgenstage_decoration, TerrainAdjustment terrainadjustment) {
        return new Structure.c(holderset, map, worldgenstage_decoration, terrainadjustment);
    }

    private static Structure.c structure(HolderSet<BiomeBase> holderset, WorldGenStage.Decoration worldgenstage_decoration, TerrainAdjustment terrainadjustment) {
        return structure(holderset, Map.of(), worldgenstage_decoration, terrainadjustment);
    }

    private static Structure.c structure(HolderSet<BiomeBase> holderset, TerrainAdjustment terrainadjustment) {
        return structure(holderset, Map.of(), WorldGenStage.Decoration.SURFACE_STRUCTURES, terrainadjustment);
    }

    public static void bootstrap(BootstapContext<Structure> bootstapcontext) {
        HolderGetter<BiomeBase> holdergetter = bootstapcontext.lookup(Registries.BIOME);
        HolderGetter<WorldGenFeatureDefinedStructurePoolTemplate> holdergetter1 = bootstapcontext.lookup(Registries.TEMPLATE_POOL);

        bootstapcontext.register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST), Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1))))), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeaturePillagerOutpostPieces.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.MINESHAFT, new MineshaftStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_MINESHAFT), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.a.NORMAL));
        bootstapcontext.register(BuiltinStructures.MINESHAFT_MESA, new MineshaftStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_MINESHAFT_MESA), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.a.MESA));
        bootstapcontext.register(BuiltinStructures.WOODLAND_MANSION, new WoodlandMansionStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_WOODLAND_MANSION), TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.JUNGLE_TEMPLE, new JungleTempleStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_JUNGLE_TEMPLE), TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.DESERT_PYRAMID, new DesertPyramidStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_DESERT_PYRAMID), TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.IGLOO, new IglooStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_IGLOO), TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.SHIPWRECK, new ShipwreckStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SHIPWRECK), TerrainAdjustment.NONE), false));
        bootstapcontext.register(BuiltinStructures.SHIPWRECK_BEACHED, new ShipwreckStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SHIPWRECK_BEACHED), TerrainAdjustment.NONE), true));
        bootstapcontext.register(BuiltinStructures.SWAMP_HUT, new SwampHutStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SWAMP_HUT), Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1)))), EnumCreatureType.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1))))), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.STRONGHOLD, new StrongholdStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_STRONGHOLD), TerrainAdjustment.BURY)));
        bootstapcontext.register(BuiltinStructures.OCEAN_MONUMENT, new OceanMonumentStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_MONUMENT), Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4)))), EnumCreatureType.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST), EnumCreatureType.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST)), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.OCEAN_RUIN_COLD, new OceanRuinStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_COLD), TerrainAdjustment.NONE), OceanRuinStructure.a.COLD, 0.3F, 0.9F));
        bootstapcontext.register(BuiltinStructures.OCEAN_RUIN_WARM, new OceanRuinStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_WARM), TerrainAdjustment.NONE), OceanRuinStructure.a.WARM, 0.3F, 0.9F));
        bootstapcontext.register(BuiltinStructures.FORTRESS, new NetherFortressStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_NETHER_FORTRESS), Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, NetherFortressStructure.FORTRESS_ENEMIES)), WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.NETHER_FOSSIL, new NetherFossilStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_NETHER_FOSSIL), WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_THIN), UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))));
        bootstapcontext.register(BuiltinStructures.END_CITY, new EndCityStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_END_CITY), TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.BURIED_TREASURE, new BuriedTreasureStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_BURIED_TREASURE), WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
        bootstapcontext.register(BuiltinStructures.BASTION_REMNANT, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_BASTION_REMNANT), TerrainAdjustment.NONE), holdergetter1.getOrThrow(WorldGenFeatureBastionPieces.START), 6, ConstantHeight.of(VerticalAnchor.absolute(33)), false));
        bootstapcontext.register(BuiltinStructures.VILLAGE_PLAINS, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeatureVillagePlain.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.VILLAGE_DESERT, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeatureDesertVillage.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.VILLAGE_SAVANNA, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_SAVANNA), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeatureVillageSavanna.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.VILLAGE_SNOWY, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_SNOWY), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeatureVillageSnowy.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.VILLAGE_TAIGA, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_TAIGA), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(WorldGenFeatureVillageTaiga.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_STANDARD, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_STANDARD), TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.a(RuinedPortalPiece.b.UNDERGROUND, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_DESERT, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_DESERT), TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.PARTLY_BURIED, 0.0F, 0.0F, false, false, false, false, 1.0F)));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_JUNGLE), TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.8F, true, true, false, false, 1.0F)));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_SWAMP), TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_OCEAN_FLOOR, 0.0F, 0.5F, false, true, false, false, 1.0F)));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN), TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.a(RuinedPortalPiece.b.IN_MOUNTAIN, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_OCEAN), TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_OCEAN_FLOOR, 0.0F, 0.8F, false, false, true, false, 1.0F)));
        bootstapcontext.register(BuiltinStructures.RUINED_PORTAL_NETHER, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_NETHER), TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.IN_NETHER, 0.5F, 0.0F, false, false, false, true, 1.0F)));
        bootstapcontext.register(BuiltinStructures.ANCIENT_CITY, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_ANCIENT_CITY), (Map) Arrays.stream(EnumCreatureType.values()).collect(Collectors.toMap((enumcreaturetype) -> {
            return enumcreaturetype;
        }, (enumcreaturetype) -> {
            return new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create());
        })), WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_BOX), holdergetter1.getOrThrow(AncientCityStructurePieces.START), Optional.of(new MinecraftKey("city_anchor")), 7, ConstantHeight.of(VerticalAnchor.absolute(-27)), false, Optional.empty(), 116));
    }
}

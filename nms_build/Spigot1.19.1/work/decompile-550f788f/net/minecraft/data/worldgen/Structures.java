package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
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

    public static final Holder<Structure> PILLAGER_OUTPOST = register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(structure(BiomeTags.HAS_PILLAGER_OUTPOST, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1))))), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), WorldGenFeaturePillagerOutpostPieces.START, 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> MINESHAFT = register(BuiltinStructures.MINESHAFT, new MineshaftStructure(structure(BiomeTags.HAS_MINESHAFT, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.a.NORMAL));
    public static final Holder<Structure> MINESHAFT_MESA = register(BuiltinStructures.MINESHAFT_MESA, new MineshaftStructure(structure(BiomeTags.HAS_MINESHAFT_MESA, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.a.MESA));
    public static final Holder<Structure> WOODLAND_MANSION = register(BuiltinStructures.WOODLAND_MANSION, new WoodlandMansionStructure(structure(BiomeTags.HAS_WOODLAND_MANSION, TerrainAdjustment.NONE)));
    public static final Holder<Structure> JUNGLE_TEMPLE = register(BuiltinStructures.JUNGLE_TEMPLE, new JungleTempleStructure(structure(BiomeTags.HAS_JUNGLE_TEMPLE, TerrainAdjustment.NONE)));
    public static final Holder<Structure> DESERT_PYRAMID = register(BuiltinStructures.DESERT_PYRAMID, new DesertPyramidStructure(structure(BiomeTags.HAS_DESERT_PYRAMID, TerrainAdjustment.NONE)));
    public static final Holder<Structure> IGLOO = register(BuiltinStructures.IGLOO, new IglooStructure(structure(BiomeTags.HAS_IGLOO, TerrainAdjustment.NONE)));
    public static final Holder<Structure> SHIPWRECK = register(BuiltinStructures.SHIPWRECK, new ShipwreckStructure(structure(BiomeTags.HAS_SHIPWRECK, TerrainAdjustment.NONE), false));
    public static final Holder<Structure> SHIPWRECK_BEACHED = register(BuiltinStructures.SHIPWRECK_BEACHED, new ShipwreckStructure(structure(BiomeTags.HAS_SHIPWRECK_BEACHED, TerrainAdjustment.NONE), true));
    public static final Holder<Structure> SWAMP_HUT = register(BuiltinStructures.SWAMP_HUT, new SwampHutStructure(structure(BiomeTags.HAS_SWAMP_HUT, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1)))), EnumCreatureType.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1))))), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    public static final Holder<Structure> STRONGHOLD = register(BuiltinStructures.STRONGHOLD, new StrongholdStructure(structure(BiomeTags.HAS_STRONGHOLD, TerrainAdjustment.BURY)));
    public static final Holder<Structure> OCEAN_MONUMENT = register(BuiltinStructures.OCEAN_MONUMENT, new OceanMonumentStructure(structure(BiomeTags.HAS_OCEAN_MONUMENT, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4)))), EnumCreatureType.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST), EnumCreatureType.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST)), WorldGenStage.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    public static final Holder<Structure> OCEAN_RUIN_COLD = register(BuiltinStructures.OCEAN_RUIN_COLD, new OceanRuinStructure(structure(BiomeTags.HAS_OCEAN_RUIN_COLD, TerrainAdjustment.NONE), OceanRuinStructure.a.COLD, 0.3F, 0.9F));
    public static final Holder<Structure> OCEAN_RUIN_WARM = register(BuiltinStructures.OCEAN_RUIN_WARM, new OceanRuinStructure(structure(BiomeTags.HAS_OCEAN_RUIN_WARM, TerrainAdjustment.NONE), OceanRuinStructure.a.WARM, 0.3F, 0.9F));
    public static final Holder<Structure> FORTRESS = register(BuiltinStructures.FORTRESS, new NetherFortressStructure(structure(BiomeTags.HAS_NETHER_FORTRESS, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, NetherFortressStructure.FORTRESS_ENEMIES)), WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
    public static final Holder<Structure> NETHER_FOSSIL = register(BuiltinStructures.NETHER_FOSSIL, new NetherFossilStructure(structure(BiomeTags.HAS_NETHER_FOSSIL, WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_THIN), UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))));
    public static final Holder<Structure> END_CITY = register(BuiltinStructures.END_CITY, new EndCityStructure(structure(BiomeTags.HAS_END_CITY, TerrainAdjustment.NONE)));
    public static final Holder<Structure> BURIED_TREASURE = register(BuiltinStructures.BURIED_TREASURE, new BuriedTreasureStructure(structure(BiomeTags.HAS_BURIED_TREASURE, WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
    public static final Holder<Structure> BASTION_REMNANT = register(BuiltinStructures.BASTION_REMNANT, new JigsawStructure(structure(BiomeTags.HAS_BASTION_REMNANT, TerrainAdjustment.NONE), WorldGenFeatureBastionPieces.START, 6, ConstantHeight.of(VerticalAnchor.absolute(33)), false));
    public static final Holder<Structure> VILLAGE_PLAINS = register(BuiltinStructures.VILLAGE_PLAINS, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_PLAINS, TerrainAdjustment.BEARD_THIN), WorldGenFeatureVillagePlain.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> VILLAGE_DESERT = register(BuiltinStructures.VILLAGE_DESERT, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_DESERT, TerrainAdjustment.BEARD_THIN), WorldGenFeatureDesertVillage.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> VILLAGE_SAVANNA = register(BuiltinStructures.VILLAGE_SAVANNA, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_SAVANNA, TerrainAdjustment.BEARD_THIN), WorldGenFeatureVillageSavanna.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> VILLAGE_SNOWY = register(BuiltinStructures.VILLAGE_SNOWY, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_SNOWY, TerrainAdjustment.BEARD_THIN), WorldGenFeatureVillageSnowy.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> VILLAGE_TAIGA = register(BuiltinStructures.VILLAGE_TAIGA, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_TAIGA, TerrainAdjustment.BEARD_THIN), WorldGenFeatureVillageTaiga.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, HeightMap.Type.WORLD_SURFACE_WG));
    public static final Holder<Structure> RUINED_PORTAL_STANDARD = register(BuiltinStructures.RUINED_PORTAL_STANDARD, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_STANDARD, TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.a(RuinedPortalPiece.b.UNDERGROUND, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
    public static final Holder<Structure> RUINED_PORTAL_DESERT = register(BuiltinStructures.RUINED_PORTAL_DESERT, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_DESERT, TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.PARTLY_BURIED, 0.0F, 0.0F, false, false, false, false, 1.0F)));
    public static final Holder<Structure> RUINED_PORTAL_JUNGLE = register(BuiltinStructures.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_JUNGLE, TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.8F, true, true, false, false, 1.0F)));
    public static final Holder<Structure> RUINED_PORTAL_SWAMP = register(BuiltinStructures.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_SWAMP, TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_OCEAN_FLOOR, 0.0F, 0.5F, false, true, false, false, 1.0F)));
    public static final Holder<Structure> RUINED_PORTAL_MOUNTAIN = register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN, TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.a(RuinedPortalPiece.b.IN_MOUNTAIN, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
    public static final Holder<Structure> RUINED_PORTAL_OCEAN = register(BuiltinStructures.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_OCEAN, TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.ON_OCEAN_FLOOR, 0.0F, 0.8F, false, false, true, false, 1.0F)));
    public static final Holder<Structure> RUINED_PORTAL_NETHER = register(BuiltinStructures.RUINED_PORTAL_NETHER, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_NETHER, TerrainAdjustment.NONE), new RuinedPortalStructure.a(RuinedPortalPiece.b.IN_NETHER, 0.5F, 0.0F, false, false, false, true, 1.0F)));
    public static final Holder<Structure> ANCIENT_CITY = register(BuiltinStructures.ANCIENT_CITY, new JigsawStructure(structure(BiomeTags.HAS_ANCIENT_CITY, (Map) Arrays.stream(EnumCreatureType.values()).collect(Collectors.toMap((enumcreaturetype) -> {
        return enumcreaturetype;
    }, (enumcreaturetype) -> {
        return new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create());
    })), WorldGenStage.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_BOX), AncientCityStructurePieces.START, Optional.of(new MinecraftKey("city_anchor")), 7, ConstantHeight.of(VerticalAnchor.absolute(-27)), false, Optional.empty(), 116));

    public Structures() {}

    public static Holder<? extends Structure> bootstrap(IRegistry<Structure> iregistry) {
        return Structures.MINESHAFT;
    }

    private static Structure.c structure(TagKey<BiomeBase> tagkey, Map<EnumCreatureType, StructureSpawnOverride> map, WorldGenStage.Decoration worldgenstage_decoration, TerrainAdjustment terrainadjustment) {
        return new Structure.c(biomes(tagkey), map, worldgenstage_decoration, terrainadjustment);
    }

    private static Structure.c structure(TagKey<BiomeBase> tagkey, WorldGenStage.Decoration worldgenstage_decoration, TerrainAdjustment terrainadjustment) {
        return structure(tagkey, Map.of(), worldgenstage_decoration, terrainadjustment);
    }

    private static Structure.c structure(TagKey<BiomeBase> tagkey, TerrainAdjustment terrainadjustment) {
        return structure(tagkey, Map.of(), WorldGenStage.Decoration.SURFACE_STRUCTURES, terrainadjustment);
    }

    private static Holder<Structure> register(ResourceKey<Structure> resourcekey, Structure structure) {
        return RegistryGeneration.register(RegistryGeneration.STRUCTURES, resourcekey, structure);
    }

    private static HolderSet<BiomeBase> biomes(TagKey<BiomeBase> tagkey) {
        return RegistryGeneration.BIOME.getOrCreateTag(tagkey);
    }
}

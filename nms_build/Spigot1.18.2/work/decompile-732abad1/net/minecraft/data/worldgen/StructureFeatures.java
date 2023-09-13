package net.minecraft.data.worldgen;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRuinedPortal;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;
import net.minecraft.world.level.levelgen.feature.WorldGenNether;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;

public class StructureFeatures {

    public static final Holder<StructureFeature<?, ?>> PILLAGER_OUTPOST = register(BuiltinStructures.PILLAGER_OUTPOST, StructureGenerator.PILLAGER_OUTPOST.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeaturePillagerOutpostPieces.START, 7), BiomeTags.HAS_PILLAGER_OUTPOST, true, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1)))))));
    public static final Holder<StructureFeature<?, ?>> MINESHAFT = register(BuiltinStructures.MINESHAFT, StructureGenerator.MINESHAFT.configured(new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.NORMAL), BiomeTags.HAS_MINESHAFT));
    public static final Holder<StructureFeature<?, ?>> MINESHAFT_MESA = register(BuiltinStructures.MINESHAFT_MESA, StructureGenerator.MINESHAFT.configured(new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.MESA), BiomeTags.HAS_MINESHAFT_MESA));
    public static final Holder<StructureFeature<?, ?>> WOODLAND_MANSION = register(BuiltinStructures.WOODLAND_MANSION, StructureGenerator.WOODLAND_MANSION.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_WOODLAND_MANSION));
    public static final Holder<StructureFeature<?, ?>> JUNGLE_TEMPLE = register(BuiltinStructures.JUNGLE_TEMPLE, StructureGenerator.JUNGLE_TEMPLE.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_JUNGLE_TEMPLE));
    public static final Holder<StructureFeature<?, ?>> DESERT_PYRAMID = register(BuiltinStructures.DESERT_PYRAMID, StructureGenerator.DESERT_PYRAMID.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_DESERT_PYRAMID));
    public static final Holder<StructureFeature<?, ?>> IGLOO = register(BuiltinStructures.IGLOO, StructureGenerator.IGLOO.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_IGLOO));
    public static final Holder<StructureFeature<?, ?>> SHIPWRECK = register(BuiltinStructures.SHIPWRECK, StructureGenerator.SHIPWRECK.configured(new WorldGenFeatureShipwreckConfiguration(false), BiomeTags.HAS_SHIPWRECK));
    public static final Holder<StructureFeature<?, ?>> SHIPWRECK_BEACHED = register(BuiltinStructures.SHIPWRECK_BEACHED, StructureGenerator.SHIPWRECK.configured(new WorldGenFeatureShipwreckConfiguration(true), BiomeTags.HAS_SHIPWRECK_BEACHED));
    public static final Holder<StructureFeature<?, ?>> SWAMP_HUT = register(BuiltinStructures.SWAMP_HUT, StructureGenerator.SWAMP_HUT.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_SWAMP_HUT, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1)))), EnumCreatureType.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1)))))));
    public static final Holder<StructureFeature<?, ?>> STRONGHOLD = register(BuiltinStructures.STRONGHOLD, StructureGenerator.STRONGHOLD.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_STRONGHOLD, true));
    public static final Holder<StructureFeature<?, ?>> OCEAN_MONUMENT = register(BuiltinStructures.OCEAN_MONUMENT, StructureGenerator.OCEAN_MONUMENT.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_OCEAN_MONUMENT, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4)))), EnumCreatureType.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST), EnumCreatureType.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.a.STRUCTURE, BiomeSettingsMobs.EMPTY_MOB_LIST))));
    public static final Holder<StructureFeature<?, ?>> OCEAN_RUIN_COLD = register(BuiltinStructures.OCEAN_RUIN_COLD, StructureGenerator.OCEAN_RUIN.configured(new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F), BiomeTags.HAS_OCEAN_RUIN_COLD));
    public static final Holder<StructureFeature<?, ?>> OCEAN_RUIN_WARM = register(BuiltinStructures.OCEAN_RUIN_WARM, StructureGenerator.OCEAN_RUIN.configured(new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F), BiomeTags.HAS_OCEAN_RUIN_WARM));
    public static final Holder<StructureFeature<?, ?>> FORTRESS = register(BuiltinStructures.FORTRESS, StructureGenerator.FORTRESS.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_NETHER_FORTRESS, Map.of(EnumCreatureType.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.a.PIECE, WorldGenNether.FORTRESS_ENEMIES))));
    public static final Holder<StructureFeature<?, ?>> NETHER_FOSSIL = register(BuiltinStructures.NETHER_FOSSIL, StructureGenerator.NETHER_FOSSIL.configured(new RangeConfiguration(UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))), BiomeTags.HAS_NETHER_FOSSIL, true));
    public static final Holder<StructureFeature<?, ?>> END_CITY = register(BuiltinStructures.END_CITY, StructureGenerator.END_CITY.configured(WorldGenFeatureEmptyConfiguration.INSTANCE, BiomeTags.HAS_END_CITY));
    public static final Holder<StructureFeature<?, ?>> BURIED_TREASURE = register(BuiltinStructures.BURIED_TREASURE, StructureGenerator.BURIED_TREASURE.configured(new WorldGenFeatureConfigurationChance(0.01F), BiomeTags.HAS_BURIED_TREASURE));
    public static final Holder<StructureFeature<?, ?>> BASTION_REMNANT = register(BuiltinStructures.BASTION_REMNANT, StructureGenerator.BASTION_REMNANT.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureBastionPieces.START, 6), BiomeTags.HAS_BASTION_REMNANT));
    public static final Holder<StructureFeature<?, ?>> VILLAGE_PLAINS = register(BuiltinStructures.VILLAGE_PLAINS, StructureGenerator.VILLAGE.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureVillagePlain.START, 6), BiomeTags.HAS_VILLAGE_PLAINS, true));
    public static final Holder<StructureFeature<?, ?>> VILLAGE_DESERT = register(BuiltinStructures.VILLAGE_DESERT, StructureGenerator.VILLAGE.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureDesertVillage.START, 6), BiomeTags.HAS_VILLAGE_DESERT, true));
    public static final Holder<StructureFeature<?, ?>> VILLAGE_SAVANNA = register(BuiltinStructures.VILLAGE_SAVANNA, StructureGenerator.VILLAGE.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureVillageSavanna.START, 6), BiomeTags.HAS_VILLAGE_SAVANNA, true));
    public static final Holder<StructureFeature<?, ?>> VILLAGE_SNOWY = register(BuiltinStructures.VILLAGE_SNOWY, StructureGenerator.VILLAGE.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureVillageSnowy.START, 6), BiomeTags.HAS_VILLAGE_SNOWY, true));
    public static final Holder<StructureFeature<?, ?>> VILLAGE_TAIGA = register(BuiltinStructures.VILLAGE_TAIGA, StructureGenerator.VILLAGE.configured(new WorldGenFeatureVillageConfiguration(WorldGenFeatureVillageTaiga.START, 6), BiomeTags.HAS_VILLAGE_TAIGA, true));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_STANDARD = register(BuiltinStructures.RUINED_PORTAL_STANDARD, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.STANDARD), BiomeTags.HAS_RUINED_PORTAL_STANDARD));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_DESERT = register(BuiltinStructures.RUINED_PORTAL_DESERT, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.DESERT), BiomeTags.HAS_RUINED_PORTAL_DESERT));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = register(BuiltinStructures.RUINED_PORTAL_JUNGLE, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.JUNGLE), BiomeTags.HAS_RUINED_PORTAL_JUNGLE));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_SWAMP = register(BuiltinStructures.RUINED_PORTAL_SWAMP, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.SWAMP), BiomeTags.HAS_RUINED_PORTAL_SWAMP));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.MOUNTAIN), BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_OCEAN = register(BuiltinStructures.RUINED_PORTAL_OCEAN, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.OCEAN), BiomeTags.HAS_RUINED_PORTAL_OCEAN));
    public static final Holder<StructureFeature<?, ?>> RUINED_PORTAL_NETHER = register(BuiltinStructures.RUINED_PORTAL_NETHER, StructureGenerator.RUINED_PORTAL.configured(new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.NETHER), BiomeTags.HAS_RUINED_PORTAL_NETHER));

    public StructureFeatures() {}

    public static Holder<? extends StructureFeature<?, ?>> bootstrap() {
        return StructureFeatures.MINESHAFT;
    }

    private static <FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> Holder<StructureFeature<?, ?>> register(ResourceKey<StructureFeature<?, ?>> resourcekey, StructureFeature<FC, F> structurefeature) {
        return RegistryGeneration.register(RegistryGeneration.CONFIGURED_STRUCTURE_FEATURE, resourcekey, structurefeature);
    }
}

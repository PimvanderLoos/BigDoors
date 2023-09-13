package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRuinedPortal;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;

public class StructureFeatures {

    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> PILLAGER_OUTPOST = a("pillager_outpost", StructureGenerator.PILLAGER_OUTPOST.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeaturePillagerOutpostPieces.START;
    }, 7))));
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> MINESHAFT = a("mineshaft", StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.NORMAL))));
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> MINESHAFT_MESA = a("mineshaft_mesa", StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.MESA))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> WOODLAND_MANSION = a("mansion", StructureGenerator.WOODLAND_MANSION.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> JUNGLE_TEMPLE = a("jungle_pyramid", StructureGenerator.JUNGLE_TEMPLE.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> DESERT_PYRAMID = a("desert_pyramid", StructureGenerator.DESERT_PYRAMID.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> IGLOO = a("igloo", StructureGenerator.IGLOO.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> SHIPWRECK = a("shipwreck", StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false))));
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> SHIPWRECH_BEACHED = a("shipwreck_beached", StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(true))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> SWAMP_HUT = a("swamp_hut", StructureGenerator.SWAMP_HUT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> STRONGHOLD = a("stronghold", StructureGenerator.STRONGHOLD.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> OCEAN_MONUMENT = a("monument", StructureGenerator.OCEAN_MONUMENT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> OCEAN_RUIN_COLD = a("ocean_ruin_cold", StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F))));
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> OCEAN_RUIN_WARM = a("ocean_ruin_warm", StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> NETHER_BRIDGE = a("fortress", StructureGenerator.NETHER_BRIDGE.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureChanceDecoratorRangeConfiguration, ? extends StructureGenerator<WorldGenFeatureChanceDecoratorRangeConfiguration>> NETHER_FOSSIL = a("nether_fossil", StructureGenerator.NETHER_FOSSIL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureChanceDecoratorRangeConfiguration(UniformHeight.a(VerticalAnchor.a(32), VerticalAnchor.c(2))))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> END_CITY = a("end_city", StructureGenerator.END_CITY.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.INSTANCE));
    public static final StructureFeature<WorldGenFeatureConfigurationChance, ? extends StructureGenerator<WorldGenFeatureConfigurationChance>> BURIED_TREASURE = a("buried_treasure", StructureGenerator.BURIED_TREASURE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.01F))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> BASTION_REMNANT = a("bastion_remnant", StructureGenerator.BASTION_REMNANT.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureBastionPieces.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> VILLAGE_PLAINS = a("village_plains", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillagePlain.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> VILLAGE_DESERT = a("village_desert", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureDesertVillage.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> VILLAGE_SAVANNA = a("village_savanna", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageSavanna.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> VILLAGE_SNOWY = a("village_snowy", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageSnowy.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> VILLAGE_TAIGA = a("village_taiga", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageTaiga.START;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_STANDARD = a("ruined_portal", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.STANDARD))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_DESERT = a("ruined_portal_desert", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.DESERT))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_JUNGLE = a("ruined_portal_jungle", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.JUNGLE))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_SWAMP = a("ruined_portal_swamp", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.SWAMP))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_MOUNTAIN = a("ruined_portal_mountain", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.MOUNTAIN))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_OCEAN = a("ruined_portal_ocean", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.OCEAN))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> RUINED_PORTAL_NETHER = a("ruined_portal_nether", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.NETHER))));

    public StructureFeatures() {}

    private static <FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> StructureFeature<FC, F> a(String s, StructureFeature<FC, F> structurefeature) {
        return (StructureFeature) RegistryGeneration.a(RegistryGeneration.CONFIGURED_STRUCTURE_FEATURE, s, (Object) structurefeature);
    }
}

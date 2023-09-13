package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRuinedPortal;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureOceanRuin;

public class StructureFeatures {

    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> a = a("pillager_outpost", StructureGenerator.PILLAGER_OUTPOST.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeaturePillagerOutpostPieces.a;
    }, 7))));
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> b = a("mineshaft", StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.NORMAL))));
    public static final StructureFeature<WorldGenMineshaftConfiguration, ? extends StructureGenerator<WorldGenMineshaftConfiguration>> c = a("mineshaft_mesa", StructureGenerator.MINESHAFT.a((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004F, WorldGenMineshaft.Type.MESA))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> d = a("mansion", StructureGenerator.MANSION.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> e = a("jungle_pyramid", StructureGenerator.JUNGLE_PYRAMID.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> f = a("desert_pyramid", StructureGenerator.DESERT_PYRAMID.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> g = a("igloo", StructureGenerator.IGLOO.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> h = a("shipwreck", StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false))));
    public static final StructureFeature<WorldGenFeatureShipwreckConfiguration, ? extends StructureGenerator<WorldGenFeatureShipwreckConfiguration>> i = a("shipwreck_beached", StructureGenerator.SHIPWRECK.a((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(true))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> j = a("swamp_hut", StructureGenerator.SWAMP_HUT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> k = a("stronghold", StructureGenerator.STRONGHOLD.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> l = a("monument", StructureGenerator.MONUMENT.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> m = a("ocean_ruin_cold", StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.COLD, 0.3F, 0.9F))));
    public static final StructureFeature<WorldGenFeatureOceanRuinConfiguration, ? extends StructureGenerator<WorldGenFeatureOceanRuinConfiguration>> n = a("ocean_ruin_warm", StructureGenerator.OCEAN_RUIN.a((WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F))));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> o = a("fortress", StructureGenerator.FORTRESS.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> p = a("nether_fossil", StructureGenerator.NETHER_FOSSIL.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureEmptyConfiguration, ? extends StructureGenerator<WorldGenFeatureEmptyConfiguration>> q = a("end_city", StructureGenerator.ENDCITY.a((WorldGenFeatureConfiguration) WorldGenFeatureEmptyConfiguration.b));
    public static final StructureFeature<WorldGenFeatureConfigurationChance, ? extends StructureGenerator<WorldGenFeatureConfigurationChance>> r = a("buried_treasure", StructureGenerator.BURIED_TREASURE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.01F))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> s = a("bastion_remnant", StructureGenerator.BASTION_REMNANT.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureBastionPieces.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> t = a("village_plains", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillagePlain.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> u = a("village_desert", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureDesertVillage.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> v = a("village_savanna", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageSavanna.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> w = a("village_snowy", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageSnowy.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureVillageConfiguration, ? extends StructureGenerator<WorldGenFeatureVillageConfiguration>> x = a("village_taiga", StructureGenerator.VILLAGE.a((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration(() -> {
        return WorldGenFeatureVillageTaiga.a;
    }, 6))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> y = a("ruined_portal", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.STANDARD))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> z = a("ruined_portal_desert", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.DESERT))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> A = a("ruined_portal_jungle", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.JUNGLE))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> B = a("ruined_portal_swamp", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.SWAMP))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> C = a("ruined_portal_mountain", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.MOUNTAIN))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> D = a("ruined_portal_ocean", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.OCEAN))));
    public static final StructureFeature<WorldGenFeatureRuinedPortalConfiguration, ? extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration>> E = a("ruined_portal_nether", StructureGenerator.RUINED_PORTAL.a((WorldGenFeatureConfiguration) (new WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type.NETHER))));

    private static <FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> StructureFeature<FC, F> a(String s, StructureFeature<FC, F> structurefeature) {
        return (StructureFeature) RegistryGeneration.a(RegistryGeneration.f, s, (Object) structurefeature);
    }
}

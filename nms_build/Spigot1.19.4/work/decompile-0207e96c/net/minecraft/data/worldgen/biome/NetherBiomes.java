package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.worldgen.BiomeSettings;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeParticles;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.CaveSound;
import net.minecraft.world.level.biome.CaveSoundSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class NetherBiomes {

    public NetherBiomes() {}

    public static BiomeBase netherWastes(HolderGetter<PlacedFeature> holdergetter, HolderGetter<WorldGenCarverWrapper<?>> holdergetter1) {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 50, 4, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 100, 4, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 2, 4, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.PIGLIN, 15, 4, 4)).addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).build();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a(holdergetter, holdergetter1)).addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.NETHER_CAVE).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA);

        BiomeSettings.addDefaultMushrooms(biomesettingsgeneration_a);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED);
        BiomeSettings.addNetherDefaultOres(biomesettingsgeneration_a);
        return (new BiomeBase.a()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeFog.a()).waterColor(4159204).waterFogColor(329011).fogColor(3344392).skyColor(OverworldBiomes.calculateSkyColor(2.0F)).ambientLoopSound(SoundEffects.AMBIENT_NETHER_WASTES_LOOP).ambientMoodSound(new CaveSoundSettings(SoundEffects.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new CaveSound(SoundEffects.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEffects.MUSIC_BIOME_NETHER_WASTES)).build()).mobSpawnSettings(biomesettingsmobs).generationSettings(biomesettingsgeneration_a.build()).build();
    }

    public static BiomeBase soulSandValley(HolderGetter<PlacedFeature> holdergetter, HolderGetter<WorldGenCarverWrapper<?>> holdergetter1) {
        double d0 = 0.7D;
        double d1 = 0.15D;
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SKELETON, 20, 5, 5)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 50, 4, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).addMobCharge(EntityTypes.SKELETON, 0.7D, 0.15D).addMobCharge(EntityTypes.GHAST, 0.7D, 0.15D).addMobCharge(EntityTypes.ENDERMAN, 0.7D, 0.15D).addMobCharge(EntityTypes.STRIDER, 0.7D, 0.15D).build();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a(holdergetter, holdergetter1)).addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.NETHER_CAVE).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA).addFeature(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, NetherPlacements.BASALT_PILLAR).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_CRIMSON_ROOTS).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_SOUL_SAND);

        BiomeSettings.addNetherDefaultOres(biomesettingsgeneration_a);
        return (new BiomeBase.a()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeFog.a()).waterColor(4159204).waterFogColor(329011).fogColor(1787717).skyColor(OverworldBiomes.calculateSkyColor(2.0F)).ambientParticle(new BiomeParticles(Particles.ASH, 0.00625F)).ambientLoopSound(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_LOOP).ambientMoodSound(new CaveSoundSettings(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new CaveSound(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEffects.MUSIC_BIOME_SOUL_SAND_VALLEY)).build()).mobSpawnSettings(biomesettingsmobs).generationSettings(biomesettingsgeneration_a.build()).build();
    }

    public static BiomeBase basaltDeltas(HolderGetter<PlacedFeature> holdergetter, HolderGetter<WorldGenCarverWrapper<?>> holdergetter1) {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 40, 1, 1)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 100, 2, 5)).addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).build();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a(holdergetter, holdergetter1)).addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.NETHER_CAVE).addFeature(WorldGenStage.Decoration.SURFACE_STRUCTURES, NetherPlacements.DELTA).addFeature(WorldGenStage.Decoration.SURFACE_STRUCTURES, NetherPlacements.SMALL_BASALT_COLUMNS).addFeature(WorldGenStage.Decoration.SURFACE_STRUCTURES, NetherPlacements.LARGE_BASALT_COLUMNS).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.BASALT_BLOBS).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.BLACKSTONE_BLOBS).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_DELTA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED_DOUBLE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GOLD_DELTAS).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_QUARTZ_DELTAS);

        BiomeSettings.addAncientDebris(biomesettingsgeneration_a);
        return (new BiomeBase.a()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeFog.a()).waterColor(4159204).waterFogColor(329011).fogColor(6840176).skyColor(OverworldBiomes.calculateSkyColor(2.0F)).ambientParticle(new BiomeParticles(Particles.WHITE_ASH, 0.118093334F)).ambientLoopSound(SoundEffects.AMBIENT_BASALT_DELTAS_LOOP).ambientMoodSound(new CaveSoundSettings(SoundEffects.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new CaveSound(SoundEffects.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEffects.MUSIC_BIOME_BASALT_DELTAS)).build()).mobSpawnSettings(biomesettingsmobs).generationSettings(biomesettingsgeneration_a.build()).build();
    }

    public static BiomeBase crimsonForest(HolderGetter<PlacedFeature> holdergetter, HolderGetter<WorldGenCarverWrapper<?>> holdergetter1) {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 1, 2, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.HOGLIN, 9, 3, 4)).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.PIGLIN, 5, 3, 4)).addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).build();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a(holdergetter, holdergetter1)).addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.NETHER_CAVE).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA);

        BiomeSettings.addDefaultMushrooms(biomesettingsgeneration_a);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, NetherPlacements.WEEPING_VINES).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, TreePlacements.CRIMSON_FUNGI).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, NetherPlacements.CRIMSON_FOREST_VEGETATION);
        BiomeSettings.addNetherDefaultOres(biomesettingsgeneration_a);
        return (new BiomeBase.a()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeFog.a()).waterColor(4159204).waterFogColor(329011).fogColor(3343107).skyColor(OverworldBiomes.calculateSkyColor(2.0F)).ambientParticle(new BiomeParticles(Particles.CRIMSON_SPORE, 0.025F)).ambientLoopSound(SoundEffects.AMBIENT_CRIMSON_FOREST_LOOP).ambientMoodSound(new CaveSoundSettings(SoundEffects.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new CaveSound(SoundEffects.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEffects.MUSIC_BIOME_CRIMSON_FOREST)).build()).mobSpawnSettings(biomesettingsmobs).generationSettings(biomesettingsgeneration_a.build()).build();
    }

    public static BiomeBase warpedForest(HolderGetter<PlacedFeature> holdergetter, HolderGetter<WorldGenCarverWrapper<?>> holdergetter1) {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).addSpawn(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).addSpawn(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).addMobCharge(EntityTypes.ENDERMAN, 1.0D, 0.12D).build();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a(holdergetter, holdergetter1)).addCarver(WorldGenStage.Features.AIR, WorldGenCarvers.NETHER_CAVE).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA);

        BiomeSettings.addDefaultMushrooms(biomesettingsgeneration_a);
        biomesettingsgeneration_a.addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA).addFeature(WorldGenStage.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, TreePlacements.WARPED_FUNGI).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, NetherPlacements.WARPED_FOREST_VEGETATION).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, NetherPlacements.NETHER_SPROUTS).addFeature(WorldGenStage.Decoration.VEGETAL_DECORATION, NetherPlacements.TWISTING_VINES);
        BiomeSettings.addNetherDefaultOres(biomesettingsgeneration_a);
        return (new BiomeBase.a()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeFog.a()).waterColor(4159204).waterFogColor(329011).fogColor(1705242).skyColor(OverworldBiomes.calculateSkyColor(2.0F)).ambientParticle(new BiomeParticles(Particles.WARPED_SPORE, 0.01428F)).ambientLoopSound(SoundEffects.AMBIENT_WARPED_FOREST_LOOP).ambientMoodSound(new CaveSoundSettings(SoundEffects.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new CaveSound(SoundEffects.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEffects.MUSIC_BIOME_WARPED_FOREST)).build()).mobSpawnSettings(biomesettingsmobs).generationSettings(biomesettingsgeneration_a.build()).build();
    }
}

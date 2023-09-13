package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeaturePillagerOutpostPieces {

    public static final WorldGenFeatureDefinedStructurePoolTemplate START = WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/base_plates"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/base_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public WorldGenFeaturePillagerOutpostPieces() {}

    public static void bootstrap() {}

    static {
        WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/towers"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.list(ImmutableList.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/watchtower"), WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/watchtower_overgrown", ProcessorLists.OUTPOST_ROT))), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/feature_plates"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/features"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_logs"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_targets"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 6)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}

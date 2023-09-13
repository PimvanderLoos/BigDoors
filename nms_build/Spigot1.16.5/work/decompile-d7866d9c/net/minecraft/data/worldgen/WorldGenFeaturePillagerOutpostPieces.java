package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeaturePillagerOutpostPieces {

    public static final WorldGenFeatureDefinedStructurePoolTemplate a = WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/base_plates"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/base_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public static void a() {}

    static {
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/towers"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a((List) ImmutableList.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/watchtower"), WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/watchtower_overgrown", ProcessorLists.r))), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/feature_plates"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("pillager_outpost/features"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_cage1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_cage2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_logs"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_tent1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_tent2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.a("pillager_outpost/feature_targets"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.g(), 6)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}

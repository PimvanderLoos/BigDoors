package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureBastionBridge {

    public static void a() {}

    static {
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/starting_pieces"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/starting_pieces/entrance", ProcessorLists.z), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/starting_pieces/entrance_face", ProcessorLists.x), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/bridge_pieces"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/bridge_pieces/bridge", ProcessorLists.A), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/legs"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/legs/leg_0", ProcessorLists.x), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/legs/leg_1", ProcessorLists.x), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/walls"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/walls/wall_base_0", ProcessorLists.y), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/walls/wall_base_1", ProcessorLists.y), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/ramparts"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/ramparts/rampart_0", ProcessorLists.y), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/ramparts/rampart_1", ProcessorLists.y), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/rampart_plates"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/rampart_plates/plate_0", ProcessorLists.y), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/bridge/connectors"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/connectors/back_bridge_top", ProcessorLists.x), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/connectors/back_bridge_bottom", ProcessorLists.x), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}

package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureBastionPieces {

    public static final WorldGenFeatureDefinedStructurePoolTemplate a = WorldGenFeaturePieces.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/starts"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/units/air_base", ProcessorLists.x), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/hoglin_stable/air_base", ProcessorLists.x), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/treasure/big_air_full", ProcessorLists.x), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.b("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.x), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public static void a() {
        WorldGenFeatureBastionUnits.a();
        WorldGenFeatureBastionHoglinStable.a();
        WorldGenFeatureBastionTreasure.a();
        WorldGenFeatureBastionBridge.a();
        WorldGenFeatureBastionExtra.a();
    }
}

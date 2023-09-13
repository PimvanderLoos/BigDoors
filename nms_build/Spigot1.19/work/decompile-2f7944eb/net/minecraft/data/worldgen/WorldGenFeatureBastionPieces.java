package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureBastionPieces {

    public static final Holder<WorldGenFeatureDefinedStructurePoolTemplate> START = WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/starts"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public WorldGenFeatureBastionPieces() {}

    public static void bootstrap() {
        WorldGenFeatureBastionUnits.bootstrap();
        WorldGenFeatureBastionHoglinStable.bootstrap();
        WorldGenFeatureBastionTreasure.bootstrap();
        WorldGenFeatureBastionBridge.bootstrap();
        WorldGenFeatureBastionExtra.bootstrap();
    }
}

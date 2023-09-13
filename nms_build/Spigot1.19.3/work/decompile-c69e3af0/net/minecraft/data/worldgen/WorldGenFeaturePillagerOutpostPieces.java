package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class WorldGenFeaturePillagerOutpostPieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> START = WorldGenFeaturePieces.createKey("pillager_outpost/base_plates");

    public WorldGenFeaturePillagerOutpostPieces() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext) {
        HolderGetter<ProcessorList> holdergetter = bootstapcontext.lookup(Registries.PROCESSOR_LIST);
        Holder<ProcessorList> holder = holdergetter.getOrThrow(ProcessorLists.OUTPOST_ROT);
        HolderGetter<WorldGenFeatureDefinedStructurePoolTemplate> holdergetter1 = bootstapcontext.lookup(Registries.TEMPLATE_POOL);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder1 = holdergetter1.getOrThrow(WorldGenFeaturePieces.EMPTY);

        bootstapcontext.register(WorldGenFeaturePillagerOutpostPieces.START, new WorldGenFeatureDefinedStructurePoolTemplate(holder1, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/base_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "pillager_outpost/towers", new WorldGenFeatureDefinedStructurePoolTemplate(holder1, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.list(ImmutableList.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/watchtower"), WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/watchtower_overgrown", holder))), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "pillager_outpost/feature_plates", new WorldGenFeatureDefinedStructurePoolTemplate(holder1, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_plate"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.register(bootstapcontext, "pillager_outpost/features", new WorldGenFeatureDefinedStructurePoolTemplate(holder1, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_cage_with_allays"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_logs"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("pillager_outpost/feature_targets"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 6)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}

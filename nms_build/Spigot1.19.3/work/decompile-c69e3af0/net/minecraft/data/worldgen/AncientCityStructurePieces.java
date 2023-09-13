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

public class AncientCityStructurePieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> START = WorldGenFeaturePieces.createKey("ancient_city/city_center");

    public AncientCityStructurePieces() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext) {
        HolderGetter<ProcessorList> holdergetter = bootstapcontext.lookup(Registries.PROCESSOR_LIST);
        Holder<ProcessorList> holder = holdergetter.getOrThrow(ProcessorLists.ANCIENT_CITY_START_DEGRADATION);
        HolderGetter<WorldGenFeatureDefinedStructurePoolTemplate> holdergetter1 = bootstapcontext.lookup(Registries.TEMPLATE_POOL);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder1 = holdergetter1.getOrThrow(WorldGenFeaturePieces.EMPTY);

        bootstapcontext.register(AncientCityStructurePieces.START, new WorldGenFeatureDefinedStructurePoolTemplate(holder1, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_1", holder), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_2", holder), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_3", holder), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        AncientCityStructurePools.bootstrap(bootstapcontext);
    }
}

package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class AncientCityStructurePieces {

    public static final Holder<WorldGenFeatureDefinedStructurePoolTemplate> START = WorldGenFeaturePieces.register(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("ancient_city/city_center"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_1", ProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_2", ProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.single("ancient_city/city_center/city_center_3", ProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public AncientCityStructurePieces() {}

    public static void bootstrap() {
        AncientCityStructurePools.bootstrap();
    }
}

package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface WorldGenFeatureDefinedStructurePools<P extends WorldGenFeatureDefinedStructurePoolStructure> {

    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolSingle> SINGLE = a("single_pool_element", WorldGenFeatureDefinedStructurePoolSingle.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolList> LIST = a("list_pool_element", WorldGenFeatureDefinedStructurePoolList.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolFeature> FEATURE = a("feature_pool_element", WorldGenFeatureDefinedStructurePoolFeature.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolEmpty> EMPTY = a("empty_pool_element", WorldGenFeatureDefinedStructurePoolEmpty.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolLegacySingle> LEGACY = a("legacy_single_pool_element", WorldGenFeatureDefinedStructurePoolLegacySingle.CODEC);

    Codec<P> codec();

    static <P extends WorldGenFeatureDefinedStructurePoolStructure> WorldGenFeatureDefinedStructurePools<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureDefinedStructurePools) IRegistry.a(IRegistry.STRUCTURE_POOL_ELEMENT, s, (Object) (() -> {
            return codec;
        }));
    }
}

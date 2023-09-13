package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface WorldGenFeatureDefinedStructurePools<P extends WorldGenFeatureDefinedStructurePoolStructure> {

    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolSingle> a = a("single_pool_element", WorldGenFeatureDefinedStructurePoolSingle.b);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolList> b = a("list_pool_element", WorldGenFeatureDefinedStructurePoolList.a);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolFeature> c = a("feature_pool_element", WorldGenFeatureDefinedStructurePoolFeature.a);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolEmpty> d = a("empty_pool_element", WorldGenFeatureDefinedStructurePoolEmpty.a);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolLegacySingle> e = a("legacy_single_pool_element", WorldGenFeatureDefinedStructurePoolLegacySingle.a);

    Codec<P> codec();

    static <P extends WorldGenFeatureDefinedStructurePoolStructure> WorldGenFeatureDefinedStructurePools<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureDefinedStructurePools) IRegistry.a(IRegistry.STRUCTURE_POOL_ELEMENT, s, (Object) (() -> {
            return codec;
        }));
    }
}

package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface WorldGenFeatureDefinedStructurePools<P extends WorldGenFeatureDefinedStructurePoolStructure> {

    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolSingle> SINGLE = register("single_pool_element", WorldGenFeatureDefinedStructurePoolSingle.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolList> LIST = register("list_pool_element", WorldGenFeatureDefinedStructurePoolList.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolFeature> FEATURE = register("feature_pool_element", WorldGenFeatureDefinedStructurePoolFeature.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolEmpty> EMPTY = register("empty_pool_element", WorldGenFeatureDefinedStructurePoolEmpty.CODEC);
    WorldGenFeatureDefinedStructurePools<WorldGenFeatureDefinedStructurePoolLegacySingle> LEGACY = register("legacy_single_pool_element", WorldGenFeatureDefinedStructurePoolLegacySingle.CODEC);

    Codec<P> codec();

    static <P extends WorldGenFeatureDefinedStructurePoolStructure> WorldGenFeatureDefinedStructurePools<P> register(String s, Codec<P> codec) {
        return (WorldGenFeatureDefinedStructurePools) IRegistry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, s, () -> {
            return codec;
        });
    }
}

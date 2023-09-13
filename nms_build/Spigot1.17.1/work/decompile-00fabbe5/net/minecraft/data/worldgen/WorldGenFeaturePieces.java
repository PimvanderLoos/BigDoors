package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeaturePieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> EMPTY = ResourceKey.a(IRegistry.TEMPLATE_POOL_REGISTRY, new MinecraftKey("empty"));
    private static final WorldGenFeatureDefinedStructurePoolTemplate BUILTIN_EMPTY = a(new WorldGenFeatureDefinedStructurePoolTemplate(WorldGenFeaturePieces.EMPTY.a(), WorldGenFeaturePieces.EMPTY.a(), ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public WorldGenFeaturePieces() {}

    public static WorldGenFeatureDefinedStructurePoolTemplate a(WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate) {
        return (WorldGenFeatureDefinedStructurePoolTemplate) RegistryGeneration.a(RegistryGeneration.TEMPLATE_POOL, worldgenfeaturedefinedstructurepooltemplate.b(), (Object) worldgenfeaturedefinedstructurepooltemplate);
    }

    public static WorldGenFeatureDefinedStructurePoolTemplate a() {
        WorldGenFeatureBastionPieces.a();
        WorldGenFeaturePillagerOutpostPieces.a();
        WorldGenFeatureVillages.a();
        return WorldGenFeaturePieces.BUILTIN_EMPTY;
    }

    static {
        a();
    }
}

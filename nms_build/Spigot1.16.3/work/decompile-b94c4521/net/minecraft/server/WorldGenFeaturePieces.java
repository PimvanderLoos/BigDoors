package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public class WorldGenFeaturePieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> a = ResourceKey.a(IRegistry.ax, new MinecraftKey("empty"));
    private static final WorldGenFeatureDefinedStructurePoolTemplate b = a(new WorldGenFeatureDefinedStructurePoolTemplate(WorldGenFeaturePieces.a.a(), WorldGenFeaturePieces.a.a(), ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public static WorldGenFeatureDefinedStructurePoolTemplate a(WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate) {
        return (WorldGenFeatureDefinedStructurePoolTemplate) RegistryGeneration.a(RegistryGeneration.h, worldgenfeaturedefinedstructurepooltemplate.b(), (Object) worldgenfeaturedefinedstructurepooltemplate);
    }

    public static WorldGenFeatureDefinedStructurePoolTemplate a() {
        WorldGenFeatureBastionPieces.a();
        WorldGenFeaturePillagerOutpostPieces.a();
        WorldGenFeatureVillages.a();
        return WorldGenFeaturePieces.b;
    }

    static {
        a();
    }
}

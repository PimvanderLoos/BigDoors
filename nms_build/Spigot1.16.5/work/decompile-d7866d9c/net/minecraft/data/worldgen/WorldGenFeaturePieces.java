package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

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

package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeaturePieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> EMPTY = ResourceKey.create(IRegistry.TEMPLATE_POOL_REGISTRY, new MinecraftKey("empty"));
    private static final Holder<WorldGenFeatureDefinedStructurePoolTemplate> BUILTIN_EMPTY = register(new WorldGenFeatureDefinedStructurePoolTemplate(WorldGenFeaturePieces.EMPTY.location(), WorldGenFeaturePieces.EMPTY.location(), ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));

    public WorldGenFeaturePieces() {}

    public static Holder<WorldGenFeatureDefinedStructurePoolTemplate> register(WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate) {
        return RegistryGeneration.register(RegistryGeneration.TEMPLATE_POOL, worldgenfeaturedefinedstructurepooltemplate.getName(), worldgenfeaturedefinedstructurepooltemplate);
    }

    /** @deprecated */
    @Deprecated
    public static void forceBootstrap() {
        bootstrap(RegistryGeneration.TEMPLATE_POOL);
    }

    public static Holder<WorldGenFeatureDefinedStructurePoolTemplate> bootstrap(IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry) {
        WorldGenFeatureBastionPieces.bootstrap();
        WorldGenFeaturePillagerOutpostPieces.bootstrap();
        WorldGenFeatureVillages.bootstrap();
        AncientCityStructurePieces.bootstrap();
        return WorldGenFeaturePieces.BUILTIN_EMPTY;
    }
}

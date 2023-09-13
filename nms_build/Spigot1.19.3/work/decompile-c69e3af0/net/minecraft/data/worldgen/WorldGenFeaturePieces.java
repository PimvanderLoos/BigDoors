package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeaturePieces {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> EMPTY = createKey("empty");

    public WorldGenFeaturePieces() {}

    public static ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> createKey(String s) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new MinecraftKey(s));
    }

    public static void register(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext, String s, WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate) {
        bootstapcontext.register(createKey(s), worldgenfeaturedefinedstructurepooltemplate);
    }

    public static void bootstrap(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext) {
        HolderGetter<WorldGenFeatureDefinedStructurePoolTemplate> holdergetter = bootstapcontext.lookup(Registries.TEMPLATE_POOL);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder = holdergetter.getOrThrow(WorldGenFeaturePieces.EMPTY);

        bootstapcontext.register(WorldGenFeaturePieces.EMPTY, new WorldGenFeatureDefinedStructurePoolTemplate(holder, ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeatureBastionPieces.bootstrap(bootstapcontext);
        WorldGenFeaturePillagerOutpostPieces.bootstrap(bootstapcontext);
        WorldGenFeatureVillages.bootstrap(bootstapcontext);
        AncientCityStructurePieces.bootstrap(bootstapcontext);
    }
}

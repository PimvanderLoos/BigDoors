package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

public class WorldGenFeatureBastionExtra {

    public static final DefinedStructureProcessorPredicates a = new DefinedStructureProcessorPredicates(new DefinedStructureTestRandomBlock(Blocks.BLACKSTONE, 0.01F), DefinedStructureTestTrue.b, Blocks.GILDED_BLACKSTONE.getBlockData());

    public static void a() {}

    static {
        WorldGenFeatureDefinedStructureJigsawPlacement.a.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/mobs/piglin"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/melee_piglin"), 1), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/sword_piglin"), 4), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/crossbow_piglin"), 4), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/empty"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeatureDefinedStructureJigsawPlacement.a.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/mobs/hoglin"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/hoglin"), 2), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/empty"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeatureDefinedStructureJigsawPlacement.a.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/blocks/gold"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/blocks/air"), 3), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/blocks/gold"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeatureDefinedStructureJigsawPlacement.a.a(new WorldGenFeatureDefinedStructurePoolTemplate(new MinecraftKey("bastion/mobs/piglin_melee"), new MinecraftKey("empty"), ImmutableList.of(Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/melee_piglin_always"), 1), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/melee_piglin"), 5), Pair.of(new WorldGenFeatureDefinedStructurePoolSingle("bastion/mobs/sword_piglin"), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}

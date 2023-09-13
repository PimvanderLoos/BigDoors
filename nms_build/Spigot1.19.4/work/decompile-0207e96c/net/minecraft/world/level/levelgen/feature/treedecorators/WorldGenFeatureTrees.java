package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class WorldGenFeatureTrees<P extends WorldGenFeatureTree> {

    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineTrunk> TRUNK_VINE = register("trunk_vine", WorldGenFeatureTreeVineTrunk.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineLeaves> LEAVE_VINE = register("leave_vine", WorldGenFeatureTreeVineLeaves.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeCocoa> COCOA = register("cocoa", WorldGenFeatureTreeCocoa.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeBeehive> BEEHIVE = register("beehive", WorldGenFeatureTreeBeehive.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeAlterGround> ALTER_GROUND = register("alter_ground", WorldGenFeatureTreeAlterGround.CODEC);
    public static final WorldGenFeatureTrees<AttachedToLeavesDecorator> ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesDecorator.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFeatureTree> WorldGenFeatureTrees<P> register(String s, Codec<P> codec) {
        return (WorldGenFeatureTrees) IRegistry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, s, new WorldGenFeatureTrees<>(codec));
    }

    private WorldGenFeatureTrees(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> codec() {
        return this.codec;
    }
}

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFeatureTrees<P extends WorldGenFeatureTree> {

    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineTrunk> TRUNK_VINE = a("trunk_vine", WorldGenFeatureTreeVineTrunk.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineLeaves> LEAVE_VINE = a("leave_vine", WorldGenFeatureTreeVineLeaves.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeCocoa> COCOA = a("cocoa", WorldGenFeatureTreeCocoa.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeBeehive> BEEHIVE = a("beehive", WorldGenFeatureTreeBeehive.CODEC);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeAlterGround> ALTER_GROUND = a("alter_ground", WorldGenFeatureTreeAlterGround.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFeatureTree> WorldGenFeatureTrees<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureTrees) IRegistry.a(IRegistry.TREE_DECORATOR_TYPES, s, (Object) (new WorldGenFeatureTrees<>(codec)));
    }

    private WorldGenFeatureTrees(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}

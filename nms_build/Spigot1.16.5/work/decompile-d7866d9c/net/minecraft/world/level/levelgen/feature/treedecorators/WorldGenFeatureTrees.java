package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFeatureTrees<P extends WorldGenFeatureTree> {

    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineTrunk> a = a("trunk_vine", WorldGenFeatureTreeVineTrunk.a);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineLeaves> b = a("leave_vine", WorldGenFeatureTreeVineLeaves.a);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeCocoa> c = a("cocoa", WorldGenFeatureTreeCocoa.a);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeBeehive> d = a("beehive", WorldGenFeatureTreeBeehive.a);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeAlterGround> e = a("alter_ground", WorldGenFeatureTreeAlterGround.a);
    private final Codec<P> f;

    private static <P extends WorldGenFeatureTree> WorldGenFeatureTrees<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureTrees) IRegistry.a(IRegistry.TREE_DECORATOR_TYPE, s, (Object) (new WorldGenFeatureTrees<>(codec)));
    }

    private WorldGenFeatureTrees(Codec<P> codec) {
        this.f = codec;
    }

    public Codec<P> a() {
        return this.f;
    }
}

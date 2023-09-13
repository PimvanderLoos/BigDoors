package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureTrees<P extends WorldGenFeatureTree> {

    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineTrunk> a = a("trunk_vine", WorldGenFeatureTreeVineTrunk::new);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeVineLeaves> b = a("leave_vine", WorldGenFeatureTreeVineLeaves::new);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeCocoa> c = a("cocoa", WorldGenFeatureTreeCocoa::new);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeBeehive> d = a("beehive", WorldGenFeatureTreeBeehive::new);
    public static final WorldGenFeatureTrees<WorldGenFeatureTreeAlterGround> e = a("alter_ground", WorldGenFeatureTreeAlterGround::new);
    private final Function<Dynamic<?>, P> f;

    private static <P extends WorldGenFeatureTree> WorldGenFeatureTrees<P> a(String s, Function<Dynamic<?>, P> function) {
        return (WorldGenFeatureTrees) IRegistry.a(IRegistry.w, s, (Object) (new WorldGenFeatureTrees<>(function)));
    }

    private WorldGenFeatureTrees(Function<Dynamic<?>, P> function) {
        this.f = function;
    }

    public P a(Dynamic<?> dynamic) {
        return (WorldGenFeatureTree) this.f.apply(dynamic);
    }
}

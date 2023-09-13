package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFoilagePlacers<P extends WorldGenFoilagePlacer> {

    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBlob> a = a("blob_foliage_placer", WorldGenFoilagePlacerBlob::new);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerSpruce> b = a("spruce_foliage_placer", WorldGenFoilagePlacerSpruce::new);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerPine> c = a("pine_foliage_placer", WorldGenFoilagePlacerPine::new);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerAcacia> d = a("acacia_foliage_placer", WorldGenFoilagePlacerAcacia::new);
    private final Function<Dynamic<?>, P> e;

    private static <P extends WorldGenFoilagePlacer> WorldGenFoilagePlacers<P> a(String s, Function<Dynamic<?>, P> function) {
        return (WorldGenFoilagePlacers) IRegistry.a(IRegistry.v, s, (Object) (new WorldGenFoilagePlacers<>(function)));
    }

    private WorldGenFoilagePlacers(Function<Dynamic<?>, P> function) {
        this.e = function;
    }

    public P a(Dynamic<?> dynamic) {
        return (WorldGenFoilagePlacer) this.e.apply(dynamic);
    }
}

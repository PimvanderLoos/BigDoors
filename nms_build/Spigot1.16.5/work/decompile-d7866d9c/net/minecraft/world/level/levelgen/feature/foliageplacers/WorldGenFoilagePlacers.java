package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFoilagePlacers<P extends WorldGenFoilagePlacer> {

    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBlob> a = a("blob_foliage_placer", WorldGenFoilagePlacerBlob.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerSpruce> b = a("spruce_foliage_placer", WorldGenFoilagePlacerSpruce.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerPine> c = a("pine_foliage_placer", WorldGenFoilagePlacerPine.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerAcacia> d = a("acacia_foliage_placer", WorldGenFoilagePlacerAcacia.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBush> e = a("bush_foliage_placer", WorldGenFoilagePlacerBush.c);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerFancy> f = a("fancy_foliage_placer", WorldGenFoilagePlacerFancy.c);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerJungle> g = a("jungle_foliage_placer", WorldGenFoilagePlacerJungle.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerMegaPine> h = a("mega_pine_foliage_placer", WorldGenFoilagePlacerMegaPine.a);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerDarkOak> i = a("dark_oak_foliage_placer", WorldGenFoilagePlacerDarkOak.a);
    private final Codec<P> j;

    private static <P extends WorldGenFoilagePlacer> WorldGenFoilagePlacers<P> a(String s, Codec<P> codec) {
        return (WorldGenFoilagePlacers) IRegistry.a(IRegistry.FOLIAGE_PLACER_TYPE, s, (Object) (new WorldGenFoilagePlacers<>(codec)));
    }

    private WorldGenFoilagePlacers(Codec<P> codec) {
        this.j = codec;
    }

    public Codec<P> a() {
        return this.j;
    }
}

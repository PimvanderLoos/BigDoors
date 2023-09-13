package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFoilagePlacers<P extends WorldGenFoilagePlacer> {

    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBlob> BLOB_FOLIAGE_PLACER = a("blob_foliage_placer", WorldGenFoilagePlacerBlob.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerSpruce> SPRUCE_FOLIAGE_PLACER = a("spruce_foliage_placer", WorldGenFoilagePlacerSpruce.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerPine> PINE_FOLIAGE_PLACER = a("pine_foliage_placer", WorldGenFoilagePlacerPine.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerAcacia> ACACIA_FOLIAGE_PLACER = a("acacia_foliage_placer", WorldGenFoilagePlacerAcacia.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBush> BUSH_FOLIAGE_PLACER = a("bush_foliage_placer", WorldGenFoilagePlacerBush.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerFancy> FANCY_FOLIAGE_PLACER = a("fancy_foliage_placer", WorldGenFoilagePlacerFancy.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerJungle> MEGA_JUNGLE_FOLIAGE_PLACER = a("jungle_foliage_placer", WorldGenFoilagePlacerJungle.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerMegaPine> MEGA_PINE_FOLIAGE_PLACER = a("mega_pine_foliage_placer", WorldGenFoilagePlacerMegaPine.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerDarkOak> DARK_OAK_FOLIAGE_PLACER = a("dark_oak_foliage_placer", WorldGenFoilagePlacerDarkOak.CODEC);
    public static final WorldGenFoilagePlacers<RandomSpreadFoliagePlacer> RANDOM_SPREAD_FOLIAGE_PLACER = a("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFoilagePlacer> WorldGenFoilagePlacers<P> a(String s, Codec<P> codec) {
        return (WorldGenFoilagePlacers) IRegistry.a(IRegistry.FOLIAGE_PLACER_TYPES, s, (Object) (new WorldGenFoilagePlacers<>(codec)));
    }

    private WorldGenFoilagePlacers(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}

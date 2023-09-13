package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class WorldGenFoilagePlacers<P extends WorldGenFoilagePlacer> {

    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBlob> BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", WorldGenFoilagePlacerBlob.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerSpruce> SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", WorldGenFoilagePlacerSpruce.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerPine> PINE_FOLIAGE_PLACER = register("pine_foliage_placer", WorldGenFoilagePlacerPine.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerAcacia> ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", WorldGenFoilagePlacerAcacia.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerBush> BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", WorldGenFoilagePlacerBush.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerFancy> FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", WorldGenFoilagePlacerFancy.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerJungle> MEGA_JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", WorldGenFoilagePlacerJungle.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerMegaPine> MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", WorldGenFoilagePlacerMegaPine.CODEC);
    public static final WorldGenFoilagePlacers<WorldGenFoilagePlacerDarkOak> DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", WorldGenFoilagePlacerDarkOak.CODEC);
    public static final WorldGenFoilagePlacers<RandomSpreadFoliagePlacer> RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
    public static final WorldGenFoilagePlacers<CherryFoliagePlacer> CHERRY_FOLIAGE_PLACER = register("cherry_foliage_placer", CherryFoliagePlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFoilagePlacer> WorldGenFoilagePlacers<P> register(String s, Codec<P> codec) {
        return (WorldGenFoilagePlacers) IRegistry.register(BuiltInRegistries.FOLIAGE_PLACER_TYPE, s, new WorldGenFoilagePlacers<>(codec));
    }

    private WorldGenFoilagePlacers(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> codec() {
        return this.codec;
    }
}

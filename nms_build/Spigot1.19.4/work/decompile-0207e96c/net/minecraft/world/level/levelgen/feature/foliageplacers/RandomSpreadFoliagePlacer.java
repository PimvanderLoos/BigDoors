package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class RandomSpreadFoliagePlacer extends WorldGenFoilagePlacer {

    public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(IntProvider.codec(1, 512).fieldOf("foliage_height").forGetter((randomspreadfoliageplacer) -> {
            return randomspreadfoliageplacer.foliageHeight;
        }), Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter((randomspreadfoliageplacer) -> {
            return randomspreadfoliageplacer.leafPlacementAttempts;
        }))).apply(instance, RandomSpreadFoliagePlacer::new);
    });
    private final IntProvider foliageHeight;
    private final int leafPlacementAttempts;

    public RandomSpreadFoliagePlacer(IntProvider intprovider, IntProvider intprovider1, IntProvider intprovider2, int i) {
        super(intprovider, intprovider1);
        this.foliageHeight = intprovider2;
        this.leafPlacementAttempts = i;
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.RANDOM_SPREAD_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.pos();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i1 = 0; i1 < this.leafPlacementAttempts; ++i1) {
            blockposition_mutableblockposition.setWithOffset(blockposition, randomsource.nextInt(k) - randomsource.nextInt(k), randomsource.nextInt(j) - randomsource.nextInt(j), randomsource.nextInt(k) - randomsource.nextInt(k));
            tryPlaceLeaf(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition_mutableblockposition);
        }

    }

    @Override
    public int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.foliageHeight.sample(randomsource);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return false;
    }
}

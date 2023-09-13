package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerBush extends WorldGenFoilagePlacerBlob {

    public static final Codec<WorldGenFoilagePlacerBush> CODEC = RecordCodecBuilder.create((instance) -> {
        return blobParts(instance).apply(instance, WorldGenFoilagePlacerBush::new);
    });

    public WorldGenFoilagePlacerBush(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1, i);
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.BUSH_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = k + worldgenfoilageplacer_a.radiusOffset() - 1 - i1;

            this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.pos(), j1, i1, worldgenfoilageplacer_a.doubleTrunk());
        }

    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && randomsource.nextInt(2) == 0;
    }
}

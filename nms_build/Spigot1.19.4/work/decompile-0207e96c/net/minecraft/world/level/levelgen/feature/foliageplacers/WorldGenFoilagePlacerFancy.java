package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerFancy extends WorldGenFoilagePlacerBlob {

    public static final Codec<WorldGenFoilagePlacerFancy> CODEC = RecordCodecBuilder.create((instance) -> {
        return blobParts(instance).apply(instance, WorldGenFoilagePlacerFancy::new);
    });

    public WorldGenFoilagePlacerFancy(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1, i);
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.FANCY_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = k + (i1 != l && i1 != l - j ? 1 : 0);

            this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.pos(), j1, i1, worldgenfoilageplacer_a.doubleTrunk());
        }

    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return MathHelper.square((float) i + 0.5F) + MathHelper.square((float) k + 0.5F) > (float) (l * l);
    }
}

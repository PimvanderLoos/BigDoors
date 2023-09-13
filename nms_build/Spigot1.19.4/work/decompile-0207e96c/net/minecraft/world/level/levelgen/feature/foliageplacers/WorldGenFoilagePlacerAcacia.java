package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerAcacia extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerAcacia> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).apply(instance, WorldGenFoilagePlacerAcacia::new);
    });

    public WorldGenFoilagePlacerAcacia(IntProvider intprovider, IntProvider intprovider1) {
        super(intprovider, intprovider1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        boolean flag = worldgenfoilageplacer_a.doubleTrunk();
        BlockPosition blockposition = worldgenfoilageplacer_a.pos().above(l);

        this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_a.radiusOffset(), -1 - j, flag);
        this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, k - 1, -j, flag);
        this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_a.radiusOffset() - 1, 0, flag);
    }

    @Override
    public int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return j == 0 ? (i > 1 || k > 1) && i != 0 && k != 0 : i == l && k == l && l > 0;
    }
}

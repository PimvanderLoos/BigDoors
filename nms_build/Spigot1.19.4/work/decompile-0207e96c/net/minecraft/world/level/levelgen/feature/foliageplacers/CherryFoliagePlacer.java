package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class CherryFoliagePlacer extends WorldGenFoilagePlacer {

    public static final Codec<CherryFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).and(instance.group(IntProvider.codec(4, 16).fieldOf("height").forGetter((cherryfoliageplacer) -> {
            return cherryfoliageplacer.height;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("wide_bottom_layer_hole_chance").forGetter((cherryfoliageplacer) -> {
            return cherryfoliageplacer.wideBottomLayerHoleChance;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("corner_hole_chance").forGetter((cherryfoliageplacer) -> {
            return cherryfoliageplacer.wideBottomLayerHoleChance;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_chance").forGetter((cherryfoliageplacer) -> {
            return cherryfoliageplacer.hangingLeavesChance;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_extension_chance").forGetter((cherryfoliageplacer) -> {
            return cherryfoliageplacer.hangingLeavesExtensionChance;
        }))).apply(instance, CherryFoliagePlacer::new);
    });
    private final IntProvider height;
    private final float wideBottomLayerHoleChance;
    private final float cornerHoleChance;
    private final float hangingLeavesChance;
    private final float hangingLeavesExtensionChance;

    public CherryFoliagePlacer(IntProvider intprovider, IntProvider intprovider1, IntProvider intprovider2, float f, float f1, float f2, float f3) {
        super(intprovider, intprovider1);
        this.height = intprovider2;
        this.wideBottomLayerHoleChance = f;
        this.cornerHoleChance = f1;
        this.hangingLeavesChance = f2;
        this.hangingLeavesExtensionChance = f3;
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.CHERRY_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        boolean flag = worldgenfoilageplacer_a.doubleTrunk();
        BlockPosition blockposition = worldgenfoilageplacer_a.pos().above(l);
        int i1 = k + worldgenfoilageplacer_a.radiusOffset() - 1;

        this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, i1 - 2, j - 3, flag);
        this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, i1 - 1, j - 4, flag);

        for (int j1 = j - 5; j1 >= 0; --j1) {
            this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, i1, j1, flag);
        }

        this.placeLeavesRowWithHangingLeavesBelow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, i1, -1, flag, this.hangingLeavesChance, this.hangingLeavesExtensionChance);
        this.placeLeavesRowWithHangingLeavesBelow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, blockposition, i1 - 1, -2, flag, this.hangingLeavesChance, this.hangingLeavesExtensionChance);
    }

    @Override
    public int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.height.sample(randomsource);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        if (j == -1 && (i == l || k == l) && randomsource.nextFloat() < this.wideBottomLayerHoleChance) {
            return true;
        } else {
            boolean flag1 = i == l && k == l;
            boolean flag2 = l > 2;

            return flag2 ? flag1 || i + k > l * 2 - 2 && randomsource.nextFloat() < this.cornerHoleChance : flag1 && randomsource.nextFloat() < this.cornerHoleChance;
        }
    }
}

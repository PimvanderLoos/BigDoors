package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerMegaPine extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerMegaPine> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).and(IntProvider.codec(0, 24).fieldOf("crown_height").forGetter((worldgenfoilageplacermegapine) -> {
            return worldgenfoilageplacermegapine.crownHeight;
        })).apply(instance, WorldGenFoilagePlacerMegaPine::new);
    });
    private final IntProvider crownHeight;

    public WorldGenFoilagePlacerMegaPine(IntProvider intprovider, IntProvider intprovider1, IntProvider intprovider2) {
        super(intprovider, intprovider1);
        this.crownHeight = intprovider2;
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.pos();
        int i1 = 0;

        for (int j1 = blockposition.getY() - j + l; j1 <= blockposition.getY() + l; ++j1) {
            int k1 = blockposition.getY() - j1;
            int l1 = k + worldgenfoilageplacer_a.radiusOffset() + MathHelper.floor((float) k1 / (float) j * 3.5F);
            int i2;

            if (k1 > 0 && l1 == i1 && (j1 & 1) == 0) {
                i2 = l1 + 1;
            } else {
                i2 = l1;
            }

            this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, new BlockPosition(blockposition.getX(), j1, blockposition.getZ()), i2, 0, worldgenfoilageplacer_a.doubleTrunk());
            i1 = l1;
        }

    }

    @Override
    public int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.crownHeight.sample(randomsource);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return i + k >= 7 ? true : i * i + k * k > l * l;
    }
}

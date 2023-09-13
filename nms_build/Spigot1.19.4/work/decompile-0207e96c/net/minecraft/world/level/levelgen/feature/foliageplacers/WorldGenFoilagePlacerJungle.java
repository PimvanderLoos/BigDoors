package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerJungle extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerJungle> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter((worldgenfoilageplacerjungle) -> {
            return worldgenfoilageplacerjungle.height;
        })).apply(instance, WorldGenFoilagePlacerJungle::new);
    });
    protected final int height;

    public WorldGenFoilagePlacerJungle(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1);
        this.height = i;
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.MEGA_JUNGLE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, RandomSource randomsource, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        int i1 = worldgenfoilageplacer_a.doubleTrunk() ? j : 1 + randomsource.nextInt(2);

        for (int j1 = l; j1 >= l - i1; --j1) {
            int k1 = k + worldgenfoilageplacer_a.radiusOffset() + 1 - j1;

            this.placeLeavesRow(virtuallevelreadable, worldgenfoilageplacer_b, randomsource, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.pos(), k1, j1, worldgenfoilageplacer_a.doubleTrunk());
        }

    }

    @Override
    public int foliageHeight(RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.height;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource randomsource, int i, int j, int k, int l, boolean flag) {
        return i + k >= 7 ? true : i * i + k * k > l * l;
    }
}

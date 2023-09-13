package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerDarkOak extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerDarkOak> CODEC = RecordCodecBuilder.create((instance) -> {
        return foliagePlacerParts(instance).apply(instance, WorldGenFoilagePlacerDarkOak::new);
    });

    public WorldGenFoilagePlacerDarkOak(IntProvider intprovider, IntProvider intprovider1) {
        super(intprovider, intprovider1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> type() {
        return WorldGenFoilagePlacers.DARK_OAK_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.pos().above(l);
        boolean flag = worldgenfoilageplacer_a.doubleTrunk();

        if (flag) {
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, -1, flag);
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 3, 0, flag);
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, 1, flag);
            if (random.nextBoolean()) {
                this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k, 2, flag);
            }
        } else {
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, -1, flag);
            this.placeLeavesRow(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 1, 0, flag);
        }

    }

    @Override
    public int foliageHeight(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 4;
    }

    @Override
    protected boolean shouldSkipLocationSigned(Random random, int i, int j, int k, int l, boolean flag) {
        return j == 0 && flag && (i == -l || i >= l) && (k == -l || k >= l) ? true : super.shouldSkipLocationSigned(random, i, j, k, l, flag);
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int i, int j, int k, int l, boolean flag) {
        return j == -1 && !flag ? i == l && k == l : (j == 1 ? i + k > l * 2 - 2 : false);
    }
}

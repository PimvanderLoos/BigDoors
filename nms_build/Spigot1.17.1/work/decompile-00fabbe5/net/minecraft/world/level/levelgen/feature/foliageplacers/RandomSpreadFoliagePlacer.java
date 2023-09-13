package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class RandomSpreadFoliagePlacer extends WorldGenFoilagePlacer {

    public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(instance.group(IntProvider.b(1, 512).fieldOf("foliage_height").forGetter((randomspreadfoliageplacer) -> {
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
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.RANDOM_SPREAD_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.a();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i1 = 0; i1 < this.leafPlacementAttempts; ++i1) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k));
            a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition_mutableblockposition);
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.foliageHeight.a(random);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return false;
    }
}

package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {

    public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).and(instance.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", 1).forGetter((bendingtrunkplacer) -> {
            return bendingtrunkplacer.minHeightForLeaves;
        }), IntProvider.b(1, 64).fieldOf("bend_length").forGetter((bendingtrunkplacer) -> {
            return bendingtrunkplacer.bendLength;
        }))).apply(instance, BendingTrunkPlacer::new);
    });
    private final int minHeightForLeaves;
    private final IntProvider bendLength;

    public BendingTrunkPlacer(int i, int j, int k, int l, IntProvider intprovider) {
        super(i, j, k);
        this.minHeightForLeaves = l;
        this.bendLength = intprovider;
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.BENDING_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
        int j = i - 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        BlockPosition blockposition1 = blockposition_mutableblockposition.down();

        a(virtuallevelreadable, biconsumer, random, blockposition1, worldgenfeaturetreeconfiguration);
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();

        int k;

        for (k = 0; k <= j; ++k) {
            if (k + 1 >= j + random.nextInt(2)) {
                blockposition_mutableblockposition.c(enumdirection);
            }

            if (WorldGenTrees.e(virtuallevelreadable, blockposition_mutableblockposition)) {
                b(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
            }

            if (k >= this.minHeightForLeaves) {
                list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.immutableCopy(), 0, false));
            }

            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        k = this.bendLength.a(random);

        for (int l = 0; l <= k; ++l) {
            if (WorldGenTrees.e(virtuallevelreadable, blockposition_mutableblockposition)) {
                b(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
            }

            list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.immutableCopy(), 0, false));
            blockposition_mutableblockposition.c(enumdirection);
        }

        return list;
    }
}

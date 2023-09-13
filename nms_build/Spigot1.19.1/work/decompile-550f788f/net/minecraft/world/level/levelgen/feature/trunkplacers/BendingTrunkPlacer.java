package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {

    public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).and(instance.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", 1).forGetter((bendingtrunkplacer) -> {
            return bendingtrunkplacer.minHeightForLeaves;
        }), IntProvider.codec(1, 64).fieldOf("bend_length").forGetter((bendingtrunkplacer) -> {
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
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.BENDING_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource);
        int j = i - 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        BlockPosition blockposition1 = blockposition_mutableblockposition.below();

        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1, worldgenfeaturetreeconfiguration);
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();

        int k;

        for (k = 0; k <= j; ++k) {
            if (k + 1 >= j + randomsource.nextInt(2)) {
                blockposition_mutableblockposition.move(enumdirection);
            }

            if (WorldGenTrees.validTreePos(virtuallevelreadable, blockposition_mutableblockposition)) {
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
            }

            if (k >= this.minHeightForLeaves) {
                list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.immutable(), 0, false));
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        k = this.bendLength.sample(randomsource);

        for (int l = 0; l <= k; ++l) {
            if (WorldGenTrees.validTreePos(virtuallevelreadable, blockposition_mutableblockposition)) {
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
            }

            list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.immutable(), 0, false));
            blockposition_mutableblockposition.move(enumdirection);
        }

        return list;
    }
}

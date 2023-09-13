package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerGiant extends TrunkPlacer {

    public static final Codec<TrunkPlacerGiant> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).apply(instance, TrunkPlacerGiant::new);
    });

    public TrunkPlacerGiant(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.GIANT_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        BlockPosition blockposition1 = blockposition.below();

        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1, worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.east(), worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.south(), worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.south().east(), worldgenfeaturetreeconfiguration);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            this.placeLogIfFreeWithOffset(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 0, j, 0);
            if (j < i - 1) {
                this.placeLogIfFreeWithOffset(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 1, j, 0);
                this.placeLogIfFreeWithOffset(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 1, j, 1);
                this.placeLogIfFreeWithOffset(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 0, j, 1);
            }
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.a(blockposition.above(i), 0, true));
    }

    private void placeLogIfFreeWithOffset(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, int j, int k) {
        blockposition_mutableblockposition.setWithOffset(blockposition, i, j, k);
        this.placeLogIfFree(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
    }
}

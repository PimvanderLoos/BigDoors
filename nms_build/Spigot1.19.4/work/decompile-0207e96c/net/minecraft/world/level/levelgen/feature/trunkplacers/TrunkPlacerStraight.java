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

public class TrunkPlacerStraight extends TrunkPlacer {

    public static final Codec<TrunkPlacerStraight> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).apply(instance, TrunkPlacerStraight::new);
    });

    public TrunkPlacerStraight(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.STRAIGHT_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition.below(), worldgenfeaturetreeconfiguration);

        for (int j = 0; j < i; ++j) {
            this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition.above(j), worldgenfeaturetreeconfiguration);
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.a(blockposition.above(i), 0, false));
    }
}

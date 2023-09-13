package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerStraight extends TrunkPlacer {

    public static final Codec<TrunkPlacerStraight> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerStraight::new);
    });

    public TrunkPlacerStraight(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.STRAIGHT_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        a(virtuallevelreadable, biconsumer, random, blockposition.down(), worldgenfeaturetreeconfiguration);

        for (int j = 0; j < i; ++j) {
            b(virtuallevelreadable, biconsumer, random, blockposition.up(j), worldgenfeaturetreeconfiguration);
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.a(blockposition.up(i), 0, false));
    }
}

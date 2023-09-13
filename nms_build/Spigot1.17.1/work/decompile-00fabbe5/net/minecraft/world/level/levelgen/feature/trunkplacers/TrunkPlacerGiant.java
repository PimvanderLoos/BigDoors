package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerGiant extends TrunkPlacer {

    public static final Codec<TrunkPlacerGiant> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerGiant::new);
    });

    public TrunkPlacerGiant(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.GIANT_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        BlockPosition blockposition1 = blockposition.down();

        a(virtuallevelreadable, biconsumer, random, blockposition1, worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.east(), worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.south(), worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.south().east(), worldgenfeaturetreeconfiguration);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            a(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 0, j, 0);
            if (j < i - 1) {
                a(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 1, j, 0);
                a(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 1, j, 1);
                a(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, blockposition, 0, j, 1);
            }
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.a(blockposition.up(i), 0, true));
    }

    private static void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, int j, int k) {
        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, i, j, k);
        a(virtuallevelreadable, biconsumer, random, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration);
    }
}

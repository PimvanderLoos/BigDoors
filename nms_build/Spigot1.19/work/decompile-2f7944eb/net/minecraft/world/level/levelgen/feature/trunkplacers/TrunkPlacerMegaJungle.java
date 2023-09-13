package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerMegaJungle extends TrunkPlacerGiant {

    public static final Codec<TrunkPlacerMegaJungle> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).apply(instance, TrunkPlacerMegaJungle::new);
    });

    public TrunkPlacerMegaJungle(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();

        list.addAll(super.placeTrunk(virtuallevelreadable, biconsumer, randomsource, i, blockposition, worldgenfeaturetreeconfiguration));

        for (int j = i - 2 - randomsource.nextInt(4); j > i / 2; j -= 2 + randomsource.nextInt(4)) {
            float f = randomsource.nextFloat() * 6.2831855F;
            int k = 0;
            int l = 0;

            for (int i1 = 0; i1 < 5; ++i1) {
                k = (int) (1.5F + MathHelper.cos(f) * (float) i1);
                l = (int) (1.5F + MathHelper.sin(f) * (float) i1);
                BlockPosition blockposition1 = blockposition.offset(k, j - 3 + i1 / 2, l);

                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition1, worldgenfeaturetreeconfiguration);
            }

            list.add(new WorldGenFoilagePlacer.a(blockposition.offset(k, j, l), -2, false));
        }

        return list;
    }
}

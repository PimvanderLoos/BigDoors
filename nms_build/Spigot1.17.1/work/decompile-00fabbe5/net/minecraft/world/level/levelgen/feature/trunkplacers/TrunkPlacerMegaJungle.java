package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerMegaJungle extends TrunkPlacerGiant {

    public static final Codec<TrunkPlacerMegaJungle> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerMegaJungle::new);
    });

    public TrunkPlacerMegaJungle(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();

        list.addAll(super.a(virtuallevelreadable, biconsumer, random, i, blockposition, worldgenfeaturetreeconfiguration));

        for (int j = i - 2 - random.nextInt(4); j > i / 2; j -= 2 + random.nextInt(4)) {
            float f = random.nextFloat() * 6.2831855F;
            int k = 0;
            int l = 0;

            for (int i1 = 0; i1 < 5; ++i1) {
                k = (int) (1.5F + MathHelper.cos(f) * (float) i1);
                l = (int) (1.5F + MathHelper.sin(f) * (float) i1);
                BlockPosition blockposition1 = blockposition.c(k, j - 3 + i1 / 2, l);

                b(virtuallevelreadable, biconsumer, random, blockposition1, worldgenfeaturetreeconfiguration);
            }

            list.add(new WorldGenFoilagePlacer.a(blockposition.c(k, j, l), -2, false));
        }

        return list;
    }
}

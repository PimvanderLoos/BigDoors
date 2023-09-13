package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerMegaJungle extends TrunkPlacerGiant {

    public static final Codec<TrunkPlacerMegaJungle> b = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerMegaJungle::new);
    });

    public TrunkPlacerMegaJungle(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.d;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.b> list = Lists.newArrayList();

        list.addAll(super.a(virtuallevelwritable, random, i, blockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration));

        for (int j = i - 2 - random.nextInt(4); j > i / 2; j -= 2 + random.nextInt(4)) {
            float f = random.nextFloat() * 6.2831855F;
            int k = 0;
            int l = 0;

            for (int i1 = 0; i1 < 5; ++i1) {
                k = (int) (1.5F + MathHelper.cos(f) * (float) i1);
                l = (int) (1.5F + MathHelper.sin(f) * (float) i1);
                BlockPosition blockposition1 = blockposition.b(k, j - 3 + i1 / 2, l);

                a(virtuallevelwritable, random, blockposition1, set, structureboundingbox, worldgenfeaturetreeconfiguration);
            }

            list.add(new WorldGenFoilagePlacer.b(blockposition.b(k, j, l), -2, false));
        }

        return list;
    }
}

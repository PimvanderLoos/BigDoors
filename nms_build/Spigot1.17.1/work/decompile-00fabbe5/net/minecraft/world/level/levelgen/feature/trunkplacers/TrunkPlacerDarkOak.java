package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerDarkOak extends TrunkPlacer {

    public static final Codec<TrunkPlacerDarkOak> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerDarkOak::new);
    });

    public TrunkPlacerDarkOak(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();
        BlockPosition blockposition1 = blockposition.down();

        a(virtuallevelreadable, biconsumer, random, blockposition1, worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.east(), worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.south(), worldgenfeaturetreeconfiguration);
        a(virtuallevelreadable, biconsumer, random, blockposition1.south().east(), worldgenfeaturetreeconfiguration);
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
        int j = i - random.nextInt(4);
        int k = 2 - random.nextInt(3);
        int l = blockposition.getX();
        int i1 = blockposition.getY();
        int j1 = blockposition.getZ();
        int k1 = l;
        int l1 = j1;
        int i2 = i1 + i - 1;

        int j2;
        int k2;

        for (j2 = 0; j2 < i; ++j2) {
            if (j2 >= j && k > 0) {
                k1 += enumdirection.getAdjacentX();
                l1 += enumdirection.getAdjacentZ();
                --k;
            }

            k2 = i1 + j2;
            BlockPosition blockposition2 = new BlockPosition(k1, k2, l1);

            if (WorldGenTrees.d(virtuallevelreadable, blockposition2)) {
                b(virtuallevelreadable, biconsumer, random, blockposition2, worldgenfeaturetreeconfiguration);
                b(virtuallevelreadable, biconsumer, random, blockposition2.east(), worldgenfeaturetreeconfiguration);
                b(virtuallevelreadable, biconsumer, random, blockposition2.south(), worldgenfeaturetreeconfiguration);
                b(virtuallevelreadable, biconsumer, random, blockposition2.east().south(), worldgenfeaturetreeconfiguration);
            }
        }

        list.add(new WorldGenFoilagePlacer.a(new BlockPosition(k1, i2, l1), 0, true));

        for (j2 = -1; j2 <= 2; ++j2) {
            for (k2 = -1; k2 <= 2; ++k2) {
                if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && random.nextInt(3) <= 0) {
                    int l2 = random.nextInt(3) + 2;

                    for (int i3 = 0; i3 < l2; ++i3) {
                        b(virtuallevelreadable, biconsumer, random, new BlockPosition(l + j2, i2 - i3 - 1, j1 + k2), worldgenfeaturetreeconfiguration);
                    }

                    list.add(new WorldGenFoilagePlacer.a(new BlockPosition(k1 + j2, i2, l1 + k2), 0, false));
                }
            }
        }

        return list;
    }
}

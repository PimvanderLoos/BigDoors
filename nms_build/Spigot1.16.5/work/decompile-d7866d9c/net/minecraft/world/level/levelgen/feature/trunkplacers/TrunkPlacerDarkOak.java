package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerDarkOak extends TrunkPlacer {

    public static final Codec<TrunkPlacerDarkOak> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerDarkOak::new);
    });

    public TrunkPlacerDarkOak(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.e;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.b> list = Lists.newArrayList();
        BlockPosition blockposition1 = blockposition.down();

        a(virtuallevelwritable, blockposition1);
        a(virtuallevelwritable, blockposition1.east());
        a(virtuallevelwritable, blockposition1.south());
        a(virtuallevelwritable, blockposition1.south().east());
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

            if (WorldGenTrees.d(virtuallevelwritable, blockposition2)) {
                a(virtuallevelwritable, random, blockposition2, set, structureboundingbox, worldgenfeaturetreeconfiguration);
                a(virtuallevelwritable, random, blockposition2.east(), set, structureboundingbox, worldgenfeaturetreeconfiguration);
                a(virtuallevelwritable, random, blockposition2.south(), set, structureboundingbox, worldgenfeaturetreeconfiguration);
                a(virtuallevelwritable, random, blockposition2.east().south(), set, structureboundingbox, worldgenfeaturetreeconfiguration);
            }
        }

        list.add(new WorldGenFoilagePlacer.b(new BlockPosition(k1, i2, l1), 0, true));

        for (j2 = -1; j2 <= 2; ++j2) {
            for (k2 = -1; k2 <= 2; ++k2) {
                if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && random.nextInt(3) <= 0) {
                    int l2 = random.nextInt(3) + 2;

                    for (int i3 = 0; i3 < l2; ++i3) {
                        a(virtuallevelwritable, random, new BlockPosition(l + j2, i2 - i3 - 1, j1 + k2), set, structureboundingbox, worldgenfeaturetreeconfiguration);
                    }

                    list.add(new WorldGenFoilagePlacer.b(new BlockPosition(k1 + j2, i2, l1 + k2), 0, false));
                }
            }
        }

        return list;
    }
}

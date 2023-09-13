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
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerForking extends TrunkPlacer {

    public static final Codec<TrunkPlacerForking> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerForking::new);
    });

    public TrunkPlacerForking(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.b;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        a(virtuallevelwritable, blockposition.down());
        List<WorldGenFoilagePlacer.b> list = Lists.newArrayList();
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
        int j = i - random.nextInt(4) - 1;
        int k = 3 - random.nextInt(3);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int l = blockposition.getX();
        int i1 = blockposition.getZ();
        int j1 = 0;

        int k1;

        for (int l1 = 0; l1 < i; ++l1) {
            k1 = blockposition.getY() + l1;
            if (l1 >= j && k > 0) {
                l += enumdirection.getAdjacentX();
                i1 += enumdirection.getAdjacentZ();
                --k;
            }

            if (a(virtuallevelwritable, random, (BlockPosition) blockposition_mutableblockposition.d(l, k1, i1), set, structureboundingbox, worldgenfeaturetreeconfiguration)) {
                j1 = k1 + 1;
            }
        }

        list.add(new WorldGenFoilagePlacer.b(new BlockPosition(l, j1, i1), 1, false));
        l = blockposition.getX();
        i1 = blockposition.getZ();
        EnumDirection enumdirection1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);

        if (enumdirection1 != enumdirection) {
            k1 = j - random.nextInt(2) - 1;
            int i2 = 1 + random.nextInt(3);

            j1 = 0;

            for (int j2 = k1; j2 < i && i2 > 0; --i2) {
                if (j2 >= 1) {
                    int k2 = blockposition.getY() + j2;

                    l += enumdirection1.getAdjacentX();
                    i1 += enumdirection1.getAdjacentZ();
                    if (a(virtuallevelwritable, random, (BlockPosition) blockposition_mutableblockposition.d(l, k2, i1), set, structureboundingbox, worldgenfeaturetreeconfiguration)) {
                        j1 = k2 + 1;
                    }
                }

                ++j2;
            }

            if (j1 > 1) {
                list.add(new WorldGenFoilagePlacer.b(new BlockPosition(l, j1, i1), 0, false));
            }
        }

        return list;
    }
}

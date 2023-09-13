package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerDarkOak extends TrunkPlacer {

    public static final Codec<TrunkPlacerDarkOak> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).apply(instance, TrunkPlacerDarkOak::new);
    });

    public TrunkPlacerDarkOak(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();
        BlockPosition blockposition1 = blockposition.below();

        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1, worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.east(), worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.south(), worldgenfeaturetreeconfiguration);
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition1.south().east(), worldgenfeaturetreeconfiguration);
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource);
        int j = i - randomsource.nextInt(4);
        int k = 2 - randomsource.nextInt(3);
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
                k1 += enumdirection.getStepX();
                l1 += enumdirection.getStepZ();
                --k;
            }

            k2 = i1 + j2;
            BlockPosition blockposition2 = new BlockPosition(k1, k2, l1);

            if (WorldGenTrees.isAirOrLeaves(virtuallevelreadable, blockposition2)) {
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition2, worldgenfeaturetreeconfiguration);
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition2.east(), worldgenfeaturetreeconfiguration);
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition2.south(), worldgenfeaturetreeconfiguration);
                this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition2.east().south(), worldgenfeaturetreeconfiguration);
            }
        }

        list.add(new WorldGenFoilagePlacer.a(new BlockPosition(k1, i2, l1), 0, true));

        for (j2 = -1; j2 <= 2; ++j2) {
            for (k2 = -1; k2 <= 2; ++k2) {
                if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && randomsource.nextInt(3) <= 0) {
                    int l2 = randomsource.nextInt(3) + 2;

                    for (int i3 = 0; i3 < l2; ++i3) {
                        this.placeLog(virtuallevelreadable, biconsumer, randomsource, new BlockPosition(l + j2, i2 - i3 - 1, j1 + k2), worldgenfeaturetreeconfiguration);
                    }

                    list.add(new WorldGenFoilagePlacer.a(new BlockPosition(k1 + j2, i2, l1 + k2), 0, false));
                }
            }
        }

        return list;
    }
}

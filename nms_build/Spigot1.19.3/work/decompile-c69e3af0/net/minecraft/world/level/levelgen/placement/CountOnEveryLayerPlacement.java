package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;

/** @deprecated */
@Deprecated
public class CountOnEveryLayerPlacement extends PlacementModifier {

    public static final Codec<CountOnEveryLayerPlacement> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountOnEveryLayerPlacement::new, (countoneverylayerplacement) -> {
        return countoneverylayerplacement.count;
    }).codec();
    private final IntProvider count;

    private CountOnEveryLayerPlacement(IntProvider intprovider) {
        this.count = intprovider;
    }

    public static CountOnEveryLayerPlacement of(IntProvider intprovider) {
        return new CountOnEveryLayerPlacement(intprovider);
    }

    public static CountOnEveryLayerPlacement of(int i) {
        return of(ConstantInt.of(i));
    }

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        Builder<BlockPosition> builder = Stream.builder();
        int i = 0;

        boolean flag;

        do {
            flag = false;

            for (int j = 0; j < this.count.sample(randomsource); ++j) {
                int k = randomsource.nextInt(16) + blockposition.getX();
                int l = randomsource.nextInt(16) + blockposition.getZ();
                int i1 = placementcontext.getHeight(HeightMap.Type.MOTION_BLOCKING, k, l);
                int j1 = findOnGroundYPosition(placementcontext, k, i1, l, i);

                if (j1 != Integer.MAX_VALUE) {
                    builder.add(new BlockPosition(k, j1, l));
                    flag = true;
                }
            }

            ++i;
        } while (flag);

        return builder.build();
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.COUNT_ON_EVERY_LAYER;
    }

    private static int findOnGroundYPosition(PlacementContext placementcontext, int i, int j, int k, int l) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, j, k);
        int i1 = 0;
        IBlockData iblockdata = placementcontext.getBlockState(blockposition_mutableblockposition);

        for (int j1 = j; j1 >= placementcontext.getMinBuildHeight() + 1; --j1) {
            blockposition_mutableblockposition.setY(j1 - 1);
            IBlockData iblockdata1 = placementcontext.getBlockState(blockposition_mutableblockposition);

            if (!isEmpty(iblockdata1) && isEmpty(iblockdata) && !iblockdata1.is(Blocks.BEDROCK)) {
                if (i1 == l) {
                    return blockposition_mutableblockposition.getY() + 1;
                }

                ++i1;
            }

            iblockdata = iblockdata1;
        }

        return Integer.MAX_VALUE;
    }

    private static boolean isEmpty(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER) || iblockdata.is(Blocks.LAVA);
    }
}

package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;

public class DoubleBlockFinder {

    public DoubleBlockFinder() {}

    public static <S extends TileEntity> DoubleBlockFinder.Result<S> combineWithNeigbour(TileEntityTypes<S> tileentitytypes, Function<IBlockData, DoubleBlockFinder.BlockType> function, Function<IBlockData, EnumDirection> function1, BlockStateDirection blockstatedirection, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, BiPredicate<GeneratorAccess, BlockPosition> bipredicate) {
        S s0 = tileentitytypes.getBlockEntity(generatoraccess, blockposition);

        if (s0 == null) {
            return DoubleBlockFinder.Combiner::acceptNone;
        } else if (bipredicate.test(generatoraccess, blockposition)) {
            return DoubleBlockFinder.Combiner::acceptNone;
        } else {
            DoubleBlockFinder.BlockType doubleblockfinder_blocktype = (DoubleBlockFinder.BlockType) function.apply(iblockdata);
            boolean flag = doubleblockfinder_blocktype == DoubleBlockFinder.BlockType.SINGLE;
            boolean flag1 = doubleblockfinder_blocktype == DoubleBlockFinder.BlockType.FIRST;

            if (flag) {
                return new DoubleBlockFinder.Result.Single<>(s0);
            } else {
                BlockPosition blockposition1 = blockposition.relative((EnumDirection) function1.apply(iblockdata));
                IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

                if (iblockdata1.is(iblockdata.getBlock())) {
                    DoubleBlockFinder.BlockType doubleblockfinder_blocktype1 = (DoubleBlockFinder.BlockType) function.apply(iblockdata1);

                    if (doubleblockfinder_blocktype1 != DoubleBlockFinder.BlockType.SINGLE && doubleblockfinder_blocktype != doubleblockfinder_blocktype1 && iblockdata1.getValue(blockstatedirection) == iblockdata.getValue(blockstatedirection)) {
                        if (bipredicate.test(generatoraccess, blockposition1)) {
                            return DoubleBlockFinder.Combiner::acceptNone;
                        }

                        S s1 = tileentitytypes.getBlockEntity(generatoraccess, blockposition1);

                        if (s1 != null) {
                            S s2 = flag1 ? s0 : s1;
                            S s3 = flag1 ? s1 : s0;

                            return new DoubleBlockFinder.Result.Double<>(s2, s3);
                        }
                    }
                }

                return new DoubleBlockFinder.Result.Single<>(s0);
            }
        }
    }

    public interface Result<S> {

        <T> T apply(DoubleBlockFinder.Combiner<? super S, T> doubleblockfinder_combiner);

        public static final class Single<S> implements DoubleBlockFinder.Result<S> {

            private final S single;

            public Single(S s0) {
                this.single = s0;
            }

            @Override
            public <T> T apply(DoubleBlockFinder.Combiner<? super S, T> doubleblockfinder_combiner) {
                return doubleblockfinder_combiner.acceptSingle(this.single);
            }
        }

        public static final class Double<S> implements DoubleBlockFinder.Result<S> {

            private final S first;
            private final S second;

            public Double(S s0, S s1) {
                this.first = s0;
                this.second = s1;
            }

            @Override
            public <T> T apply(DoubleBlockFinder.Combiner<? super S, T> doubleblockfinder_combiner) {
                return doubleblockfinder_combiner.acceptDouble(this.first, this.second);
            }
        }
    }

    public static enum BlockType {

        SINGLE, FIRST, SECOND;

        private BlockType() {}
    }

    public interface Combiner<S, T> {

        T acceptDouble(S s0, S s1);

        T acceptSingle(S s0);

        T acceptNone();
    }
}

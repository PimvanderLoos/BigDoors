package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public interface ChangeOverTimeBlock<T extends Enum<T>> {

    int SCAN_DISTANCE = 4;

    Optional<IBlockData> getNext(IBlockData iblockdata);

    float getChanceModifier();

    default void onRandomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        float f = 0.05688889F;

        if (randomsource.nextFloat() < 0.05688889F) {
            this.applyChangeOverTime(iblockdata, worldserver, blockposition, randomsource);
        }

    }

    T getAge();

    default void applyChangeOverTime(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        int i = this.getAge().ordinal();
        int j = 0;
        int k = 0;
        Iterator iterator = BlockPosition.withinManhattan(blockposition, 4, 4, 4).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            int l = blockposition1.distManhattan(blockposition);

            if (l > 4) {
                break;
            }

            if (!blockposition1.equals(blockposition)) {
                IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);
                Block block = iblockdata1.getBlock();

                if (block instanceof ChangeOverTimeBlock) {
                    Enum<?> oenum = ((ChangeOverTimeBlock) block).getAge();

                    if (this.getAge().getClass() == oenum.getClass()) {
                        int i1 = oenum.ordinal();

                        if (i1 < i) {
                            return;
                        }

                        if (i1 > i) {
                            ++k;
                        } else {
                            ++j;
                        }
                    }
                }
            }
        }

        float f = (float) (k + 1) / (float) (k + j + 1);
        float f1 = f * f * this.getChanceModifier();

        if (randomsource.nextFloat() < f1) {
            this.getNext(iblockdata).ifPresent((iblockdata2) -> {
                worldserver.setBlockAndUpdate(blockposition, iblockdata2);
            });
        }

    }
}

package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;

public interface ChangeOverTimeBlock<T extends Enum<T>> {

    int SCAN_DISTANCE = 4;

    Optional<IBlockData> a(IBlockData iblockdata);

    float a();

    default void a_(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        float f = 0.05688889F;

        if (random.nextFloat() < 0.05688889F) {
            this.c(iblockdata, worldserver, blockposition, random);
        }

    }

    T b();

    default void c(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = this.b().ordinal();
        int j = 0;
        int k = 0;
        Iterator iterator = BlockPosition.a(blockposition, 4, 4, 4).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            int l = blockposition1.k(blockposition);

            if (l > 4) {
                break;
            }

            if (!blockposition1.equals(blockposition)) {
                IBlockData iblockdata1 = worldserver.getType(blockposition1);
                Block block = iblockdata1.getBlock();

                if (block instanceof ChangeOverTimeBlock) {
                    Enum<?> oenum = ((ChangeOverTimeBlock) block).b();

                    if (this.b().getClass() == oenum.getClass()) {
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
        float f1 = f * f * this.a();

        if (random.nextFloat() < f1) {
            this.a(iblockdata).ifPresent((iblockdata2) -> {
                worldserver.setTypeUpdate(blockposition, iblockdata2);
            });
        }

    }
}

package net.minecraft.server;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LightEngine implements ILightEngine {

    private static final Logger a = LogManager.getLogger();
    private static final EnumDirection[] b = EnumDirection.values();
    private final IntPriorityQueue c = new IntArrayFIFOQueue(786);

    public LightEngine() {}

    public int a(IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBrightness(this.a(), blockposition);
    }

    public void a(IWorldWriter iworldwriter, BlockPosition blockposition, int i) {
        iworldwriter.a(this.a(), blockposition, i);
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition).b(iblockaccess, blockposition);
    }

    protected int b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition).e();
    }

    private int a(@Nullable EnumDirection enumdirection, int i, int j, int k, int l) {
        int i1 = 7;

        if (enumdirection != null) {
            i1 = enumdirection.ordinal();
        }

        return i1 << 24 | i << 18 | j << 10 | k << 4 | l << 0;
    }

    private int a(int i) {
        return i >> 18 & 63;
    }

    private int b(int i) {
        return i >> 10 & 255;
    }

    private int c(int i) {
        return i >> 4 & 63;
    }

    private int d(int i) {
        return i >> 0 & 15;
    }

    @Nullable
    private EnumDirection e(int i) {
        int j = i >> 24 & 7;

        return j == 7 ? null : EnumDirection.values()[i >> 24 & 7];
    }

    protected void a(GeneratorAccess generatoraccess, ChunkCoordIntPair chunkcoordintpair) {
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            while (!this.c.isEmpty()) {
                int i = this.c.dequeueInt();
                int j = this.d(i);
                int k = this.a(i) - 16;
                int l = this.b(i);
                int i1 = this.c(i) - 16;
                EnumDirection enumdirection = this.e(i);
                EnumDirection[] aenumdirection = LightEngine.b;
                int j1 = aenumdirection.length;

                for (int k1 = 0; k1 < j1; ++k1) {
                    EnumDirection enumdirection1 = aenumdirection[k1];

                    if (enumdirection1 != enumdirection) {
                        int l1 = k + enumdirection1.getAdjacentX();
                        int i2 = l + enumdirection1.getAdjacentY();
                        int j2 = i1 + enumdirection1.getAdjacentZ();

                        if (i2 <= 255 && i2 >= 0) {
                            blockposition_pooledblockposition.c(l1 + chunkcoordintpair.d(), i2, j2 + chunkcoordintpair.e());
                            int k2 = this.a((IBlockAccess) generatoraccess, (BlockPosition) blockposition_pooledblockposition);
                            int l2 = j - Math.max(k2, 1);

                            if (l2 > 0 && l2 > this.a((IWorldReader) generatoraccess, (BlockPosition) blockposition_pooledblockposition)) {
                                this.a((IWorldWriter) generatoraccess, blockposition_pooledblockposition, l2);
                                this.a(chunkcoordintpair, blockposition_pooledblockposition, l2);
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

    }

    protected void a(ChunkCoordIntPair chunkcoordintpair, int i, int j, int k, int l) {
        int i1 = i - chunkcoordintpair.d() + 16;
        int j1 = k - chunkcoordintpair.e() + 16;

        this.c.enqueue(this.a((EnumDirection) null, i1, j, j1, l));
    }

    protected void a(ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition, int i) {
        this.a(chunkcoordintpair, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
    }
}

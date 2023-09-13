package net.minecraft.server;

public class LightEngineSky extends LightEngine {

    public static final EnumDirection[] a = new EnumDirection[] { EnumDirection.WEST, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH};

    public LightEngineSky() {}

    public EnumSkyBlock a() {
        return EnumSkyBlock.SKY;
    }

    public void a(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {
        int i = ichunkaccess.getPos().d();
        int j = ichunkaccess.getPos().e();
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition1 = BlockPosition.PooledBlockPosition.r();
            Throwable throwable1 = null;

            try {
                for (int k = 0; k < 16; ++k) {
                    for (int l = 0; l < 16; ++l) {
                        int i1 = ichunkaccess.a(HeightMap.Type.LIGHT_BLOCKING, k, l) + 1;
                        int j1 = k + i;
                        int k1 = l + j;

                        for (int l1 = i1; l1 < ichunkaccess.getSections().length * 16 - 1; ++l1) {
                            blockposition_pooledblockposition.c(j1, l1, k1);
                            this.a((IWorldWriter) regionlimitedworldaccess, blockposition_pooledblockposition, 15);
                        }

                        this.a(ichunkaccess.getPos(), j1, i1, k1, 15);
                        EnumDirection[] aenumdirection = LightEngineSky.a;
                        int i2 = aenumdirection.length;

                        for (int j2 = 0; j2 < i2; ++j2) {
                            EnumDirection enumdirection = aenumdirection[j2];
                            int k2 = regionlimitedworldaccess.a(HeightMap.Type.LIGHT_BLOCKING, j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ());

                            if (k2 - i1 >= 2) {
                                for (int l2 = i1; l2 <= k2; ++l2) {
                                    blockposition_pooledblockposition1.c(j1 + enumdirection.getAdjacentX(), l2, k1 + enumdirection.getAdjacentZ());
                                    int i3 = regionlimitedworldaccess.getType(blockposition_pooledblockposition1).b(regionlimitedworldaccess, blockposition_pooledblockposition1);

                                    if (i3 != regionlimitedworldaccess.K()) {
                                        this.a((IWorldWriter) regionlimitedworldaccess, blockposition_pooledblockposition1, 15 - i3 - 1);
                                        this.a(ichunkaccess.getPos(), blockposition_pooledblockposition1, 15 - i3 - 1);
                                    }
                                }
                            }
                        }
                    }
                }

                this.a((GeneratorAccess) regionlimitedworldaccess, ichunkaccess.getPos());
            } catch (Throwable throwable2) {
                throwable1 = throwable2;
                throw throwable2;
            } finally {
                if (blockposition_pooledblockposition1 != null) {
                    if (throwable1 != null) {
                        try {
                            blockposition_pooledblockposition1.close();
                        } catch (Throwable throwable3) {
                            throwable1.addSuppressed(throwable3);
                        }
                    } else {
                        blockposition_pooledblockposition1.close();
                    }
                }

            }
        } catch (Throwable throwable4) {
            throwable = throwable4;
            throw throwable4;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable5) {
                        throwable.addSuppressed(throwable5);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

    }
}

package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenForestTree extends WorldGenTreeAbstract<WorldGenMegaTreeConfiguration> {

    public WorldGenForestTree(Function<Dynamic<?>, ? extends WorldGenMegaTreeConfiguration> function) {
        super(function);
    }

    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        int i = random.nextInt(3) + random.nextInt(2) + worldgenmegatreeconfiguration.p;
        int j = blockposition.getX();
        int k = blockposition.getY();
        int l = blockposition.getZ();

        if (k >= 1 && k + i + 1 < 256) {
            BlockPosition blockposition1 = blockposition.down();

            if (!g(virtuallevelwritable, blockposition1)) {
                return false;
            } else if (!this.a(virtuallevelwritable, blockposition, i)) {
                return false;
            } else {
                this.a(virtuallevelwritable, blockposition1);
                this.a(virtuallevelwritable, blockposition1.east());
                this.a(virtuallevelwritable, blockposition1.south());
                this.a(virtuallevelwritable, blockposition1.south().east());
                EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
                int i1 = i - random.nextInt(4);
                int j1 = 2 - random.nextInt(3);
                int k1 = j;
                int l1 = l;
                int i2 = k + i - 1;

                int j2;
                int k2;

                for (j2 = 0; j2 < i; ++j2) {
                    if (j2 >= i1 && j1 > 0) {
                        k1 += enumdirection.getAdjacentX();
                        l1 += enumdirection.getAdjacentZ();
                        --j1;
                    }

                    k2 = k + j2;
                    BlockPosition blockposition2 = new BlockPosition(k1, k2, l1);

                    if (f(virtuallevelwritable, blockposition2)) {
                        this.a(virtuallevelwritable, random, blockposition2, set, structureboundingbox, worldgenmegatreeconfiguration);
                        this.a(virtuallevelwritable, random, blockposition2.east(), set, structureboundingbox, worldgenmegatreeconfiguration);
                        this.a(virtuallevelwritable, random, blockposition2.south(), set, structureboundingbox, worldgenmegatreeconfiguration);
                        this.a(virtuallevelwritable, random, blockposition2.east().south(), set, structureboundingbox, worldgenmegatreeconfiguration);
                    }
                }

                for (j2 = -2; j2 <= 0; ++j2) {
                    for (k2 = -2; k2 <= 0; ++k2) {
                        byte b0 = -1;

                        this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2, i2 + b0, l1 + k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        this.b(virtuallevelwritable, random, new BlockPosition(1 + k1 - j2, i2 + b0, l1 + k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2, i2 + b0, 1 + l1 - k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        this.b(virtuallevelwritable, random, new BlockPosition(1 + k1 - j2, i2 + b0, 1 + l1 - k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        if ((j2 > -2 || k2 > -1) && (j2 != -1 || k2 != -2)) {
                            byte b1 = 1;

                            this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2, i2 + b1, l1 + k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                            this.b(virtuallevelwritable, random, new BlockPosition(1 + k1 - j2, i2 + b1, l1 + k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                            this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2, i2 + b1, 1 + l1 - k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                            this.b(virtuallevelwritable, random, new BlockPosition(1 + k1 - j2, i2 + b1, 1 + l1 - k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        }
                    }
                }

                if (random.nextBoolean()) {
                    this.b(virtuallevelwritable, random, new BlockPosition(k1, i2 + 2, l1), set1, structureboundingbox, worldgenmegatreeconfiguration);
                    this.b(virtuallevelwritable, random, new BlockPosition(k1 + 1, i2 + 2, l1), set1, structureboundingbox, worldgenmegatreeconfiguration);
                    this.b(virtuallevelwritable, random, new BlockPosition(k1 + 1, i2 + 2, l1 + 1), set1, structureboundingbox, worldgenmegatreeconfiguration);
                    this.b(virtuallevelwritable, random, new BlockPosition(k1, i2 + 2, l1 + 1), set1, structureboundingbox, worldgenmegatreeconfiguration);
                }

                for (j2 = -3; j2 <= 4; ++j2) {
                    for (k2 = -3; k2 <= 4; ++k2) {
                        if ((j2 != -3 || k2 != -3) && (j2 != -3 || k2 != 4) && (j2 != 4 || k2 != -3) && (j2 != 4 || k2 != 4) && (Math.abs(j2) < 3 || Math.abs(k2) < 3)) {
                            this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2, i2, l1 + k2), set1, structureboundingbox, worldgenmegatreeconfiguration);
                        }
                    }
                }

                for (j2 = -1; j2 <= 2; ++j2) {
                    for (k2 = -1; k2 <= 2; ++k2) {
                        if ((j2 < 0 || j2 > 1 || k2 < 0 || k2 > 1) && random.nextInt(3) <= 0) {
                            int l2 = random.nextInt(3) + 2;

                            int i3;

                            for (i3 = 0; i3 < l2; ++i3) {
                                this.a(virtuallevelwritable, random, new BlockPosition(j + j2, i2 - i3 - 1, l + k2), set, structureboundingbox, worldgenmegatreeconfiguration);
                            }

                            int j3;

                            for (i3 = -1; i3 <= 1; ++i3) {
                                for (j3 = -1; j3 <= 1; ++j3) {
                                    this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2 + i3, i2, l1 + k2 + j3), set1, structureboundingbox, worldgenmegatreeconfiguration);
                                }
                            }

                            for (i3 = -2; i3 <= 2; ++i3) {
                                for (j3 = -2; j3 <= 2; ++j3) {
                                    if (Math.abs(i3) != 2 || Math.abs(j3) != 2) {
                                        this.b(virtuallevelwritable, random, new BlockPosition(k1 + j2 + i3, i2 - 1, l1 + k2 + j3), set1, structureboundingbox, worldgenmegatreeconfiguration);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, int i) {
        int j = blockposition.getX();
        int k = blockposition.getY();
        int l = blockposition.getZ();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i1 = 0; i1 <= i + 1; ++i1) {
            byte b0 = 1;

            if (i1 == 0) {
                b0 = 0;
            }

            if (i1 >= i - 1) {
                b0 = 2;
            }

            for (int j1 = -b0; j1 <= b0; ++j1) {
                for (int k1 = -b0; k1 <= b0; ++k1) {
                    if (!a(virtuallevelreadable, (BlockPosition) blockposition_mutableblockposition.d(j + j1, k + i1, l + k1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}

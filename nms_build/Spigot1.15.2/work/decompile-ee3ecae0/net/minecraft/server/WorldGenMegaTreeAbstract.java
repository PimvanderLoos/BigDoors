package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public abstract class WorldGenMegaTreeAbstract<T extends WorldGenFeatureTreeConfiguration> extends WorldGenTreeAbstract<T> {

    public WorldGenMegaTreeAbstract(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected int a(Random random, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        int i = random.nextInt(3) + worldgenmegatreeconfiguration.p;

        if (worldgenmegatreeconfiguration.a > 1) {
            i += random.nextInt(worldgenmegatreeconfiguration.a);
        }

        return i;
    }

    private boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, int i) {
        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            for (int j = 0; j <= 1 + i; ++j) {
                byte b0 = 2;

                if (j == 0) {
                    b0 = 1;
                } else if (j >= 1 + i - 2) {
                    b0 = 2;
                }

                for (int k = -b0; k <= b0 && flag; ++k) {
                    for (int l = -b0; l <= b0 && flag; ++l) {
                        if (blockposition.getY() + j < 0 || blockposition.getY() + j >= 256 || !a(virtuallevelreadable, blockposition.b(k, j, l))) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    private boolean b(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        if (g(virtuallevelwritable, blockposition1) && blockposition.getY() >= 2) {
            this.a(virtuallevelwritable, blockposition1);
            this.a(virtuallevelwritable, blockposition1.east());
            this.a(virtuallevelwritable, blockposition1.south());
            this.a(virtuallevelwritable, blockposition1.south().east());
            return true;
        } else {
            return false;
        }
    }

    protected boolean a(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition, int i) {
        return this.a((VirtualLevelReadable) virtuallevelwritable, blockposition, i) && this.b(virtuallevelwritable, blockposition);
    }

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        int j = i * i;

        for (int k = -i; k <= i + 1; ++k) {
            for (int l = -i; l <= i + 1; ++l) {
                int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
                int j1 = Math.min(Math.abs(l), Math.abs(l - 1));

                if (i1 + j1 < 7 && i1 * i1 + j1 * j1 <= j) {
                    this.b(virtuallevelwritable, random, blockposition.b(k, 0, l), set, structureboundingbox, worldgenfeaturetreeconfiguration);
                }
            }
        }

    }

    protected void b(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        int j = i * i;

        for (int k = -i; k <= i; ++k) {
            for (int l = -i; l <= i; ++l) {
                if (k * k + l * l <= j) {
                    this.b(virtuallevelwritable, random, blockposition.b(k, 0, l), set, structureboundingbox, worldgenfeaturetreeconfiguration);
                }
            }
        }

    }

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenMegaTreeConfiguration worldgenmegatreeconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            blockposition_mutableblockposition.g(blockposition).e(0, j, 0);
            if (a((VirtualLevelReadable) virtuallevelwritable, (BlockPosition) blockposition_mutableblockposition)) {
                this.a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenmegatreeconfiguration);
            }

            if (j < i - 1) {
                blockposition_mutableblockposition.g(blockposition).e(1, j, 0);
                if (a((VirtualLevelReadable) virtuallevelwritable, (BlockPosition) blockposition_mutableblockposition)) {
                    this.a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenmegatreeconfiguration);
                }

                blockposition_mutableblockposition.g(blockposition).e(1, j, 1);
                if (a((VirtualLevelReadable) virtuallevelwritable, (BlockPosition) blockposition_mutableblockposition)) {
                    this.a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenmegatreeconfiguration);
                }

                blockposition_mutableblockposition.g(blockposition).e(0, j, 1);
                if (a((VirtualLevelReadable) virtuallevelwritable, (BlockPosition) blockposition_mutableblockposition)) {
                    this.a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenmegatreeconfiguration);
                }
            }
        }

    }
}

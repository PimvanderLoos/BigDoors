package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenBigTree extends WorldGenTreeAbstract<WorldGenFeatureSmallTreeConfigurationConfiguration> {

    public WorldGenBigTree(Function<Dynamic<?>, ? extends WorldGenFeatureSmallTreeConfigurationConfiguration> function) {
        super(function);
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, float f, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        int i = (int) ((double) f + 0.618D);

        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                if (Math.pow((double) Math.abs(j) + 0.5D, 2.0D) + Math.pow((double) Math.abs(k) + 0.5D, 2.0D) <= (double) (f * f)) {
                    this.b(virtuallevelwritable, random, blockposition.b(j, 0, k), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
                }
            }
        }

    }

    private float a(int i, int j) {
        if ((float) j < (float) i * 0.3F) {
            return -1.0F;
        } else {
            float f = (float) i / 2.0F;
            float f1 = f - (float) j;
            float f2 = MathHelper.c(f * f - f1 * f1);

            if (f1 == 0.0F) {
                f2 = f;
            } else if (Math.abs(f1) >= f) {
                return 0.0F;
            }

            return f2 * 0.5F;
        }
    }

    private float a(int i) {
        return i >= 0 && i < 5 ? (i != 0 && i != 4 ? 3.0F : 2.0F) : -1.0F;
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        for (int i = 0; i < 5; ++i) {
            this.a(virtuallevelwritable, random, blockposition.up(i), this.a(i), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
        }

    }

    private int a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, BlockPosition blockposition1, boolean flag, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        if (!flag && Objects.equals(blockposition, blockposition1)) {
            return -1;
        } else {
            BlockPosition blockposition2 = blockposition1.b(-blockposition.getX(), -blockposition.getY(), -blockposition.getZ());
            int i = this.a(blockposition2);
            float f = (float) blockposition2.getX() / (float) i;
            float f1 = (float) blockposition2.getY() / (float) i;
            float f2 = (float) blockposition2.getZ() / (float) i;

            for (int j = 0; j <= i; ++j) {
                BlockPosition blockposition3 = blockposition.a((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));

                if (flag) {
                    this.a((IWorldWriter) virtuallevelwritable, blockposition3, (IBlockData) worldgenfeaturesmalltreeconfigurationconfiguration.m.a(random, blockposition3).set(BlockLogAbstract.AXIS, this.a(blockposition, blockposition3)), structureboundingbox);
                    set.add(blockposition3);
                } else if (!a((VirtualLevelReadable) virtuallevelwritable, blockposition3)) {
                    return j;
                }
            }

            return -1;
        }
    }

    private int a(BlockPosition blockposition) {
        int i = MathHelper.a(blockposition.getX());
        int j = MathHelper.a(blockposition.getY());
        int k = MathHelper.a(blockposition.getZ());

        return k > i && k > j ? k : (j > i ? j : i);
    }

    private EnumDirection.EnumAxis a(BlockPosition blockposition, BlockPosition blockposition1) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.Y;
        int i = Math.abs(blockposition1.getX() - blockposition.getX());
        int j = Math.abs(blockposition1.getZ() - blockposition.getZ());
        int k = Math.max(i, j);

        if (k > 0) {
            if (i == k) {
                enumdirection_enumaxis = EnumDirection.EnumAxis.X;
            } else if (j == k) {
                enumdirection_enumaxis = EnumDirection.EnumAxis.Z;
            }
        }

        return enumdirection_enumaxis;
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, List<WorldGenBigTree.a> list, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenBigTree.a worldgenbigtree_a = (WorldGenBigTree.a) iterator.next();

            if (this.b(i, worldgenbigtree_a.r() - blockposition.getY())) {
                this.a(virtuallevelwritable, random, worldgenbigtree_a, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            }
        }

    }

    private boolean b(int i, int j) {
        return (double) j >= (double) i * 0.2D;
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        this.a(virtuallevelwritable, random, blockposition, blockposition.up(i), true, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
    }

    private void b(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, List<WorldGenBigTree.a> list, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenBigTree.a worldgenbigtree_a = (WorldGenBigTree.a) iterator.next();
            int j = worldgenbigtree_a.r();
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ());

            if (!blockposition1.equals(worldgenbigtree_a) && this.b(i, j - blockposition.getY())) {
                this.a(virtuallevelwritable, random, blockposition1, worldgenbigtree_a, true, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            }
        }

    }

    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        Random random1 = new Random(random.nextLong());
        int i = this.b(virtuallevelwritable, random, blockposition, 5 + random1.nextInt(12), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);

        if (i == -1) {
            return false;
        } else {
            this.a(virtuallevelwritable, blockposition.down());
            int j = (int) ((double) i * 0.618D);

            if (j >= i) {
                j = i - 1;
            }

            double d0 = 1.0D;
            int k = (int) (1.382D + Math.pow(1.0D * (double) i / 13.0D, 2.0D));

            if (k < 1) {
                k = 1;
            }

            int l = blockposition.getY() + j;
            int i1 = i - 5;
            List<WorldGenBigTree.a> list = Lists.newArrayList();

            list.add(new WorldGenBigTree.a(blockposition.up(i1), l));

            for (; i1 >= 0; --i1) {
                float f = this.a(i, i1);

                if (f >= 0.0F) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        double d1 = 1.0D;
                        double d2 = 1.0D * (double) f * ((double) random1.nextFloat() + 0.328D);
                        double d3 = (double) (random1.nextFloat() * 2.0F) * 3.141592653589793D;
                        double d4 = d2 * Math.sin(d3) + 0.5D;
                        double d5 = d2 * Math.cos(d3) + 0.5D;
                        BlockPosition blockposition1 = blockposition.a(d4, (double) (i1 - 1), d5);
                        BlockPosition blockposition2 = blockposition1.up(5);

                        if (this.a(virtuallevelwritable, random, blockposition1, blockposition2, false, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration) == -1) {
                            int k1 = blockposition.getX() - blockposition1.getX();
                            int l1 = blockposition.getZ() - blockposition1.getZ();
                            double d6 = (double) blockposition1.getY() - Math.sqrt((double) (k1 * k1 + l1 * l1)) * 0.381D;
                            int i2 = d6 > (double) l ? l : (int) d6;
                            BlockPosition blockposition3 = new BlockPosition(blockposition.getX(), i2, blockposition.getZ());

                            if (this.a(virtuallevelwritable, random, blockposition3, blockposition1, false, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration) == -1) {
                                list.add(new WorldGenBigTree.a(blockposition1, blockposition3.getY()));
                            }
                        }
                    }
                }
            }

            this.a(virtuallevelwritable, random, i, blockposition, list, set1, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            this.a(virtuallevelwritable, random, blockposition, j, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            this.b(virtuallevelwritable, random, i, blockposition, list, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            return true;
        }
    }

    private int b(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, int i, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        if (!h(virtuallevelwritable, blockposition.down())) {
            return -1;
        } else {
            int j = this.a(virtuallevelwritable, random, blockposition, blockposition.up(i - 1), false, set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);

            return j == -1 ? i : (j < 6 ? -1 : j);
        }
    }

    static class a extends BlockPosition {

        private final int b;

        public a(BlockPosition blockposition, int i) {
            super(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            this.b = i;
        }

        public int r() {
            return this.b;
        }
    }
}

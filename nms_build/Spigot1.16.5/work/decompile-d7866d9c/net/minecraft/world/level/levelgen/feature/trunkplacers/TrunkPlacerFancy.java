package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerFancy extends TrunkPlacer {

    public static final Codec<TrunkPlacerFancy> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerFancy::new);
    });

    public TrunkPlacerFancy(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.f;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        boolean flag = true;
        int j = i + 2;
        int k = MathHelper.floor((double) j * 0.618D);

        if (!worldgenfeaturetreeconfiguration.e) {
            a(virtuallevelwritable, blockposition.down());
        }

        double d0 = 1.0D;
        int l = Math.min(1, MathHelper.floor(1.382D + Math.pow(1.0D * (double) j / 13.0D, 2.0D)));
        int i1 = blockposition.getY() + k;
        int j1 = j - 5;
        List<TrunkPlacerFancy.a> list = Lists.newArrayList();

        list.add(new TrunkPlacerFancy.a(blockposition.up(j1), i1));

        for (; j1 >= 0; --j1) {
            float f = this.b(j, j1);

            if (f >= 0.0F) {
                for (int k1 = 0; k1 < l; ++k1) {
                    double d1 = 1.0D;
                    double d2 = 1.0D * (double) f * ((double) random.nextFloat() + 0.328D);
                    double d3 = (double) (random.nextFloat() * 2.0F) * 3.141592653589793D;
                    double d4 = d2 * Math.sin(d3) + 0.5D;
                    double d5 = d2 * Math.cos(d3) + 0.5D;
                    BlockPosition blockposition1 = blockposition.a(d4, (double) (j1 - 1), d5);
                    BlockPosition blockposition2 = blockposition1.up(5);

                    if (this.a(virtuallevelwritable, random, blockposition1, blockposition2, false, set, structureboundingbox, worldgenfeaturetreeconfiguration)) {
                        int l1 = blockposition.getX() - blockposition1.getX();
                        int i2 = blockposition.getZ() - blockposition1.getZ();
                        double d6 = (double) blockposition1.getY() - Math.sqrt((double) (l1 * l1 + i2 * i2)) * 0.381D;
                        int j2 = d6 > (double) i1 ? i1 : (int) d6;
                        BlockPosition blockposition3 = new BlockPosition(blockposition.getX(), j2, blockposition.getZ());

                        if (this.a(virtuallevelwritable, random, blockposition3, blockposition1, false, set, structureboundingbox, worldgenfeaturetreeconfiguration)) {
                            list.add(new TrunkPlacerFancy.a(blockposition1, blockposition3.getY()));
                        }
                    }
                }
            }
        }

        this.a(virtuallevelwritable, random, blockposition, blockposition.up(k), true, set, structureboundingbox, worldgenfeaturetreeconfiguration);
        this.a(virtuallevelwritable, random, j, blockposition, list, set, structureboundingbox, worldgenfeaturetreeconfiguration);
        List<WorldGenFoilagePlacer.b> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TrunkPlacerFancy.a trunkplacerfancy_a = (TrunkPlacerFancy.a) iterator.next();

            if (this.a(j, trunkplacerfancy_a.a() - blockposition.getY())) {
                list1.add(trunkplacerfancy_a.a);
            }
        }

        return list1;
    }

    private boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, BlockPosition blockposition1, boolean flag, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (!flag && Objects.equals(blockposition, blockposition1)) {
            return true;
        } else {
            BlockPosition blockposition2 = blockposition1.b(-blockposition.getX(), -blockposition.getY(), -blockposition.getZ());
            int i = this.a(blockposition2);
            float f = (float) blockposition2.getX() / (float) i;
            float f1 = (float) blockposition2.getY() / (float) i;
            float f2 = (float) blockposition2.getZ() / (float) i;

            for (int j = 0; j <= i; ++j) {
                BlockPosition blockposition3 = blockposition.a((double) (0.5F + (float) j * f), (double) (0.5F + (float) j * f1), (double) (0.5F + (float) j * f2));

                if (flag) {
                    a(virtuallevelwritable, blockposition3, (IBlockData) worldgenfeaturetreeconfiguration.b.a(random, blockposition3).set(BlockRotatable.AXIS, this.a(blockposition, blockposition3)), structureboundingbox);
                    set.add(blockposition3.immutableCopy());
                } else if (!WorldGenTrees.c(virtuallevelwritable, blockposition3)) {
                    return false;
                }
            }

            return true;
        }
    }

    private int a(BlockPosition blockposition) {
        int i = MathHelper.a(blockposition.getX());
        int j = MathHelper.a(blockposition.getY());
        int k = MathHelper.a(blockposition.getZ());

        return Math.max(i, Math.max(j, k));
    }

    private EnumDirection.EnumAxis a(BlockPosition blockposition, BlockPosition blockposition1) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.Y;
        int i = Math.abs(blockposition1.getX() - blockposition.getX());
        int j = Math.abs(blockposition1.getZ() - blockposition.getZ());
        int k = Math.max(i, j);

        if (k > 0) {
            if (i == k) {
                enumdirection_enumaxis = EnumDirection.EnumAxis.X;
            } else {
                enumdirection_enumaxis = EnumDirection.EnumAxis.Z;
            }
        }

        return enumdirection_enumaxis;
    }

    private boolean a(int i, int j) {
        return (double) j >= (double) i * 0.2D;
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, List<TrunkPlacerFancy.a> list, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TrunkPlacerFancy.a trunkplacerfancy_a = (TrunkPlacerFancy.a) iterator.next();
            int j = trunkplacerfancy_a.a();
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ());

            if (!blockposition1.equals(trunkplacerfancy_a.a.a()) && this.a(i, j - blockposition.getY())) {
                this.a(virtuallevelwritable, random, blockposition1, trunkplacerfancy_a.a.a(), true, set, structureboundingbox, worldgenfeaturetreeconfiguration);
            }
        }

    }

    private float b(int i, int j) {
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

    static class a {

        private final WorldGenFoilagePlacer.b a;
        private final int b;

        public a(BlockPosition blockposition, int i) {
            this.a = new WorldGenFoilagePlacer.b(blockposition, 0, false);
            this.b = i;
        }

        public int a() {
            return this.b;
        }
    }
}

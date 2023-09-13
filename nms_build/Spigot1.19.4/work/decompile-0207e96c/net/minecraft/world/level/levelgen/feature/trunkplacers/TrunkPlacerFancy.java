package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class TrunkPlacerFancy extends TrunkPlacer {

    public static final Codec<TrunkPlacerFancy> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).apply(instance, TrunkPlacerFancy::new);
    });
    private static final double TRUNK_HEIGHT_SCALE = 0.618D;
    private static final double CLUSTER_DENSITY_MAGIC = 1.382D;
    private static final double BRANCH_SLOPE = 0.381D;
    private static final double BRANCH_LENGTH_MAGIC = 0.328D;

    public TrunkPlacerFancy(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        boolean flag = true;
        int j = i + 2;
        int k = MathHelper.floor((double) j * 0.618D);

        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition.below(), worldgenfeaturetreeconfiguration);
        double d0 = 1.0D;
        int l = Math.min(1, MathHelper.floor(1.382D + Math.pow(1.0D * (double) j / 13.0D, 2.0D)));
        int i1 = blockposition.getY() + k;
        int j1 = j - 5;
        List<TrunkPlacerFancy.a> list = Lists.newArrayList();

        list.add(new TrunkPlacerFancy.a(blockposition.above(j1), i1));

        for (; j1 >= 0; --j1) {
            float f = treeShape(j, j1);

            if (f >= 0.0F) {
                for (int k1 = 0; k1 < l; ++k1) {
                    double d1 = 1.0D;
                    double d2 = 1.0D * (double) f * ((double) randomsource.nextFloat() + 0.328D);
                    double d3 = (double) (randomsource.nextFloat() * 2.0F) * 3.141592653589793D;
                    double d4 = d2 * Math.sin(d3) + 0.5D;
                    double d5 = d2 * Math.cos(d3) + 0.5D;
                    BlockPosition blockposition1 = blockposition.offset(MathHelper.floor(d4), j1 - 1, MathHelper.floor(d5));
                    BlockPosition blockposition2 = blockposition1.above(5);

                    if (this.makeLimb(virtuallevelreadable, biconsumer, randomsource, blockposition1, blockposition2, false, worldgenfeaturetreeconfiguration)) {
                        int l1 = blockposition.getX() - blockposition1.getX();
                        int i2 = blockposition.getZ() - blockposition1.getZ();
                        double d6 = (double) blockposition1.getY() - Math.sqrt((double) (l1 * l1 + i2 * i2)) * 0.381D;
                        int j2 = d6 > (double) i1 ? i1 : (int) d6;
                        BlockPosition blockposition3 = new BlockPosition(blockposition.getX(), j2, blockposition.getZ());

                        if (this.makeLimb(virtuallevelreadable, biconsumer, randomsource, blockposition3, blockposition1, false, worldgenfeaturetreeconfiguration)) {
                            list.add(new TrunkPlacerFancy.a(blockposition1, blockposition3.getY()));
                        }
                    }
                }
            }
        }

        this.makeLimb(virtuallevelreadable, biconsumer, randomsource, blockposition, blockposition.above(k), true, worldgenfeaturetreeconfiguration);
        this.makeBranches(virtuallevelreadable, biconsumer, randomsource, j, blockposition, list, worldgenfeaturetreeconfiguration);
        List<WorldGenFoilagePlacer.a> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TrunkPlacerFancy.a trunkplacerfancy_a = (TrunkPlacerFancy.a) iterator.next();

            if (this.trimBranches(j, trunkplacerfancy_a.getBranchBase() - blockposition.getY())) {
                list1.add(trunkplacerfancy_a.attachment);
            }
        }

        return list1;
    }

    private boolean makeLimb(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, BlockPosition blockposition1, boolean flag, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (!flag && Objects.equals(blockposition, blockposition1)) {
            return true;
        } else {
            BlockPosition blockposition2 = blockposition1.offset(-blockposition.getX(), -blockposition.getY(), -blockposition.getZ());
            int i = this.getSteps(blockposition2);
            float f = (float) blockposition2.getX() / (float) i;
            float f1 = (float) blockposition2.getY() / (float) i;
            float f2 = (float) blockposition2.getZ() / (float) i;

            for (int j = 0; j <= i; ++j) {
                BlockPosition blockposition3 = blockposition.offset(MathHelper.floor(0.5F + (float) j * f), MathHelper.floor(0.5F + (float) j * f1), MathHelper.floor(0.5F + (float) j * f2));

                if (flag) {
                    this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition3, worldgenfeaturetreeconfiguration, (iblockdata) -> {
                        return (IBlockData) iblockdata.trySetValue(BlockRotatable.AXIS, this.getLogAxis(blockposition, blockposition3));
                    });
                } else if (!this.isFree(virtuallevelreadable, blockposition3)) {
                    return false;
                }
            }

            return true;
        }
    }

    private int getSteps(BlockPosition blockposition) {
        int i = MathHelper.abs(blockposition.getX());
        int j = MathHelper.abs(blockposition.getY());
        int k = MathHelper.abs(blockposition.getZ());

        return Math.max(i, Math.max(j, k));
    }

    private EnumDirection.EnumAxis getLogAxis(BlockPosition blockposition, BlockPosition blockposition1) {
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

    private boolean trimBranches(int i, int j) {
        return (double) j >= (double) i * 0.2D;
    }

    private void makeBranches(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, List<TrunkPlacerFancy.a> list, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TrunkPlacerFancy.a trunkplacerfancy_a = (TrunkPlacerFancy.a) iterator.next();
            int j = trunkplacerfancy_a.getBranchBase();
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ());

            if (!blockposition1.equals(trunkplacerfancy_a.attachment.pos()) && this.trimBranches(i, j - blockposition.getY())) {
                this.makeLimb(virtuallevelreadable, biconsumer, randomsource, blockposition1, trunkplacerfancy_a.attachment.pos(), true, worldgenfeaturetreeconfiguration);
            }
        }

    }

    private static float treeShape(int i, int j) {
        if ((float) j < (float) i * 0.3F) {
            return -1.0F;
        } else {
            float f = (float) i / 2.0F;
            float f1 = f - (float) j;
            float f2 = MathHelper.sqrt(f * f - f1 * f1);

            if (f1 == 0.0F) {
                f2 = f;
            } else if (Math.abs(f1) >= f) {
                return 0.0F;
            }

            return f2 * 0.5F;
        }
    }

    private static class a {

        final WorldGenFoilagePlacer.a attachment;
        private final int branchBase;

        public a(BlockPosition blockposition, int i) {
            this.attachment = new WorldGenFoilagePlacer.a(blockposition, 0, false);
            this.branchBase = i;
        }

        public int getBranchBase() {
            return this.branchBase;
        }
    }
}

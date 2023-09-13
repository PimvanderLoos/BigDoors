package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import net.minecraft.SystemUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructureJigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class Beardifier implements DensityFunctions.c {

    public static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = 24;
    private static final float[] BEARD_KERNEL = (float[]) SystemUtils.make(new float[13824], (afloat) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    afloat[i * 24 * 24 + j * 24 + k] = (float) computeBeardContribution(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private final ObjectListIterator<Beardifier.a> pieceIterator;
    private final ObjectListIterator<WorldGenFeatureDefinedStructureJigsawJunction> junctionIterator;

    public static Beardifier forStructuresInChunk(StructureManager structuremanager, ChunkCoordIntPair chunkcoordintpair) {
        int i = chunkcoordintpair.getMinBlockX();
        int j = chunkcoordintpair.getMinBlockZ();
        ObjectList<Beardifier.a> objectlist = new ObjectArrayList(10);
        ObjectList<WorldGenFeatureDefinedStructureJigsawJunction> objectlist1 = new ObjectArrayList(32);

        structuremanager.startsForStructure(chunkcoordintpair, (structure) -> {
            return structure.terrainAdaptation() != TerrainAdjustment.NONE;
        }).forEach((structurestart) -> {
            TerrainAdjustment terrainadjustment = structurestart.getStructure().terrainAdaptation();
            Iterator iterator = structurestart.getPieces().iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece.isCloseToChunk(chunkcoordintpair, 12)) {
                    if (structurepiece instanceof WorldGenFeaturePillagerOutpostPoolPiece) {
                        WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = (WorldGenFeaturePillagerOutpostPoolPiece) structurepiece;
                        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturepillageroutpostpoolpiece.getElement().getProjection();

                        if (worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID) {
                            objectlist.add(new Beardifier.a(worldgenfeaturepillageroutpostpoolpiece.getBoundingBox(), terrainadjustment, worldgenfeaturepillageroutpostpoolpiece.getGroundLevelDelta()));
                        }

                        Iterator iterator1 = worldgenfeaturepillageroutpostpoolpiece.getJunctions().iterator();

                        while (iterator1.hasNext()) {
                            WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) iterator1.next();
                            int k = worldgenfeaturedefinedstructurejigsawjunction.getSourceX();
                            int l = worldgenfeaturedefinedstructurejigsawjunction.getSourceZ();

                            if (k > i - 12 && l > j - 12 && k < i + 15 + 12 && l < j + 15 + 12) {
                                objectlist1.add(worldgenfeaturedefinedstructurejigsawjunction);
                            }
                        }
                    } else {
                        objectlist.add(new Beardifier.a(structurepiece.getBoundingBox(), terrainadjustment, 0));
                    }
                }
            }

        });
        return new Beardifier(objectlist.iterator(), objectlist1.iterator());
    }

    @VisibleForTesting
    public Beardifier(ObjectListIterator<Beardifier.a> objectlistiterator, ObjectListIterator<WorldGenFeatureDefinedStructureJigsawJunction> objectlistiterator1) {
        this.pieceIterator = objectlistiterator;
        this.junctionIterator = objectlistiterator1;
    }

    @Override
    public double compute(DensityFunction.b densityfunction_b) {
        int i = densityfunction_b.blockX();
        int j = densityfunction_b.blockY();
        int k = densityfunction_b.blockZ();

        int l;
        int i1;
        double d0;
        double d1;

        for (d1 = 0.0D; this.pieceIterator.hasNext(); d1 += d0) {
            Beardifier.a beardifier_a = (Beardifier.a) this.pieceIterator.next();
            StructureBoundingBox structureboundingbox = beardifier_a.box();

            l = beardifier_a.groundLevelDelta();
            i1 = Math.max(0, Math.max(structureboundingbox.minX() - i, i - structureboundingbox.maxX()));
            int j1 = Math.max(0, Math.max(structureboundingbox.minZ() - k, k - structureboundingbox.maxZ()));
            int k1 = structureboundingbox.minY() + l;
            int l1 = j - k1;
            int i2;

            switch (beardifier_a.terrainAdjustment()) {
                case NONE:
                    i2 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                    i2 = l1;
                    break;
                case BEARD_BOX:
                    i2 = Math.max(0, Math.max(k1 - j, j - structureboundingbox.maxY()));
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            int j2 = i2;

            switch (beardifier_a.terrainAdjustment()) {
                case NONE:
                    d0 = 0.0D;
                    break;
                case BURY:
                    d0 = getBuryContribution(i1, j2, j1);
                    break;
                case BEARD_THIN:
                case BEARD_BOX:
                    d0 = getBeardContribution(i1, j2, j1, l1) * 0.8D;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }
        }

        this.pieceIterator.back(Integer.MAX_VALUE);

        while (this.junctionIterator.hasNext()) {
            WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) this.junctionIterator.next();
            int k2 = i - worldgenfeaturedefinedstructurejigsawjunction.getSourceX();

            l = j - worldgenfeaturedefinedstructurejigsawjunction.getSourceGroundY();
            i1 = k - worldgenfeaturedefinedstructurejigsawjunction.getSourceZ();
            d1 += getBeardContribution(k2, l, i1, l) * 0.4D;
        }

        this.junctionIterator.back(Integer.MAX_VALUE);
        return d1;
    }

    @Override
    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    private static double getBuryContribution(int i, int j, int k) {
        double d0 = MathHelper.length((double) i, (double) j / 2.0D, (double) k);

        return MathHelper.clampedMap(d0, 0.0D, 6.0D, 1.0D, 0.0D);
    }

    private static double getBeardContribution(int i, int j, int k, int l) {
        int i1 = i + 12;
        int j1 = j + 12;
        int k1 = k + 12;

        if (isInKernelRange(i1) && isInKernelRange(j1) && isInKernelRange(k1)) {
            double d0 = (double) l + 0.5D;
            double d1 = MathHelper.lengthSquared((double) i, d0, (double) k);
            double d2 = -d0 * MathHelper.fastInvSqrt(d1 / 2.0D) / 2.0D;

            return d2 * (double) Beardifier.BEARD_KERNEL[k1 * 24 * 24 + i1 * 24 + j1];
        } else {
            return 0.0D;
        }
    }

    private static boolean isInKernelRange(int i) {
        return i >= 0 && i < 24;
    }

    private static double computeBeardContribution(int i, int j, int k) {
        return computeBeardContribution(i, (double) j + 0.5D, k);
    }

    private static double computeBeardContribution(int i, double d0, int j) {
        double d1 = MathHelper.lengthSquared((double) i, d0, (double) j);
        double d2 = Math.pow(2.718281828459045D, -d1 / 16.0D);

        return d2;
    }

    @VisibleForTesting
    public static record a(StructureBoundingBox box, TerrainAdjustment terrainAdjustment, int groundLevelDelta) {

    }
}

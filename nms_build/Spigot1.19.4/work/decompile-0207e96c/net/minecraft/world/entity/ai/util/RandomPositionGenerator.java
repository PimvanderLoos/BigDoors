package net.minecraft.world.entity.ai.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class RandomPositionGenerator {

    private static final int RANDOM_POS_ATTEMPTS = 10;

    public RandomPositionGenerator() {}

    public static BlockPosition generateRandomDirection(RandomSource randomsource, int i, int j) {
        int k = randomsource.nextInt(2 * i + 1) - i;
        int l = randomsource.nextInt(2 * j + 1) - j;
        int i1 = randomsource.nextInt(2 * i + 1) - i;

        return new BlockPosition(k, l, i1);
    }

    @Nullable
    public static BlockPosition generateRandomDirectionWithinRadians(RandomSource randomsource, int i, int j, int k, double d0, double d1, double d2) {
        double d3 = MathHelper.atan2(d1, d0) - 1.5707963705062866D;
        double d4 = d3 + (double) (2.0F * randomsource.nextFloat() - 1.0F) * d2;
        double d5 = Math.sqrt(randomsource.nextDouble()) * (double) MathHelper.SQRT_OF_TWO * (double) i;
        double d6 = -d5 * Math.sin(d4);
        double d7 = d5 * Math.cos(d4);

        if (Math.abs(d6) <= (double) i && Math.abs(d7) <= (double) i) {
            int l = randomsource.nextInt(2 * j + 1) - j + k;

            return BlockPosition.containing(d6, (double) l, d7);
        } else {
            return null;
        }
    }

    @VisibleForTesting
    public static BlockPosition moveUpOutOfSolid(BlockPosition blockposition, int i, Predicate<BlockPosition> predicate) {
        if (!predicate.test(blockposition)) {
            return blockposition;
        } else {
            BlockPosition blockposition1;

            for (blockposition1 = blockposition.above(); blockposition1.getY() < i && predicate.test(blockposition1); blockposition1 = blockposition1.above()) {
                ;
            }

            return blockposition1;
        }
    }

    @VisibleForTesting
    public static BlockPosition moveUpToAboveSolid(BlockPosition blockposition, int i, int j, Predicate<BlockPosition> predicate) {
        if (i < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + i + ", expected >= 0");
        } else if (!predicate.test(blockposition)) {
            return blockposition;
        } else {
            BlockPosition blockposition1;

            for (blockposition1 = blockposition.above(); blockposition1.getY() < j && predicate.test(blockposition1); blockposition1 = blockposition1.above()) {
                ;
            }

            BlockPosition blockposition2;
            BlockPosition blockposition3;

            for (blockposition3 = blockposition1; blockposition3.getY() < j && blockposition3.getY() - blockposition1.getY() < i; blockposition3 = blockposition2) {
                blockposition2 = blockposition3.above();
                if (predicate.test(blockposition2)) {
                    break;
                }
            }

            return blockposition3;
        }
    }

    @Nullable
    public static Vec3D generateRandomPos(EntityCreature entitycreature, Supplier<BlockPosition> supplier) {
        Objects.requireNonNull(entitycreature);
        return generateRandomPos(supplier, entitycreature::getWalkTargetValue);
    }

    @Nullable
    public static Vec3D generateRandomPos(Supplier<BlockPosition> supplier, ToDoubleFunction<BlockPosition> todoublefunction) {
        double d0 = Double.NEGATIVE_INFINITY;
        BlockPosition blockposition = null;

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = (BlockPosition) supplier.get();

            if (blockposition1 != null) {
                double d1 = todoublefunction.applyAsDouble(blockposition1);

                if (d1 > d0) {
                    d0 = d1;
                    blockposition = blockposition1;
                }
            }
        }

        return blockposition != null ? Vec3D.atBottomCenterOf(blockposition) : null;
    }

    public static BlockPosition generateRandomPosTowardDirection(EntityCreature entitycreature, int i, RandomSource randomsource, BlockPosition blockposition) {
        int j = blockposition.getX();
        int k = blockposition.getZ();

        if (entitycreature.hasRestriction() && i > 1) {
            BlockPosition blockposition1 = entitycreature.getRestrictCenter();

            if (entitycreature.getX() > (double) blockposition1.getX()) {
                j -= randomsource.nextInt(i / 2);
            } else {
                j += randomsource.nextInt(i / 2);
            }

            if (entitycreature.getZ() > (double) blockposition1.getZ()) {
                k -= randomsource.nextInt(i / 2);
            } else {
                k += randomsource.nextInt(i / 2);
            }
        }

        return BlockPosition.containing((double) j + entitycreature.getX(), (double) blockposition.getY() + entitycreature.getY(), (double) k + entitycreature.getZ());
    }
}

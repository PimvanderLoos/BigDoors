package net.minecraft.util;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {

    private static final long UUID_VERSION = 61440L;
    private static final long UUID_VERSION_TYPE_4 = 16384L;
    private static final long UUID_VARIANT = -4611686018427387904L;
    private static final long UUID_VARIANT_2 = Long.MIN_VALUE;
    public static final float PI = 3.1415927F;
    public static final float HALF_PI = 1.5707964F;
    public static final float TWO_PI = 6.2831855F;
    public static final float DEG_TO_RAD = 0.017453292F;
    public static final float RAD_TO_DEG = 57.295776F;
    public static final float EPSILON = 1.0E-5F;
    public static final float SQRT_OF_TWO = sqrt(2.0F);
    private static final float SIN_SCALE = 10430.378F;
    private static final float[] SIN = (float[]) SystemUtils.make(new float[65536], (afloat) -> {
        for (int i = 0; i < afloat.length; ++i) {
            afloat[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }

    });
    private static final RandomSource RANDOM = RandomSource.createThreadSafe();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double ONE_SIXTH = 0.16666666666666666D;
    private static final int FRAC_EXP = 8;
    private static final int LUT_SIZE = 257;
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    public MathHelper() {}

    public static float sin(float f) {
        return MathHelper.SIN[(int) (f * 10430.378F) & '\uffff'];
    }

    public static float cos(float f) {
        return MathHelper.SIN[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt((double) f);
    }

    public static int floor(float f) {
        int i = (int) f;

        return f < (float) i ? i - 1 : i;
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

    public static long lfloor(double d0) {
        long i = (long) d0;

        return d0 < (double) i ? i - 1L : i;
    }

    public static float abs(float f) {
        return Math.abs(f);
    }

    public static int abs(int i) {
        return Math.abs(i);
    }

    public static int ceil(float f) {
        int i = (int) f;

        return f > (float) i ? i + 1 : i;
    }

    public static int ceil(double d0) {
        int i = (int) d0;

        return d0 > (double) i ? i + 1 : i;
    }

    public static int clamp(int i, int j, int k) {
        return Math.min(Math.max(i, j), k);
    }

    public static float clamp(float f, float f1, float f2) {
        return f < f1 ? f1 : Math.min(f, f2);
    }

    public static double clamp(double d0, double d1, double d2) {
        return d0 < d1 ? d1 : Math.min(d0, d2);
    }

    public static double clampedLerp(double d0, double d1, double d2) {
        return d2 < 0.0D ? d0 : (d2 > 1.0D ? d1 : lerp(d2, d0, d1));
    }

    public static float clampedLerp(float f, float f1, float f2) {
        return f2 < 0.0F ? f : (f2 > 1.0F ? f1 : lerp(f2, f, f1));
    }

    public static double absMax(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return Math.max(d0, d1);
    }

    public static int floorDiv(int i, int j) {
        return Math.floorDiv(i, j);
    }

    public static int nextInt(RandomSource randomsource, int i, int j) {
        return i >= j ? i : randomsource.nextInt(j - i + 1) + i;
    }

    public static float nextFloat(RandomSource randomsource, float f, float f1) {
        return f >= f1 ? f : randomsource.nextFloat() * (f1 - f) + f;
    }

    public static double nextDouble(RandomSource randomsource, double d0, double d1) {
        return d0 >= d1 ? d0 : randomsource.nextDouble() * (d1 - d0) + d0;
    }

    public static boolean equal(float f, float f1) {
        return Math.abs(f1 - f) < 1.0E-5F;
    }

    public static boolean equal(double d0, double d1) {
        return Math.abs(d1 - d0) < 9.999999747378752E-6D;
    }

    public static int positiveModulo(int i, int j) {
        return Math.floorMod(i, j);
    }

    public static float positiveModulo(float f, float f1) {
        return (f % f1 + f1) % f1;
    }

    public static double positiveModulo(double d0, double d1) {
        return (d0 % d1 + d1) % d1;
    }

    public static boolean isMultipleOf(int i, int j) {
        return i % j == 0;
    }

    public static int wrapDegrees(int i) {
        int j = i % 360;

        if (j >= 180) {
            j -= 360;
        }

        if (j < -180) {
            j += 360;
        }

        return j;
    }

    public static float wrapDegrees(float f) {
        float f1 = f % 360.0F;

        if (f1 >= 180.0F) {
            f1 -= 360.0F;
        }

        if (f1 < -180.0F) {
            f1 += 360.0F;
        }

        return f1;
    }

    public static double wrapDegrees(double d0) {
        double d1 = d0 % 360.0D;

        if (d1 >= 180.0D) {
            d1 -= 360.0D;
        }

        if (d1 < -180.0D) {
            d1 += 360.0D;
        }

        return d1;
    }

    public static float degreesDifference(float f, float f1) {
        return wrapDegrees(f1 - f);
    }

    public static float degreesDifferenceAbs(float f, float f1) {
        return abs(degreesDifference(f, f1));
    }

    public static float rotateIfNecessary(float f, float f1, float f2) {
        float f3 = degreesDifference(f, f1);
        float f4 = clamp(f3, -f2, f2);

        return f1 - f4;
    }

    public static float approach(float f, float f1, float f2) {
        f2 = abs(f2);
        return f < f1 ? clamp(f + f2, f, f1) : clamp(f - f2, f1, f);
    }

    public static float approachDegrees(float f, float f1, float f2) {
        float f3 = degreesDifference(f, f1);

        return approach(f, f + f3, f2);
    }

    public static int getInt(String s, int i) {
        return NumberUtils.toInt(s, i);
    }

    public static int smallestEncompassingPowerOfTwo(int i) {
        int j = i - 1;

        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    public static boolean isPowerOfTwo(int i) {
        return i != 0 && (i & i - 1) == 0;
    }

    public static int ceillog2(int i) {
        i = isPowerOfTwo(i) ? i : smallestEncompassingPowerOfTwo(i);
        return MathHelper.MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) i * 125613361L >> 27) & 31];
    }

    public static int log2(int i) {
        return ceillog2(i) - (isPowerOfTwo(i) ? 0 : 1);
    }

    public static int color(float f, float f1, float f2) {
        return ColorUtil.b.color(0, floor(f * 255.0F), floor(f1 * 255.0F), floor(f2 * 255.0F));
    }

    public static float frac(float f) {
        return f - (float) floor(f);
    }

    public static double frac(double d0) {
        return d0 - (double) lfloor(d0);
    }

    /** @deprecated */
    @Deprecated
    public static long getSeed(BaseBlockPosition baseblockposition) {
        return getSeed(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    /** @deprecated */
    @Deprecated
    public static long getSeed(int i, int j, int k) {
        long l = (long) (i * 3129871) ^ (long) k * 116129781L ^ (long) j;

        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID createInsecureUUID(RandomSource randomsource) {
        long i = randomsource.nextLong() & -61441L | 16384L;
        long j = randomsource.nextLong() & 4611686018427387903L | Long.MIN_VALUE;

        return new UUID(i, j);
    }

    public static UUID createInsecureUUID() {
        return createInsecureUUID(MathHelper.RANDOM);
    }

    public static double inverseLerp(double d0, double d1, double d2) {
        return (d0 - d1) / (d2 - d1);
    }

    public static float inverseLerp(float f, float f1, float f2) {
        return (f - f1) / (f2 - f1);
    }

    public static boolean rayIntersectsAABB(Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb) {
        double d0 = (axisalignedbb.minX + axisalignedbb.maxX) * 0.5D;
        double d1 = (axisalignedbb.maxX - axisalignedbb.minX) * 0.5D;
        double d2 = vec3d.x - d0;

        if (Math.abs(d2) > d1 && d2 * vec3d1.x >= 0.0D) {
            return false;
        } else {
            double d3 = (axisalignedbb.minY + axisalignedbb.maxY) * 0.5D;
            double d4 = (axisalignedbb.maxY - axisalignedbb.minY) * 0.5D;
            double d5 = vec3d.y - d3;

            if (Math.abs(d5) > d4 && d5 * vec3d1.y >= 0.0D) {
                return false;
            } else {
                double d6 = (axisalignedbb.minZ + axisalignedbb.maxZ) * 0.5D;
                double d7 = (axisalignedbb.maxZ - axisalignedbb.minZ) * 0.5D;
                double d8 = vec3d.z - d6;

                if (Math.abs(d8) > d7 && d8 * vec3d1.z >= 0.0D) {
                    return false;
                } else {
                    double d9 = Math.abs(vec3d1.x);
                    double d10 = Math.abs(vec3d1.y);
                    double d11 = Math.abs(vec3d1.z);
                    double d12 = vec3d1.y * d8 - vec3d1.z * d5;

                    if (Math.abs(d12) > d4 * d11 + d7 * d10) {
                        return false;
                    } else {
                        d12 = vec3d1.z * d2 - vec3d1.x * d8;
                        if (Math.abs(d12) > d1 * d11 + d7 * d9) {
                            return false;
                        } else {
                            d12 = vec3d1.x * d5 - vec3d1.y * d2;
                            return Math.abs(d12) < d1 * d10 + d4 * d9;
                        }
                    }
                }
            }
        }
    }

    public static double atan2(double d0, double d1) {
        double d2 = d1 * d1 + d0 * d0;

        if (Double.isNaN(d2)) {
            return Double.NaN;
        } else {
            boolean flag = d0 < 0.0D;

            if (flag) {
                d0 = -d0;
            }

            boolean flag1 = d1 < 0.0D;

            if (flag1) {
                d1 = -d1;
            }

            boolean flag2 = d0 > d1;
            double d3;

            if (flag2) {
                d3 = d1;
                d1 = d0;
                d0 = d3;
            }

            d3 = fastInvSqrt(d2);
            d1 *= d3;
            d0 *= d3;
            double d4 = MathHelper.FRAC_BIAS + d0;
            int i = (int) Double.doubleToRawLongBits(d4);
            double d5 = MathHelper.ASIN_TAB[i];
            double d6 = MathHelper.COS_TAB[i];
            double d7 = d4 - MathHelper.FRAC_BIAS;
            double d8 = d0 * d6 - d1 * d7;
            double d9 = (6.0D + d8 * d8) * d8 * 0.16666666666666666D;
            double d10 = d5 + d9;

            if (flag2) {
                d10 = 1.5707963267948966D - d10;
            }

            if (flag1) {
                d10 = 3.141592653589793D - d10;
            }

            if (flag) {
                d10 = -d10;
            }

            return d10;
        }
    }

    public static float invSqrt(float f) {
        return org.joml.Math.invsqrt(f);
    }

    public static double invSqrt(double d0) {
        return org.joml.Math.invsqrt(d0);
    }

    /** @deprecated */
    @Deprecated
    public static double fastInvSqrt(double d0) {
        double d1 = 0.5D * d0;
        long i = Double.doubleToRawLongBits(d0);

        i = 6910469410427058090L - (i >> 1);
        d0 = Double.longBitsToDouble(i);
        d0 *= 1.5D - d1 * d0 * d0;
        return d0;
    }

    public static float fastInvCubeRoot(float f) {
        int i = Float.floatToIntBits(f);

        i = 1419967116 - i / 3;
        float f1 = Float.intBitsToFloat(i);

        f1 = 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
        f1 = 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
        return f1;
    }

    public static int hsvToRgb(float f, float f1, float f2) {
        int i = (int) (f * 6.0F) % 6;
        float f3 = f * 6.0F - (float) i;
        float f4 = f2 * (1.0F - f1);
        float f5 = f2 * (1.0F - f3 * f1);
        float f6 = f2 * (1.0F - (1.0F - f3) * f1);
        float f7;
        float f8;
        float f9;

        switch (i) {
            case 0:
                f7 = f2;
                f8 = f6;
                f9 = f4;
                break;
            case 1:
                f7 = f5;
                f8 = f2;
                f9 = f4;
                break;
            case 2:
                f7 = f4;
                f8 = f2;
                f9 = f6;
                break;
            case 3:
                f7 = f4;
                f8 = f5;
                f9 = f2;
                break;
            case 4:
                f7 = f6;
                f8 = f4;
                f9 = f2;
                break;
            case 5:
                f7 = f2;
                f8 = f4;
                f9 = f5;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + f1 + ", " + f2);
        }

        return ColorUtil.b.color(0, clamp((int) (f7 * 255.0F), 0, 255), clamp((int) (f8 * 255.0F), 0, 255), clamp((int) (f9 * 255.0F), 0, 255));
    }

    public static int murmurHash3Mixer(int i) {
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return i;
    }

    public static int binarySearch(int i, int j, IntPredicate intpredicate) {
        int k = j - i;

        while (k > 0) {
            int l = k / 2;
            int i1 = i + l;

            if (intpredicate.test(i1)) {
                k = l;
            } else {
                i = i1 + 1;
                k -= l + 1;
            }
        }

        return i;
    }

    public static int lerpInt(float f, int i, int j) {
        return i + floor(f * (float) (j - i));
    }

    public static float lerp(float f, float f1, float f2) {
        return f1 + f * (f2 - f1);
    }

    public static double lerp(double d0, double d1, double d2) {
        return d1 + d0 * (d2 - d1);
    }

    public static double lerp2(double d0, double d1, double d2, double d3, double d4, double d5) {
        return lerp(d1, lerp(d0, d2, d3), lerp(d0, d4, d5));
    }

    public static double lerp3(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10) {
        return lerp(d2, lerp2(d0, d1, d3, d4, d5, d6), lerp2(d0, d1, d7, d8, d9, d10));
    }

    public static float catmullrom(float f, float f1, float f2, float f3, float f4) {
        return 0.5F * (2.0F * f2 + (f3 - f1) * f + (2.0F * f1 - 5.0F * f2 + 4.0F * f3 - f4) * f * f + (3.0F * f2 - f1 - 3.0F * f3 + f4) * f * f * f);
    }

    public static double smoothstep(double d0) {
        return d0 * d0 * d0 * (d0 * (d0 * 6.0D - 15.0D) + 10.0D);
    }

    public static double smoothstepDerivative(double d0) {
        return 30.0D * d0 * d0 * (d0 - 1.0D) * (d0 - 1.0D);
    }

    public static int sign(double d0) {
        return d0 == 0.0D ? 0 : (d0 > 0.0D ? 1 : -1);
    }

    public static float rotLerp(float f, float f1, float f2) {
        return f1 + f * wrapDegrees(f2 - f1);
    }

    public static float triangleWave(float f, float f1) {
        return (Math.abs(f % f1 - f1 * 0.5F) - f1 * 0.25F) / (f1 * 0.25F);
    }

    public static float square(float f) {
        return f * f;
    }

    public static double square(double d0) {
        return d0 * d0;
    }

    public static int square(int i) {
        return i * i;
    }

    public static long square(long i) {
        return i * i;
    }

    public static double clampedMap(double d0, double d1, double d2, double d3, double d4) {
        return clampedLerp(d3, d4, inverseLerp(d0, d1, d2));
    }

    public static float clampedMap(float f, float f1, float f2, float f3, float f4) {
        return clampedLerp(f3, f4, inverseLerp(f, f1, f2));
    }

    public static double map(double d0, double d1, double d2, double d3, double d4) {
        return lerp(inverseLerp(d0, d1, d2), d3, d4);
    }

    public static float map(float f, float f1, float f2, float f3, float f4) {
        return lerp(inverseLerp(f, f1, f2), f3, f4);
    }

    public static double wobble(double d0) {
        return d0 + (2.0D * RandomSource.create((long) floor(d0 * 3000.0D)).nextDouble() - 1.0D) * 1.0E-7D / 2.0D;
    }

    public static int roundToward(int i, int j) {
        return positiveCeilDiv(i, j) * j;
    }

    public static int positiveCeilDiv(int i, int j) {
        return -Math.floorDiv(-i, j);
    }

    public static int randomBetweenInclusive(RandomSource randomsource, int i, int j) {
        return randomsource.nextInt(j - i + 1) + i;
    }

    public static float randomBetween(RandomSource randomsource, float f, float f1) {
        return randomsource.nextFloat() * (f1 - f) + f;
    }

    public static float normal(RandomSource randomsource, float f, float f1) {
        return f + (float) randomsource.nextGaussian() * f1;
    }

    public static double lengthSquared(double d0, double d1) {
        return d0 * d0 + d1 * d1;
    }

    public static double length(double d0, double d1) {
        return Math.sqrt(lengthSquared(d0, d1));
    }

    public static double lengthSquared(double d0, double d1, double d2) {
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public static double length(double d0, double d1, double d2) {
        return Math.sqrt(lengthSquared(d0, d1, d2));
    }

    public static int quantize(double d0, int i) {
        return floor(d0 / (double) i) * i;
    }

    public static IntStream outFromOrigin(int i, int j, int k) {
        return outFromOrigin(i, j, k, 1);
    }

    public static IntStream outFromOrigin(int i, int j, int k, int l) {
        if (j > k) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", k, j));
        } else if (l < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", l));
        } else {
            return i >= j && i <= k ? IntStream.iterate(i, (i1) -> {
                int j1 = Math.abs(i - i1);

                return i - j1 >= j || i + j1 <= k;
            }, (i1) -> {
                boolean flag = i1 <= i;
                int j1 = Math.abs(i - i1);
                boolean flag1 = i + j1 + l <= k;

                if (!flag || !flag1) {
                    int k1 = i - j1 - (flag ? l : 0);

                    if (k1 >= j) {
                        return k1;
                    }
                }

                return i + j1 + l;
            }) : IntStream.empty();
        }
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d0 = (double) i / 256.0D;
            double d1 = Math.asin(d0);

            MathHelper.COS_TAB[i] = Math.cos(d1);
            MathHelper.ASIN_TAB[i] = d1;
        }

    }
}

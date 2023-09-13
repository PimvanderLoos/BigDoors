package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {

    private static final int BIG_ENOUGH_INT = 1024;
    private static final float BIG_ENOUGH_FLOAT = 1024.0F;
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
    public static final float SQRT_OF_TWO = c(2.0F);
    private static final float SIN_SCALE = 10430.378F;
    private static final float[] SIN = (float[]) SystemUtils.a((Object) (new float[65536]), (afloat) -> {
        for (int i = 0; i < afloat.length; ++i) {
            afloat[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }

    });
    private static final Random RANDOM = new Random();
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

    public static float c(float f) {
        return (float) Math.sqrt((double) f);
    }

    public static int d(float f) {
        int i = (int) f;

        return f < (float) i ? i - 1 : i;
    }

    public static int a(double d0) {
        return (int) (d0 + 1024.0D) - 1024;
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

    public static long c(double d0) {
        long i = (long) d0;

        return d0 < (double) i ? i - 1L : i;
    }

    public static int d(double d0) {
        return (int) (d0 >= 0.0D ? d0 : -d0 + 1.0D);
    }

    public static float e(float f) {
        return Math.abs(f);
    }

    public static int a(int i) {
        return Math.abs(i);
    }

    public static int f(float f) {
        int i = (int) f;

        return f > (float) i ? i + 1 : i;
    }

    public static int e(double d0) {
        int i = (int) d0;

        return d0 > (double) i ? i + 1 : i;
    }

    public static byte a(byte b0, byte b1, byte b2) {
        return b0 < b1 ? b1 : (b0 > b2 ? b2 : b0);
    }

    public static int clamp(int i, int j, int k) {
        return i < j ? j : (i > k ? k : i);
    }

    public static long a(long i, long j, long k) {
        return i < j ? j : (i > k ? k : i);
    }

    public static float a(float f, float f1, float f2) {
        return f < f1 ? f1 : (f > f2 ? f2 : f);
    }

    public static double a(double d0, double d1, double d2) {
        return d0 < d1 ? d1 : (d0 > d2 ? d2 : d0);
    }

    public static double b(double d0, double d1, double d2) {
        return d2 < 0.0D ? d0 : (d2 > 1.0D ? d1 : d(d2, d0, d1));
    }

    public static float b(float f, float f1, float f2) {
        return f2 < 0.0F ? f : (f2 > 1.0F ? f1 : h(f2, f, f1));
    }

    public static double a(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

    public static int a(int i, int j) {
        return Math.floorDiv(i, j);
    }

    public static int nextInt(Random random, int i, int j) {
        return i >= j ? i : random.nextInt(j - i + 1) + i;
    }

    public static float a(Random random, float f, float f1) {
        return f >= f1 ? f : random.nextFloat() * (f1 - f) + f;
    }

    public static double a(Random random, double d0, double d1) {
        return d0 >= d1 ? d0 : random.nextDouble() * (d1 - d0) + d0;
    }

    public static double a(long[] along) {
        long i = 0L;
        long[] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long l = along1[k];

            i += l;
        }

        return (double) i / (double) along.length;
    }

    public static boolean a(float f, float f1) {
        return Math.abs(f1 - f) < 1.0E-5F;
    }

    public static boolean b(double d0, double d1) {
        return Math.abs(d1 - d0) < 9.999999747378752E-6D;
    }

    public static int b(int i, int j) {
        return Math.floorMod(i, j);
    }

    public static float b(float f, float f1) {
        return (f % f1 + f1) % f1;
    }

    public static double c(double d0, double d1) {
        return (d0 % d1 + d1) % d1;
    }

    public static int b(int i) {
        int j = i % 360;

        if (j >= 180) {
            j -= 360;
        }

        if (j < -180) {
            j += 360;
        }

        return j;
    }

    public static float g(float f) {
        float f1 = f % 360.0F;

        if (f1 >= 180.0F) {
            f1 -= 360.0F;
        }

        if (f1 < -180.0F) {
            f1 += 360.0F;
        }

        return f1;
    }

    public static double f(double d0) {
        double d1 = d0 % 360.0D;

        if (d1 >= 180.0D) {
            d1 -= 360.0D;
        }

        if (d1 < -180.0D) {
            d1 += 360.0D;
        }

        return d1;
    }

    public static float c(float f, float f1) {
        return g(f1 - f);
    }

    public static float d(float f, float f1) {
        return e(c(f, f1));
    }

    public static float c(float f, float f1, float f2) {
        float f3 = c(f, f1);
        float f4 = a(f3, -f2, f2);

        return f1 - f4;
    }

    public static float d(float f, float f1, float f2) {
        f2 = e(f2);
        return f < f1 ? a(f + f2, f, f1) : a(f - f2, f1, f);
    }

    public static float e(float f, float f1, float f2) {
        float f3 = c(f, f1);

        return d(f, f + f3, f2);
    }

    public static int a(String s, int i) {
        return NumberUtils.toInt(s, i);
    }

    public static int a(String s, int i, int j) {
        return Math.max(j, a(s, i));
    }

    public static double a(String s, double d0) {
        try {
            return Double.parseDouble(s);
        } catch (Throwable throwable) {
            return d0;
        }
    }

    public static double a(String s, double d0, double d1) {
        return Math.max(d1, a(s, d0));
    }

    public static int c(int i) {
        int j = i - 1;

        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    public static boolean d(int i) {
        return i != 0 && (i & i - 1) == 0;
    }

    public static int e(int i) {
        i = d(i) ? i : c(i);
        return MathHelper.MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) i * 125613361L >> 27) & 31];
    }

    public static int f(int i) {
        return e(i) - (d(i) ? 0 : 1);
    }

    public static int f(float f, float f1, float f2) {
        return b(d(f * 255.0F), d(f1 * 255.0F), d(f2 * 255.0F));
    }

    public static int b(int i, int j, int k) {
        int l = (i << 8) + j;

        l = (l << 8) + k;
        return l;
    }

    public static int c(int i, int j) {
        int k = (i & 16711680) >> 16;
        int l = (j & 16711680) >> 16;
        int i1 = (i & '\uff00') >> 8;
        int j1 = (j & '\uff00') >> 8;
        int k1 = (i & 255) >> 0;
        int l1 = (j & 255) >> 0;
        int i2 = (int) ((float) k * (float) l / 255.0F);
        int j2 = (int) ((float) i1 * (float) j1 / 255.0F);
        int k2 = (int) ((float) k1 * (float) l1 / 255.0F);

        return i & -16777216 | i2 << 16 | j2 << 8 | k2;
    }

    public static int a(int i, float f, float f1, float f2) {
        int j = (i & 16711680) >> 16;
        int k = (i & '\uff00') >> 8;
        int l = (i & 255) >> 0;
        int i1 = (int) ((float) j * f);
        int j1 = (int) ((float) k * f1);
        int k1 = (int) ((float) l * f2);

        return i & -16777216 | i1 << 16 | j1 << 8 | k1;
    }

    public static float h(float f) {
        return f - (float) d(f);
    }

    public static double g(double d0) {
        return d0 - (double) c(d0);
    }

    public static Vec3D a(Vec3D vec3d, Vec3D vec3d1, Vec3D vec3d2, Vec3D vec3d3, double d0) {
        double d1 = ((-d0 + 2.0D) * d0 - 1.0D) * d0 * 0.5D;
        double d2 = ((3.0D * d0 - 5.0D) * d0 * d0 + 2.0D) * 0.5D;
        double d3 = ((-3.0D * d0 + 4.0D) * d0 + 1.0D) * d0 * 0.5D;
        double d4 = (d0 - 1.0D) * d0 * d0 * 0.5D;

        return new Vec3D(vec3d.x * d1 + vec3d1.x * d2 + vec3d2.x * d3 + vec3d3.x * d4, vec3d.y * d1 + vec3d1.y * d2 + vec3d2.y * d3 + vec3d3.y * d4, vec3d.z * d1 + vec3d1.z * d2 + vec3d2.z * d3 + vec3d3.z * d4);
    }

    public static long a(BaseBlockPosition baseblockposition) {
        return c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static long c(int i, int j, int k) {
        long l = (long) (i * 3129871) ^ (long) k * 116129781L ^ (long) j;

        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID a(Random random) {
        long i = random.nextLong() & -61441L | 16384L;
        long j = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;

        return new UUID(i, j);
    }

    public static UUID a() {
        return a(MathHelper.RANDOM);
    }

    public static double c(double d0, double d1, double d2) {
        return (d0 - d1) / (d2 - d1);
    }

    public static boolean a(Vec3D vec3d, Vec3D vec3d1, AxisAlignedBB axisalignedbb) {
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

    public static double d(double d0, double d1) {
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

            d3 = h(d2);
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

    public static float i(float f) {
        float f1 = 0.5F * f;
        int i = Float.floatToIntBits(f);

        i = 1597463007 - (i >> 1);
        f = Float.intBitsToFloat(i);
        f *= 1.5F - f1 * f * f;
        return f;
    }

    public static double h(double d0) {
        double d1 = 0.5D * d0;
        long i = Double.doubleToRawLongBits(d0);

        i = 6910469410427058090L - (i >> 1);
        d0 = Double.longBitsToDouble(i);
        d0 *= 1.5D - d1 * d0 * d0;
        return d0;
    }

    public static float j(float f) {
        int i = Float.floatToIntBits(f);

        i = 1419967116 - i / 3;
        float f1 = Float.intBitsToFloat(i);

        f1 = 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
        f1 = 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
        return f1;
    }

    public static int g(float f, float f1, float f2) {
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

        int j = clamp((int) (f7 * 255.0F), 0, 255);
        int k = clamp((int) (f8 * 255.0F), 0, 255);
        int l = clamp((int) (f9 * 255.0F), 0, 255);

        return j << 16 | k << 8 | l;
    }

    public static int g(int i) {
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return i;
    }

    public static long a(long i) {
        i ^= i >>> 33;
        i *= -49064778989728563L;
        i ^= i >>> 33;
        i *= -4265267296055464877L;
        i ^= i >>> 33;
        return i;
    }

    public static double[] a(double... adouble) {
        float f = 0.0F;
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            f = (float) ((double) f + d0);
        }

        int k;

        for (k = 0; k < adouble.length; ++k) {
            adouble[k] /= (double) f;
        }

        for (k = 0; k < adouble.length; ++k) {
            adouble[k] += k == 0 ? 0.0D : adouble[k - 1];
        }

        return adouble;
    }

    public static int a(Random random, double[] adouble) {
        double d0 = random.nextDouble();

        for (int i = 0; i < adouble.length; ++i) {
            if (d0 < adouble[i]) {
                return i;
            }
        }

        return adouble.length;
    }

    public static double[] a(double d0, double d1, double d2, int i, int j) {
        double[] adouble = new double[j - i + 1];
        int k = 0;

        for (int l = i; l <= j; ++l) {
            adouble[k] = Math.max(0.0D, d0 * StrictMath.exp(-((double) l - d2) * ((double) l - d2) / (2.0D * d1 * d1)));
            ++k;
        }

        return adouble;
    }

    public static double[] a(double d0, double d1, double d2, double d3, double d4, double d5, int i, int j) {
        double[] adouble = new double[j - i + 1];
        int k = 0;

        for (int l = i; l <= j; ++l) {
            adouble[k] = Math.max(0.0D, d0 * StrictMath.exp(-((double) l - d2) * ((double) l - d2) / (2.0D * d1 * d1)) + d3 * StrictMath.exp(-((double) l - d5) * ((double) l - d5) / (2.0D * d4 * d4)));
            ++k;
        }

        return adouble;
    }

    public static double[] a(double d0, double d1, int i, int j) {
        double[] adouble = new double[j - i + 1];
        int k = 0;

        for (int l = i; l <= j; ++l) {
            adouble[k] = Math.max(d0 * StrictMath.log((double) l) + d1, 0.0D);
            ++k;
        }

        return adouble;
    }

    public static int a(int i, int j, IntPredicate intpredicate) {
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

    public static float h(float f, float f1, float f2) {
        return f1 + f * (f2 - f1);
    }

    public static double d(double d0, double d1, double d2) {
        return d1 + d0 * (d2 - d1);
    }

    public static double a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return d(d1, d(d0, d2, d3), d(d0, d4, d5));
    }

    public static double a(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10) {
        return d(d2, a(d0, d1, d3, d4, d5, d6), a(d0, d1, d7, d8, d9, d10));
    }

    public static double i(double d0) {
        return d0 * d0 * d0 * (d0 * (d0 * 6.0D - 15.0D) + 10.0D);
    }

    public static double j(double d0) {
        return 30.0D * d0 * d0 * (d0 - 1.0D) * (d0 - 1.0D);
    }

    public static int k(double d0) {
        return d0 == 0.0D ? 0 : (d0 > 0.0D ? 1 : -1);
    }

    public static float i(float f, float f1, float f2) {
        return f1 + f * g(f2 - f1);
    }

    public static float j(float f, float f1, float f2) {
        return Math.min(f * f * 0.6F + f1 * f1 * ((3.0F + f1) / 4.0F) + f2 * f2 * 0.8F, 1.0F);
    }

    @Deprecated
    public static float k(float f, float f1, float f2) {
        float f3;

        for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F) {
            ;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        return f + f2 * f3;
    }

    @Deprecated
    public static float l(double d0) {
        while (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        while (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return (float) d0;
    }

    public static float e(float f, float f1) {
        return (Math.abs(f % f1 - f1 * 0.5F) - f1 * 0.25F) / (f1 * 0.25F);
    }

    public static float k(float f) {
        return f * f;
    }

    public static double m(double d0) {
        return d0 * d0;
    }

    public static int h(int i) {
        return i * i;
    }

    public static double a(double d0, double d1, double d2, double d3, double d4) {
        return b(d3, d4, c(d0, d1, d2));
    }

    public static double b(double d0, double d1, double d2, double d3, double d4) {
        return d(c(d0, d1, d2), d3, d4);
    }

    public static double n(double d0) {
        return d0 + (2.0D * (new Random((long) floor(d0 * 3000.0D))).nextDouble() - 1.0D) * 1.0E-7D / 2.0D;
    }

    public static int d(int i, int j) {
        return (i + j - 1) / j * j;
    }

    public static int b(Random random, int i, int j) {
        return random.nextInt(j - i + 1) + i;
    }

    public static float b(Random random, float f, float f1) {
        return random.nextFloat() * (f1 - f) + f;
    }

    public static float c(Random random, float f, float f1) {
        return f + (float) random.nextGaussian() * f1;
    }

    public static double a(int i, double d0, int j) {
        return Math.sqrt((double) (i * i) + d0 * d0 + (double) (j * j));
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

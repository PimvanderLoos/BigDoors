package net.minecraft.data.worldgen;

import net.minecraft.util.CubicSpline;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class TerrainProvider {

    private static final float DEEP_OCEAN_CONTINENTALNESS = -0.51F;
    private static final float OCEAN_CONTINENTALNESS = -0.4F;
    private static final float PLAINS_CONTINENTALNESS = 0.1F;
    private static final float BEACH_CONTINENTALNESS = -0.15F;
    private static final ToFloatFunction<Float> NO_TRANSFORM = ToFloatFunction.IDENTITY;
    private static final ToFloatFunction<Float> AMPLIFIED_OFFSET = ToFloatFunction.createUnlimited((f) -> {
        return f < 0.0F ? f : f * 2.0F;
    });
    private static final ToFloatFunction<Float> AMPLIFIED_FACTOR = ToFloatFunction.createUnlimited((f) -> {
        return 1.25F - 6.25F / (f + 5.0F);
    });
    private static final ToFloatFunction<Float> AMPLIFIED_JAGGEDNESS = ToFloatFunction.createUnlimited((f) -> {
        return f * 2.0F;
    });

    public TerrainProvider() {}

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldOffset(I i0, I i1, I i2, boolean flag) {
        ToFloatFunction<Float> tofloatfunction = flag ? TerrainProvider.AMPLIFIED_OFFSET : TerrainProvider.NO_TRANSFORM;
        CubicSpline<C, I> cubicspline = buildErosionOffsetSpline(i1, i2, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, tofloatfunction);
        CubicSpline<C, I> cubicspline1 = buildErosionOffsetSpline(i1, i2, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, tofloatfunction);
        CubicSpline<C, I> cubicspline2 = buildErosionOffsetSpline(i1, i2, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, tofloatfunction);
        CubicSpline<C, I> cubicspline3 = buildErosionOffsetSpline(i1, i2, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, tofloatfunction);

        return CubicSpline.builder(i0, tofloatfunction).addPoint(-1.1F, 0.044F).addPoint(-1.02F, -0.2222F).addPoint(-0.51F, -0.2222F).addPoint(-0.44F, -0.12F).addPoint(-0.18F, -0.12F).addPoint(-0.16F, cubicspline).addPoint(-0.15F, cubicspline).addPoint(-0.1F, cubicspline1).addPoint(0.25F, cubicspline2).addPoint(1.0F, cubicspline3).build();
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldFactor(I i0, I i1, I i2, I i3, boolean flag) {
        ToFloatFunction<Float> tofloatfunction = flag ? TerrainProvider.AMPLIFIED_FACTOR : TerrainProvider.NO_TRANSFORM;

        return CubicSpline.builder(i0, TerrainProvider.NO_TRANSFORM).addPoint(-0.19F, 3.95F).addPoint(-0.15F, getErosionFactor(i1, i2, i3, 6.25F, true, TerrainProvider.NO_TRANSFORM)).addPoint(-0.1F, getErosionFactor(i1, i2, i3, 5.47F, true, tofloatfunction)).addPoint(0.03F, getErosionFactor(i1, i2, i3, 5.08F, true, tofloatfunction)).addPoint(0.06F, getErosionFactor(i1, i2, i3, 4.69F, false, tofloatfunction)).build();
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldJaggedness(I i0, I i1, I i2, I i3, boolean flag) {
        ToFloatFunction<Float> tofloatfunction = flag ? TerrainProvider.AMPLIFIED_JAGGEDNESS : TerrainProvider.NO_TRANSFORM;
        float f = 0.65F;

        return CubicSpline.builder(i0, tofloatfunction).addPoint(-0.11F, 0.0F).addPoint(0.03F, buildErosionJaggednessSpline(i1, i2, i3, 1.0F, 0.5F, 0.0F, 0.0F, tofloatfunction)).addPoint(0.65F, buildErosionJaggednessSpline(i1, i2, i3, 1.0F, 1.0F, 1.0F, 0.0F, tofloatfunction)).build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionJaggednessSpline(I i0, I i1, I i2, float f, float f1, float f2, float f3, ToFloatFunction<Float> tofloatfunction) {
        float f4 = -0.5775F;
        CubicSpline<C, I> cubicspline = buildRidgeJaggednessSpline(i1, i2, f, f2, tofloatfunction);
        CubicSpline<C, I> cubicspline1 = buildRidgeJaggednessSpline(i1, i2, f1, f3, tofloatfunction);

        return CubicSpline.builder(i0, tofloatfunction).addPoint(-1.0F, cubicspline).addPoint(-0.78F, cubicspline1).addPoint(-0.5775F, cubicspline1).addPoint(-0.375F, 0.0F).build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildRidgeJaggednessSpline(I i0, I i1, float f, float f1, ToFloatFunction<Float> tofloatfunction) {
        float f2 = NoiseRouterData.peaksAndValleys(0.4F);
        float f3 = NoiseRouterData.peaksAndValleys(0.56666666F);
        float f4 = (f2 + f3) / 2.0F;
        CubicSpline.b<C, I> cubicspline_b = CubicSpline.builder(i1, tofloatfunction);

        cubicspline_b.addPoint(f2, 0.0F);
        if (f1 > 0.0F) {
            cubicspline_b.addPoint(f4, buildWeirdnessJaggednessSpline(i0, f1, tofloatfunction));
        } else {
            cubicspline_b.addPoint(f4, 0.0F);
        }

        if (f > 0.0F) {
            cubicspline_b.addPoint(1.0F, buildWeirdnessJaggednessSpline(i0, f, tofloatfunction));
        } else {
            cubicspline_b.addPoint(1.0F, 0.0F);
        }

        return cubicspline_b.build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildWeirdnessJaggednessSpline(I i0, float f, ToFloatFunction<Float> tofloatfunction) {
        float f1 = 0.63F * f;
        float f2 = 0.3F * f;

        return CubicSpline.builder(i0, tofloatfunction).addPoint(-0.01F, f1).addPoint(0.01F, f2).build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> getErosionFactor(I i0, I i1, I i2, float f, boolean flag, ToFloatFunction<Float> tofloatfunction) {
        CubicSpline<C, I> cubicspline = CubicSpline.builder(i1, tofloatfunction).addPoint(-0.2F, 6.3F).addPoint(0.2F, f).build();
        CubicSpline.b<C, I> cubicspline_b = CubicSpline.builder(i0, tofloatfunction).addPoint(-0.6F, cubicspline).addPoint(-0.5F, CubicSpline.builder(i1, tofloatfunction).addPoint(-0.05F, 6.3F).addPoint(0.05F, 2.67F).build()).addPoint(-0.35F, cubicspline).addPoint(-0.25F, cubicspline).addPoint(-0.1F, CubicSpline.builder(i1, tofloatfunction).addPoint(-0.05F, 2.67F).addPoint(0.05F, 6.3F).build()).addPoint(0.03F, cubicspline);
        CubicSpline cubicspline1;
        CubicSpline cubicspline2;

        if (flag) {
            cubicspline1 = CubicSpline.builder(i1, tofloatfunction).addPoint(0.0F, f).addPoint(0.1F, 0.625F).build();
            cubicspline2 = CubicSpline.builder(i2, tofloatfunction).addPoint(-0.9F, f).addPoint(-0.69F, cubicspline1).build();
            cubicspline_b.addPoint(0.35F, f).addPoint(0.45F, cubicspline2).addPoint(0.55F, cubicspline2).addPoint(0.62F, f);
        } else {
            cubicspline1 = CubicSpline.builder(i2, tofloatfunction).addPoint(-0.7F, cubicspline).addPoint(-0.15F, 1.37F).build();
            cubicspline2 = CubicSpline.builder(i2, tofloatfunction).addPoint(0.45F, cubicspline).addPoint(0.7F, 1.56F).build();
            cubicspline_b.addPoint(0.05F, cubicspline2).addPoint(0.4F, cubicspline2).addPoint(0.45F, cubicspline1).addPoint(0.55F, cubicspline1).addPoint(0.58F, f);
        }

        return cubicspline_b.build();
    }

    private static float calculateSlope(float f, float f1, float f2, float f3) {
        return (f1 - f) / (f3 - f2);
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildMountainRidgeSplineWithPoints(I i0, float f, boolean flag, ToFloatFunction<Float> tofloatfunction) {
        CubicSpline.b<C, I> cubicspline_b = CubicSpline.builder(i0, tofloatfunction);
        float f1 = -0.7F;
        float f2 = -1.0F;
        float f3 = mountainContinentalness(-1.0F, f, -0.7F);
        float f4 = 1.0F;
        float f5 = mountainContinentalness(1.0F, f, -0.7F);
        float f6 = calculateMountainRidgeZeroContinentalnessPoint(f);
        float f7 = -0.65F;
        float f8;

        if (-0.65F < f6 && f6 < 1.0F) {
            f8 = mountainContinentalness(-0.65F, f, -0.7F);
            float f9 = -0.75F;
            float f10 = mountainContinentalness(-0.75F, f, -0.7F);
            float f11 = calculateSlope(f3, f10, -1.0F, -0.75F);

            cubicspline_b.addPoint(-1.0F, f3, f11);
            cubicspline_b.addPoint(-0.75F, f10);
            cubicspline_b.addPoint(-0.65F, f8);
            float f12 = mountainContinentalness(f6, f, -0.7F);
            float f13 = calculateSlope(f12, f5, f6, 1.0F);
            float f14 = 0.01F;

            cubicspline_b.addPoint(f6 - 0.01F, f12);
            cubicspline_b.addPoint(f6, f12, f13);
            cubicspline_b.addPoint(1.0F, f5, f13);
        } else {
            f8 = calculateSlope(f3, f5, -1.0F, 1.0F);
            if (flag) {
                cubicspline_b.addPoint(-1.0F, Math.max(0.2F, f3));
                cubicspline_b.addPoint(0.0F, MathHelper.lerp(0.5F, f3, f5), f8);
            } else {
                cubicspline_b.addPoint(-1.0F, f3, f8);
            }

            cubicspline_b.addPoint(1.0F, f5, f8);
        }

        return cubicspline_b.build();
    }

    private static float mountainContinentalness(float f, float f1, float f2) {
        float f3 = 1.17F;
        float f4 = 0.46082947F;
        float f5 = 1.0F - (1.0F - f1) * 0.5F;
        float f6 = 0.5F * (1.0F - f1);
        float f7 = (f + 1.17F) * 0.46082947F;
        float f8 = f7 * f5 - f6;

        return f < f2 ? Math.max(f8, -0.2222F) : Math.max(f8, 0.0F);
    }

    private static float calculateMountainRidgeZeroContinentalnessPoint(float f) {
        float f1 = 1.17F;
        float f2 = 0.46082947F;
        float f3 = 1.0F - (1.0F - f) * 0.5F;
        float f4 = 0.5F * (1.0F - f);

        return f4 / (0.46082947F * f3) - 1.17F;
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionOffsetSpline(I i0, I i1, float f, float f1, float f2, float f3, float f4, float f5, boolean flag, boolean flag1, ToFloatFunction<Float> tofloatfunction) {
        float f6 = 0.6F;
        float f7 = 0.5F;
        float f8 = 0.5F;
        CubicSpline<C, I> cubicspline = buildMountainRidgeSplineWithPoints(i1, MathHelper.lerp(f3, 0.6F, 1.5F), flag1, tofloatfunction);
        CubicSpline<C, I> cubicspline1 = buildMountainRidgeSplineWithPoints(i1, MathHelper.lerp(f3, 0.6F, 1.0F), flag1, tofloatfunction);
        CubicSpline<C, I> cubicspline2 = buildMountainRidgeSplineWithPoints(i1, f3, flag1, tofloatfunction);
        CubicSpline<C, I> cubicspline3 = ridgeSpline(i1, f - 0.15F, 0.5F * f3, MathHelper.lerp(0.5F, 0.5F, 0.5F) * f3, 0.5F * f3, 0.6F * f3, 0.5F, tofloatfunction);
        CubicSpline<C, I> cubicspline4 = ridgeSpline(i1, f, f4 * f3, f1 * f3, 0.5F * f3, 0.6F * f3, 0.5F, tofloatfunction);
        CubicSpline<C, I> cubicspline5 = ridgeSpline(i1, f, f4, f4, f1, f2, 0.5F, tofloatfunction);
        CubicSpline<C, I> cubicspline6 = ridgeSpline(i1, f, f4, f4, f1, f2, 0.5F, tofloatfunction);
        CubicSpline<C, I> cubicspline7 = CubicSpline.builder(i1, tofloatfunction).addPoint(-1.0F, f).addPoint(-0.4F, cubicspline5).addPoint(0.0F, f2 + 0.07F).build();
        CubicSpline<C, I> cubicspline8 = ridgeSpline(i1, -0.02F, f5, f5, f1, f2, 0.0F, tofloatfunction);
        CubicSpline.b<C, I> cubicspline_b = CubicSpline.builder(i0, tofloatfunction).addPoint(-0.85F, cubicspline).addPoint(-0.7F, cubicspline1).addPoint(-0.4F, cubicspline2).addPoint(-0.35F, cubicspline3).addPoint(-0.1F, cubicspline4).addPoint(0.2F, cubicspline5);

        if (flag) {
            cubicspline_b.addPoint(0.4F, cubicspline6).addPoint(0.45F, cubicspline7).addPoint(0.55F, cubicspline7).addPoint(0.58F, cubicspline6);
        }

        cubicspline_b.addPoint(0.7F, cubicspline8);
        return cubicspline_b.build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> ridgeSpline(I i0, float f, float f1, float f2, float f3, float f4, float f5, ToFloatFunction<Float> tofloatfunction) {
        float f6 = Math.max(0.5F * (f1 - f), f5);
        float f7 = 5.0F * (f2 - f1);

        return CubicSpline.builder(i0, tofloatfunction).addPoint(-1.0F, f, f6).addPoint(-0.4F, f1, Math.min(f6, f7)).addPoint(0.0F, f2, f7).addPoint(0.4F, f3, 2.0F * (f3 - f2)).addPoint(1.0F, f4, 0.7F * (f4 - f3)).build();
    }
}

package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;

public final class TerrainShaper {

    private static final Codec<CubicSpline<TerrainShaper.b>> SPLINE_CODEC = CubicSpline.codec(TerrainShaper.a.WIDE_CODEC);
    public static final Codec<TerrainShaper> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(TerrainShaper.SPLINE_CODEC.fieldOf("offset").forGetter(TerrainShaper::offsetSampler), TerrainShaper.SPLINE_CODEC.fieldOf("factor").forGetter(TerrainShaper::factorSampler), TerrainShaper.SPLINE_CODEC.fieldOf("jaggedness").forGetter((terrainshaper) -> {
            return terrainshaper.jaggednessSampler;
        })).apply(instance, TerrainShaper::new);
    });
    private static final float GLOBAL_OFFSET = -0.50375F;
    private static final ToFloatFunction<Float> NO_TRANSFORM = (ofloat) -> {
        return ofloat;
    };
    private final CubicSpline<TerrainShaper.b> offsetSampler;
    private final CubicSpline<TerrainShaper.b> factorSampler;
    private final CubicSpline<TerrainShaper.b> jaggednessSampler;

    public TerrainShaper(CubicSpline<TerrainShaper.b> cubicspline, CubicSpline<TerrainShaper.b> cubicspline1, CubicSpline<TerrainShaper.b> cubicspline2) {
        this.offsetSampler = cubicspline;
        this.factorSampler = cubicspline1;
        this.jaggednessSampler = cubicspline2;
    }

    private static float getAmplifiedOffset(float f) {
        return f < 0.0F ? f : f * 2.0F;
    }

    private static float getAmplifiedFactor(float f) {
        return 1.25F - 6.25F / (f + 5.0F);
    }

    private static float getAmplifiedJaggedness(float f) {
        return f * 2.0F;
    }

    public static TerrainShaper overworld(boolean flag) {
        ToFloatFunction<Float> tofloatfunction = flag ? TerrainShaper::getAmplifiedOffset : TerrainShaper.NO_TRANSFORM;
        ToFloatFunction<Float> tofloatfunction1 = flag ? TerrainShaper::getAmplifiedFactor : TerrainShaper.NO_TRANSFORM;
        ToFloatFunction<Float> tofloatfunction2 = flag ? TerrainShaper::getAmplifiedJaggedness : TerrainShaper.NO_TRANSFORM;
        CubicSpline<TerrainShaper.b> cubicspline = buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline1 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline2 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline3 = buildErosionOffsetSpline(-0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, tofloatfunction);
        float f = -0.51F;
        float f1 = -0.4F;
        float f2 = 0.1F;
        float f3 = -0.15F;
        CubicSpline<TerrainShaper.b> cubicspline4 = CubicSpline.builder(TerrainShaper.a.CONTINENTS, tofloatfunction).addPoint(-1.1F, 0.044F, 0.0F).addPoint(-1.02F, -0.2222F, 0.0F).addPoint(-0.51F, -0.2222F, 0.0F).addPoint(-0.44F, -0.12F, 0.0F).addPoint(-0.18F, -0.12F, 0.0F).addPoint(-0.16F, cubicspline, 0.0F).addPoint(-0.15F, cubicspline, 0.0F).addPoint(-0.1F, cubicspline1, 0.0F).addPoint(0.25F, cubicspline2, 0.0F).addPoint(1.0F, cubicspline3, 0.0F).build();
        CubicSpline<TerrainShaper.b> cubicspline5 = CubicSpline.builder(TerrainShaper.a.CONTINENTS, TerrainShaper.NO_TRANSFORM).addPoint(-0.19F, 3.95F, 0.0F).addPoint(-0.15F, getErosionFactor(6.25F, true, TerrainShaper.NO_TRANSFORM), 0.0F).addPoint(-0.1F, getErosionFactor(5.47F, true, tofloatfunction1), 0.0F).addPoint(0.03F, getErosionFactor(5.08F, true, tofloatfunction1), 0.0F).addPoint(0.06F, getErosionFactor(4.69F, false, tofloatfunction1), 0.0F).build();
        float f4 = 0.65F;
        CubicSpline<TerrainShaper.b> cubicspline6 = CubicSpline.builder(TerrainShaper.a.CONTINENTS, tofloatfunction2).addPoint(-0.11F, 0.0F, 0.0F).addPoint(0.03F, buildErosionJaggednessSpline(1.0F, 0.5F, 0.0F, 0.0F, tofloatfunction2), 0.0F).addPoint(0.65F, buildErosionJaggednessSpline(1.0F, 1.0F, 1.0F, 0.0F, tofloatfunction2), 0.0F).build();

        return new TerrainShaper(cubicspline4, cubicspline5, cubicspline6);
    }

    private static CubicSpline<TerrainShaper.b> buildErosionJaggednessSpline(float f, float f1, float f2, float f3, ToFloatFunction<Float> tofloatfunction) {
        float f4 = -0.5775F;
        CubicSpline<TerrainShaper.b> cubicspline = buildRidgeJaggednessSpline(f, f2, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline1 = buildRidgeJaggednessSpline(f1, f3, tofloatfunction);

        return CubicSpline.builder(TerrainShaper.a.EROSION, tofloatfunction).addPoint(-1.0F, cubicspline, 0.0F).addPoint(-0.78F, cubicspline1, 0.0F).addPoint(-0.5775F, cubicspline1, 0.0F).addPoint(-0.375F, 0.0F, 0.0F).build();
    }

    private static CubicSpline<TerrainShaper.b> buildRidgeJaggednessSpline(float f, float f1, ToFloatFunction<Float> tofloatfunction) {
        float f2 = peaksAndValleys(0.4F);
        float f3 = peaksAndValleys(0.56666666F);
        float f4 = (f2 + f3) / 2.0F;
        CubicSpline.b<TerrainShaper.b> cubicspline_b = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction);

        cubicspline_b.addPoint(f2, 0.0F, 0.0F);
        if (f1 > 0.0F) {
            cubicspline_b.addPoint(f4, buildWeirdnessJaggednessSpline(f1, tofloatfunction), 0.0F);
        } else {
            cubicspline_b.addPoint(f4, 0.0F, 0.0F);
        }

        if (f > 0.0F) {
            cubicspline_b.addPoint(1.0F, buildWeirdnessJaggednessSpline(f, tofloatfunction), 0.0F);
        } else {
            cubicspline_b.addPoint(1.0F, 0.0F, 0.0F);
        }

        return cubicspline_b.build();
    }

    private static CubicSpline<TerrainShaper.b> buildWeirdnessJaggednessSpline(float f, ToFloatFunction<Float> tofloatfunction) {
        float f1 = 0.63F * f;
        float f2 = 0.3F * f;

        return CubicSpline.builder(TerrainShaper.a.WEIRDNESS, tofloatfunction).addPoint(-0.01F, f1, 0.0F).addPoint(0.01F, f2, 0.0F).build();
    }

    private static CubicSpline<TerrainShaper.b> getErosionFactor(float f, boolean flag, ToFloatFunction<Float> tofloatfunction) {
        CubicSpline<TerrainShaper.b> cubicspline = CubicSpline.builder(TerrainShaper.a.WEIRDNESS, tofloatfunction).addPoint(-0.2F, 6.3F, 0.0F).addPoint(0.2F, f, 0.0F).build();
        CubicSpline.b<TerrainShaper.b> cubicspline_b = CubicSpline.builder(TerrainShaper.a.EROSION, tofloatfunction).addPoint(-0.6F, cubicspline, 0.0F).addPoint(-0.5F, CubicSpline.builder(TerrainShaper.a.WEIRDNESS, tofloatfunction).addPoint(-0.05F, 6.3F, 0.0F).addPoint(0.05F, 2.67F, 0.0F).build(), 0.0F).addPoint(-0.35F, cubicspline, 0.0F).addPoint(-0.25F, cubicspline, 0.0F).addPoint(-0.1F, CubicSpline.builder(TerrainShaper.a.WEIRDNESS, tofloatfunction).addPoint(-0.05F, 2.67F, 0.0F).addPoint(0.05F, 6.3F, 0.0F).build(), 0.0F).addPoint(0.03F, cubicspline, 0.0F);
        CubicSpline cubicspline1;
        CubicSpline cubicspline2;

        if (flag) {
            cubicspline1 = CubicSpline.builder(TerrainShaper.a.WEIRDNESS, tofloatfunction).addPoint(0.0F, f, 0.0F).addPoint(0.1F, 0.625F, 0.0F).build();
            cubicspline2 = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction).addPoint(-0.9F, f, 0.0F).addPoint(-0.69F, cubicspline1, 0.0F).build();
            cubicspline_b.addPoint(0.35F, f, 0.0F).addPoint(0.45F, cubicspline2, 0.0F).addPoint(0.55F, cubicspline2, 0.0F).addPoint(0.62F, f, 0.0F);
        } else {
            cubicspline1 = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction).addPoint(-0.7F, cubicspline, 0.0F).addPoint(-0.15F, 1.37F, 0.0F).build();
            cubicspline2 = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction).addPoint(0.45F, cubicspline, 0.0F).addPoint(0.7F, 1.56F, 0.0F).build();
            cubicspline_b.addPoint(0.05F, cubicspline2, 0.0F).addPoint(0.4F, cubicspline2, 0.0F).addPoint(0.45F, cubicspline1, 0.0F).addPoint(0.55F, cubicspline1, 0.0F).addPoint(0.58F, f, 0.0F);
        }

        return cubicspline_b.build();
    }

    private static float calculateSlope(float f, float f1, float f2, float f3) {
        return (f1 - f) / (f3 - f2);
    }

    private static CubicSpline<TerrainShaper.b> buildMountainRidgeSplineWithPoints(float f, boolean flag, ToFloatFunction<Float> tofloatfunction) {
        CubicSpline.b<TerrainShaper.b> cubicspline_b = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction);
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
            cubicspline_b.addPoint(-0.75F, f10, 0.0F);
            cubicspline_b.addPoint(-0.65F, f8, 0.0F);
            float f12 = mountainContinentalness(f6, f, -0.7F);
            float f13 = calculateSlope(f12, f5, f6, 1.0F);
            float f14 = 0.01F;

            cubicspline_b.addPoint(f6 - 0.01F, f12, 0.0F);
            cubicspline_b.addPoint(f6, f12, f13);
            cubicspline_b.addPoint(1.0F, f5, f13);
        } else {
            f8 = calculateSlope(f3, f5, -1.0F, 1.0F);
            if (flag) {
                cubicspline_b.addPoint(-1.0F, Math.max(0.2F, f3), 0.0F);
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

    private static CubicSpline<TerrainShaper.b> buildErosionOffsetSpline(float f, float f1, float f2, float f3, float f4, float f5, boolean flag, boolean flag1, ToFloatFunction<Float> tofloatfunction) {
        float f6 = 0.6F;
        float f7 = 0.5F;
        float f8 = 0.5F;
        CubicSpline<TerrainShaper.b> cubicspline = buildMountainRidgeSplineWithPoints(MathHelper.lerp(f3, 0.6F, 1.5F), flag1, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline1 = buildMountainRidgeSplineWithPoints(MathHelper.lerp(f3, 0.6F, 1.0F), flag1, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline2 = buildMountainRidgeSplineWithPoints(f3, flag1, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline3 = ridgeSpline(f - 0.15F, 0.5F * f3, MathHelper.lerp(0.5F, 0.5F, 0.5F) * f3, 0.5F * f3, 0.6F * f3, 0.5F, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline4 = ridgeSpline(f, f4 * f3, f1 * f3, 0.5F * f3, 0.6F * f3, 0.5F, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline5 = ridgeSpline(f, f4, f4, f1, f2, 0.5F, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline6 = ridgeSpline(f, f4, f4, f1, f2, 0.5F, tofloatfunction);
        CubicSpline<TerrainShaper.b> cubicspline7 = CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction).addPoint(-1.0F, f, 0.0F).addPoint(-0.4F, cubicspline5, 0.0F).addPoint(0.0F, f2 + 0.07F, 0.0F).build();
        CubicSpline<TerrainShaper.b> cubicspline8 = ridgeSpline(-0.02F, f5, f5, f1, f2, 0.0F, tofloatfunction);
        CubicSpline.b<TerrainShaper.b> cubicspline_b = CubicSpline.builder(TerrainShaper.a.EROSION, tofloatfunction).addPoint(-0.85F, cubicspline, 0.0F).addPoint(-0.7F, cubicspline1, 0.0F).addPoint(-0.4F, cubicspline2, 0.0F).addPoint(-0.35F, cubicspline3, 0.0F).addPoint(-0.1F, cubicspline4, 0.0F).addPoint(0.2F, cubicspline5, 0.0F);

        if (flag) {
            cubicspline_b.addPoint(0.4F, cubicspline6, 0.0F).addPoint(0.45F, cubicspline7, 0.0F).addPoint(0.55F, cubicspline7, 0.0F).addPoint(0.58F, cubicspline6, 0.0F);
        }

        cubicspline_b.addPoint(0.7F, cubicspline8, 0.0F);
        return cubicspline_b.build();
    }

    private static CubicSpline<TerrainShaper.b> ridgeSpline(float f, float f1, float f2, float f3, float f4, float f5, ToFloatFunction<Float> tofloatfunction) {
        float f6 = Math.max(0.5F * (f1 - f), f5);
        float f7 = 5.0F * (f2 - f1);

        return CubicSpline.builder(TerrainShaper.a.RIDGES, tofloatfunction).addPoint(-1.0F, f, f6).addPoint(-0.4F, f1, Math.min(f6, f7)).addPoint(0.0F, f2, f7).addPoint(0.4F, f3, 2.0F * (f3 - f2)).addPoint(1.0F, f4, 0.7F * (f4 - f3)).build();
    }

    public void addDebugBiomesToVisualizeSplinePoints(Consumer<Pair<Climate.d, ResourceKey<BiomeBase>>> consumer) {
        Climate.b climate_b = Climate.b.span(-1.0F, 1.0F);

        consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b, climate_b, climate_b, Climate.b.point(0.0F), climate_b, 0.01F), Biomes.PLAINS));
        CubicSpline.d<TerrainShaper.b> cubicspline_d = (CubicSpline.d) buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, TerrainShaper.NO_TRANSFORM);
        ResourceKey<BiomeBase> resourcekey = Biomes.DESERT;
        float[] afloat = cubicspline_d.locations();
        int i = afloat.length;

        Float ofloat;
        int j;

        for (j = 0; j < i; ++j) {
            ofloat = afloat[j];
            consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b, climate_b, Climate.b.point(ofloat), Climate.b.point(0.0F), climate_b, 0.0F), resourcekey));
            resourcekey = resourcekey == Biomes.DESERT ? Biomes.BADLANDS : Biomes.DESERT;
        }

        afloat = ((CubicSpline.d) this.offsetSampler).locations();
        i = afloat.length;

        for (j = 0; j < i; ++j) {
            ofloat = afloat[j];
            consumer.accept(Pair.of(Climate.parameters(climate_b, climate_b, Climate.b.point(ofloat), climate_b, Climate.b.point(0.0F), climate_b, 0.0F), Biomes.SNOWY_TAIGA));
        }

    }

    @VisibleForDebug
    public CubicSpline<TerrainShaper.b> offsetSampler() {
        return this.offsetSampler;
    }

    @VisibleForDebug
    public CubicSpline<TerrainShaper.b> factorSampler() {
        return this.factorSampler;
    }

    @VisibleForDebug
    public CubicSpline<TerrainShaper.b> jaggednessSampler() {
        return this.jaggednessSampler;
    }

    public float offset(TerrainShaper.b terrainshaper_b) {
        return this.offsetSampler.apply(terrainshaper_b) + -0.50375F;
    }

    public float factor(TerrainShaper.b terrainshaper_b) {
        return this.factorSampler.apply(terrainshaper_b);
    }

    public float jaggedness(TerrainShaper.b terrainshaper_b) {
        return this.jaggednessSampler.apply(terrainshaper_b);
    }

    public TerrainShaper.b makePoint(float f, float f1, float f2) {
        return new TerrainShaper.b(f, f1, peaksAndValleys(f2), f2);
    }

    public static float peaksAndValleys(float f) {
        return -(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F;
    }

    @VisibleForTesting
    protected static enum a implements INamable, ToFloatFunction<TerrainShaper.b> {

        CONTINENTS(TerrainShaper.b::continents, "continents"), EROSION(TerrainShaper.b::erosion, "erosion"), WEIRDNESS(TerrainShaper.b::weirdness, "weirdness"),
        /** @deprecated */
        @Deprecated
        RIDGES(TerrainShaper.b::ridges, "ridges");

        private static final Map<String, TerrainShaper.a> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(TerrainShaper.a::getSerializedName, (terrainshaper_a) -> {
            return terrainshaper_a;
        }));
        private static final Codec<TerrainShaper.a> CODEC;
        static final Codec<ToFloatFunction<TerrainShaper.b>> WIDE_CODEC;
        private final ToFloatFunction<TerrainShaper.b> reference;
        private final String name;

        private a(ToFloatFunction tofloatfunction, String s) {
            this.reference = tofloatfunction;
            this.name = s;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public float apply(TerrainShaper.b terrainshaper_b) {
            return this.reference.apply(terrainshaper_b);
        }

        static {
            Supplier supplier = TerrainShaper.a::values;
            Map map = TerrainShaper.a.BY_NAME;

            Objects.requireNonNull(map);
            CODEC = INamable.fromEnum(supplier, map::get);
            WIDE_CODEC = TerrainShaper.a.CODEC.flatComapMap((terrainshaper_a) -> {
                return terrainshaper_a;
            }, (tofloatfunction) -> {
                DataResult dataresult;

                if (tofloatfunction instanceof TerrainShaper.a) {
                    TerrainShaper.a terrainshaper_a = (TerrainShaper.a) tofloatfunction;

                    dataresult = DataResult.success(terrainshaper_a);
                } else {
                    dataresult = DataResult.error("Not a coordinate resolver: " + tofloatfunction);
                }

                return dataresult;
            });
        }
    }

    public static record b(float a, float b, float c, float d) {

        private final float continents;
        private final float erosion;
        private final float ridges;
        private final float weirdness;

        public b(float f, float f1, float f2, float f3) {
            this.continents = f;
            this.erosion = f1;
            this.ridges = f2;
            this.weirdness = f3;
        }

        public float continents() {
            return this.continents;
        }

        public float erosion() {
            return this.erosion;
        }

        public float ridges() {
            return this.ridges;
        }

        public float weirdness() {
            return this.weirdness;
        }
    }
}

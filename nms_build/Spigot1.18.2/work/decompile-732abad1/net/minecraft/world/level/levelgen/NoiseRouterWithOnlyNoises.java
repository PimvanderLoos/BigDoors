package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public record NoiseRouterWithOnlyNoises(DensityFunction b, DensityFunction c, DensityFunction d, DensityFunction e, DensityFunction f, DensityFunction g, DensityFunction h, DensityFunction i, DensityFunction j, DensityFunction k, DensityFunction l, DensityFunction m, DensityFunction n, DensityFunction o, DensityFunction p) {

    private final DensityFunction barrierNoise;
    private final DensityFunction fluidLevelFloodednessNoise;
    private final DensityFunction fluidLevelSpreadNoise;
    private final DensityFunction lavaNoise;
    private final DensityFunction temperature;
    private final DensityFunction vegetation;
    private final DensityFunction continents;
    private final DensityFunction erosion;
    private final DensityFunction depth;
    private final DensityFunction ridges;
    private final DensityFunction initialDensityWithoutJaggedness;
    private final DensityFunction finalDensity;
    private final DensityFunction veinToggle;
    private final DensityFunction veinRidged;
    private final DensityFunction veinGap;
    public static final Codec<NoiseRouterWithOnlyNoises> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(field("barrier", NoiseRouterWithOnlyNoises::barrierNoise), field("fluid_level_floodedness", NoiseRouterWithOnlyNoises::fluidLevelFloodednessNoise), field("fluid_level_spread", NoiseRouterWithOnlyNoises::fluidLevelSpreadNoise), field("lava", NoiseRouterWithOnlyNoises::lavaNoise), field("temperature", NoiseRouterWithOnlyNoises::temperature), field("vegetation", NoiseRouterWithOnlyNoises::vegetation), field("continents", NoiseRouterWithOnlyNoises::continents), field("erosion", NoiseRouterWithOnlyNoises::erosion), field("depth", NoiseRouterWithOnlyNoises::depth), field("ridges", NoiseRouterWithOnlyNoises::ridges), field("initial_density_without_jaggedness", NoiseRouterWithOnlyNoises::initialDensityWithoutJaggedness), field("final_density", NoiseRouterWithOnlyNoises::finalDensity), field("vein_toggle", NoiseRouterWithOnlyNoises::veinToggle), field("vein_ridged", NoiseRouterWithOnlyNoises::veinRidged), field("vein_gap", NoiseRouterWithOnlyNoises::veinGap)).apply(instance, NoiseRouterWithOnlyNoises::new);
    });

    public NoiseRouterWithOnlyNoises(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunction densityfunction3, DensityFunction densityfunction4, DensityFunction densityfunction5, DensityFunction densityfunction6, DensityFunction densityfunction7, DensityFunction densityfunction8, DensityFunction densityfunction9, DensityFunction densityfunction10, DensityFunction densityfunction11, DensityFunction densityfunction12, DensityFunction densityfunction13, DensityFunction densityfunction14) {
        this.barrierNoise = densityfunction;
        this.fluidLevelFloodednessNoise = densityfunction1;
        this.fluidLevelSpreadNoise = densityfunction2;
        this.lavaNoise = densityfunction3;
        this.temperature = densityfunction4;
        this.vegetation = densityfunction5;
        this.continents = densityfunction6;
        this.erosion = densityfunction7;
        this.depth = densityfunction8;
        this.ridges = densityfunction9;
        this.initialDensityWithoutJaggedness = densityfunction10;
        this.finalDensity = densityfunction11;
        this.veinToggle = densityfunction12;
        this.veinRidged = densityfunction13;
        this.veinGap = densityfunction14;
    }

    private static RecordCodecBuilder<NoiseRouterWithOnlyNoises, DensityFunction> field(String s, Function<NoiseRouterWithOnlyNoises, DensityFunction> function) {
        return DensityFunction.HOLDER_HELPER_CODEC.fieldOf(s).forGetter(function);
    }

    public NoiseRouterWithOnlyNoises mapAll(DensityFunction.e densityfunction_e) {
        return new NoiseRouterWithOnlyNoises(this.barrierNoise.mapAll(densityfunction_e), this.fluidLevelFloodednessNoise.mapAll(densityfunction_e), this.fluidLevelSpreadNoise.mapAll(densityfunction_e), this.lavaNoise.mapAll(densityfunction_e), this.temperature.mapAll(densityfunction_e), this.vegetation.mapAll(densityfunction_e), this.continents.mapAll(densityfunction_e), this.erosion.mapAll(densityfunction_e), this.depth.mapAll(densityfunction_e), this.ridges.mapAll(densityfunction_e), this.initialDensityWithoutJaggedness.mapAll(densityfunction_e), this.finalDensity.mapAll(densityfunction_e), this.veinToggle.mapAll(densityfunction_e), this.veinRidged.mapAll(densityfunction_e), this.veinGap.mapAll(densityfunction_e));
    }

    public DensityFunction barrierNoise() {
        return this.barrierNoise;
    }

    public DensityFunction fluidLevelFloodednessNoise() {
        return this.fluidLevelFloodednessNoise;
    }

    public DensityFunction fluidLevelSpreadNoise() {
        return this.fluidLevelSpreadNoise;
    }

    public DensityFunction lavaNoise() {
        return this.lavaNoise;
    }

    public DensityFunction temperature() {
        return this.temperature;
    }

    public DensityFunction vegetation() {
        return this.vegetation;
    }

    public DensityFunction continents() {
        return this.continents;
    }

    public DensityFunction erosion() {
        return this.erosion;
    }

    public DensityFunction depth() {
        return this.depth;
    }

    public DensityFunction ridges() {
        return this.ridges;
    }

    public DensityFunction initialDensityWithoutJaggedness() {
        return this.initialDensityWithoutJaggedness;
    }

    public DensityFunction finalDensity() {
        return this.finalDensity;
    }

    public DensityFunction veinToggle() {
        return this.veinToggle;
    }

    public DensityFunction veinRidged() {
        return this.veinRidged;
    }

    public DensityFunction veinGap() {
        return this.veinGap;
    }
}

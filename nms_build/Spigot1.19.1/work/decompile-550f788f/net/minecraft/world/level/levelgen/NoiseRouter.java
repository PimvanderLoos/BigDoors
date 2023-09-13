package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {

    public static final Codec<NoiseRouter> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(field("barrier", NoiseRouter::barrierNoise), field("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise), field("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise), field("lava", NoiseRouter::lavaNoise), field("temperature", NoiseRouter::temperature), field("vegetation", NoiseRouter::vegetation), field("continents", NoiseRouter::continents), field("erosion", NoiseRouter::erosion), field("depth", NoiseRouter::depth), field("ridges", NoiseRouter::ridges), field("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness), field("final_density", NoiseRouter::finalDensity), field("vein_toggle", NoiseRouter::veinToggle), field("vein_ridged", NoiseRouter::veinRidged), field("vein_gap", NoiseRouter::veinGap)).apply(instance, NoiseRouter::new);
    });

    private static RecordCodecBuilder<NoiseRouter, DensityFunction> field(String s, Function<NoiseRouter, DensityFunction> function) {
        return DensityFunction.HOLDER_HELPER_CODEC.fieldOf(s).forGetter(function);
    }

    public NoiseRouter mapAll(DensityFunction.f densityfunction_f) {
        return new NoiseRouter(this.barrierNoise.mapAll(densityfunction_f), this.fluidLevelFloodednessNoise.mapAll(densityfunction_f), this.fluidLevelSpreadNoise.mapAll(densityfunction_f), this.lavaNoise.mapAll(densityfunction_f), this.temperature.mapAll(densityfunction_f), this.vegetation.mapAll(densityfunction_f), this.continents.mapAll(densityfunction_f), this.erosion.mapAll(densityfunction_f), this.depth.mapAll(densityfunction_f), this.ridges.mapAll(densityfunction_f), this.initialDensityWithoutJaggedness.mapAll(densityfunction_f), this.finalDensity.mapAll(densityfunction_f), this.veinToggle.mapAll(densityfunction_f), this.veinRidged.mapAll(densityfunction_f), this.veinGap.mapAll(densityfunction_f));
    }
}

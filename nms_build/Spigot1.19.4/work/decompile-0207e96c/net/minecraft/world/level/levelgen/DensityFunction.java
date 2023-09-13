package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public interface DensityFunction {

    Codec<DensityFunction> DIRECT_CODEC = DensityFunctions.DIRECT_CODEC;
    Codec<Holder<DensityFunction>> CODEC = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC);
    Codec<DensityFunction> HOLDER_HELPER_CODEC = DensityFunction.CODEC.xmap(DensityFunctions.j::new, (densityfunction) -> {
        if (densityfunction instanceof DensityFunctions.j) {
            DensityFunctions.j densityfunctions_j = (DensityFunctions.j) densityfunction;

            return densityfunctions_j.function();
        } else {
            return new Holder.a<>(densityfunction);
        }
    });

    double compute(DensityFunction.b densityfunction_b);

    void fillArray(double[] adouble, DensityFunction.a densityfunction_a);

    DensityFunction mapAll(DensityFunction.f densityfunction_f);

    double minValue();

    double maxValue();

    KeyDispatchDataCodec<? extends DensityFunction> codec();

    default DensityFunction clamp(double d0, double d1) {
        return new DensityFunctions.g(this, d0, d1);
    }

    default DensityFunction abs() {
        return DensityFunctions.map(this, DensityFunctions.k.a.ABS);
    }

    default DensityFunction square() {
        return DensityFunctions.map(this, DensityFunctions.k.a.SQUARE);
    }

    default DensityFunction cube() {
        return DensityFunctions.map(this, DensityFunctions.k.a.CUBE);
    }

    default DensityFunction halfNegative() {
        return DensityFunctions.map(this, DensityFunctions.k.a.HALF_NEGATIVE);
    }

    default DensityFunction quarterNegative() {
        return DensityFunctions.map(this, DensityFunctions.k.a.QUARTER_NEGATIVE);
    }

    default DensityFunction squeeze() {
        return DensityFunctions.map(this, DensityFunctions.k.a.SQUEEZE);
    }

    public static record e(int blockX, int blockY, int blockZ) implements DensityFunction.b {

    }

    public interface b {

        int blockX();

        int blockY();

        int blockZ();

        default Blender getBlender() {
            return Blender.empty();
        }
    }

    public interface d extends DensityFunction {

        @Override
        default void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        default DensityFunction mapAll(DensityFunction.f densityfunction_f) {
            return densityfunction_f.apply(this);
        }
    }

    public interface f {

        DensityFunction apply(DensityFunction densityfunction);

        default DensityFunction.c visitNoise(DensityFunction.c densityfunction_c) {
            return densityfunction_c;
        }
    }

    public static record c(Holder<NoiseGeneratorNormal.a> noiseData, @Nullable NoiseGeneratorNormal noise) {

        public static final Codec<DensityFunction.c> CODEC = NoiseGeneratorNormal.a.CODEC.xmap((holder) -> {
            return new DensityFunction.c(holder, (NoiseGeneratorNormal) null);
        }, DensityFunction.c::noiseData);

        public c(Holder<NoiseGeneratorNormal.a> holder) {
            this(holder, (NoiseGeneratorNormal) null);
        }

        public double getValue(double d0, double d1, double d2) {
            return this.noise == null ? 0.0D : this.noise.getValue(d0, d1, d2);
        }

        public double maxValue() {
            return this.noise == null ? 2.0D : this.noise.maxValue();
        }
    }

    public interface a {

        DensityFunction.b forIndex(int i);

        void fillAllDirectly(double[] adouble, DensityFunction densityfunction);
    }
}

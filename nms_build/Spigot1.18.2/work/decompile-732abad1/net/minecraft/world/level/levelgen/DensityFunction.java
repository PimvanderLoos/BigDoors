package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.blending.Blender;

public interface DensityFunction {

    Codec<DensityFunction> DIRECT_CODEC = DensityFunctions.DIRECT_CODEC;
    Codec<Holder<DensityFunction>> CODEC = RegistryFileCodec.create(IRegistry.DENSITY_FUNCTION_REGISTRY, DensityFunction.DIRECT_CODEC);
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

    DensityFunction mapAll(DensityFunction.e densityfunction_e);

    double minValue();

    double maxValue();

    Codec<? extends DensityFunction> codec();

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

    public static record d(int a, int b, int c) implements DensityFunction.b {

        private final int blockX;
        private final int blockY;
        private final int blockZ;

        public d(int i, int j, int k) {
            this.blockX = i;
            this.blockY = j;
            this.blockZ = k;
        }

        @Override
        public int blockX() {
            return this.blockX;
        }

        @Override
        public int blockY() {
            return this.blockY;
        }

        @Override
        public int blockZ() {
            return this.blockZ;
        }
    }

    public interface b {

        int blockX();

        int blockY();

        int blockZ();

        default Blender getBlender() {
            return Blender.empty();
        }
    }

    public interface c extends DensityFunction {

        @Override
        default void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        default DensityFunction mapAll(DensityFunction.e densityfunction_e) {
            return (DensityFunction) densityfunction_e.apply(this);
        }
    }

    public interface e extends Function<DensityFunction, DensityFunction> {}

    public interface a {

        DensityFunction.b forIndex(int i);

        void fillAllDirectly(double[] adouble, DensityFunction densityfunction);
    }
}
